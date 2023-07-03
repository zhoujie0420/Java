package com.tianji.remark.task;

import com.tianji.remark.service.ILikedRecordService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Description:
 * @Date: 2023/4/21 10:06
 */
@Component
public class LikedTimesCheckTask {

    // 业务类型，我们这里只有两个
    private static final List<String> BIZ_TYPES = List.of("QA", "NOTE");

    // 每次取数大小，避免单次太多
    private static final int MAX_BIZ_SIZE = 30;


    @Resource
    private ILikedRecordService recordService;

    @Scheduled(fixedDelay = 20000)
    public void checkLikedTimes() {
        for (String bizType : BIZ_TYPES) {
            recordService.readCacheAndSendMessage(bizType, MAX_BIZ_SIZE);
        }
    }
}
