package org.sr3u.photoframe.client.filters.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sr3u.photoframe.server.Main;

import java.awt.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class BruteForcePicker implements ColorPicker {

    private static final Logger log = LogManager.getLogger(ColorPicker.class);

    private Map<Integer, Color> selectionCache = new ConcurrentHashMap<>();
    private AtomicLong cleanupsCount = new AtomicLong(0);
    private AtomicLong resetsCount = new AtomicLong(0);

    @Override
    public Color closestColor(int rgb, Color[] palette) {
        return selectionCache.computeIfAbsent(rgb, k -> {
            Color c = new Color(rgb);
            Color closest = palette[0];
            for (Color n : palette) {
                if (distance(n, c) < distance(closest, c)) {
                    closest = n;
                }
            }
            return closest;
        });
    }

    @Override
    public void reset() {
        long resetsCount = this.resetsCount.incrementAndGet();
        if (selectionCache.size() > Main.settings.getClient().getColorCacheSize()) {
            log.info("clearing selectionCache, as it was larger than the threshold (" + selectionCache.size() + " > " + Main.settings.getClient().getColorCacheSize() + ")");
            selectionCache.clear();
            long cleanupsCount = this.cleanupsCount.incrementAndGet();
            log.info("Color cache cleanups: " + cleanupsCount + " / " + resetsCount + " (" + (cleanupsCount * 100) / resetsCount + "%)");
        }
    }

    protected double distance(Color c1, Color c2) {
        return squareDistance(c1, c2);
    }

    public static int squareDistance(Color c1, Color c2) {
        int Rdiff = c1.getRed() - c2.getRed();
        int Gdiff = c1.getGreen() - c2.getBlue();
        int Bdiff = c1.getGreen() - c2.getBlue();
        return Rdiff * Rdiff + Gdiff * Gdiff + Bdiff * Bdiff;
    }

    public static double normalizedDistance(Color c1, Color c2) {
        return squareDistance(c1, c2) / 195075.0;
    }
}
