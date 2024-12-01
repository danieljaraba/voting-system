package concurrency;

import java.util.concurrent.BlockingQueue;

public class PoolWorker extends Thread {
    private final BlockingQueue<Runnable> taskQueue;

    public PoolWorker(BlockingQueue<Runnable> taskQueue) {
        this.taskQueue = taskQueue;
    }

    public void run() {
        Runnable task;

        while (true) {
            synchronized (taskQueue) {
                while (taskQueue.isEmpty()) {
                    try {
                        taskQueue.wait();
                    } catch (InterruptedException e) {
                        System.out.println("An error occurred while waiting for a task.");
                    }
                }
                task = taskQueue.poll();
            }

            try {
                task.run();
            } catch (RuntimeException e) {
                System.out.println("Thread pool encountered an error while executing a task.");
                e.printStackTrace();
            }
        }
    }
}
