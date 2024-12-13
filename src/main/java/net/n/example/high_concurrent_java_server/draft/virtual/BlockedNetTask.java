package net.n.example.high_concurrent_java_server.draft.virtual;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * situation : Using platform thread on # of OS threads = 1, run two tasks concurrently, one
 * CPU-bound task and one IO-bound task.
 * 
 * conclusion : The first run is IO-bound and blocked the second CPU-bound task.
 */
public class BlockedNetTask {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        // Set a single OS thread limit programmatically or by running this code in a constrained
        // environment
        System.out.println("Starting Virtual Thread Execution");

        // Create a thread pool with 4 threads
        ExecutorService threadPool = Executors.newFixedThreadPool(1);

        threadPool.submit(() -> {
            String serverAddress = "localhost"; // Replace with your server's address
            int serverPort = 8080; // Port number (80 for HTTP)

            try (Socket socket = new Socket(serverAddress, serverPort)) {
                System.out.println(" [IO Task] Connected to the server: " + serverAddress);

                // Get input and output streams for communication
                OutputStream output = socket.getOutputStream();
                PrintWriter writer = new PrintWriter(output, true);

                InputStream input = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));

                // Send a simple HTTP GET request
                String request = "GET / HTTP/1.1\r\n" + "Host: " + serverAddress + "\r\n"
                        + "Connection: close\r\n" + "bye\r\n";

                writer.println(request);

                // Read and print the server's response
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(" [IO Task] " + line);
                }

            } catch (IOException e) {
                System.err.println("Error connecting to the server: " + e.getMessage());
            }
        });

        Thread.sleep(1000);

        threadPool.submit(() -> {
            System.out.println("[CPU Task] 22222-22222");
        });

        threadPool.shutdown();
    }

    public static class PlatformServer {
        public static void main(String[] args) {
            // Define the server port
            int port = 8080; // You can change the port number as needed

            // Try-with-resources to ensure resources are closed automatically
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                System.out.println("Server is listening on port " + port);

                while (true) {
                    // Accept an incoming client connection
                    Socket socket = serverSocket.accept();
                    System.out.println("New client connected");

                    // Handle client communication in a separate thread
                    new ClientHandler(socket).start();
                }
            } catch (IOException ex) {
                System.err.println("Server exception: " + ex.getMessage());
                ex.printStackTrace();
            }
        }

        // Inner class to handle client communication
        private static class ClientHandler extends Thread {
            private Socket socket;

            public ClientHandler(Socket socket) {
                this.socket = socket;
            }

            @Override
            public void run() {
                try (InputStream input = socket.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                        OutputStream output = socket.getOutputStream();
                        PrintWriter writer = new PrintWriter(output, true)) {
                    String text;

                    // Read messages from the client until "bye" is received
                    while ((text = reader.readLine()) != null) {
                        System.out.println("Received: " + text);

                        try {
                            Thread.sleep(20);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException ex) {
                    System.err.println("Server error: " + ex.getMessage());
                    ex.printStackTrace();
                } finally {
                    try {
                        socket.close();
                    } catch (IOException ex) {
                        System.err.println("Failed to close socket: " + ex.getMessage());
                    }
                }
            }
        }
    }
}
