package com.DesignPattern.BridgePattern;

/**
 * @author zhouj
 * @create 2023/7/2 18:11
 */

public abstract class Car {
    protected Engine engine;

    public Car(Engine engine) {
        this.engine = engine;
    }

    public abstract void installEngine();
}
