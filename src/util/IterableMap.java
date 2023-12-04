package util;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * <p>
 * A Map that is iterable and can represent a HashMap or a TreeMap with sorted
 * keys when passing a {@link Comparator} to the constructor.
 * </p>
 * 
 * @param <K> the key type
 * @param <V> the value type
 */
public class IterableMap<K, V> implements Map<K, V>, Iterable<V> {// does not extend specific Map so it can represent
																	// Hash- and TreeMap

	/** the internal map */
	private Map<K, V> internalMap;

	/**
	 * Instantiates a new iterable HashMap.
	 */
	public IterableMap() {
		internalMap = new HashMap<K, V>();
	}

	/**
	 * Instantiates a new iterable TreeMap with sorted keys.
	 *
	 * @param comparator The comparator for sorting keys.
	 */
	public IterableMap(Comparator<K> comparator) {
		internalMap = new TreeMap<K, V>(comparator);
	}
	
	/**
	 * Creates a copy of the given map.
	 * @return a copy of the given map
	 */
	public IterableMap<K,V> copy(){
		IterableMap<K,V> newMap = new IterableMap<K, V>();
		for (K k: internalMap.keySet())
			newMap.put(k, internalMap.get(k));
		return newMap;
	}

	/**
	 * Converts given map to HashSet.
	 * @return the map as Set
	 */
	public Set<V> castToSet() {
		Set<V> set = new HashSet<V>();
		
		for (V v: this)
			set.add(v);
		
		return set;
	}
	
	@Override
	public Iterator<V> iterator() {
		return new CustomIterator<V>(this);
	}

	// iterator using the keyIterator for iterating over values
	private class CustomIterator<S> implements Iterator<S> {

		private Map<K, S> map;
		private Iterator<K> keyIterator;

		public CustomIterator(Map<K, S> map) {
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

	// implement interface Map<K,V>
	@Override
	public int size() {
		return internalMap.size();
	}

	@Override
	public boolean isEmpty() {
		return internalMap.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return internalMap.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return internalMap.containsValue(value);
	}

	@Override
	public V get(Object key) {
		return internalMap.get(key);
	}

	@Override
	public V put(K key, V value) {
		return internalMap.put(key, value);
	}

	@Override
	public V remove(Object key) {
		return internalMap.remove(key);
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		internalMap.putAll(m);
	}

	@Override
	public void clear() {
		internalMap.clear();
	}

	@Override
	public Set<K> keySet() {
		return internalMap.keySet();
	}

	@Override
	public Collection<V> values() {
		return internalMap.values();
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		return internalMap.entrySet();
	}

}
