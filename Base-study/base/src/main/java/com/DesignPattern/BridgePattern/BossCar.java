package com.DesignPattern.BridgePattern;

/**
 * @author zhouj
 * @create 2023/7/2 18:13
 */

public class BossCar extends RefinedCar {
    public BossCar(Engine engine) {
        super(engine);
    }

    @Override
    public void installEngine() {

    }

    public String getBrand() {
        return "Boss";
    }
}