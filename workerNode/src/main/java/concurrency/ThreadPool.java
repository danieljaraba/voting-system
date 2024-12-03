package concurrency;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ThreadPool {

    private int numberOfThreads;
    private PoolWorker[] threads;
    private final BlockingQueue<Runnable> taskQueue;

    public ThreadPool(int numberOfThreads) {
        this.numberOfThreads = numberOfThreads;
        taskQueue = new LinkedBlockingQueue<>();
        threads = new PoolWorker[numberOfThreads];

        for (int i = 0; i < numberOfThreads; i++) {
            threads[i] = new PoolWorker(taskQueue);
            threads[i].start();
        }
    }

    public void execute(Runnable task) {
        synchronized (taskQueue) {
            taskQueue.add(task);
            taskQueue.notify();
        }
    }

    public void setNumberOfThreads(int newNumberOfThreads) {
        if (newNumberOfThreads <= 0) {
            throw new IllegalArgumentException("Number of threads must be greater than zero.");
        }

        synchronized (taskQueue) {
            if (newNumberOfThreads > numberOfThreads) {
                PoolWorker[] newThreads = new PoolWorker[newNumberOfThreads];
                System.arraycopy(threads, 0, newThreads, 0, numberOfThreads);

                for (int i = numberOfThreads; i < newNumberOfThreads; i++) {
                    newThreads[i] = new PoolWorker(taskQueue);
                    newThreads[i].start();
                }

                threads = newThreads;
            } else if (newNumberOfThreads < numberOfThreads) {
                for (int i = newNumberOfThreads; i < numberOfThreads; i++) {
                    threads[i].interrupt();
                }

                PoolWorker[] newThreads = new PoolWorker[newNumberOfThreads];
                System.arraycopy(threads, 0, newThreads, 0, newNumberOfThreads);
                threads = newThreads;
            }

            numberOfThreads = newNumberOfThreads;
        }
    }
}
