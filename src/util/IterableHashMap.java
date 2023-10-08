package util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class IterableHashMap<K,V> extends HashMap<K,V> implements Iterable<V>{

	private static final long serialVersionUID = 1L;

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
	
}
