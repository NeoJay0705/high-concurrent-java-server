package net.n.example.high_concurrent_java_server.draft.virtual;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
 * situation : Using virtual threads on # of OS threads = 1, run two tasks concurrently, one
 * CPU-bound task and one IO-bound task. Globally on Thread.ofVirtual() and
 * Executors.newVirtualThreadPerTaskExecutor()
 * 
 * conclusion : The first run is IO-bound. But the CPU-bound task finished first.
 */
public class NonBlockedNetTask {
    // mvn exec:java -Dexec.mainClass=net.n.example.high_concurrent_java_server.draft.virtual.NonBlockedNetTask -q
    public static void main(String[] args) throws InterruptedException {
        // Set a single OS thread limit programmatically or by running this code in a constrained
        // environment
        System.out.println("Starting Virtual Thread Execution");

        // Set the properties programmatically
        System.setProperty("jdk.virtualThreadScheduler.parallelism", "1");
        System.setProperty("jdk.virtualThreadScheduler.maxPoolSize", "1");
        System.setProperty("jdk.virtualThreadScheduler.minRunnable", "1");

        // Verify the values
        String parallelismValue = System.getProperty("jdk.virtualThreadScheduler.parallelism");
        String maxPoolSizeValue = System.getProperty("jdk.virtualThreadScheduler.maxPoolSize");
        String minRunnableValue = System.getProperty("jdk.virtualThreadScheduler.minRunnable");

        System.out.println("Virtual Thread Scheduler Configuration:");
        System.out.println("Parallelism: " + parallelismValue);
        System.out.println("Max Pool Size: " + maxPoolSizeValue);
        System.out.println("Min Runnable: " + minRunnableValue);

        var t1 = Thread.ofVirtual().start(() -> {
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
                String request = "GET / HTTP/1.1\r\n";

                writer.println(request);

                // Read and print the server's response
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(" [IO Task] " + line);
                    break;
                }

            } catch (IOException e) {
                System.err.println("Error connecting to the server: " + e.getMessage());
            }
        });

        Thread.sleep(1000);

        Thread.ofVirtual().start(() -> {
            System.out
                    .println("[CPU Task] thread 2 is running on thread: " + Thread.currentThread());
        });

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            // Submit multiple tasks to virtual threads
            for (int i = 0; i < 1; i++) {
                int taskNumber = i;
                executor.submit(() -> {
                    System.out.printf("Task %d is running on thread: %s%n", taskNumber,
                            Thread.currentThread());
                    try {
                        Thread.sleep(100); // Simulate work
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        System.err.println("Task was interrupted");
                    }
                    System.out.printf("Task %d completed%n", taskNumber);
                });
            }
        } catch (Exception e) {
            System.err.println("Error occurred: " + e.getMessage());
        }

        t1.join();
    }
}
