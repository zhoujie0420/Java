package com.java;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * @author zhouj
 * @create 2023/6/28 16:44
 */

public class Generics {
    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        HashSet<String> strings = new HashSet<>();
        String[] strings1 = new String[10];
        strings1[1] = "1asd";
        int i = 0;
        for (String string : strings) {
            strings1[i] = string;
            i++;
        }

    }
}
