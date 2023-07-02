package com.DesignPattern.ChainOfResponsibilityDesignPattern;

/**
 * @author zhouj
 * @create 2023/7/2 10:44
 */

public class ManagerHandler implements Handler{
    @Override
    public Boolean process(Request request) {
        if(request.getAmount().compareTo(new java.math.BigDecimal(1000)) > 0){
        //超过1000元处理不了，交给下一个
            System.out.println("ManagerHandler 处理了请求");
            return null;
        }
        //对bob不可以处理，拒绝
        return !request.getName().equalsIgnoreCase("bob");
    }
}
