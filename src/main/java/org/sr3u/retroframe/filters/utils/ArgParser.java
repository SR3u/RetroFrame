package org.sr3u.retroframe.filters.utils;

import lombok.AllArgsConstructor;
import sr3u.streamz.optionals.OptionalDoublex;
import sr3u.streamz.optionals.OptionalIntex;
import sr3u.streamz.optionals.OptionalLongex;
import sr3u.streamz.optionals.Optionalex;

import java.util.List;

@AllArgsConstructor
public class ArgParser {
    private List<String> arguments;

    public Optionalex<String> stringAt(int index) {
        if (arguments.size() > index) {
            return Optionalex.ofNullable(arguments.get(index));
        }
        return Optionalex.empty();
    }

    public OptionalIntex intAt(int index) {
        return intAt(index, 10);
    }

    public OptionalIntex intAt(int index, int radix) {
        return stringAt(index).flatMap(i -> parseInt(i, radix)).mapToInt(i -> i);
    }

    public OptionalDoublex doubleAt(int index) {
        return stringAt(index).flatMap(this::parseDouble).mapToDouble(i -> i);
    }

    public OptionalLongex longAt(int index) {
        return longAt(index, 10);
    }

    public OptionalLongex longAt(int index, int radix) {
        return stringAt(index).flatMap(i -> parseLong(i, radix)).mapToLong(i -> i);
    }

    private Optionalex<Integer> parseInt(String str, int radix) {
        try {
            return Optionalex.of(Integer.parseInt(str, radix));
        } catch (Exception e) {
            return Optionalex.empty();
        }
    }

    private Optionalex<Long> parseLong(String str, int radix) {
        try {
            return Optionalex.of(Long.parseLong(str, radix));
        } catch (Exception e) {
            return Optionalex.empty();
        }
    }

    private Optionalex<Double> parseDouble(String str) {
        try {
            return Optionalex.of(Double.parseDouble(str));
        } catch (Exception e) {
            return Optionalex.empty();
        }
    }
}
