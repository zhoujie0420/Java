package com.hmdp.utils;

import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class RedisIdWorker {
    //开始的时间戳
    private static final long BEGIN_TIMESTAMP = 1640995200L;

    //序列号的位数
    private static final int COUNT_BITS = 32;


    @Resource
    private StringRedisTemplate stringRedisTemplate;


    //生成全局唯一ID
    public long nextId (String keyPrefix){
        //生成时间戳
        LocalDateTime now = LocalDateTime.now();
        long nowSecond = now.toEpochSecond(ZoneOffset.UTC);
        long timestamp = nowSecond - BEGIN_TIMESTAMP;

        //生成序列号
        //获取当前日期
        //使用时间的目的   可以根据时间查找，不会越位
        String date = now.format(DateTimeFormatter.ofPattern("yyyy:MM:dd"));
        //自增长
        Long count = stringRedisTemplate.opsForValue().increment("icr:" + keyPrefix + ":" + date);

        //位运算 拼接 返回
        return timestamp << COUNT_BITS | count;
    }
}
