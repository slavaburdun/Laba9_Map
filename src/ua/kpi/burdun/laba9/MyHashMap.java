package ua.kpi.burdun.laba9;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class MyHashMap implements MyMap {

    private int initialCapacity;
    private float loadFactor;
    private SimpleEntry[] values;
    private int size;

    public MyHashMap() {
        this(16, 0.75f);
    }

    public MyHashMap(int initialCapacity) throws IllegalArgumentException {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("initial capacity less than 0");
        }
        this.initialCapacity = initialCapacity;
        loadFactor = 0.75f;
    }

    public MyHashMap(int initialCapacity, float loadFactor)
            throws IllegalArgumentException {
        if (initialCapacity < 0 || loadFactor < 0 || loadFactor > 1) {
            throw new IllegalArgumentException(
                    "initial capacity less than 0 OR loadFactor less than 0 OR loadFactor greater than 1");
        }
        this.initialCapacity = initialCapacity;
        this.loadFactor = loadFactor;

    }

    @Override
    public void clear() {
        for (int i = 0; i < values.length; i++) {
            values[i] = null;
        }
        size = 0;
    }

    @Override
    public boolean containsKey(Object key) {
        return getSimpleEntry(key) != null;
    }

    @Override
    public boolean containsValue(Object value) {
        for (int i = 0; i < values.length; i++) {
            for (SimpleEntry entry = values[i]; entry != null; entry = entry.next) {
                if (value.equals(entry.value)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Object get(Object key) {
        SimpleEntry entry = getSimpleEntry(key);
        return entry == null ? null : entry.getValue();
    }

    SimpleEntry getSimpleEntry(Object key) {
        int hash = 0;
        if (!(key == null)) {
            hash = hash(key);
        }

        for (SimpleEntry entry = values[indexFor(hash, values.length)]; entry != null; entry = entry.next) {
            Object key2;
            if (entry.hash == hash
                    && ((key2 = entry.key) == key || (key != null && key
                    .equals(entry)))) {
                return entry;
            }
        }
        return null;
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public Object put(Object key, Object value) {
        int hash = hash(key);
        int i = indexFor(hash, values.length);
        for (SimpleEntry entry = values[i]; entry != null; entry = entry.next) {
            Object k;
            if (entry.hash == hash && ((k = entry.key) == key || key.equals(k))) {
                Object oldValue = entry.value;
                entry.value = value;
                return oldValue;
            }
        }
        addSimpleEntry(hash, key, value, i);
        return null;
    }

    private void addSimpleEntry(int hash, Object key, Object value, int bucketIndex) {
        if (null != values[bucketIndex]) {
            hash = (null != key) ? hash(key) : 0;
            bucketIndex = indexFor(hash, values.length);
        }

        createEntry(hash, key, value, bucketIndex);
    }

    void createEntry(int hash, Object key, Object value, int bucketIndex) {
        SimpleEntry entry = values[bucketIndex];
        values[bucketIndex] = new SimpleEntry(hash, key, value, entry);
        size++;
    }

    @Override
    public Object remove(Object key) {
        int hash = (key == null) ? 0 : hash(key);
        int i = indexFor(hash, values.length);
        SimpleEntry prev = values[i];
        SimpleEntry entry = prev;

        while (entry != null) {
            SimpleEntry next = entry.next;
            Object k;
            if (entry.hash == hash
                    && ((k = entry.key) == key || (key != null && key.equals(k)))) {
                size--;
                if (prev == entry) {
                    values[i] = next;
                } else {
                    prev.next = next;
                }
                return entry;
            }
            prev = entry;
            entry = next;
        }
        return entry;
    }

    private int indexFor(int h, int length) {
        return h & (length - 1);
    }

    private int hash(Object k) {
        int h = 0;
        h ^= k.hashCode();
        h ^= (h >>> 20) ^ (h >>> 12);
        return h ^ (h >>> 7) ^ (h >>> 4);
    }

    @Override
    public int size() {
        return size;
    }

    
    private abstract class HashIterator<E> implements Iterator<E> {
        SimpleEntry next;        // next entry to return
        int index;              // current slot
        SimpleEntry current;     // current entry

        HashIterator() {
            if (size > 0) { // advance to first entry
                SimpleEntry[] t = values;
                while (index < t.length && (next = t[index++]) == null);
            }
        }

        @Override
        public final boolean hasNext() {
            return next != null;
        }

        final SimpleEntry nextEntry() {
            SimpleEntry e = next;
            if (e == null)
                throw new NoSuchElementException();

            if ((next = e.next) == null) {
                SimpleEntry[] t = values;
                while (index < t.length && (next = t[index++]) == null);
            }
            current = e;
            return e;
        }

        @Override
        public void remove() {
            if (current == null)
                throw new IllegalStateException();
            Object k = current.key;
            current = null;
            MyHashMap.this.remove(k);
        }
    }

    
    private final class EntryIterator extends HashIterator<MyMap.Entry> {
        @Override
        public MyMap.Entry next() {
            return nextEntry();
        }
    }
    
    
    @Override
    public Iterator entryIterator() {
        return new EntryIterator();
    }

    
    public static class SimpleEntry implements MyMap.Entry {

        final Object key;
        Object value;
        SimpleEntry next;
        int hash;

        SimpleEntry(int hash, Object key, Object value, SimpleEntry next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }

        @Override
        public Object getKey() {
            return key;
        }

        @Override
        public Object getValue() {
            return value;
        }

        @Override
        public Object setValue(Object value) {
            Object oldValue = this.value;
            this.value = value;
            return oldValue;
        }

        @Override
        public int hashCode() {
            return (key == null ? 0 : key.hashCode()) ^ (value == null ? 0 : value.hashCode());
        }

        @Override
        public boolean equals(Object obj) {

            if (!(obj instanceof SimpleEntry)) {
                return false;
            }

            SimpleEntry entry = (SimpleEntry) obj;
            Object key1 = getKey();
            Object key2 = entry.getKey();

            if (key1 == key2 || (key1 != null && key1.equals(key2))) {
                Object value1 = getValue();
                Object value2 = entry.getValue();

                if (value1 == value2 || (value1 != null && value1.equals(value2))) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public String toString() {
            return getKey() + " : " + getValue();
        }
    }

    public int getInitialCapacity() {
        return initialCapacity;
    }

    public float getLoadFactor() {
        return loadFactor;
    }
}
