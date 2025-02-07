package net.n.example.high_concurrent_java_server.draft.reflection;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;

public class ReflectionBenchmark {
    private String message = "Benchmark";

    public String getMessage(int x) {
        return x + "";
    }

    public static void main(String[] args) throws Throwable {
        ReflectionBenchmark obj = new ReflectionBenchmark();

        // Direct Call
        long start = System.nanoTime();
        for (int i = 0; i < 1000000; i++) {
            String result = obj.getMessage(1);
        }
        long end = System.nanoTime();
        System.out.println("Direct call: " + (end - start) + " ns");

        // Reflection
        Method method = ReflectionBenchmark.class.getMethod("getMessage", int.class);
        start = System.nanoTime();
        for (int i = 0; i < 1000000; i++) {
            method.invoke(obj, 2);
        }
        end = System.nanoTime();
        System.out.println("Reflection call: " + (end - start) + " ns");

        // MethodHandles
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        MethodHandle handle = lookup.findVirtual(ReflectionBenchmark.class, "getMessage",
                MethodType.methodType(String.class, int.class));
        start = System.nanoTime();
        for (int i = 0; i < 1000000; i++) {
            // fast
            handle.invoke(obj, 3);
        }
        end = System.nanoTime();
        System.out.println("MethodHandle call: " + (end - start) + " ns");

        start = System.nanoTime();
        for (int i = 0; i < 1000000; i++) {
            String result = (String) handle.invokeExact(obj, 4);
        }
        end = System.nanoTime();
        System.out.println("MethodHandle invokeExact call: " + (end - start) + " ns");
    }
}
