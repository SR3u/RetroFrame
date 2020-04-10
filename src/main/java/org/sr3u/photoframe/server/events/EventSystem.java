package org.sr3u.photoframe.server.events;

import org.apache.log4j.Logger;
import org.sr3u.photoframe.misc.util.ThrottlingExecutor;
import org.sr3u.photoframe.server.Main;
import sr3u.streamz.functionals.Consumerex;
import sr3u.streamz.streams.Streamex;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;

public class EventSystem {
    private static final Logger log = Logger.getLogger(EventSystem.class);

    Set<Consumerex<Event>> eventHandlers = new HashSet<>();
    ThrottlingExecutor executor = new ThrottlingExecutor(Executors.newFixedThreadPool(Main.settings.getProcessingTreads()),
            Main.settings.getProcessingTreads() * 2);

    public void registerHandler(Consumerex<Event> eventHandler) {
        try {
            executor.submit(() -> eventHandlers.add(eventHandler));
        } catch (InterruptedException e) {
            log.error(e);
        }
    }

    public void fireEvent(Event event) {
        try {
            executor.submit(() -> Streamex.ofStream(eventHandlers.stream())
                    .forEach(e -> {
                        try {
                            e.accept(event);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }));
        } catch (InterruptedException e) {
            log.error(e);
        }
    }

}
