package com.ht.bigdata1.entity;

import com.ht.bigdata1.validata.SplitFileInfoRequest;
import lombok.Getter;
import lombok.Setter;
/**
 * @Description: 用于保存临时文件的参数信息
 * @Author: yjs
 * @createTime: 2022年05月11日 13:17:10
 * @version: 1.0
 */
@Getter
@Setter
public class SplitFileParam {
    /**
     * 临时文件的文件名
     */
    private String tmpFileName;

    /**
     * 临时文件的本地存储路径
     */
    private String tmpLocalPath;

    /**
     * 最终的文件名
     */
    private String fileName;

    /**
     * 文件本地存储路径
     */
    private String localPath;

    /**
     * 文件给外部访问的url
     */
    private String webUrl;

    /**
     * 前端传入的请求体（文件参数）
     */
    private SplitFileInfoRequest splitFileInfo;
}
