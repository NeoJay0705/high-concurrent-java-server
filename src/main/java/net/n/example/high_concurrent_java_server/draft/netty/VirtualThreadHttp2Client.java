package net.n.example.high_concurrent_java_server.draft.netty;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * not support too many concurrent streams
 */
@Deprecated
public class VirtualThreadHttp2Client {

    public static void main(String[] args) {
        // Set the properties programmatically
        System.setProperty("jdk.virtualThreadScheduler.parallelism", "1");
        System.setProperty("jdk.virtualThreadScheduler.maxPoolSize", "1");
        System.setProperty("jdk.virtualThreadScheduler.minRunnable", "1");

        Executor virtualThreadExecutor = task -> Thread.ofVirtual().start(task);

        // both server and client have maximum streams on a connection: java.io.IOException: too
        // many concurrent streams
        // Create an HttpClient with HTTP/2 enabled
        HttpClient httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2) // Use
                // HTTP/2
                .executor(virtualThreadExecutor).connectTimeout(Duration.ofSeconds(10)) // Connection
                                                                                        // timeout
                .build();

        ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
        // *** not support too many concurrent streams ***
        for (int i = 0; i < 1000; i++) {
            if (i % 100 == 0) {
                // thread sleep
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            executorService.submit(() -> {
                // Define the request
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/test/lag_long")) // Replace
                        // with
                        // your
                        // desired
                        // URL
                        // .timeout(Duration.ofSeconds(5)) // Set request/response timeout
                        .GET() // HTTP GET method
                        .build();

                try {
                    // Send the request and get the response
                    HttpResponse<String> response =
                            httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                    // Print the response status code
                    // System.out.println("Status Code: " + response.statusCode());

                    // Print the response body
                    if (!response.body().endsWith("lag_long"))
                        System.out.println(Thread.currentThread().getName() + " Response Body: "
                                + response.body());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        executorService.close();
    }
}
