package com.ht.bigdata1.service.impl;

import com.ht.bigdata1.config.CustomConfig;
import com.ht.bigdata1.entity.FileSplitUploadResponse;
import com.ht.bigdata1.entity.SplitFileParam;
import com.ht.bigdata1.service.FileSplitUploadService;
import com.ht.bigdata1.util.FileSplitUploadStatusEnum;
import com.ht.bigdata1.util.VelificationUtil;
import com.ht.bigdata1.validata.SplitFileInfoRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.UUID;

/**
 * @Description: FileSplitUploadServiceImpl
 * @Author: yjs
 * @createTime: 2022年05月11日 13:27:02
 * @version: 1.0
 */
@Slf4j
@Service
public class FileSplitUploadServiceImpl implements FileSplitUploadService {
    private static final String PROPERTY_OS_NAME = "os.name";
    private static final String OS_LINUX = "linux";
    private static final String FILE_NAME_SEPARATOR = "-";
    private static final String TMP_FILE_NAME_SUFFIX = ".tmp";

    @Autowired
    private CustomConfig config;

    @Override
    public FileSplitUploadResponse splitUploadFile(SplitFileInfoRequest splitFileInfo) {
        FileSplitUploadResponse<String> response = null;
        SplitFileParam param = getParam(splitFileInfo);
        try {
            // 只有第一个分片时需要校验目录、文件是否存在。若请求有问题，一开始就从非第一分片开始传，直接抛出异常即可
            if (splitFileInfo.isFirstSplit()) {
                File tmpFile = new File(param.getTmpLocalPath());
                // 建立文件目录
                if (!tmpFile.getParentFile().exists()) {
                    tmpFile.getParentFile().mkdirs();
                }
                if (tmpFile.exists() && isConfilct(param.getTmpLocalPath())) {
                    log.warn("文件冲突，文件名：{}", param.getTmpFileName());
                    response = new FileSplitUploadResponse<>(FileSplitUploadStatusEnum.FILE_INFO_CONFLICT);
                    return response;
                }
            }
            writeFile(param.getSplitFileInfo().getSplitData(), param.getTmpLocalPath());
            if (splitFileInfo.isLastSplit()) {
                int checkResult = checkAndRenameFile(param);
                switch (checkResult) {
                    case 0:
                        response = new FileSplitUploadResponse(FileSplitUploadStatusEnum.SUCCESS);
                        response.setData(param.getWebUrl());
                        break;
                    case 1:
                        response = new FileSplitUploadResponse(FileSplitUploadStatusEnum.FILE_MODIFIED_SIZE);
                        break;
                    case 2:
                        response = new FileSplitUploadResponse(FileSplitUploadStatusEnum.FILE_MODIFIED_MD5);
                        break;
                    default:
                        log.error("check result:{}", checkResult);
                }
            } else {
                log.info("保存文件{}的第{}分片成功", param.getTmpFileName(), param.getSplitFileInfo().getSplitIndex());
                response = new FileSplitUploadResponse(splitFileInfo.getSplitIndex(), splitFileInfo.getSplitTotal());
            }
        } catch (Exception e) {
            log.error("文件上传失败: ", e);
            deleteFile(new File(param.getTmpLocalPath()));
            if (e.getMessage().equals(FileSplitUploadStatusEnum.OVER_MAX_SIZE.getCode().toString())) {
                response = new FileSplitUploadResponse(FileSplitUploadStatusEnum.OVER_MAX_SIZE);
            } else {
                response = new FileSplitUploadResponse(FileSplitUploadStatusEnum.SYSTEM_ERROR);
            }
        }
        return response;
    }

    /**
     * 校验文件并重命名
     * 为了减少对象的频繁创建与销毁，把校验和重命名放在一起
     *
     * @param param 文件参数
     * @return 0-成功/1-文件大小不符/2-文件md5不符
     * @throws IOException IO异常
     */
    private int checkAndRenameFile(SplitFileParam param) throws IOException {
        File file = new File(param.getTmpLocalPath());
        // check lenth
        if (param.getSplitFileInfo().getTotalSize() - file.length() != 0) {
            log.warn("文件大小不符：{}", param.getTmpFileName());
            deleteFile(file);
            return 1;
        }
        // check md5
        byte[] fileBytes = new byte[(int) file.length()];
        InputStream is = new FileInputStream(file);
        is.read(fileBytes);
        String md5Hex = VelificationUtil.getMD5Hex(fileBytes);
        is.close();
        if (!md5Hex.equals(param.getSplitFileInfo().getFileMd5())) {
            log.warn("文件md5不符：{}", param.getTmpFileName());
            deleteFile(file);
            return 2;
        }
        log.info("文件校验成功：{}", param.getTmpFileName());
        if (file.renameTo(new File(param.getLocalPath()))) {
            log.info("文件重命名成功：{} -> ", param.getTmpFileName(), param.getFileName());
        }
        return 0;
    }

    /**
     * 写入分片的数据到文件中
     *
     * @param splitData   分片数据
     * @param tmpFilePath 临时文件路径
     * @throws IOException IO异常
     */
    private void writeFile(byte[] splitData, String tmpFilePath) throws IOException {
        RandomAccessFile randomAccessFile = new RandomAccessFile(tmpFilePath, "rw");
        if (randomAccessFile.length() + splitData.length > config.getMaxFileSize()) {
            randomAccessFile.close();
            throw new RuntimeException(FileSplitUploadStatusEnum.OVER_MAX_SIZE.getCode().toString());
        }
        randomAccessFile.seek(randomAccessFile.length());
        randomAccessFile.write(splitData);
        randomAccessFile.close();
    }

    /**
     * 获取保存文件需要的参数
     *
     * @param splitFileInfo 前端上传的文件信息
     * @return 保存文件需要的参数
     */
    private SplitFileParam getParam(SplitFileInfoRequest splitFileInfo) {
        SplitFileParam param = new SplitFileParam();
        // 文件保存路径（OS下的路径）
        String localPathPre = System.getProperty(PROPERTY_OS_NAME).equalsIgnoreCase(OS_LINUX)
                ? config.getFileUploadInLinux() : config.getFileUploadInWindows();
        // 临时文件名
        String tmpFileName = splitFileInfo.getFileMd5()
                .concat(FILE_NAME_SEPARATOR)
                .concat(splitFileInfo.getFileName())
                .concat(TMP_FILE_NAME_SUFFIX);
        param.setTmpFileName(tmpFileName);
        param.setTmpLocalPath(localPathPre.concat(tmpFileName));
        // 最终的文件名
        if (splitFileInfo.isLastSplit()) {
            String fileName = UUID.randomUUID().toString()
                    .concat(FILE_NAME_SEPARATOR)
                    .concat(splitFileInfo.getFileName());
            param.setFileName(fileName);
            param.setLocalPath(localPathPre.concat(fileName));
            String webUrl = config.getFileUploadServerUrl().concat(fileName);
            param.setWebUrl(webUrl);
        }
        param.setSplitFileInfo(splitFileInfo);
        return param;
    }

    /**
     * 文件是否冲突
     * 如果md5和文件名都相同，就会产生冲突
     * 根据文件最大上传时间来判断到底是冲突还是脏数据，如果是脏数据，删除脏数据
     *
     * @param filePath 文件路径
     * @return false-脏数据/true-文件冲突
     */
    private boolean isConfilct(String filePath) {
        Long fileCreateTimeInMills = getFileCreateTimeInMills(filePath);
        if (System.currentTimeMillis() - fileCreateTimeInMills > config.getMaxUploadTime()) {
            deleteFile(new File(filePath));
            return false;
        }
        return true;
    }

    /**
     * 删除文件
     *
     * @param file 待删除文件
     */
    private void deleteFile(File file) {
        boolean delete = file.delete();
        if (!delete) {
            log.warn("文件删除失败:{}", file.getPath());
        }
    }

    /**
     * 获取文件创建时间
     *
     * @param filePath 文件路径
     * @return 文件创建时间戳
     */
    private static Long getFileCreateTimeInMills(String filePath) {
        try {
            Path path = Paths.get(filePath);
            BasicFileAttributeView basicview = Files.getFileAttributeView(path, BasicFileAttributeView.class, LinkOption.NOFOLLOW_LINKS);
            BasicFileAttributes attr = basicview.readAttributes();
            return attr.creationTime().toMillis();
        } catch (Exception e) {
            log.warn("获取文件创建时间出错：filePath:{}, errorMsg:{}", filePath, e);
            File file = new File(filePath);
            return file.lastModified();
        }
    }

}

