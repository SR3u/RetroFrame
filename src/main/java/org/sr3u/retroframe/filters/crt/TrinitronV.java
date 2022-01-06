package org.sr3u.retroframe.filters.crt;

public class TrinitronV extends Trinitron {
    @Override
    protected int getIndex(int x, int y) {
        return x % scanlineFilters.length;
    }
}
