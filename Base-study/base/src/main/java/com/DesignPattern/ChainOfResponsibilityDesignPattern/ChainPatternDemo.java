package com.DesignPattern.ChainOfResponsibilityDesignPattern;



/**
 * @author: elk
 * @create: 2024-01-18 14:21
 **/

public class ChainPatternDemo {
    private static AbstractLogger getChainOfLoggers() {
        ErrorLogger errorLogger = new ErrorLogger(AbstractLogger.ERROR);
        ErrorLogger fileLogger = new ErrorLogger(AbstractLogger.DEBUG);
        ErrorLogger consoleLogger = new ErrorLogger(AbstractLogger.INFO);

        errorLogger.setNextLogger(fileLogger);
        fileLogger.setNextLogger(consoleLogger);
        return errorLogger;
    }

    public static void main(String[] args) {
        AbstractLogger chainOfLoggers = getChainOfLoggers();
        chainOfLoggers.logMessage(AbstractLogger.INFO, "This is an information.");
        chainOfLoggers.logMessage(AbstractLogger.DEBUG, "This is an debug level information.");
        chainOfLoggers.logMessage(AbstractLogger.ERROR, "This is an error information.");
    }
}
