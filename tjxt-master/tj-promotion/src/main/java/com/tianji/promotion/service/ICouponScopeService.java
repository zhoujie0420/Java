package com.tianji.promotion.service;

import com.tianji.promotion.domain.po.CouponScope;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 优惠券作用范围信息 服务类
 * </p>
 *
 * @author hb
 * @since 2023-04-25
 */
public interface ICouponScopeService extends IService<CouponScope> {

    void removeByCouponId(Long couponId);
}
