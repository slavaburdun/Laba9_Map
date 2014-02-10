package ua.kpi.burdun.laba9;

import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.NoSuchElementException;

public class MyTreeMap implements MyMap {

    private SimpleEntry root;
    private int size;
    private Comparator comparator;

    public MyTreeMap() {
    }

    public MyTreeMap(Comparator comparator) {
        this.comparator = comparator;
    }

    @Override
    public void clear() {
        size = 0;
        root = null;
    }

    @Override
    public boolean containsKey(Object key) {
        return getSimpleEntry(key) != null;
    }

    private SimpleEntry getSimpleEntry(Object key) {
        //if (key == null)
        if (comparator != null) {
            return getSimpleEntryUsingComparator(key);
        }

        Comparable k = (Comparable) key;

        SimpleEntry tempRoot = root;

        while (tempRoot != null) {
            int cmp = k.compareTo(tempRoot.key);
            if (cmp < 0) {
                tempRoot = tempRoot.left;
            } else if (cmp > 0) {
                tempRoot = tempRoot.right;
            } else {
                return tempRoot;
            }
        }
        return null;
    }

    private SimpleEntry getSimpleEntryUsingComparator(Object key) {
        if (comparator != null) {
            SimpleEntry tempRoot = root;

            while (tempRoot != null) {
                int cmp = comparator.compare(key, tempRoot.key);
                if (cmp < 0) {
                    tempRoot = tempRoot.left;
                } else if (cmp > 0) {
                    tempRoot = tempRoot.right;
                } else {
                    return tempRoot;
                }
            }
        }
        return null;
    }

    @Override
    public boolean containsValue(Object value) {
        for (SimpleEntry entry = getFirstSimpleEntry(); entry != null; entry = findFirstLessThan(entry)) {
            if (value == null ? entry.value == null : value.equals(entry.value)) {
                return true;
            }
        }
        return false;
    }

    private SimpleEntry getFirstSimpleEntry() {
        SimpleEntry tempRoot = root;

        if (tempRoot != null) {
            while (tempRoot.left != null) {
                tempRoot = tempRoot.left;
            }
        }

        return tempRoot;
    }

    private SimpleEntry getLastSimpleEntry() {
        SimpleEntry tempRoot = root;

        if (tempRoot != null) {
            while (tempRoot.right != null) {
                tempRoot = tempRoot.right;
            }
        }

        return tempRoot;
    }

    static SimpleEntry findFirstLessThan(SimpleEntry simpleEntry) {
        if (simpleEntry == null) {
            return null;
        } else if (simpleEntry.right != null) {
            SimpleEntry temp = simpleEntry.right;
            while (temp.left != null) {
                temp = temp.left;
            }
            return temp;
        } else {
            SimpleEntry tempUp = simpleEntry.up;
            SimpleEntry down = simpleEntry;
            while (tempUp != null && down == tempUp.right) {
                down = tempUp;
                tempUp = tempUp.up;
            }
            return tempUp;
        }
    }

    @Override
    public Object get(Object key) {
        SimpleEntry result = getSimpleEntry(key);
        return (result == null ? null : result.value);
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public Object put(Object key, Object value) {
        SimpleEntry tempRoot = root;

        if (tempRoot == null) {
            root = new SimpleEntry(key, value, null);
            size = 1;
            return null;
        }

        int compRez;
        SimpleEntry up;

        do {
            up = tempRoot;
            compRez = comparator.compare(key, tempRoot.key);
            if (compRez < 0) {
                tempRoot = tempRoot.left;
            } else if (compRez > 0) {
                tempRoot = tempRoot.right;
            } else {
                return tempRoot.setValue(value);
            }
        } while (tempRoot != null);

        SimpleEntry entry = new SimpleEntry(key, value, up);
        if (compRez < 0) {
            up.left = entry;
        } else {
            up.right = entry;
        }
        size++;

        return null;
    }

    @Override
    public Object remove(Object key) {
        SimpleEntry result = getSimpleEntry(key);
        if (result == null) {
            return null;
        }

        Object oldValue = result.value;
        deleteSimpleEntry(result);
        return oldValue;
    }

    private void deleteSimpleEntry(SimpleEntry entry) {
        size--;
        if (entry.left != null && entry.right != null) {
            SimpleEntry firstGreater = findFirstLessThan(entry);
            entry.key = firstGreater.key;
            entry.value = firstGreater.value;
            entry = firstGreater;
        }
    }

    @Override
    public int size() {
        return size;
    }

    
    
    static <K,V> MyTreeMap.SimpleEntry successor(SimpleEntry t) {
        if (t == null)
            return null;
        else if (t.right != null) {
            SimpleEntry p = t.right;
            while (p.left != null)
                p = p.left;
            return p;
        } else {
            SimpleEntry p = t.parent;
            SimpleEntry ch = t;
            while (p != null && ch == p.right) {
                ch = p;
                p = p.parent;
            }
            return p;
        }
    }

    /**
     * Returns the predecessor of the specified Entry, or null if no such.
     */
    static <K,V> SimpleEntry predecessor(SimpleEntry t) {
        if (t == null)
            return null;
        else if (t.left != null) {
            SimpleEntry p = t.left;
            while (p.right != null)
                p = p.right;
            return p;
        } else {
            SimpleEntry p = t.parent;
            SimpleEntry ch = t;
            while (p != null && ch == p.left) {
                ch = p;
                p = p.parent;
            }
            return p;
        }
    }
    
    
    private void deleteEntry(SimpleEntry p) {
        size--;

        // If strictly internal, copy successor's element to p and then make p
        // point to successor.
        if (p.left != null && p.right != null) {
            SimpleEntry s = successor(p);
            p.key = s.key;
            p.value = s.value;
            p = s;
        } // p has 2 children

        // Start fixup at replacement node, if it exists.
        SimpleEntry replacement = (p.left != null ? p.left : p.right);

        if (replacement != null) {
            // Link replacement to parent
            replacement.parent = p.parent;
            if (p.parent == null)
                root = replacement;
            else if (p == p.parent.left)
                p.parent.left  = replacement;
            else
                p.parent.right = replacement;

            // Null out links so they are OK to use by fixAfterDeletion.
            p.left = p.right = p.parent = null;
            
        } else if (p.parent == null) { // return if we are the only node.
            root = null;
        } else { //  No children. Use self as phantom replacement and unlink.
            if (p.parent != null) {
                if (p == p.parent.left)
                    p.parent.left = null;
                else if (p == p.parent.right)
                    p.parent.right = null;
                p.parent = null;
            }
        }
    }
    
    
    abstract class PrivateEntryIterator<T> implements Iterator<T> {
        SimpleEntry next;
        SimpleEntry lastReturned;
        int expectedModCount;

        PrivateEntryIterator(SimpleEntry first) {
            //expectedModCount = modCount;
            lastReturned = null;
            next = first;
        }

        public final boolean hasNext() {
            return next != null;
        }

        final SimpleEntry nextEntry() {
            SimpleEntry e = next;
            if (e == null)
                throw new NoSuchElementException();
            next = successor(e);
            lastReturned = e;
            return e;
        }

        final SimpleEntry prevEntry() {
            SimpleEntry e = next;
            if (e == null)
                throw new NoSuchElementException();
            next = predecessor(e);
            lastReturned = e;
            return e;
        }

        public void remove() {
            if (lastReturned == null)
                throw new IllegalStateException();
            // deleted entries are replaced by their successors
            if (lastReturned.left != null && lastReturned.right != null)
                next = lastReturned;
            deleteEntry(lastReturned);
            lastReturned = null;
        }
    }
    
    @Override
    public Iterator entryIterator() {
        // TODO Auto-generated method stub
        return null;
    }

    public class SimpleEntry implements Entry {

        Object key;
        Object value;
        SimpleEntry left, right, up;
        SimpleEntry parent;

        public SimpleEntry(Object key, Object value, SimpleEntry up) {
            this.key = key;
            this.value = value;
            this.up = up;
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
            Object oldValue = value;
            this.value = value;
            return oldValue;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof SimpleEntry)) {
                return false;
            }
            SimpleEntry entry = (SimpleEntry) obj;
            return key == null ? entry.getKey() == null : key.equals(entry.getKey())
                    && value == null ? entry.getValue() == null : value.equals(entry.getValue());
        }

        @Override
        public int hashCode() {
            return (key == null ? 0 : key.hashCode()) ^ (value == null ? 0 : value.hashCode());
        }

        @Override
        public String toString() {
            return key + " : " + value;
        }
    }
}
