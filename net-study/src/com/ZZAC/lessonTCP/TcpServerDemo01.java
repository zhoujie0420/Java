package com.ZZAC.lessonTCP;

import com.sun.security.ntlm.Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class TcpServerDemo01 {
    public static void main(String[] args) throws IOException {
        try{
            ServerSocket serverSocket = new ServerSocket(9999);

            Socket socket = serverSocket.accept();

            InputStream is = socket.getInputStream();


            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while((len = is.read(buffer))!=-1) {
                baos.write(buffer, 0, len);
            }

            System.out.println(baos.toString());
            baos.close();
            is.close();
            socket.close();
            serverSocket.close();

        }catch(IOException e){
            e.printStackTrace();
        }finally {

        }


    }

}
