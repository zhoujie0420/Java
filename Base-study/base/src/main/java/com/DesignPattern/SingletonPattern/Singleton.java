package com.DesignPattern.SingletonPattern;/**
 * @author zhouj
 * @create 2023/6/19 16:36
 */

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @ClassName : Singleton  //类名
 * @Description :   //双重校验锁实现对象单例（线程安全）
 * @Author : dell //作者
 * @Date: 2023/6/19  16:36
 */


public class Singleton {
  private volatile static Singleton instance;
  private Singleton(){}

    public static  Singleton getInstance(){
      if(instance == null){
          synchronized (Singleton.class){
              Singleton singleton = new Singleton();
          }
      }
      return instance;
    }
}
