package com.tianji.promotion.service;

import com.tianji.common.domain.dto.PageDTO;
import com.tianji.promotion.domain.po.Coupon;
import com.tianji.promotion.domain.po.ExchangeCode;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tianji.promotion.domain.query.CodeQuery;

/**
 * <p>
 * 兑换码 服务类
 * </p>
 *
 * @author hb
 * @since 2023-04-25
 */
public interface IExchangeCodeService extends IService<ExchangeCode> {

    void asyncGenerateCode(Coupon coupon);

    PageDTO<String> queryCodePage(CodeQuery query);

    boolean updateExchangeMark(long serialNum, boolean mark);

    Long exchangeTargetId(long serialNum);
}
