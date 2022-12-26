package com.ZZAC.lessonUDP;

import java.net.*;
import java.nio.channels.DatagramChannel;
import java.nio.charset.StandardCharsets;

public class UdpclientDemo01 {
    public static void main(String[] args) throws Exception {
        DatagramSocket socket = new DatagramSocket();

        String msg = "hello zhoujie ";
        InetAddress localhost = InetAddress.getByName("localhost");
        int port = 9090;

        DatagramPacket packet = new DatagramPacket(msg.getBytes(),0,msg.getBytes().length,localhost,port);

        socket.send(packet);

        socket.close();

    }
}
