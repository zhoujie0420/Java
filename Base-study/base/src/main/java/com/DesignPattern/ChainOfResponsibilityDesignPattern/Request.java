package com.DesignPattern.ChainOfResponsibilityDesignPattern;

import java.math.BigDecimal;

/**
 * @author zhouj
 * @create 2023/7/2 10:42
 */

public class Request {
    private String name;
    private BigDecimal amount;

    public Request(String name, BigDecimal amount) {
        this.name = name;
        this.amount = amount;
    }

    public String getName() {
        return name;
    }
    public BigDecimal getAmount() {
        return amount;
    }
}
