package org.hashlang.runtime;


import java.util.Iterator;
import java.util.Map;

import org.hashlang.util.Err;

public class MapIterator implements Iterator {

	private Map map;
	private Iterator keyIterator;

	public MapIterator(Map map) {
		this.map = map;
		this.keyIterator = map.keySet().iterator();
	}

	public boolean hasNext() {
		return keyIterator.hasNext();
	}

	public Object next() {
		Object k = keyIterator.next();
		return new Object[] { k, map.get(k) };
	}

	public void remove() {
		throw Err.notImplemented();
	}

}
