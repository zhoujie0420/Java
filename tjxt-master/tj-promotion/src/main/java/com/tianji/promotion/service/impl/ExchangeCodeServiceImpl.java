package com.tianji.promotion.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tianji.common.domain.dto.PageDTO;
import com.tianji.common.utils.CollUtils;
import com.tianji.promotion.domain.po.Coupon;
import com.tianji.promotion.domain.po.ExchangeCode;
import com.tianji.promotion.domain.query.CodeQuery;
import com.tianji.promotion.mapper.ExchangeCodeMapper;
import com.tianji.promotion.service.IExchangeCodeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.tianji.promotion.utils.CodeUtil;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.tianji.common.constants.PromotionConstants.COUPON_RANGE_KEY;

@Service
public class ExchangeCodeServiceImpl extends ServiceImpl<ExchangeCodeMapper, ExchangeCode> implements IExchangeCodeService {

    private static final String COUPON_CODE_SERIAL_KEY = "coupon:code:serial";

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    @Async("generateExchangeCodeExecutor")
    public void asyncGenerateCode(Coupon coupon) {

        // 1-获取Redis自增序列号
        BoundValueOperations<String, String> codeOps = stringRedisTemplate.boundValueOps(COUPON_CODE_SERIAL_KEY);

        // 2-获取要发放的数量
        Integer totalNum = coupon.getTotalNum();
        Long num = null;
        // 循环生成
        List<ExchangeCode> list = new ArrayList<>(totalNum);
        for (int i = 0; i < totalNum; i++) {

            num = codeOps.increment(1);
            if (Objects.isNull(num)) {
                return;
            }

            // 3-生成兑换码
            String code = CodeUtil.generateCode(num, coupon.getId());

            ExchangeCode exchangeCode = new ExchangeCode();
            exchangeCode.setCode(code);
            exchangeCode.setId(num.intValue());
            exchangeCode.setExchangeTargetId(coupon.getId());
            exchangeCode.setExpiredTime(coupon.getIssueEndTime());
            list.add(exchangeCode);
        }

        saveBatch(list);

        // 4.写入Redis缓存，member：couponId，score：兑换码的最大序列号
        stringRedisTemplate.opsForZSet().add(COUPON_RANGE_KEY, coupon.getId().toString(), num);
    }

    @Override
    public PageDTO<String> queryCodePage(CodeQuery query) {
        // 1.分页查询兑换码
        Page<ExchangeCode> page = lambdaQuery()
                .eq(ExchangeCode::getStatus, query.getStatus())
                .eq(ExchangeCode::getExchangeTargetId, query.getCouponId())
                .page(query.toMpPage());
        // 2.返回数据
        return PageDTO.of(page, ExchangeCode::getCode);
    }

    @Override
    public boolean updateExchangeMark(long serialNum, boolean mark) {
        Boolean bit = stringRedisTemplate.opsForValue().setBit(COUPON_CODE_SERIAL_KEY, serialNum, mark);
        return null != bit && bit;
    }

    @Override
    public Long exchangeTargetId(long serialNum) {
        // 1.查询score值比当前序列号大的第一个优惠券
        Set<String> results = stringRedisTemplate.opsForZSet().rangeByScore(
                COUPON_RANGE_KEY, serialNum, serialNum + 5000, 0L, 1L);
        if (CollUtils.isEmpty(results)) {
            return null;
        }
        // 2.数据转换
        String next = results.iterator().next();
        return Long.parseLong(next);
    }
}
