package com.ZZAC.lessonTCP;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class TcpServerDemo02 {

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(9000);

        Socket socket = serverSocket.accept();

        InputStream is = socket.getInputStream();

        FileOutputStream fos = new FileOutputStream(new File("receive.jpg"));

        byte[] buffer = new byte[1024];
        int len;
        while((len = is.read(buffer))!= -1){
            fos.write(buffer,0,len);
        }


        OutputStream os = socket.getOutputStream();
        os.write("接受完毕".getBytes(StandardCharsets.UTF_8));



        fos.close();
        is.close();
        socket.close();
        serverSocket.close();
    }
}
