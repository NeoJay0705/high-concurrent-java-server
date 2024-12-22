package net.n.example.high_concurrent_java_server.draft.web.controller;

import java.security.Principal;
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
import net.n.example.high_concurrent_java_server.draft.web.authentication.CustomPrincipal;
import net.n.example.high_concurrent_java_server.draft.web.authorization.RequiresAuthority;


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

    @GetMapping("auth")
    public String auth(Principal principal) {
        CustomPrincipal p = (CustomPrincipal) principal;
        return p.getName();
    }

    @GetMapping("authorize")
    @RequiresAuthority({"USER"})
    public String authorize(Principal principal) {
        CustomPrincipal p = (CustomPrincipal) principal;
        return p.getName();
    }

    @GetMapping("/lag1")
    public String lag1() throws InterruptedException {
        Thread.sleep(1000);

        System.out.println(System.currentTimeMillis());
        return "lag1";
    }

    @GetMapping("/lag2")
    public String lag2() throws InterruptedException {
        Thread.sleep(1000);
        return "lag2";
    }

    @GetMapping("/lag3")
    public String lag3() {
        return "lag1";
    }

    @GetMapping("/lag_long")
    public String lagLong() throws InterruptedException {
        Thread.sleep(1000 * 60 * 20);
        return "lag_long";
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
