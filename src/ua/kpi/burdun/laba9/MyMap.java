package ua.kpi.burdun.laba9;

import java.util.Iterator;
import java.util.Map;

public interface MyMap {

    void clear();

    boolean containsKey(Object key);

    boolean containsValue(Object value);

    Object get(Object key);

    boolean isEmpty();

    Object put(Object key, Object value);

    Object remove(Object key);

    int size();

    Iterator entryIterator();

    public interface Entry {

        @Override
        boolean equals(Object o);

        Object getKey();

        Object getValue();

        @Override
        int hashCode();

        Object setValue(Object value);
    }
}
