package com.ht.bigdata1.entity;


import com.ht.bigdata1.util.FileSplitUploadStatusEnum;
import lombok.*;

/**
 * @Description: FileSplitUploadResponse
 * @Author: yjs
 * @createTime: 2022年05月11日 13:14:58
 * @version: 1.0
 */
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileSplitUploadResponse<T> {
    private static final String SPLIT_SUCCESS_MSG = "第%d分片上传完成，共%d分片。请继续上传后续分片";
    private Integer code;
    private Integer status;
    private String msg;
    private T data;

    public FileSplitUploadResponse(FileSplitUploadStatusEnum statusEnum) {
        this.code = statusEnum.getCode();
        this.status = statusEnum.getStatus();
        this.msg = statusEnum.getMsg();
    }

    public FileSplitUploadResponse(Integer splitIndex, Integer splitTotal) {
        FileSplitUploadStatusEnum statusEnum = FileSplitUploadStatusEnum.SPLIT_SUCCESS;
        this.code = statusEnum.getCode();
        this.status = statusEnum.getStatus();
        this.msg = String.format(SPLIT_SUCCESS_MSG, splitIndex, splitTotal);
    }
}