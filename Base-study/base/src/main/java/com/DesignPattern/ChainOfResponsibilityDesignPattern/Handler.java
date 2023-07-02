package com.DesignPattern.ChainOfResponsibilityDesignPattern;

/**
 * @author zhouj
 * @create 2023/7/2 10:43
 */

public interface Handler {
    /**
     * 处理请求
     * @param request
     * @return
     * true 成功
     * false 失败
     * null 交个下一个处理
     */
    Boolean process(Request request);
}
