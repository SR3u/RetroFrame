package org.sr3u.retroframe.filters.crt;

public class TrinitronH extends Trinitron {
    @Override
    protected int getIndex(int x, int y) {
        return y % scanlineFilters.length;
    }
}
