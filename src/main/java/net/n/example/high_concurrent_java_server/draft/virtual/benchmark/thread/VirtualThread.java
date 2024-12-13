package net.n.example.high_concurrent_java_server.draft.virtual.benchmark.thread;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VirtualThread {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please specify 'io' or 'cpu' as the task type.");
            return;
        }

        String taskType = args[0].toLowerCase();

        long workLoop = 10000L;
        int ioMillis = 20;
        Runnable task;
        switch (taskType) {
            case "io":
                task = ioTask(20);
                System.out.println("io cost: " + ioMillis);
                break;

            case "cpu":
                task = cpuTask(workLoop);
                break;

            default:
                System.out.println("Invalid task type. Use 'io' or 'cpu'.");
                return;
        }

        int numberOfRequest = 1000000;

        ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor(); // Virtual
                                                                                       // threads
                                                                                       // executor

        System.out.println(
                String.format("numberOfRequest=%,d, workLoop=%,d", numberOfRequest, workLoop));

        for (int i = 0; i < numberOfRequest; i++) {
            executorService.submit(task);
        }

        executorService.close();

        // Get the MemoryMXBean
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();

        // Get heap memory usage
        MemoryUsage heapMemoryUsage = memoryMXBean.getHeapMemoryUsage();
        long used = heapMemoryUsage.getUsed(); // Memory currently used
        long max = heapMemoryUsage.getMax(); // Maximum heap size
        long committed = heapMemoryUsage.getCommitted(); // Allocated memory

        System.out.println("Heap Memory Usage:");
        System.out.println("Used: " + used / (1024 * 1024) + " MB");
        System.out.println("Committed: " + committed / (1024 * 1024) + " MB");
        System.out.println("Max: " + max / (1024 * 1024) + " MB");
    }

    public static final Runnable ioTask(int ioMillis) {
        return () -> {
            try {
                Thread.sleep(ioMillis);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
    }

    public static final Runnable cpuTask(long workLoop) {
        return () -> {
            long sum = 0;
            for (long j = 0; j < workLoop; j++) {
                sum += j; // Simulate computation
            }
        };
    }
}
