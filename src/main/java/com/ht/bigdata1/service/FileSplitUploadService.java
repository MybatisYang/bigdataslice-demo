package com.ht.bigdata1.service;

import com.ht.bigdata1.entity.FileSplitUploadResponse;
import com.ht.bigdata1.validata.SplitFileInfoRequest;

/**
 * @Description: 文件分片上传
 * @Author: yjs
 * @createTime: 2022年05月11日 13:26:00
 * @version: 1.0
 */
public interface FileSplitUploadService {
    /**
     * 文件分片上传
     *
     * @param splitFileInfo 文件信息
     * @return 上传结果
     */
    FileSplitUploadResponse splitUploadFile(SplitFileInfoRequest splitFileInfo);
}
