package xyz.hardliner.beat.concurrency;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ConcurrentOrderedExecutor<K> implements AutoCloseable {

    private static final int THREADS_NUMBER = Runtime.getRuntime().availableProcessors();
    private static final int QUEUE_SIZE = 1000 * THREADS_NUMBER;

    private final Map<K, QueueWithSemaphore> queues;
    private final ThreadPoolExecutor executor;

    public ConcurrentOrderedExecutor() {
        queues = new HashMap<>();
        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(THREADS_NUMBER);
    }

    public void planTask(K key, Runnable task) {
        if (!queues.containsKey(key)) {
            queues.put(key, new QueueWithSemaphore());
        }

        while (executor.getQueue().size() > QUEUE_SIZE) {
            Thread.onSpinWait();
        }

        var queue = queues.get(key);
        queue.enqueueTask(task);
        executor.execute(processQueue(queue));
    }

    private Runnable processQueue(QueueWithSemaphore queue) {
        return () -> {
            if (!queue.semaphore.tryAcquire()) {
                return;
            }
            while (queue.peekTask() != null) {
                queue.pollTask().run();
            }
            queue.semaphore.release();
        };
    }

    @Override
    public void close() {
        try {
            executor.shutdown();
            executor.awaitTermination(1L, TimeUnit.MINUTES);
            for (QueueWithSemaphore queue : queues.values()) {
                while (queue.peekTask() != null) {
                    var task = queue.pollTask();
                    task.run();
                }
            }
        } catch (InterruptedException ex) {
            throw new IllegalStateException("Cannot terminate executor service", ex);
        }
    }
}
