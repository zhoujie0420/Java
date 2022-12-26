package com.ZZAC.chat;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UdpreceiverDemo01 {
    public static void main(String[] args) throws IOException {
        DatagramSocket socket = new DatagramSocket(6666);
        while(true){
            byte[] container = new byte[1024];
            DatagramPacket packet = new DatagramPacket(container, 0, container.length);
            socket.receive(packet);

            byte[] data = packet.getData();
            String receiverData = new String(data, 0, packet.getLength());

            System.out.println(receiverData);
            if(receiverData.equals("bye")){
                break;
            }
        }

        socket.close();
    }
}
