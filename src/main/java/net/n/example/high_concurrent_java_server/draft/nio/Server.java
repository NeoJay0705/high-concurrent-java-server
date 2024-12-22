package net.n.example.high_concurrent_java_server.draft.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class Server {

    public static void main(String[] args) throws IOException {
        // Create a selector
        Selector selector = Selector.open();

        // Create a server socket channel
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.socket().bind(new InetSocketAddress(8080));

        // Register the server socket channel with the selector to accept connections
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        System.out.println("Server is listening on port 8080...");

        while (true) {
            // Wait for events
            selector.select();

            // Get all keys with events
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectedKeys.iterator();

            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();

                if (key.isAcceptable()) {
                    // Accept new connections
                    SocketChannel clientChannel = serverSocketChannel.accept();
                    clientChannel.configureBlocking(false);
                    clientChannel.register(selector, SelectionKey.OP_READ);
                    System.out.println(
                            "Accepted connection from " + clientChannel.getRemoteAddress());

                } else if (key.isReadable()) {
                    // Handle readable data
                    SocketChannel clientChannel = (SocketChannel) key.channel();
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    int bytesRead = clientChannel.read(buffer);

                    if (bytesRead == -1) {
                        // Client closed connection
                        clientChannel.close();
                    } else {
                        // Echo the received data and send a basic HTTP response
                        buffer.flip();
                        String request = new String(buffer.array(), 0, buffer.limit());
                        System.out.println("Received request:\n" + request);

                        String httpResponse = "HTTP/1.1 200 OK\r\n" + "Content-Type: text/plain\r\n"
                                + "Content-Length: 12\r\n" + "\r\n" + "Hello World!";

                        clientChannel.write(ByteBuffer.wrap(httpResponse.getBytes()));
                        clientChannel.close();
                    }
                }

                // Remove the key from the set to prevent processing it again
                iterator.remove();
            }
        }
    }
}
