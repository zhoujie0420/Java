package com.DesignPattern.ChainOfResponsibilityDesignPattern;

/**
 * @author: elk
 * @create: 2024-01-18 14:21
 **/

public class ErrorLogger extends AbstractLogger {

    public ErrorLogger(int level) {
        this.level = level;
    }

    @Override
    protected void write(String message) {
        System.out.println("ErrorLogger::Logger: " + message);
    }
}
