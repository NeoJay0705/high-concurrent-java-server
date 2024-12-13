package net.n.example.high_concurrent_java_server.draft.controller;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;


@RestController
@RequestMapping("/test")
@Slf4j
@RequiredArgsConstructor
public class TestController {

    private final Aynscer asyncer;

    @SneakyThrows
    @GetMapping("/hello")
    public String hello(@RequestParam String name) {
        log.info("Async method received request, executing thread is: {}", Thread.currentThread());
        Future<String> asyncDefault = asyncer.asyncDefault();
        Future<String> asyncPlt = asyncer.asyncPlatform();
        Future<String> asyncVir = asyncer.asyncVirtual();
        log.info(asyncDefault.get() + " " + asyncPlt.get() + " " + asyncVir.get());
        return "Hello " + name;
    }

    @GetMapping("/pause")
    public String pause() throws InterruptedException {
        Thread.sleep(20);
        return "OK";
    }


    @Component
    public static class Aynscer {
        @Async
        public CompletableFuture<String> asyncDefault() {
            log.info("asyncDefault method received request, executing thread is: {}",
                    Thread.currentThread());
            return CompletableFuture.completedFuture("hello world !!!!");
        }

        @Async("platformThreadPool")
        public CompletableFuture<String> asyncPlatform() {
            log.info("asyncPlatform method received request, executing thread is: {}",
                    Thread.currentThread());
            return CompletableFuture.completedFuture("hello world !!!!");
        }

        @Async("virtualThreadPool")
        public CompletableFuture<String> asyncVirtual() {
            log.info("asyncVirtual method received request, executing thread is: {}",
                    Thread.currentThread());
            return CompletableFuture.completedFuture("hello world !!!!");
        }
    }
}
