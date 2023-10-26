package util;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class IterableMap<K,V> implements Map<K,V>, Iterable<V>{

	protected Map<K,V> internalMap;
	
	public IterableMap() {
		internalMap = new HashMap<K, V>();
	}
	
	public IterableMap(Comparator<K> comparator){
		internalMap = new TreeMap<K, V>(comparator);
	}
	
	
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
