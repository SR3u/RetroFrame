package org.sr3u.retroframe.filters;

import lombok.Value;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Value
public class ImageFilterChain implements ImageFilter {

    List<ImageFilter> filters;
    ImageFilter.Context context = new ImageFilter.Context();

    public ImageFilterChain(List<ImageFilter> filters) {
        this.filters = filters;
    }

    public static ImageFilterChainBuilder builder() {
        return new ImageFilterChainBuilder();
    }

    @Override
    public ImageFilter init(List<String> parameters) {
        return this;
    }

    @Override
    public Image apply(Image image) throws Exception {
        context.setOriginalSize(new Dimension(image.getWidth(null), image.getHeight(null)));
        reset();
        for (ImageFilter f : filters) {
            image = f.apply(context, image);
        }
        return image;
    }

    public static class ImageFilterChainBuilder {
        private ArrayList<ImageFilter> filters;

        ImageFilterChainBuilder() {
        }

        public ImageFilterChainBuilder filter(ImageFilter filter) {
            if (this.filters == null) {
                this.filters = new ArrayList<>();
            }
            this.filters.add(filter);
            return this;
        }

        public ImageFilterChainBuilder filters(Collection<? extends ImageFilter> filters) {
            if (this.filters == null) {
                this.filters = new ArrayList<>();
            }
            this.filters.addAll(filters);
            return this;
        }

        public ImageFilterChainBuilder clearFilters() {
            if (this.filters != null) {
                this.filters.clear();
            }
            return this;
        }

        public ImageFilterChainBuilder unwrapChains() {
            List<ImageFilter> unwrapped = this.filters;
            int oldSize;
            do {
                oldSize = unwrapped.size();
                unwrapped = unwrapped.stream()
                        .flatMap(f -> {
                            if (f instanceof ImageFilterChain) {
                                ImageFilterChain ifc = (ImageFilterChain) f;
                                return ifc.getFilters().stream();
                            } else {
                                return Stream.of(f);
                            }
                        })
                        .collect(Collectors.toList());
            } while (unwrapped.size() != oldSize);
            return ImageFilterChain.builder().filters(unwrapped).joinFast();
        }

        public ImageFilterChainBuilder joinFast() {
            ImageFilterChainBuilder result = ImageFilterChain.builder();
            FastImageFilterChain.FastImageFilterChainBuilder fastBuilder = null;
            for (ImageFilter filter : filters) {
                if (filter instanceof FastImageFilter) {
                    if (fastBuilder == null) {
                        fastBuilder = FastImageFilterChain.builder();
                    }
                    fastBuilder.filter((FastImageFilter) filter);
                } else {
                    if (fastBuilder != null) {
                        result.filter(fastBuilder.unwrapChains().build());
                        fastBuilder = null;
                    }
                    result.filter(filter);
                }
            }
            if (fastBuilder != null) {
                result.filter(fastBuilder.unwrapChains().build());
            }
            return result;
        }

        public ImageFilterChain build() {
            List<ImageFilter> filters;
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

            return new ImageFilterChain(filters);
        }

        @Override
        public String toString() {
            return "ImageFilterChain.ImageFilterChainBuilder(filters=" + this.filters + ")";
        }
    }

    @Override
    public void reset() {
        for (ImageFilter filter : filters) {
            filter.reset();
        }
    }

}
