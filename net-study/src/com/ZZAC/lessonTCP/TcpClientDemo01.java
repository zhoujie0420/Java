package com.ZZAC.lessonTCP;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class TcpClientDemo01 {
    public static void main(String[] args) throws IOException {
        InetAddress server = InetAddress.getByName("127.0.0.1");
        int port = 9999;
        Socket socket = new Socket(server, port);

        OutputStream os = socket.getOutputStream();

        os.write("zhouyibuxuexi".getBytes());




    }
}
