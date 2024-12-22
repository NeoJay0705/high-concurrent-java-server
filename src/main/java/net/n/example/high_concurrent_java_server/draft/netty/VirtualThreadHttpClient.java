package net.n.example.high_concurrent_java_server.draft.netty;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Deprecated
public class VirtualThreadHttpClient {
    public static void main(String[] args) {
        // Set the properties programmatically
        System.setProperty("jdk.virtualThreadScheduler.parallelism", "1");
        System.setProperty("jdk.virtualThreadScheduler.maxPoolSize", "1");
        System.setProperty("jdk.virtualThreadScheduler.minRunnable", "1");

        ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
        for (int i = 0; i < 2000; i++) {
            executorService.submit(() -> {
                // Create the HttpClient
                HttpClient httpClient =
                        HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build(); // Set
                                                                                                // connect
                                                                                                // timeout;

                // Define the request
                HttpRequest request =
                        HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/test/lag1")) // Replace
                                // with
                                // your
                                // desired
                                // URL
                                .timeout(Duration.ofSeconds(5)) // Set request/response timeout
                                .GET() // HTTP GET method
                                .build();

                try {
                    // Send the request and get the response
                    HttpResponse<String> response =
                            httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                    // Print the response status code
                    // System.out.println("Status Code: " + response.statusCode());

                    // Print the response body
                    if (!response.body().endsWith("lag1"))
                        System.out.println("Response Body: " + response.body());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        executorService.close();
    }

}
