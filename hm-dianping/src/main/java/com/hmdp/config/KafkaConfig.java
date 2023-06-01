package com.hmdp.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName : KafkaConfig  //类名
 * @Description :   //描述
 * @Author : dell //作者
 * @Date: 2023/6/1  10:15
 */

@Configuration
public class KafkaConfig {

    /**
     * 创建一个名为topic.test的Topic并设置分区数为8，分区副本数为2
     */
    @Bean
    public NewTopic initialTopic() {
        return new NewTopic("topic.test", 8, (short) 2);
    }
}
