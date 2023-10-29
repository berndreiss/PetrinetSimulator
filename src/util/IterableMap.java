package util;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

// TODO: Auto-generated Javadoc
/**
 * The Class IterableMap.
 *
 * @param <K> the key type
 * @param <V> the value type
 */
public class IterableMap<K,V> implements Map<K,V>, Iterable<V>{

	/** The internal map. */
	private Map<K,V> internalMap;
	
	/**
	 * Instantiates a new iterable map.
	 */
	public IterableMap() {
		internalMap = new HashMap<K, V>();
	}
	
	/**
	 * Instantiates a new iterable map.
	 *
	 * @param comparator the comparator
	 */
	public IterableMap(Comparator<K> comparator){
		internalMap = new TreeMap<K, V>(comparator);
	}
	
	
	/**
	 * Iterator.
	 *
	 * @return the iterator
	 */
	@Override
	public Iterator<V> iterator() {
		return new CustomIterator<V>(this);
	}

	private class CustomIterator<S> implements Iterator<S>{

		private Map<K, S> map;
		private Iterator<K> keyIterator;
		public CustomIterator(Map<K,S> map) {
			this.map = map;
			this.keyIterator = map.keySet().iterator();
		}
		@Override
		public boolean hasNext() {
			return keyIterator.hasNext();
		}

		@Override
		public S next() {
			return map.get(keyIterator.next());
		}
		
	}

	/**
	 * Size.
	 *
	 * @return the int
	 */
	@Override
	public int size() {
		return internalMap.size();
	}

	/**
	 * Checks if is empty.
	 *
	 * @return true, if is empty
	 */
	@Override
	public boolean isEmpty() {
		return internalMap.isEmpty();
	}

	/**
	 * Contains key.
	 *
	 * @param key the key
	 * @return true, if successful
	 */
	@Override
	public boolean containsKey(Object key) {
		return internalMap.containsKey(key);
	}

	/**
	 * Contains value.
	 *
	 * @param value the value
	 * @return true, if successful
	 */
	@Override
	public boolean containsValue(Object value) {
		return internalMap.containsValue(value);
	}

	/**
	 * Gets the.
	 *
	 * @param key the key
	 * @return the v
	 */
	@Override
	public V get(Object key) {
		return internalMap.get(key);
	}

	/**
	 * Put.
	 *
	 * @param key the key
	 * @param value the value
	 * @return the v
	 */
	@Override
	public V put(K key, V value) {
		return internalMap.put(key, value);
	}

	/**
	 * Removes the.
	 *
	 * @param key the key
	 * @return the v
	 */
	@Override
	public V remove(Object key) {
		return internalMap.remove(key);
	}

	/**
	 * Put all.
	 *
	 * @param m the m
	 */
	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		internalMap.putAll(m);
	}

	/**
	 * Clear.
	 */
	@Override
	public void clear() {
		internalMap.clear();
	}

	/**
	 * Key set.
	 *
	 * @return the sets the
	 */
	@Override
	public Set<K> keySet() {
		return internalMap.keySet();
	}

	/**
	 * Values.
	 *
	 * @return the collection
	 */
	@Override
	public Collection<V> values() {
		return internalMap.values();
	}

	/**
	 * Entry set.
	 *
	 * @return the sets the
	 */
	@Override
	public Set<Entry<K, V>> entrySet() {
		return internalMap.entrySet();
	}
	
}
