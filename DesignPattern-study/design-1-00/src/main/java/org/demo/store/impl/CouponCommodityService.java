package org.demo.store.impl;

import org.demo.store.ICommodity;

import java.util.Map;
import java.util.logging.Logger;

/**
 * @author jiezhou
 */
public class CouponCommodityService implements ICommodity {
    private Logger logger = Logger.getLogger(CouponCommodityService.class.getName());
    private CouponService couponService = new CouponService();
    @Override
    public void sendCommodity(String uId, String commodityId, String bizId, Map<String,String> extMap) throws Exception {
    

    }
}
