package com.app.jonathan.willimissbart.misc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class NotGuava {

    public static <T> T getFirst(Iterable<T> iterable, T defaultVal)  {
        Iterator<T> iterator = iterable.iterator();
        return iterator.hasNext() ? iterator.next() : defaultVal;
    }

    public static <T> ArrayList<T> newArrayList() {
        return new ArrayList<>();
    }

    @SafeVarargs
    public static <T> ArrayList<T> newArrayList(T... objects) {
        ArrayList<T> arrayList = new ArrayList<>();
        Collections.addAll(arrayList, objects);
        return arrayList;
    }

    public static <T> ArrayList<T> newArrayList(List<T> objects) {
        return new ArrayList<>(objects);
    }

    public static <T> HashSet<T> newHashSet() {
        return new HashSet<>();
    }

    @SafeVarargs
    public static <T> HashSet<T> newHashSet(T... objects) {
        HashSet<T> hashSet = new HashSet<>();
        Collections.addAll(hashSet, objects);
        return hashSet;
    }

    public static <K, V> HashMap<K, V> newHashMap() {
        return new HashMap<>();
    }
}
