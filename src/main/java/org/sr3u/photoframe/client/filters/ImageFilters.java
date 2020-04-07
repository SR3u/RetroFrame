package org.sr3u.photoframe.client.filters;

import org.reflections.Reflections;
import sr3u.streamz.functionals.Supplierex;
import sr3u.streamz.optionals.Optionalex;

import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

public enum ImageFilters {

    INSTANCE;

    private Map<String, Class<? extends ImageFilter>> bySimpleName = new HashMap<>();
    private Map<String, Class<? extends ImageFilter>> byFullName = new HashMap<>();
    private Map<String, Supplierex<ImageFilter>> byAlias = new HashMap<>();

    ImageFilters() {
        Reflections reflections = new Reflections("");
        Set<Class<? extends ImageFilter>> subTypesOf = reflections.getSubTypesOf(ImageFilter.class);
        for (Class<? extends ImageFilter> c : subTypesOf) {
            if (!c.equals(ImageFilterChain.class) && !c.isInterface() && !Modifier.isAbstract(c.getModifiers())) {
                bySimpleName.put(c.getSimpleName(), c);
                byFullName.put(c.getCanonicalName(), c);
            }
        }
        byAlias.put("Macintosh", () -> parse("Sierra3(LUMINANCE, #000000, #111111, #222222, #333333, #444444,#555555,#666666,#777777,#888888,#999999,#AAAAAA,#BBBBBB,#CCCCCC,#DDDDDD,#EEEEEE,#FFFFFF)Atkinson(Monochrome)"));
    }

    public static ImageFilter parse(String chainString) {
        String[] split = chainString.split("\\)\\s*");
        ImageFilterChain.ImageFilterChainBuilder builder = ImageFilterChain.builder();
        for (String param : split) {
            builder.filter(INSTANCE.get(param + ")"));
        }
        return builder.unwrapChains().build();
    }

    public ImageFilter get(String paramString) {
        return getSupplier(paramString).wrap().get();
    }

    Supplierex<ImageFilter> getSupplier(String paramString) {
        String[] split = paramString.split("\\(");
        String name = split[0];
        int beginIndex = paramString.indexOf('(');
        int endIndex = paramString.indexOf(')');
        List<String> parameters = new ArrayList<>();
        if (beginIndex != -1 && endIndex != -1) {
            parameters = Arrays.stream(paramString.substring(beginIndex + 1, endIndex).split(","))
                    .filter(Objects::nonNull)
                    .map(String::trim)
                    .collect(Collectors.toList());
        }
        if (byAlias.containsKey(name)) {
            return byAlias.get(name);
        }
        Class<? extends ImageFilter> aClass = Optionalex.ofNullable(bySimpleName.getOrDefault(name, byFullName.get(name)))
                .orElseThrow();
        List<String> finalParameters = parameters;
        return () -> aClass
                .newInstance()
                .init(finalParameters);
    }
}
