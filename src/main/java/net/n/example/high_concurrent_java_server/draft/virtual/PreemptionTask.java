package net.n.example.high_concurrent_java_server.draft.virtual;

/*
 * situation : Using virtual thread on # of OS threads = 1, the CPU-bound task blocks all other
 * threads
 * 
 * conclusion : Preemption by a running task.
 */
public class PreemptionTask {
    public static void main(String[] args) throws InterruptedException {
        // Set a single OS thread limit programmatically or by running this code in a constrained
        // environment
        System.out.println("Starting Virtual Thread Execution");

        // Set the properties programmatically
        // Globally set on Thread.ofVirtual()
        System.setProperty("jdk.virtualThreadScheduler.parallelism", "2");
        System.setProperty("jdk.virtualThreadScheduler.maxPoolSize", "2");
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
            System.out.println("11111-11111");
            for (int i = 0; i < 3000000000L; i++) {
                if (i % 10000000000L == 0) {
                    System.out.println(i);
                }
            }
            System.out.println("11111");
        });

        Thread.sleep(2000);

        Thread.ofVirtual().start(() -> {
            System.out.println("22222-22222");
        });

        System.out.println("All tasks are submitted");

        t1.join();
    }
}
