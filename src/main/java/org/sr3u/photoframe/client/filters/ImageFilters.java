package org.sr3u.photoframe.client.filters;

import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
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
    private Set<Class<? extends ImageFilter>> excludedClasses = new HashSet<>(Arrays.asList(
            ImageFilterChain.class, FastImageFilterChain.class
    ));

    ImageFilters() {
        Reflections reflections = new Reflections(new ConfigurationBuilder().setUrls(ClasspathHelper.forJavaClassPath()));
        Set<Class<? extends ImageFilter>> subTypesOf = reflections.getSubTypesOf(ImageFilter.class);
        for (Class<? extends ImageFilter> c : subTypesOf) {
            if (!excludedClasses.contains(c) && !c.isInterface() && !Modifier.isAbstract(c.getModifiers())) {
                bySimpleName.put(c.getSimpleName().toLowerCase(), c);
                byFullName.put(c.getCanonicalName(), c);
            }
        }
        addAlias("Macintosh", "resize 512 342 | Atkinson Monochrome");
        addAlias("Commodore64", "resize 320 200 | Atkinson c64");
        addAlias("ZxSpectrum", "resize 348 256 | Spectrum 32 24");
    }

    private void addAlias(String alias, String value) {
        byAlias.put(alias.toLowerCase(), () -> parse(value));
    }

    public static ImageFilter parse(String chainString) {
        String[] split = chainString.split("\\s*\\|\\s*");
        ImageFilterChain.ImageFilterChainBuilder builder = ImageFilterChain.builder();
        for (String param : split) {
            ImageFilter filter = INSTANCE.get(param);
            builder.filter(filter);
        }
        return builder.unwrapChains().build();
    }

    public ImageFilter get(String paramString) {
        return getSupplier(paramString).wrap().get();
    }

    Supplierex<ImageFilter> getSupplier(String paramString) {
        String[] split = paramString.split(" ");
        String name = split[0].toLowerCase();
        List<String> parameters = new ArrayList<>();
        if (split.length > 1) {
            parameters = Arrays.stream(split)
                    .skip(1)
                    .filter(Objects::nonNull)
                    .filter(s -> !s.isEmpty())
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
