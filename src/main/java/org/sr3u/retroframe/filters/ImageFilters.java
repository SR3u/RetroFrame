package org.sr3u.retroframe.filters;

import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import sr3u.streamz.functionals.Supplierex;
import sr3u.streamz.optionals.Optionalex;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum ImageFilters {

    INSTANCE;

    private final Map<String, Class<? extends ImageFilter>> bySimpleName = new HashMap<>();
    private final Map<String, Class<? extends ImageFilter>> byFullName = new HashMap<>();
    private final Map<String, Supplierex<ImageFilter>> byAlias = new HashMap<>();
    @SuppressWarnings("FieldCanBeLocal")
    private final Set<Class<? extends ImageFilter>> excludedClasses = new HashSet<>(Arrays.asList(
            ImageFilterChain.class, FastImageFilterChain.class
    ));

    ImageFilters() {
        Reflections reflections =
                new Reflections(new ConfigurationBuilder().setUrls(ClasspathHelper.forJavaClassPath()));
        Set<Class<? extends ImageFilter>> subTypesOf = reflections.getSubTypesOf(ImageFilter.class);
        for (Class<? extends ImageFilter> c : subTypesOf) {
            if (!excludedClasses.contains(c) && !c.isInterface() && !Modifier.isAbstract(c.getModifiers())) {
                bySimpleName.put(c.getSimpleName().toLowerCase(), c);
                byFullName.put(c.getCanonicalName(), c);
            }
        }
        addAlias("Macintosh", "resize 512 342 | Atkinson Monochrome");
        addAlias("MacintoshHD", "Atkinson Monochrome");
        addAlias("MacintoshClassic", "resize 512 342 | Atkinson Monochrome");
        addAlias("MacintoshClassicHD", "Atkinson Monochrome");
        addAlias("Commodore64", "resize 320 200 | Atkinson c64");
        addAlias("Commodore64HD", "Atkinson c64");
        addAlias("ZxSpectrum", "resize 348 256 | atkinson ZXFULL | buffer | Spectrum");
        addAlias("ZxSpectrumHD", "atkinson ZXFULL");
        addAlias("IBMPCCGA", "resize 320 200 | atkinson CGA");
        addAlias("IBMPCCGAHD", "atkinson CGA");
        addAlias("IBM_PC_CGA", "resize 320 200 | atkinson CGA");
        addAlias("IBM_PC_CGA_HD", "atkinson CGA");
        addAlias("Trinitron", "TrinitronV");
        addAlias("TV", "Trinitron");
        addAlias("CRT", "Trinitron");
        addAlias("ColorPrinter", "Atkinson CMYK");
        addAlias("Printer", "ColorPrinter");
        addAlias("Drawing", "applyPalette Crayola");
        addAlias("GameBoyHD", "Atkinson GameBoy");
        addAlias("GameBoy", "resize 160 144 | Atkinson GameBoy");
        addAlias("Technicolor2", "applyPalette Technicolor");
        addAlias("Technicolor", "Technicolor2");
    }

    public static List<String> getAllAvailable() {
        return Stream.concat(
                INSTANCE.byAlias.keySet().stream(),
                INSTANCE.bySimpleName.keySet().stream()
        ).sorted().collect(Collectors.toList());
    }

    public static boolean isValid(FilterDescriptor filterDescriptor) {
        return INSTANCE.get(filterDescriptor.toString()) != null;
    }

    public void addAlias(String alias, String value) {
        byAlias.put(alias.toLowerCase(), () -> parse(value));
    }

    public static ImageFilter parse(String chainString) {
        String[] split = splitChain(chainString);
        ImageFilterChain.ImageFilterChainBuilder builder = ImageFilterChain.builder();
        for (String param : split) {
            ImageFilter filter = INSTANCE.get(param);
            builder.filter(filter);
        }
        return builder.unwrapChains().build();
    }

    public static List<FilterDescriptor> parseDescriptors(String chainString) {
        return Arrays.stream(splitChain(chainString))
                .map(INSTANCE::parseFilterDescriptor)
                .collect(Collectors.toList());
    }

    private static String[] splitChain(String chainString) {
        return chainString.split("\\s*\\|\\s*");
    }

    public ImageFilter get(String paramString) {
        return getSupplier(paramString).wrap().get();
    }

    Supplierex<ImageFilter> getSupplier(String paramString) {
        FilterDescriptor pair = parseFilterDescriptor(paramString);
        String name = pair.getName();
        List<String> parameters = pair.getParameters();
        if (byAlias.containsKey(name)) {
            return byAlias.get(name);
        }
        Class<? extends ImageFilter> aClass = Optionalex.ofNullable(bySimpleName.getOrDefault(name,
                byFullName.get(name)))
                .orElseThrow();
        return () -> aClass
                .getConstructor()
                .newInstance()
                .init(parameters);
    }

    private FilterDescriptor parseFilterDescriptor(String paramString) {
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
        return new FilterDescriptor(name, parameters);
    }

    public static ImageFilter.Info aboutFilter(String name) {
        return INSTANCE.get(name).getInfo();
    }
}
