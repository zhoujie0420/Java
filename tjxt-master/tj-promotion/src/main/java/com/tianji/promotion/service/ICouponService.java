package com.tianji.promotion.service;

import com.tianji.common.domain.dto.PageDTO;
import com.tianji.promotion.domain.dto.CouponFormDTO;
import com.tianji.promotion.domain.dto.CouponIssueFormDTO;
import com.tianji.promotion.domain.po.Coupon;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tianji.promotion.domain.query.CouponQuery;
import com.tianji.promotion.domain.vo.CouponDetailVO;
import com.tianji.promotion.domain.vo.CouponPageVO;
import com.tianji.promotion.domain.vo.CouponVO;

import java.util.List;

/**
 * <p>
 * 优惠券的规则信息 服务类
 * </p>
 *
 * @author hb
 * @since 2023-04-25
 */
public interface ICouponService extends IService<Coupon> {

    void saveCoupon(CouponFormDTO coupon);

    CouponDetailVO queryCouponById(Long id);

    void updateCoupon(CouponFormDTO couponDTO);

    void deleteById(Long id);

    PageDTO<CouponPageVO> queryCouponPage(CouponQuery query);

    void beginIssue(CouponIssueFormDTO issue);

    void pauseIssue(Long id);

    void issueCouponByPage(int page, int size);

    void stopCouponByPage(int page, int size);

    List<CouponVO> queryIssuingCoupons();
}
