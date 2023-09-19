package org.demo.store;

import java.util.Map;

/**
 * @author jiezhou
 */
public interface ICommodity {
    void sendCommodity(String uId, String commodityId, String bizId, Map<String,String> extMap) throws Exception;
}
