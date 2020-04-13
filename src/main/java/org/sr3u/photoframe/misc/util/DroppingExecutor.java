package org.sr3u.photoframe.misc.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.*;

public class DroppingExecutor implements Executor {
    private static final Logger log = LogManager.getLogger(DroppingExecutor.class);

    private final int queueDepth;
    private final ThreadPoolExecutor delegate;
    private final BlockingQueue<Runnable> queue;

    public DroppingExecutor(int threads, int queueDepth) {
        this.queueDepth = queueDepth;
        this.queue = new LinkedBlockingQueue<>();
        this.delegate = new ThreadPoolExecutor(threads, threads, 0L, TimeUnit.MILLISECONDS, queue,
                (r, executor) -> {
                    log.error("Delegate rejected execution, will try to clear queue");
                    queue.clear();
                });
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public void execute(Runnable command) {
        if (command == null) {
            return;
        }
        int currentlyExecuting = queue.size();
        if (currentlyExecuting > queueDepth) {
            log.warn("Dropped execution queue because exceeded max queue depth " + currentlyExecuting + " > " + queueDepth);
            queue.clear();
        }
        try {
            delegate.execute(command);
        } catch (RejectedExecutionException e) {
            queue.clear();
            delegate.execute(command);
        }
    }
}
