package net.n.example.high_concurrent_java_server.draft.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class VirtualServer {
    public static void main(String[] args) {
        // Define the server port
        int port = 8080; // You can change the port number as needed

        // Try-with-resources to ensure resources are closed automatically
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is listening on port " + port);

            // AtomicInteger counter = new AtomicInteger(0);
            while (true) {
                // Accept an incoming client connection
                Socket socket = serverSocket.accept();
                System.out.println("New client connected");

                // Handle client communication in a separate thread
                Thread.ofVirtual().start(() -> {
                    try (InputStream input = socket.getInputStream();
                            BufferedReader reader =
                                    new BufferedReader(new InputStreamReader(input));
                            OutputStream output = socket.getOutputStream();
                            PrintWriter writer = new PrintWriter(output, true)) {
                        String text;

                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        // Read messages from the client until "bye" is received
                        while ((text = reader.readLine()) != null) {
                            System.out.println("Received: " + text);
                            if ("bye".equalsIgnoreCase(text)) {
                                break;
                            }
                            writer.append("server send: " + text + "\n");
                            writer.flush();
                            System.out.println("server send: " + text);
                        }
                    } catch (IOException ex) {
                        System.err.println("Server error: " + ex.getMessage());
                        ex.printStackTrace();
                    } finally {
                        // try {
                        // // counter.incrementAndGet();
                        // socket.close();
                        // // System.out.println("Client disconnected: " + counter.get());
                        // } catch (IOException ex) {
                        // System.err.println("Failed to close socket: " + ex.getMessage());
                        // }
                    }
                });
            }
        } catch (IOException ex) {
            System.err.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
