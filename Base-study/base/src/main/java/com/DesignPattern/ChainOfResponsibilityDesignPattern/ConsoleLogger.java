package com.DesignPattern.ChainOfResponsibilityDesignPattern;

/**
 * @author: elk
 * @create: 2024-01-18 14:21
 **/

public class ConsoleLogger extends AbstractLogger {
    public ConsoleLogger(int level) {
        this.level = level;
    }

    @Override
    protected void write(String message) {
        System.out.println("ConsoleLogger::Logger: " + message);
    }
}
