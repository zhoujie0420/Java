package com.DesignPattern.BuilderPattern;

import javax.lang.model.element.VariableElement;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhouj
 * @create 2023/6/30 23:42
 */

public class Meal {
    private List<Item> items = new ArrayList<Item>();

    public void addItem(Item item) {
        items.add(item);
    }

    public float getCost() {
        float cost=0.0f;
        for(Item item:items) {
            cost+=item.price();
        }
        return cost;
    }

    public float showItems() {
        for(Item item:items) {
            System.out.print("Item:"+item.name());
            System.out.print(",Packing:"+item.packing().pack());
            System.out.println(",Price:"+item.price());
        }
        return getCost();
    }
}
