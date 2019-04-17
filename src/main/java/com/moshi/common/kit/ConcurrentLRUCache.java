package com.moshi.common.kit;

import org.cliffc.high_scale_lib.NonBlockingHashMap;

import java.util.ArrayDeque;
import java.util.Map;
import java.util.Queue;
import java.util.function.BiFunction;

/**
 * Concurrent LRU cache.
 *
 * Note that remove operation on this structure is SLOW! Avoid using it.
 *
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class ConcurrentLRUCache<K, V> extends NonBlockingHashMap<K, V> {

  private int maxSize;
  private final Queue<K> queue = new ArrayDeque<>();

  public ConcurrentLRUCache(int maxSize) {
    this.maxSize = maxSize;
    checkSize();
  }

  public ConcurrentLRUCache(int initialCapacity, int maxSize) {
    super(initialCapacity);
    this.maxSize = maxSize;
    checkSize();
  }


  public void setMaxSize(int maxSize) {
    this.maxSize = maxSize;
    checkRemoveOldest();
  }

  @Override
  public V put(K key, V value) {
    V v = super.put(key, value);
    if (v == null) {
      entryAdded(key);
    }
    checkRemoveOldest();
    return v;
  }

  @Override
  public void putAll(Map<? extends K, ? extends V> m) {
    for (K k: m.keySet()) {
      if (!super.containsKey(k)) {
        entryAdded(k);
      }
    }
    super.putAll(m);
    checkRemoveOldest();
  }

  @Override
  public V remove(Object key) {
    V v = super.remove(key);
    if (v != null) {
      entryRemoved(key);
    }
    return v;
  }

  @Override
  public void clear() {
    super.clear();
    queue.clear();
  }

  @Override
  public V putIfAbsent(K key, V value) {
    V v = super.putIfAbsent(key, value);
    if (v == null) {
      entryAdded(key);
    }
    checkRemoveOldest();
    return v;
  }

  @Override
  public boolean remove(Object key, Object value) {
    boolean removed = super.remove(key, value);
    if (removed) {
      entryRemoved(value);
    }
    return removed;
  }

  @Override
  public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
    throw new UnsupportedOperationException();
  }

  private void entryAdded(K k) {
    queue.add(k);
  }

  private void entryRemoved(Object o) {
    if (!queue.remove(o)) {
      throw new IllegalStateException("Failed to remove");
    }
  }

  private void checkRemoveOldest() {
    while (queue.size() > maxSize) {
      K k = queue.poll();
      if (k != null) {
        super.remove(k);
      }
    }
  }

  private void checkSize() {
    if (maxSize < 1) {
      throw new IllegalArgumentException("maxSize must be >= 1");
    }
  }

  public int queueSize() {
    return queue.size();
  }
}