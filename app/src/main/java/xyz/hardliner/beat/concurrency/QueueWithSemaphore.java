package xyz.hardliner.beat.concurrency;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class QueueWithSemaphore {

    public final Semaphore semaphore;
    public final Queue<Runnable> tasks;

    public QueueWithSemaphore() {
        this.semaphore = new Semaphore(1);
        this.tasks = new LinkedList<>();
    }

    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    public void enqueueTask(Runnable runnable) {
        tasks.add(runnable);
    }

    public Runnable pollTask() {
        return tasks.poll();
    }

    public int getSize() {
        return tasks.size();
    }
}
