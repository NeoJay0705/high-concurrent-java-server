package net.n.example.high_concurrent_java_server.configuration;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.support.TaskExecutorAdapter;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncTaskConfiguration implements AsyncConfigurer {

    @Bean(name = "platformThreadPool")
    public Executor platformThreadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(20);
        // executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("platform-async#");
        executor.initialize();
        return executor;
    }

    @Bean(name = "virtualThreadPool")
    public Executor virtualThreadPool() {
        return new TaskExecutorAdapter(Executors
                .newThreadPerTaskExecutor(Thread.ofVirtual().name("virtual-async#", 1).factory()));
    }

    @Override
    public Executor getAsyncExecutor() {
        return virtualThreadPool();
    }
}
