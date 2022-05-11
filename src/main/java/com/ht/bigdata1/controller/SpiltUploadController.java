package com.ht.bigdata1.controller;

import com.ht.bigdata1.entity.FileSplitUploadResponse;
import com.ht.bigdata1.service.FileSplitUploadService;
import com.ht.bigdata1.util.FileSplitUploadStatusEnum;
import com.ht.bigdata1.validata.SplitFileInfoRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @Description: 文件分片上传
 * @Author: yjs
 * @createTime: 2022年05月11日 13:27:54
 * @version: 1.0
 */
@Slf4j
@RestController
@RequestMapping("/file")
public class SpiltUploadController {
    @Autowired
    FileSplitUploadService fileSplitUploadService;

    /**
     * 上传文件到本地
     *
     * @param splitFileInfo 文件
     * @return
     */
    @PostMapping("/upload")
    public FileSplitUploadResponse<String> uploadFileToLocal(@RequestBody @Valid SplitFileInfoRequest splitFileInfo) {
        if (splitFileInfo.getFileMd5().length() != 32
                || splitFileInfo.getSplitSize() - splitFileInfo.getSplitData().length != 0
                || splitFileInfo.getSplitIndex() > splitFileInfo.getSplitTotal()) {
            recordValidFailReason(splitFileInfo);
            FileSplitUploadResponse<String> response = new FileSplitUploadResponse(FileSplitUploadStatusEnum.PARAM_VALID_FAIL);
            return response;
        }
        return fileSplitUploadService.splitUploadFile(splitFileInfo);
    }

    private void recordValidFailReason(SplitFileInfoRequest splitFileInfo) {
        log.info("文件信息校验失败:" +
                        "\nfileMd5:{}" +
                        "\ntotalSize:{}" +
                        "\nsplitTotal:{}" +
                        "\nsplitIndex:{}" +
                        "\nsplitSize:{}" +
                        "\nsplitData.size:{}",
                splitFileInfo.getFileMd5(),
                splitFileInfo.getTotalSize(),
                splitFileInfo.getSplitTotal(),
                splitFileInfo.getSplitIndex(),
                splitFileInfo.getSplitSize(),
                splitFileInfo.getSplitData().length);
    }
}
