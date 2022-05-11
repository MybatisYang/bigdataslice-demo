package com.ht.bigdata1.validata;

import lombok.Data;

import javax.validation.constraints.*;

/**
 * @Description: SplitFileInfoRequest
 * @Author: yjs
 * @createTime: 2022年05月11日 13:13:57
 * @version: 1.0
 */

/**
 * 注：这里我使用的Spring Boot的版本为2.4.0，以下对参数的注解的message属性值，均不会返回给前端，若需要返回，请使用其他版本的javax.validation
 */
@Data
public class SplitFileInfoRequest {
    /**
     * 文件名称
     */
    @NotBlank(message = "无效的文件名，请重新上传")
    private String fileName;

    /**
     * 整个文件的md5
     */
    @NotBlank(message = "无效的文件md5，请重新上传")
    private String fileMd5;

    /**
     * 文件总大小
     * 由于需要读取整个文件到byte数组中以获取md5值，所以文件大小不能超过byte数组大小的限制（即Integer.MAX_VALUE）
     */
    @NotNull(message = "文件总大小不能为空")
    @Min(value = 1, message = "无效的文件总大小，请重新上传")
    @Max(value = Integer.MAX_VALUE, message = "文件总大小超出限制")
    private Integer totalSize;

    /**
     * 总分片数
     */
    @NotNull(message = "总分片数不能为空")
    @Min(value = 1, message = "无效的总分片数，请重新上传")
    private Integer splitTotal;

    /**
     * 当前为第几分片（从1开始）
     */
    @NotNull(message = "分片索引不能为空")
    @Min(value = 1, message = "无效的分片索引，请重新上传")
    private Integer splitIndex;

    /**
     * 当前分片大小
     */
    @NotNull(message = "当前分片大小不能为空")
    @Min(value = 1, message = "文件分片大小为1B-512KB")
    @Max(value = 2097152, message = "文件分片大小为1B-512KB")
    private Integer splitSize;

    /**
     * 当前文件分片内容(最大512k)
     */
    @NotNull(message = "当前文件分片内容不能为空")
    @Size(min = 1, max = 2097152, message = "文件分片大小限制为1B-512KB")
    private byte[] splitData;

    public boolean isFirstSplit() {
        return splitIndex.equals(1);
    }

    public boolean isLastSplit() {
        return splitIndex.equals(splitTotal);
    }
}
