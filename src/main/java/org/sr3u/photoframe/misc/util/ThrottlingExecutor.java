package org.sr3u.photoframe.misc.util;

import javax.annotation.concurrent.ThreadSafe;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.Semaphore;

/**
 * BoundedExecutor
 * <p/>
 * Using a Semaphore to throttle task submission
 */
@ThreadSafe
public class ThrottlingExecutor {
    private final Executor exec;
    private final Semaphore semaphore;

    public ThrottlingExecutor(Executor exec, int queueDepth) {
        this.exec = exec;
        this.semaphore = new Semaphore(queueDepth);
    }

    public void submit(final Runnable command) throws InterruptedException {
        semaphore.acquire();
        try {
            exec.execute(() -> {
                try {
                    command.run();
                } finally {
                    semaphore.release();
                }
            });
        } catch (RejectedExecutionException e) {
            semaphore.release();
        }
    }
}