package com.ht.bigdata1.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 文件分片上传状态码
 * http状态码见：http://tools.jb51.net/table/http_status_code
 */
@Getter
@AllArgsConstructor
public enum FileSplitUploadStatusEnum {
    SUCCESS(200, 1000, "整个文件上传完成"),
    SPLIT_SUCCESS(100, 1001, "分片上传完成，请继续上传后续分片"),
    PARAM_VALID_FAIL(400, 1002, "文件参数校验失败"),
    FILE_INFO_CONFLICT(409, 1003, "当前上传人数较多，文件冲突，请修改文件名或稍后上传"),
    OVER_MAX_SIZE(424, 1004, "文件大小超出限制"),
    FILE_MODIFIED_SIZE(424, 1005, "文件被篡改-文件长度不符"),
    FILE_MODIFIED_MD5(424, 1006, "文件被篡改-md5校验失败"),
    SYSTEM_ERROR(500, 1007, "系统异常");

    /**
     * 通用的http状态码
     */
    Integer code;

    /**
     * 上传文件分片的内部状态码
     */
    Integer status;

    /**
     * 状态描述
     */
    String msg;
}
