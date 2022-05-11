package com.ht.bigdata1.component;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @Description: StartupRunnerTwo
 * @Author: yjs
 * @createTime: 2022年05月11日 16:00:33
 * @version: 1.0
 */
@Component
@Order(value=2)
public class StartupRunnerTwo implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {
        System.out.println(">>>>>>>>>>>>>>>服务第二顺序启动执行，执行加载数据等操作<<<<<<<<<<<<<");
    }
}
