package com.tianji.promotion.handler;

import com.tianji.common.utils.NumberUtils;
import com.tianji.promotion.service.ICouponService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class CouponJobHandler {

    @Resource
    private ICouponService couponService;

    @XxlJob("couponIssueJobHandler")
    public void handleCouponIssueJob() {
        // 1.获取分片信息，作为分页信息
        int page = XxlJobHelper.getShardIndex();
        String param = XxlJobHelper.getJobParam();
        int size = NumberUtils.parseInt(param);
        log.debug("准备开始处理优惠券发放任务，page={},size={}", page, size);
        // 2.分页处理待发放状态的优惠券
        couponService.issueCouponByPage(page, size);
    }


    @XxlJob("couponStopJobHandler")
    public void handleCouponStopJob() {
        // 1.获取分片信息，作为分页信息
        int page = XxlJobHelper.getShardIndex();
        String param = XxlJobHelper.getJobParam();
        int size = NumberUtils.parseInt(param);
        log.debug("准备开始处理优惠券停止任务，page={},size={}", page, size);
        // 2.分页处理待发放状态的优惠券
        couponService.stopCouponByPage(page, size);
    }
}