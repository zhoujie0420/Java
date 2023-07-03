package com.tianji.promotion.service.impl;

import cn.hutool.core.bean.copier.CopyOptions;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tianji.common.autoconfigure.mq.RabbitMqHelper;
import com.tianji.common.autoconfigure.redisson.annotations.Lock;
import com.tianji.common.constants.MqConstants;
import com.tianji.common.constants.PromotionConstants;
import com.tianji.common.domain.dto.PageDTO;
import com.tianji.common.exceptions.BadRequestException;
import com.tianji.common.exceptions.BizIllegalException;
import com.tianji.common.exceptions.DbException;
import com.tianji.common.utils.BeanUtils;
import com.tianji.common.utils.CollUtils;
import com.tianji.common.utils.UserContext;
import com.tianji.promotion.domain.dto.UserCouponDTO;
import com.tianji.promotion.domain.po.Coupon;
import com.tianji.promotion.domain.po.ExchangeCode;
import com.tianji.promotion.domain.po.UserCoupon;
import com.tianji.promotion.domain.query.UserCouponQuery;
import com.tianji.promotion.domain.vo.UserCouponVO;
import com.tianji.promotion.enums.CouponStatus;
import com.tianji.promotion.enums.UserCouponStatus;
import com.tianji.promotion.mapper.CouponMapper;
import com.tianji.promotion.mapper.UserCouponMapper;
import com.tianji.promotion.service.IExchangeCodeService;
import com.tianji.promotion.service.IUserCouponService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tianji.promotion.strategy.discount.DiscountStrategy;
import org.redisson.api.RedissonClient;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.tianji.promotion.utils.CodeUtil;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserCouponServiceImpl extends ServiceImpl<UserCouponMapper, UserCoupon> implements IUserCouponService {

    @Resource
    private CouponMapper couponMapper;

    @Resource
    private IExchangeCodeService codeService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private RabbitMqHelper mqHelper;

    private static final RedisScript<Long> RECEIVE_COUPON_SCRIPT;
    private static final RedisScript<String> EXCHANGE_COUPON_SCRIPT;

    static {
        RECEIVE_COUPON_SCRIPT = RedisScript.of(new ClassPathResource("lua/receive_coupon.lua"), Long.class);
        EXCHANGE_COUPON_SCRIPT = RedisScript.of(new ClassPathResource("lua/exchange_coupon.lua"), String.class);
    }

    @Override
    public void receiveCoupon(Long couponId) {

        // 1-查询优惠券信息
        Coupon coupon = queryCouponByCache(couponId);
        if (Objects.isNull(coupon)) {
            throw new BadRequestException("优惠券信息不存在");
        }

        // 2-判断是否正在发放
        LocalDateTime now = LocalDateTime.now();
//        if (now.isBefore(coupon.getIssueBeginTime()) || now.isAfter(coupon.getIssueEndTime())
//                || coupon.getStatus() != CouponStatus.ISSUING.getValue()) {
//            throw new BizIllegalException("优惠券暂不处于可领取窗口");
//        }

        // 3-判断库存是否充足(缓存数据没有issnum，因此直接用总量判断，每次发放完总数-1)
        if (coupon.getTotalNum() <= 0) {
            throw new BadRequestException("优惠券库存不足");
        }

        Long userId = UserContext.getUser();
        // 4.校验每人限领数量
        // 4.1.查询领取数量(获取的同时立马加1，实现用户券+1的效果)
        String key = PromotionConstants.USER_COUPON_CACHE_KEY_PREFIX + couponId;
        Long count = stringRedisTemplate.opsForHash().increment(key, userId.toString(), 1);
        // 4.2.校验限领数量
        if(count > coupon.getUserLimit()){
            throw new BadRequestException("超出领取数量");
        }
        // 5.扣减优惠券库存(判断完立马-1，实现总量-1的效果)
        stringRedisTemplate.opsForHash().increment(
                PromotionConstants.COUPON_CACHE_KEY_PREFIX + couponId, "totalNum", -1);

        // 6.发送MQ消息
        UserCouponDTO uc = new UserCouponDTO();
        uc.setUserId(userId);
        uc.setCouponId(couponId);
        mqHelper.send(MqConstants.Exchange.PROMOTION_EXCHANGE, MqConstants.Key.COUPON_RECEIVE_KEY, uc);
    }

    private Coupon queryCouponByCache(Long couponId) {
        // 1.准备KEY
        String key = PromotionConstants.COUPON_CACHE_KEY_PREFIX + couponId;
        // 2.查询
        Map<Object, Object> objMap = stringRedisTemplate.opsForHash().entries(key);
        if (objMap.isEmpty()) {
            return null;
        }
        // 3.数据反序列化
        return BeanUtils.mapToBean(objMap, Coupon.class, false, CopyOptions.create().ignoreError());
    }

    @Override
    @Lock(name = "lock:coupon:#{T(com.tianji.common.utils.UserContext).getUser()}")
    @Transactional
    public void checkAndSave(Long couponId, Coupon coupon) {
        Integer count = lambdaQuery()
                .eq(UserCoupon::getCouponId, couponId)
                .eq(UserCoupon::getUserId, UserContext.getUser())
                .count();
        if (coupon.getUserLimit() <= count) {
            throw new BizIllegalException("你已经超领了，请勿重复领取");
        }

        // 5-更新优惠券的已领数量
        int row = couponMapper.incrementIssueNum(couponId);
        if (row == 0) {
            throw new BizIllegalException("优惠券库存不足");
        }

        // 6-保存用户优惠券信息
        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setUserId(UserContext.getUser());
        userCoupon.setCouponId(couponId);

        // 分别按开始-结束时间、按领取天数
        LocalDateTime begin = coupon.getTermBeginTime();
        LocalDateTime end = coupon.getTermEndTime();
        if (Objects.isNull(coupon.getTermBeginTime())) {
            begin = LocalDateTime.now();
            end = LocalDateTime.now().plusDays(coupon.getTermDays());
        }
        userCoupon.setTermBeginTime(begin);
        userCoupon.setTermEndTime(end);

        save(userCoupon);
    }

    @Override
    public PageDTO<UserCouponVO> queryUserCouponPage(UserCouponQuery query) {

        // 1.查询数据
        Page<UserCoupon> page = lambdaQuery()
                .eq(query.getStatus() != null, UserCoupon::getStatus, query.getStatus())
                .page(query.toMpPage("term_end_time", true));

        // 2.非空处理
        List<UserCoupon> records = page.getRecords();
        if (CollUtils.isEmpty(records)) {
            return PageDTO.empty(page);
        }

        // 3.查询优惠券规则
        Set<Long> couponIds = records.stream().map(UserCoupon::getCouponId).collect(Collectors.toSet());
        List<Coupon> coupons = couponMapper.selectByIds(couponIds);
        if (coupons == null || coupons.size() != couponIds.size()) {
            // 优惠券信息找不到
            throw new BizIllegalException("优惠券数据异常");
        }

        Map<Long, Coupon> couponMap = coupons.stream().collect(Collectors.toMap(Coupon::getId, c -> c));
        // 4.转VO
        return PageDTO.of(page, userCoupon -> {
            Coupon coupon = couponMap.get(userCoupon.getCouponId());
            UserCouponVO v = new UserCouponVO();
            v.setName(coupon.getName());
            v.setDiscountType(coupon.getDiscountType().getValue());
            v.setSpecify(coupon.getSpecific());
            v.setDiscountValue(coupon.getDiscountValue());
            v.setId(userCoupon.getId());
            v.setTermEndTime(userCoupon.getTermEndTime());
            return v;
        });
    }

    @Override
    public void exchangeCoupon(String code) {

        // 1.校验并解析兑换码
        long serialNum = CodeUtil.parseCode(code);
        // 2.校验是否已经兑换 SETBIT KEY 4 1
        boolean exchanged = codeService.updateExchangeMark(serialNum, true);
        if (exchanged) {
            throw new BizIllegalException("兑换码已经被兑换过了");
        }
        try {
            // 3.查询兑换码对应的优惠券id
            Long couponId = codeService.exchangeTargetId(serialNum);
            if (couponId == null) {
                throw new BizIllegalException("兑换码不存在！");
            }
            Coupon coupon = queryCouponByCache(couponId);
            // 4.是否过期
//            LocalDateTime now = LocalDateTime.now();
//            if (now.isAfter(coupon.getIssueEndTime()) || now.isBefore(coupon.getIssueBeginTime())) {
//                throw new BizIllegalException("优惠券活动未开始或已经结束");
//            }

            // 5.校验每人限领数量
            Long userId = UserContext.getUser();
            // 5.1.查询领取数量
            String key = PromotionConstants.USER_COUPON_CACHE_KEY_PREFIX + couponId;
            Long count = stringRedisTemplate.opsForHash().increment(key, userId.toString(), 1);
            // 5.2.校验限领数量
            if(count > coupon.getUserLimit()){
                throw new BadRequestException("超出领取数量");
            }

            // 6.发送MQ消息通知
            UserCouponDTO uc = new UserCouponDTO();
            uc.setUserId(userId);
            uc.setCouponId(couponId);
            uc.setSerialNum((int) serialNum);
            mqHelper.send(MqConstants.Exchange.PROMOTION_EXCHANGE, MqConstants.Key.COUPON_RECEIVE_KEY, uc);
        } catch (Exception e) {
            // 重置兑换的标记 0
            codeService.updateExchangeMark(serialNum, false);
            throw e;
        }
    }

    @Override
    public void checkAndCreateUserCoupon(UserCouponDTO uc) {
        // 1.查询优惠券
        Coupon coupon = couponMapper.selectById(uc.getCouponId());
        if (coupon == null) {
            throw new BizIllegalException("优惠券不存在！");
        }
        // 2.更新优惠券的已经发放的数量 + 1
        int r = couponMapper.incrementIssueNum(coupon.getId());
        if (r == 0) {
            throw new BizIllegalException("优惠券库存不足！");
        }
        // 3.新增一个用户券
        saveUserCoupon(coupon, uc.getUserId());
    }

    @Override
    public void writeOffCoupon(List<Long> userCouponIds) {
        // 1.查询优惠券
        List<UserCoupon> userCoupons = listByIds(userCouponIds);
        if (CollUtils.isEmpty(userCoupons)) {
            return;
        }
        // 2.处理数据
        List<UserCoupon> list = userCoupons.stream()
                // 过滤无效券
                .filter(coupon -> {
                    if (coupon == null) {
                        return false;
                    }
                    if (UserCouponStatus.UNUSED != coupon.getStatus()) {
                        return false;
                    }
                    LocalDateTime now = LocalDateTime.now();
                    return !now.isBefore(coupon.getTermBeginTime()) && !now.isAfter(coupon.getTermEndTime());
                })
                // 组织新增数据
                .map(coupon -> {
                    UserCoupon c = new UserCoupon();
                    c.setId(coupon.getId());
                    c.setStatus(UserCouponStatus.USED);
                    return c;
                })
                .collect(Collectors.toList());

        // 4.核销，修改优惠券状态
        boolean success = updateBatchById(list);
        if (!success) {
            return;
        }
        // 5.更新已使用数量
        List<Long> couponIds = userCoupons.stream().map(UserCoupon::getCouponId).collect(Collectors.toList());
        for (Long couponId : couponIds) {
            int c = couponMapper.incrementIssueNum(couponId);
            if (c < 1) {
                throw new DbException("更新优惠券使用数量失败！");
            }
        }
    }

    @Override
    public void refundCoupon(List<Long> userCouponIds) {
        // 1.查询优惠券
        List<UserCoupon> userCoupons = listByIds(userCouponIds);
        if (CollUtils.isEmpty(userCoupons)) {
            return;
        }
        // 2.处理优惠券数据
        List<UserCoupon> list = userCoupons.stream()
                // 过滤无效券
                .filter(coupon -> coupon != null && UserCouponStatus.USED == coupon.getStatus())
                // 更新状态字段
                .map(coupon -> {
                    UserCoupon c = new UserCoupon();
                    c.setId(coupon.getId());
                    // 3.判断有效期，是否已经过期，如果过期，则状态为 已过期，否则状态为 未使用
                    LocalDateTime now = LocalDateTime.now();
                    UserCouponStatus status = now.isAfter(coupon.getTermEndTime()) ?
                            UserCouponStatus.EXPIRED : UserCouponStatus.UNUSED;
                    c.setStatus(status);
                    return c;
                }).collect(Collectors.toList());

        // 4.修改优惠券状态
        boolean success = updateBatchById(list);
        if (!success) {
            return;
        }
        // 5.更新已使用数量
        List<Long> couponIds = userCoupons.stream().map(UserCoupon::getCouponId).collect(Collectors.toList());
        int c = couponMapper.incrUsedNum(couponIds);
        if (c < 1) {
            throw new DbException("更新优惠券使用数量失败！");
        }
    }

    @Override
    public List<String> queryDiscountRules(List<Long> userCouponIds) {
        // 1.查询优惠券信息
        List<Coupon> coupons = baseMapper.queryCouponByUserCouponIds(userCouponIds, UserCouponStatus.USED);
        if (CollUtils.isEmpty(coupons)) {
            return CollUtils.emptyList();
        }
        // 2.转换规则
        return coupons.stream()
                .map(c -> DiscountStrategy.getDiscount(c.getDiscountType()).getRule(c))
                .collect(Collectors.toList());
    }

    private void saveUserCoupon(Coupon coupon, Long userId) {
        // 1.基本信息
        UserCoupon uc = new UserCoupon();
        uc.setUserId(userId);
        uc.setCouponId(coupon.getId());
        // 2.有效期信息
        LocalDateTime termBeginTime = coupon.getTermBeginTime();
        LocalDateTime termEndTime = coupon.getTermEndTime();
        if (termBeginTime == null) {
            termBeginTime = LocalDateTime.now();
            termEndTime = termBeginTime.plusDays(coupon.getTermDays());
        }
        uc.setTermBeginTime(termBeginTime);
        uc.setTermEndTime(termEndTime);
        // 3.保存
        save(uc);
    }
}
