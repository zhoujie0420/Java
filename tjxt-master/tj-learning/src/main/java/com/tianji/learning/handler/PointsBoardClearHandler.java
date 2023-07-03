package com.tianji.learning.handler;

import com.tianji.common.utils.DateUtils;
import com.tianji.learning.constants.RedisConstants;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@Component
public class PointsBoardClearHandler {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @XxlJob("clearPointsBoardFromRedis")
    public void clearPointsBoardFromRedis(){
        // 1.获取上月时间
        LocalDateTime time = LocalDateTime.now().minusMonths(1);
        // 2.计算key
        String key = RedisConstants.POINTS_BOARD_KEY_PREFIX + time.format(DateUtils.POINTS_BOARD_SUFFIX_FORMATTER);
        // 3.删除
        stringRedisTemplate.unlink(key);
    }
}
