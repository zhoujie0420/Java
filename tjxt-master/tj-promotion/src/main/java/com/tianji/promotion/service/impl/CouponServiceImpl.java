package com.tianji.promotion.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tianji.common.constants.PromotionConstants;
import com.tianji.common.domain.dto.PageDTO;
import com.tianji.common.exceptions.BadRequestException;
import com.tianji.common.exceptions.BizIllegalException;
import com.tianji.common.utils.*;
import com.tianji.promotion.domain.dto.CouponFormDTO;
import com.tianji.promotion.domain.dto.CouponIssueFormDTO;
import com.tianji.promotion.domain.po.Coupon;
import com.tianji.promotion.domain.po.CouponScope;
import com.tianji.promotion.domain.po.UserCoupon;
import com.tianji.promotion.domain.query.CouponQuery;
import com.tianji.promotion.domain.vo.CouponDetailVO;
import com.tianji.promotion.domain.vo.CouponPageVO;
import com.tianji.promotion.domain.vo.CouponVO;
import com.tianji.promotion.enums.CouponStatus;
import com.tianji.promotion.enums.ObtainType;
import com.tianji.promotion.enums.UserCouponStatus;
import com.tianji.promotion.mapper.CouponMapper;
import com.tianji.promotion.service.ICouponScopeService;
import com.tianji.promotion.service.ICouponService;
import com.tianji.promotion.service.IExchangeCodeService;
import com.tianji.promotion.service.IUserCouponService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.tianji.promotion.enums.CouponStatus.*;

@Slf4j
@Service
public class CouponServiceImpl extends ServiceImpl<CouponMapper, Coupon> implements ICouponService {

    @Resource
    private ICouponScopeService couponScopeService;

    @Resource
    private IExchangeCodeService codeService;

    @Resource
    private IUserCouponService userCouponService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    @Transactional
    public void saveCoupon(CouponFormDTO coupon) {

        // 1-保存优惠券
        Coupon entity = BeanUtils.copyBean(coupon, Coupon.class);
        entity.setObtainWay(coupon.getObtainWay().getValue());
        save(entity);

        // 2-判断是否限定分类
        if (!coupon.getSpecific()) {
            // 没有范围限定，无需保存中间表数据，字节返回
            return;
        }

        // 3-保存中间表信息
        List<Long> scopeIds = coupon.getScopes();
        if (CollUtils.isEmpty(scopeIds)) {
            throw new BadRequestException("优惠券使用范围不能为空");
        }

        // 新增
        Long couponId = coupon.getId();
        List<CouponScope> list = scopeIds.stream().map(bizId -> {
            CouponScope scope = new CouponScope();
            scope.setCouponId(couponId);
            scope.setBizId(bizId);
            return scope;
        }).collect(Collectors.toList());
        couponScopeService.saveBatch(list);
    }

    @Override
    public CouponDetailVO queryCouponById(Long id) {
        // 1.查询优惠券
        Coupon coupon = getById(id);
        if (coupon == null) {
            throw new BadRequestException("优惠券信息不存在");
        }

        // 2.数据转换
        return BeanUtils.toBean(coupon, CouponDetailVO.class);
    }

    @Override
    public void updateCoupon(CouponFormDTO couponDTO) {

        // 1.查询旧数据
        Coupon old = getById(couponDTO.getId());
        if (old == null) {
            throw new BadRequestException("优惠券信息不存在");
        }

        // 2.更新优惠券
        Coupon coupon = BeanUtils.copyBean(couponDTO, Coupon.class);
        coupon.setObtainWay(couponDTO.getObtainWay().getValue());
        // 2.1.避免修改状态
        coupon.setStatus(null);
        // 2.2.更新
        updateById(coupon);

        // 3.判断是否要设定新的优惠券范围
        if (couponDTO.getSpecific() == null || (!old.getSpecific() && !couponDTO.getSpecific())) {
            // 3.1.本次修改没有指定范围，直接结束
            return;
        }
        // 3.2.本次修改指定了范围，判断范围数据是否存在
        if (Boolean.TRUE.equals(couponDTO.getSpecific()) && CollUtils.isEmpty(couponDTO.getScopes())) {
            // 没有指定新范围，直接结束
            return;
        }

        // 4.删除旧限定范围
        Long couponId = coupon.getId();
        couponScopeService.removeByCouponId(couponId);

        // 5.添加新范围
        List<CouponScope> list = couponDTO.getScopes().stream().map(bizId -> {
            CouponScope scope = new CouponScope();
            scope.setCouponId(couponId);
            scope.setBizId(bizId);
            return scope;
        }).collect(Collectors.toList());
        couponScopeService.saveBatch(list);
    }

    @Override
    public void deleteById(Long id) {
        boolean success = remove(new LambdaQueryWrapper<Coupon>()
                .eq(Coupon::getId, id)
                .eq(Coupon::getStatus, 1)
        );
        if (!success) {
            throw new BadRequestException("优惠券不存在或者优惠券正在使用中");
        }
    }

    @Override
    public PageDTO<CouponPageVO> queryCouponPage(CouponQuery query) {

        // 1.分页搜索
        Page<Coupon> page = lambdaQuery()
                .eq(query.getStatus() != null, Coupon::getStatus, query.getStatus())
                .eq(query.getType() != null, Coupon::getDiscountType, query.getType())
                .like(StringUtils.isNotBlank(query.getName()), Coupon::getName, query.getName())
                .page(query.toMpPageDefaultSortByCreateTimeDesc());

        // 2.数据处理
        List<Coupon> records = page.getRecords();
        if (CollUtils.isEmpty(records)) {
            return PageDTO.empty(page);
        }

        // 3.转换vo
        List<CouponPageVO> list = BeanUtils.copyList(records, CouponPageVO.class);
        return PageDTO.of(page, list);
    }

    @Override
    public void beginIssue(CouponIssueFormDTO issue) {

        // 0-券领取和使用有效期校验
        if(issue.getTermBeginTime() != null){
            // 在设置绝对使用有效期时，使用有效期必须在领取有效期之间
            if(issue.getTermBeginTime().isBefore(issue.getIssueBeginTime())
                    || issue.getTermEndTime().isAfter(issue.getIssueEndTime())){
                // 使用有效期超出了领取有效期范围，无效数据
                throw new BadRequestException("使用有效期不能超出领取有效期范围");
            }
        }

        // 1-查询优惠券信息
        Coupon localCoupon = getById(issue.getId());
        if (Objects.isNull(localCoupon)) {
            throw new BadRequestException("优惠券信息不存在");
        }

        // 2-优惠券状态校验
        if (localCoupon.getStatus() != CouponStatus.DRAFT.getValue()
                && localCoupon.getStatus() != CouponStatus.PAUSE.getValue()) {
            throw new BizIllegalException("优惠券状态不合法");
        }

        Coupon coupon = BeanUtils.copyBean(issue, Coupon.class);

        // 3-判断是否立即发放
        LocalDateTime issueBeginTime = issue.getIssueBeginTime();
        LocalDateTime now = LocalDateTime.now();
        // 如果开始时间为空，或开始时间在此之前就意味着是立即发放
        if (Objects.isNull(issueBeginTime) || issueBeginTime.isBefore(now)) {
            coupon.setStatus(ISSUING.getValue());
            // 立即发放时间写当前即可
            coupon.setIssueBeginTime(now);
        } else {
            coupon.setStatus(UN_ISSUE.getValue());
        }

        // 4-数据库更新
        updateById(coupon);

        // 5.添加缓存，前提是立刻发放的
        boolean isBegin = issueBeginTime == null || !issueBeginTime.isAfter(now);
        if (isBegin) {
            localCoupon.setIssueBeginTime(coupon.getIssueBeginTime());
            localCoupon.setIssueEndTime(coupon.getIssueEndTime());
        }
        cacheCouponInfo(localCoupon);

        // 6-判断是否需要生成兑换码：类型是兑换码+状态是待发放
        if (ObtainType.ISSUE.getValue() == localCoupon.getObtainWay()
                && CouponStatus.DRAFT.getValue() == localCoupon.getStatus()) {
            coupon.setIssueEndTime(localCoupon.getIssueEndTime());
            codeService.asyncGenerateCode(localCoupon);
        }
    }

    private void cacheCouponInfo(Coupon coupon) {
        // 1.组织数据
        Map<String, String> map = new HashMap<>(4);
        map.put("issueBeginTime", String.valueOf(coupon.getIssueBeginTime()));
        map.put("issueEndTime", String.valueOf(coupon.getIssueEndTime()));
        map.put("totalNum", String.valueOf(coupon.getTotalNum()));
        map.put("userLimit", String.valueOf(coupon.getUserLimit()));
        // 2.写缓存
        stringRedisTemplate.opsForHash().putAll(PromotionConstants.COUPON_CACHE_KEY_PREFIX + coupon.getId(), map);
    }

    @Override
    public void pauseIssue(Long id) {

        // 1-查询优惠券数据
        Coupon coupon = getById(id);
        if (Objects.isNull(coupon)) {
            throw new BadRequestException("优惠券信息不存在");
        }

        // 2-状态校验：当前券状态必须是未开始或进行中
        if (coupon.getStatus() != ISSUING.getValue()
                && coupon.getStatus() != UN_ISSUE.getValue()) {
            return;
        }

        // 3-数据库更新
        boolean success = lambdaUpdate()
                .set(Coupon::getStatus, CouponStatus.PAUSE.getValue())
                .eq(Coupon::getId, id)
                .in(Coupon::getStatus, ISSUING.getValue(), UN_ISSUE.getValue())
                .update();
        if (!success) {
            // 可能是重复更新，结束
            log.error("重复暂停优惠券");
        }

        // 4.删除缓存
        stringRedisTemplate.delete(PromotionConstants.COUPON_CACHE_KEY_PREFIX + id);
    }

    @Override
    public void issueCouponByPage(int page, int size) {

        // 1-找到未开始的数据
        Page<Coupon> p = lambdaQuery()
                .eq(Coupon::getStatus, UN_ISSUE.getValue())
                .page(new Page<>(page, size));
        // 2.判断是否有需要处理的数据
        List<Coupon> records = p.getRecords();
        if (CollUtils.isEmpty(records)) {
            // 没有数据，结束本次任务
            return;
        }

        // 3.找到需要处理的数据(已经过了发送日期的）
        LocalDateTime now = LocalDateTime.now();
        List<Coupon> list = records.stream()
                .filter(c -> now.isAfter(c.getIssueBeginTime()))
                .collect(Collectors.toList());
        if(list.isEmpty()){
            // 没有到期的券
            return;
        }

        log.debug("找到需要处理的优惠券{}条", list.size());

        // 4.修改券状态
        List<Long> ids = list.stream().map(Coupon::getId).collect(Collectors.toList());
        lambdaUpdate()
                .set(Coupon::getStatus, ISSUING.getValue())
                .in(Coupon::getId, ids)
                .update();
    }

    @Override
    public void stopCouponByPage(int page, int size) {
        // 1-找到发放中的数据
        Page<Coupon> p = lambdaQuery()
                .eq(Coupon::getStatus, ISSUING.getValue())
                .page(new Page<>(page, size));
        // 2.判断是否有需要处理的数据
        List<Coupon> records = p.getRecords();
        if (CollUtils.isEmpty(records)) {
            // 没有数据，结束本次任务
            return;
        }

        // 3.找到需要处理的数据(已经过了结束日期的）
        LocalDateTime now = LocalDateTime.now();
        List<Coupon> list = records.stream()
                .filter(c -> now.isAfter(c.getIssueEndTime()))
                .collect(Collectors.toList());
        if(list.isEmpty()){
            // 没有到期的券
            return;
        }

        log.debug("找到需要处理的优惠券{}条", list.size());

        // 4.修改券状态
        List<Long> ids = list.stream().map(Coupon::getId).collect(Collectors.toList());
        lambdaUpdate()
                .set(Coupon::getStatus, FINISHED.getValue())
                .in(Coupon::getId, ids)
                .update();
    }

    @Override
    public List<CouponVO> queryIssuingCoupons() {

        // 1-基础信息查询
        List<Coupon> coupons = lambdaQuery()
                .eq(Coupon::getStatus, ISSUING.getValue())
                .eq(Coupon::getObtainWay, ObtainType.PUBLIC)
                .list();
        if (CollUtils.isEmpty(coupons)) {
            return CollUtils.emptyList();
        }

        /*
          2-查询用户优惠券使用情况(这里需要统计两个维度的数据)
          维度1：是否可领取--针对某一个优惠券id，在用户表已领取数量
                 ①跟用户限领数量比较来确定，看看我是否还能领
                 ②跟优惠券总量比来确定，看看库存是否够
          维度2：是否可使用--查询某一个优惠券id，在用户表已领取数量且状态是待使用
         */
        List<Long> couponIds = coupons.stream().map(Coupon::getId)
                .distinct()
                .collect(Collectors.toList());
        List<UserCoupon> list = userCouponService.lambdaQuery()
                .in(UserCoupon::getCouponId, couponIds)
                .eq(UserCoupon::getUserId, UserContext.getUser())
                .list();

        // 3-统计当前用户对优惠券的已领取数量(是否可领取)
        Map<Long, Long> issueMap = list.stream()
                .collect(Collectors.groupingBy(UserCoupon::getCouponId, Collectors.counting()));

        // 4-统计当前用户已领取且未使用的数量(是否可使用)
        Map<Long, Long> usedMap = list.stream()
                .filter(c -> c.getStatus() == UserCouponStatus.UNUSED)
                .collect(Collectors.groupingBy(UserCoupon::getCouponId, Collectors.counting()));

        // 5-数据组装
        List<CouponVO> result = new ArrayList<>(coupons.size());
        for (Coupon coupon : coupons) {
            // 基础属性拷贝
            CouponVO vo = BeanUtils.copyBean(coupon, CouponVO.class);
            // 填充是否可领取(库存还有、且未达用户领取上限)
            vo.setAvailable(coupon.getTotalNum() > coupon.getIssueNum()
                            && coupon.getUserLimit() > issueMap.getOrDefault(coupon.getId(), 0L)
            );
            // 填充是否可使用(有未使用的)
            vo.setReceived(usedMap.getOrDefault(coupon.getId(), 0L) > 0);
            result.add(vo);
        }

        return result;
    }
}
