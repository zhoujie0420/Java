package com.zhou.demo01;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class TestThread4 implements Runnable{
    private String url;
    private String name;


    public TestThread4(String url,String name){
        this.url = url;
        this.name = name;
    }

    public void run(){
        WebDownloader webDownloader = new WebDownloader();
        webDownloader.downloader(url,name);
        System.out.println("下载了文件"+name);
    }

    public static void main(String[] args) {
        TestThread2 t1 = new TestThread2("https://cdn.luogu.com.cn/upload/usericon/397.png","1.png");
        TestThread2 t2 = new TestThread2("https://ipic.luogu.com.cn/yugu22s/wuyi/banner2.png","2.png");

       new Thread(t1).start();
       new Thread(t2).start();
    }

}


