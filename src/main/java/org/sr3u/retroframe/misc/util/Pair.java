package org.sr3u.retroframe.misc.util;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class Pair<K, V> {
    K key;
    V value;
}
