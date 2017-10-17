package cn.rongcapital.chorus.common.datastructure;

import java.util.*;

public class ListMap<K, V> extends HashMap<K, V> {
    private List<K> keyList = null;

    public ListMap() {
        keyList = new ArrayList<K>();
    }

    public K getKey(int index) {
        return keyList.get(index);
    }

    public V getValue(int index) {
        K key = keyList.get(index);
        return get(key);
    }

    @Override
    public V put(K key, V value) {
        keyList.add(key);
        return super.put(key, value);
    }

    @Override
    public V remove(Object key) {
        keyList.remove(key);
        return super.remove(key);
    }

    @Override
    public void clear() {
        keyList.clear();
        super.clear();
    }

    public List<K> getKeyList() {
        return keyList;
    }

    public List<V> getValueList() {
        List<V> values = new ArrayList<V>();
        for (K key : keyList) {
            values.add(get(key));
        }
        return values;
    }

    public void sort(Comparator<Object> comparator) {
        Collections.sort(keyList, comparator);
    }
}
