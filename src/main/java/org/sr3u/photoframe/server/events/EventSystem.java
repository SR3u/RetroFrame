package org.sr3u.photoframe.server.events;

import org.sr3u.photoframe.server.Server;
import sr3u.streamz.functionals.Consumerex;
import sr3u.streamz.streams.Streamex;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EventSystem {
    Set<Consumerex<Event>> eventHandlers = new HashSet<>();
    ExecutorService executor = Executors.newFixedThreadPool(Server.settings.getProcessingTreads());

    public void registerHandler(Consumerex<Event> eventHandler) {
        executor.submit(() -> eventHandlers.add(eventHandler));
    }

    public void fireEvent(Event event) {
        executor.submit(() -> Streamex.ofStream(eventHandlers.stream())
                .forEach(e -> {
                    try {
                        e.accept(event);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }));
    }

}
