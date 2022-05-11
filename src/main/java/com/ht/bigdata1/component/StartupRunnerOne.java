package com.ht.bigdata1.component;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @Description: StartupRunnerOne
 * @Author: yjs
 * @createTime: 2022年05月11日 16:00:04
 * @version: 1.0
 */
@Component
@Order(value=1)
public class StartupRunnerOne implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {
        System.out.println(">>>>>>>>>>>>>>>服务启动第一个开始执行的任务，执行加载数据等操作<<<<<<<<<<<<<");
    }
}