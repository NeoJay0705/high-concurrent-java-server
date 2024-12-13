package net.n.example.high_concurrent_java_server.draft.virtual;

import java.util.concurrent.locks.ReentrantLock;

/*
 * Using ReentrantLock to avoid pinned thread
 */
public class LockTask {
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

        // Pinned thread
        Object lock = new Object();

        var t1 = Thread.ofVirtual().start(() -> {
            System.out.println("synchronized-1-start");

            synchronized (lock) {
                try {
                    System.out.println("synchronized-1-doing");
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            System.out.println("synchronized-1-end");
        });

        Thread.sleep(1000);

        Thread.ofVirtual().start(() -> {
            System.out.println("synchronized-2-start");

            synchronized (lock) {
                System.out.println("synchronized-2-doing");
            }

            System.out.println("synchronized-2-end");
        });

        t1.join();

        // Avoid pinned by synchronized
        ReentrantLock lock1 = new ReentrantLock();

        t1 = Thread.ofVirtual().start(() -> {
            System.out.println("ReentrantLock-1-start");


            lock1.lock();
            try {
                System.out.println("ReentrantLock-1-doing");
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            lock1.unlock();


            System.out.println("ReentrantLock-1-end");
        });

        Thread.sleep(1000);

        Thread.ofVirtual().start(() -> {
            System.out.println("ReentrantLock-2-start");

            lock1.lock();
            System.out.println("ReentrantLock-2-doing");
            lock1.unlock();

            System.out.println("ReentrantLock-2-end");
        });

        t1.join();
    }
}
