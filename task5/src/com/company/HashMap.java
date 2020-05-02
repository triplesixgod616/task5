package com.company;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class HashMap<T, V> {
    static class Entry<T, V> {
        final int hash;
        final T key;
        V value;
        Entry<T, V> next;

        Entry(int hash, T key, V value, Entry<T, V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }

        public final T getKey() {
            return key;
        }

        public final V getValue() {
            return value;
        }

        public final String toString() {
            return key + "=" + value;
        }

        public final V setValue(V newValue) {
            V oldValue = value;
            value = newValue;
            return oldValue;
        }
    }

    private int capacity;
    private Entry<T, V>[] table;
    private double loadFactor;
    private int threshold;
    private int size;
    private int occupancy;

    public HashMap(int capacity, double loadFactor) {
        this.capacity = capacity;
        table = (Entry<T, V>[]) new Entry[capacity];
        this.loadFactor = loadFactor;
        threshold = (int) (capacity * loadFactor);
        size = 0;
        occupancy = 0;
    }

    public HashMap(int capacity) {
        this(capacity, 0.75);
    }

    public HashMap() {
        this(16);
    }

    public void clear() {
        if (table != null && size > 0) {
            size = 0;
            Arrays.fill(table, null);
        }
    }

    public Set<Entry<T, V>> entrySet() {
        Set<Entry<T, V>> set = new HashSet<>();

        for(Entry<T, V> e : table) {
            while(e != null) {
                set.add(e);
                e = e.next;
            }
        }

        return set;
    }

    public ArrayList<T> keySet() {
        Set<Entry<T, V>> entries = entrySet();
        ArrayList<T> set = new ArrayList<>();

        for(Entry<T, V> e : entries) {
            set.add(e.key);
        }

        return set;
    }

    public ArrayList<V> values() {
        Set<Entry<T, V>> entries = entrySet();
        ArrayList<V> list = new ArrayList<>();

        for(Entry<T, V> e : entries) {
            list.add(e.value);
        }

        return list;
    }

    public V put(T key, V value) {
        if (occupancy > threshold) resize(capacity * 2);

        int h = hash(key);

        Entry<T, V> e = table[getIndex(h, capacity)];
        if (e == null) occupancy++;

        while (e != null) {
            if (e.hash == h && key.equals(e.key)) {
                V oldValue = e.value;
                e.value = value;

                return oldValue;
            }
            e = e.next;
        }

        addEntry(h, key, value, getIndex(h, capacity));

        size++;

        return null;
    }

    public V remove(T key) throws Exception {
        int h = hash(key);

        Entry<T, V> e = table[getIndex(h, capacity)];
        Entry<T, V> eNext = e.next;

        while (e != null) {
            if(e.hash == h && key.equals(e.key)) {
                V oldValue = e.value;
                e = e.next;

                occupancy--;
                size--;

                return oldValue;
            }
            e = e.next;
            if(eNext != null) eNext = eNext.next;
        }
        throw new Exception("There is no element with that key");
    }

    public V get(T key) {
        int h = hash(key);

        Entry<T, V> e = table[getIndex(h, capacity)];
        while (e != null) {
            if (e.hash == h && key.equals(e.key)) return e.value;
            e = e.next;
        }

        return null;
    }

    public void putAll(HashMap<T, V> map) {
        for(Entry<T, V> e : map.table) {
            while(e != null) {
                put(e.key, e.value);
                e = e.next;
            }
        }
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return (size == 0);
    }

    private int hash(T key) {
        if (key == null) return 0;

        int h = key.hashCode();
        h ^= (h >>> 20) ^ (h >>> 12);

        return h ^ (h >>> 7) ^ (h >>> 4);
    }

    private int getIndex(int h, int length) {
        return h & (length - 1);
    }

    private void addEntry(int hash, T key, V value, int index) {
        Entry<T, V> e = table[index];
        table[index] = new Entry<T, V>(hash, key, value, e);
    }

    private void resize(int newCapacity) {
        if (capacity == Integer.MAX_VALUE) return;

        Entry<T, V>[] newTable = (Entry<T, V>[]) new Entry[newCapacity];
        transfer(newTable);
        table = newTable;

        capacity = newCapacity;
        threshold = newCapacity == Integer.MAX_VALUE ? newCapacity : (int) (newCapacity * loadFactor);
    }

    private void transfer(Entry<T, V>[] newTable) {
        int newLength = newTable.length;
        for (Entry<T, V> entry : table) {
            if (entry != null) {
                int h = entry.hash;
                newTable[getIndex(h, newLength)] = entry;
            }
        }
    }
}
