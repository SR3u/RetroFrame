package org.sr3u.photoframe.server.events;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sr3u.photoframe.misc.util.ThrottlingExecutor;
import org.sr3u.photoframe.server.Main;
import sr3u.streamz.functionals.Consumerex;
import sr3u.streamz.streams.Streamex;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;

public class EventSystem {
    private static final Logger log = LogManager.getLogger(EventSystem.class);

    Set<Consumerex<Event>> eventHandlers = new HashSet<>();
    ThrottlingExecutor executor = new ThrottlingExecutor(Executors.newFixedThreadPool(Main.settings.getProcessingTreads()),
            Main.settings.getProcessingTreads() * 2);

    public void registerHandler(Consumerex<Event> eventHandler) {
        executor.execute(() -> eventHandlers.add(eventHandler));
    }

    public void fireEvent(Event event) {
        executor.execute(() -> Streamex.ofStream(eventHandlers.stream())
                .forEach(e -> {
                    try {
                        e.accept(event);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }));
    }

}
