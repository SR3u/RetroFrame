package org.sr3u.photoframe.server.events;

import org.sr3u.photoframe.server.Server;
import org.sr3u.photoframe.server.util.ThrottlingExecutor;
import sr3u.streamz.functionals.Consumerex;
import sr3u.streamz.streams.Streamex;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;

public class EventSystem {
    Set<Consumerex<Event>> eventHandlers = new HashSet<>();
    ThrottlingExecutor executor = new ThrottlingExecutor(Executors.newFixedThreadPool(Server.settings.getProcessingTreads()),
            Server.settings.getProcessingTreads() * 2);

    public void registerHandler(Consumerex<Event> eventHandler) {
        try {
            executor.submit(() -> eventHandlers.add(eventHandler));
        } catch (InterruptedException e) {
            e.printStackTrace();
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
            e.printStackTrace();
        }
    }

}
