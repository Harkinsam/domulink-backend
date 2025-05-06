package com.domulink.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
public class ThreadConfig {

    @Bean
    public ThreadPoolTaskExecutor uploadExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // Core pool size (minimum number of threads)
        executor.setCorePoolSize(20);  // Number of threads to handle requests concurrently

        // Max pool size (maximum number of threads)
        executor.setMaxPoolSize(30);  // Maximum number of threads that can be created

        // Queue capacity (max number of tasks in the queue before blocking)
        executor.setQueueCapacity(100);  // Number of requests waiting for an available thread

        // Thread name prefix (useful for logging)
        executor.setThreadNamePrefix("UploadThread-");

        // Ensure threads are gracefully shut down when the app shuts down
        executor.setWaitForTasksToCompleteOnShutdown(true);

        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        return executor;
    }
}

