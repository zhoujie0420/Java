package com.hou.utils;

import org.junit.Test;

import java.util.UUID;

@SuppressWarnings("all")
public class IDUtiles { //获取随机的id

    public static String getId(){
        return UUID.randomUUID().toString().replaceAll("-","");
    }

    @Test
    public void  test(){
        System.out.println(getId());
    }

}
