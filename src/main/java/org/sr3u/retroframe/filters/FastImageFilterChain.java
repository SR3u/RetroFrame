package org.sr3u.retroframe.filters;

import lombok.Getter;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public class FastImageFilterChain implements FastImageFilter {

    List<FastImageFilter> filters;

    FastImageFilterChain(List<FastImageFilter> filters) {
        this.filters = filters;
    }

    public static FastImageFilterChainBuilder builder() {
        return new FastImageFilterChainBuilder();
    }

    @Override
    public Object createContext(BufferedImage image) {
        Context context = new Context(filters);
        for (int i = 0; i < filters.size(); i++) {
            context.subContexts.add(filters.get(i).createContext(image));
        }
        return context;
    }

    @Override
    public void apply(BufferedImage image, Object contextObject, int x, int y) throws Exception {
        Context context = (Context) contextObject;
        for (int i = 0; i < filters.size(); i++) {
            FastImageFilter fastImageFilter = filters.get(i);
            Object ctx = context.subContexts.get(i);
            fastImageFilter.apply(image, ctx, x, y);
        }
    }

    @Override
    public ImageFilter init(List<String> parameters) {
        return null;
    }

    static class Context {
        List<Object> subContexts;

        public Context(List<FastImageFilter> filters) {
            subContexts = new ArrayList<>(filters.size());
        }
    }

    public static class FastImageFilterChainBuilder {
        private ArrayList<FastImageFilter> filters;

        FastImageFilterChainBuilder() {
        }

        public FastImageFilterChain.FastImageFilterChainBuilder unwrapChains() {
            List<FastImageFilter> unwrapped = this.filters;
            int oldSize;
            do {
                oldSize = unwrapped.size();
                unwrapped = unwrapped.stream()
                        .flatMap(f -> {
                            if (f instanceof FastImageFilterChain) {
                                FastImageFilterChain ifc = (FastImageFilterChain) f;
                                return ifc.getFilters().stream();
                            } else {
                                return Stream.of(f);
                            }
                        })
                        .collect(Collectors.toList());
            } while (unwrapped.size() != oldSize);
            return FastImageFilterChain.builder().filters(new ArrayList<>(unwrapped));
        }

        public FastImageFilterChainBuilder filter(FastImageFilter filter) {
            if (this.filters == null) {
                this.filters = new ArrayList<>();
            }
            this.filters.add(filter);
            return this;
        }

        public FastImageFilterChainBuilder filters(Collection<? extends FastImageFilter> filters) {
            if (this.filters == null) {
                this.filters = new ArrayList<>();
            }
            this.filters.addAll(filters);
            return this;
        }

        public FastImageFilterChainBuilder clearFilters() {
            if (this.filters != null) {
                this.filters.clear();
            }
            return this;
        }

        public FastImageFilterChain build() {
            List<FastImageFilter> filters;
            switch (this.filters == null ? 0 : this.filters.size()) {
                case 0:
                    filters = java.util.Collections.emptyList();
                    break;
                case 1:
                    filters = java.util.Collections.singletonList(this.filters.get(0));
                    break;
                default:
                    filters = java.util.Collections.unmodifiableList(new ArrayList<>(this.filters));
            }

            return new FastImageFilterChain(filters);
        }

        @Override
        public String toString() {
            return "FastImageFilterChain.FastImageFilterChainBuilder(filters=" + this.filters + ")";
        }
    }

    @Override
    public void reset() {
        for (ImageFilter filter : filters) {
            filter.reset();
        }
    }
}
