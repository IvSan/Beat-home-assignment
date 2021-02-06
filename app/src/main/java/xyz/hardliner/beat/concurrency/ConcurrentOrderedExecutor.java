package xyz.hardliner.beat.concurrency;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ConcurrentOrderedExecutor<K> implements AutoCloseable {

    private final Map<K, QueueWithSemaphore> queues;
    private final int THREADS_NUMBER = Runtime.getRuntime().availableProcessors();
    private final int QUEUE_SIZE = 1000 * THREADS_NUMBER;
    private final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(THREADS_NUMBER);

    public ConcurrentOrderedExecutor() {
        queues = new HashMap<>();
    }

    @Override
    public void close() {
        try {
            executor.shutdown();
            executor.awaitTermination(1L, TimeUnit.MINUTES);
        } catch (InterruptedException ex) {
            throw new IllegalStateException("Cannot terminate executor service", ex);
        }
    }

    public void planTask(K key, Runnable task) {
        if (!queues.containsKey(key)) {
            queues.put(key, new QueueWithSemaphore());
        }

        var queue = queues.get(key);
        queue.enqueueTask(task);

        while (executor.getQueue().size() > QUEUE_SIZE) {
            Thread.onSpinWait();
        }
        executor.execute(processQueue(queue));
    }

    private Runnable processQueue(QueueWithSemaphore queue) {
        return () -> {
            if (!queue.semaphore.tryAcquire()) {
                return;
            }
            while (!queue.isEmpty()) {
                var task = queue.pollTask();
                if (task != null) task.run();
            }
            queue.semaphore.release();
        };
    }
}
