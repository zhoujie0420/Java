package com.tianji.learning.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.DelayQueue;

/**
* @Description: 
* @Date: 2023/4/18 14:35 
*/
@Slf4j
class DelayTaskTest {

    @Test
    public void testDelayTask() throws InterruptedException {

        DelayQueue<DelayTask<String>> delayTasks = new DelayQueue<>();

        log.info("任务开始执行, 注意我是先添加任务3，最后看看谁先执行");
        delayTasks.add(new DelayTask<String>("延迟数据3", Duration.ofSeconds(3)));
        delayTasks.add(new DelayTask<String>("延迟数据1", Duration.ofSeconds(1)));
        delayTasks.add(new DelayTask<String>("延迟数据2", Duration.ofSeconds(2)));

        while (true) {
            DelayTask<String> take = delayTasks.take();
            log.info("开始执行延迟任务：" + take.getData());
        }
    }
}