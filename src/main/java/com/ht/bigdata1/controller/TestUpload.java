package com.ht.bigdata1.controller;

import com.alibaba.fastjson.JSON;
import com.ht.bigdata1.util.VelificationUtil;
import com.ht.bigdata1.validata.SplitFileInfoRequest;
import okhttp3.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.TimeUnit;

/**
 * @Description: TestUpload
 * @Author: yjs
 * @createTime: 2022年05月11日 13:28:43
 * @version: 1.0
 */
public class TestUpload {
    private static final int sliceLength= 1048576;
    private static OkHttpClient client;
    private static final String url = "http://localhost:9988/file/upload";
    private static MediaType mediaType = MediaType.parse("application/json; charset=utf-8");

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        client = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build();


        String filePath = "E:\\my\\my.zip";
        File file = new File(filePath);
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
        byte[] splitData = new byte[sliceLength];
        long totalSize = randomAccessFile.length();
        long splitTotal = totalSize / sliceLength;
        if (totalSize % sliceLength != 0) {
            splitTotal++;
        }
        SplitFileInfoRequest body = new SplitFileInfoRequest();
        body.setFileName(file.getName());
        byte[] wholeData = new byte[(int) file.length()];
        new FileInputStream(file).read(wholeData);
        body.setFileMd5(VelificationUtil.getMD5Hex(wholeData));
        body.setTotalSize((int) file.length());
        body.setSplitTotal((int) splitTotal);

        int index = 1;
        int readSize;
        while ((readSize = randomAccessFile.read(splitData)) != -1) {
            body.setSplitSize(readSize);
            body.setSplitIndex(index);
            body.setSplitData(splitData);

            if (readSize != sliceLength) {
                byte[] lastSplitData = new byte[readSize];
                System.arraycopy(splitData, 0, lastSplitData, 0, readSize);
                body.setSplitData(lastSplitData);
            }
            Request request = new Request.Builder()
                    .url(url)
                    .post(RequestBody.create(mediaType, JSON.toJSONString(body)))
                    .build();
            Response execute = client.newCall(request).execute();
            System.out.println(execute.body().string());
            index++;
        }
        System.out.println("finished");
        long end = System.currentTimeMillis();
        System.out.println("cost:" + (end - start));
    }
}
