package org.sr3u.photoframe.misc.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
public class ThrottlingExecutor implements Executor {
    private static final Logger log = LogManager.getLogger(ThrottlingExecutor.class);
    private final Executor delegate;
    private final Semaphore semaphore;

    public ThrottlingExecutor(Executor delegate, int queueDepth) {
        this.delegate = delegate;
        this.semaphore = new Semaphore(queueDepth);
    }

    public void submit(final Runnable command) throws InterruptedException {
        semaphore.acquire();
        try {
            delegate.execute(() -> {
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

    @Override
    @SuppressWarnings("NullableProblems")
    public void execute(Runnable command) {
        if (command == null) {
            return;
        }
        try {
            submit(command);
        } catch (InterruptedException e) {
            e.printStackTrace();
            log.error(e);
        }
    }
}