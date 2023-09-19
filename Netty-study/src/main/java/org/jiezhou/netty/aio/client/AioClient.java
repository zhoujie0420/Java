package org.jiezhou.netty.aio.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousByteChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.Future;

/**
 * @author: jiezhou
 **/

public class AioClient {
    public static void main(String[] args) throws IOException {
        AsynchronousSocketChannel socketChannel = AsynchronousSocketChannel.open();
        Future<Void> connect = socketChannel.connect(new InetSocketAddress(7397));

    }
}
