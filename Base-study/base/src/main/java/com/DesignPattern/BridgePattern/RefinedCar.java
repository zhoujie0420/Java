package com.DesignPattern.BridgePattern;

/**
 * @author zhouj
 * @create 2023/7/2 18:12
 */

public abstract class RefinedCar extends Car {
    public RefinedCar(Engine engine) {
        super(engine);
    }

    public void drive() {
        this.engine.start();
        System.out.println("Drive " + getBrand() + " car...");
    }

    public abstract String getBrand();
}
