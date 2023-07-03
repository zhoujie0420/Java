package com.tianji.learning.handler;

import com.tianji.learning.service.IPointsBoardSeasonService;
import com.tianji.learning.service.IPointsBoardService;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Objects;

@Component
public class PointsBoardPersistentHandler {

    @Resource
    private IPointsBoardSeasonService seasonService;

    @Resource
    private IPointsBoardService boardService;

    /**
     * 定时创建表任务，每月1号凌晨3点执行
     */
    @XxlJob("createTableJob")
    public void createPointsBoardTableOfLastSeason() {

        // 1-获取上个月的时间
        LocalDateTime lastMonthTime = LocalDateTime.now().minusMonths(1L);

        // 2-查询赛季id
        Integer seasonId = seasonService.querySeasonByTime(lastMonthTime);
        if (Objects.isNull(seasonId)) {
            // 赛季不存在
            return;
        }
        // 3-创建表
        boardService.createPointsBoardTableBySeason(seasonId);
    }
}
