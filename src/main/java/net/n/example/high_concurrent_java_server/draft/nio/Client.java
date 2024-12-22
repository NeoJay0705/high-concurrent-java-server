package net.n.example.high_concurrent_java_server.draft.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class Client {
    public static void main(String[] args) throws IOException {
        // Define the website and port to connect to
        String host = "localhost";
        int port = 8080;

        // Create a selector
        Selector selector = Selector.open();

        // Create a SocketChannel
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);

        // Connect to the server
        socketChannel.connect(new InetSocketAddress(host, port));
        socketChannel.register(selector, SelectionKey.OP_CONNECT);

        // Create a buffer for data
        ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024);

        System.out.println("Connecting to " + host + " on port " + port + "...");

        while (true) {
            selector.select();

            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectedKeys.iterator();

            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();

                if (key.isConnectable()) {
                    // Finish the connection process
                    SocketChannel channel = (SocketChannel) key.channel();
                    if (channel.isConnectionPending()) {
                        channel.finishConnect();
                    }

                    System.out.println("Connected to " + host);

                    // Send an HTTP GET request
                    String httpRequest = "GET / HTTP/1.1\r\n" + "Host: " + host + "\r\n"
                            + "Connection: close\r\n\r\n";
                    channel.write(ByteBuffer.wrap(httpRequest.getBytes()));

                    // Register the channel for reading response
                    channel.register(selector, SelectionKey.OP_READ);
                } else if (key.isReadable()) {
                    // Read the response
                    SocketChannel channel = (SocketChannel) key.channel();
                    buffer.clear();
                    int bytesRead = channel.read(buffer);

                    if (bytesRead == -1) {
                        // Server closed connection
                        channel.close();
                        System.out.println("Connection closed by server.");
                    } else {
                        // Print the response
                        buffer.flip();
                        System.out.println("Response:");
                        System.out.println(new String(buffer.array(), 0, buffer.limit()));
                    }
                }

                // Remove the processed key
                iterator.remove();
            }
        }
    }
}
