package com.ZZAC.lessonIP;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class TestInetAddress {
    public static void main(String[] args) {
        try {
            InetAddress inetAddress1 = InetAddress.getLocalHost();
            System.out.println(inetAddress1);

            InetAddress inetAddress2 = InetAddress.getByName("www.baidu.com");
            System.out.println(inetAddress2);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
