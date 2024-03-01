package com.DesignPattern.ChainOfResponsibilityDesignPattern;

/**
 * @author: elk
 * @create: 2024-01-18 14:22
 **/

public class FileLogger extends AbstractLogger {
    public FileLogger(int level) {
        this.level = level;
    }

    @Override
    protected void write(String message) {
        System.out.println("FileLogger::Logger: " + message);
    }
}
