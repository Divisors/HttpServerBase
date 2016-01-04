package com.divisors.projectcuttlefish.httpserver.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A collection of HTTP headers (or any other String one-to-many relationships)
 * @author mailmindlin
 * @see HttpHeader
 */
public class HttpHeaders implements Map<String, Collection<String>> {
	/**
	 * Map that actually contains the data
	 */
	protected final Map<String, Collection<String>> backingMap;
	public HttpHeaders() {
		this.backingMap = new ConcurrentHashMap<String, Collection<String>>();
	}
	/**
	 * Creates from map.<br/>
	 * <b>NOTE:</b> This uses the map directly, without copying it. That means that any changes
	 * in either map will reflect to the other.
	 * @param map
	 */
	@SuppressWarnings("unchecked")
	public HttpHeaders(Map<String, ? extends Collection<String>> map) {
		this.backingMap = (Map<String, Collection<String>>) map;
	}
	@Override
	public int size() {
		return backingMap.size();
	}

	@Override
	public boolean isEmpty() {
		return backingMap.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return backingMap.containsKey(key);
	}

	/**{@inheritDoc}*/
	@Override
	public boolean containsValue(Object value) {
		return false;
	}

	@Override
	public Collection<String> get(Object key) {
		return this.backingMap.get(key);
	}
	public HttpHeader getHeader(String key) {
		return new HttpHeader(key, get(key));
	}
	public boolean add(String key, String value) {
		return this.backingMap.computeIfAbsent(key, x->new LinkedHashSet<>(1)).add(value);
	}
	/**
	 * Add all values under the given key. If 
	 * @param key
	 * @param values
	 * @return
	 */
	public boolean addAll(String key, Collection<String> values) {
		//it's kind of wierd how the authors of the javadoc for computeIfAbsent knew exactly what I wanted to do.
		return this.backingMap.computeIfAbsent(key, x->new LinkedHashSet<>(1)).addAll(values);
	}
	@Override
	public Collection<String> put(String key, Collection<String> values) {
		return this.backingMap.put(key, values);
	}
	
	public Collection<String> put(String key, String value) {
		HashSet<String> set = new HashSet<>(1);//it is 90% safe to allocate a hashset with only one slot, because most HTTP headers will only be set once.
		set.add(value);
		return put(key, new HashSet<String>(1));
	}
	public Collection<String> put(String key, String[] values) {
		return put(key, new ArrayList<>(Arrays.asList(values)));
	}
	@Override
	public Collection<String> remove(Object key) {
		return backingMap.remove(key);
	}
	/**
	 * Remove single key/value pair from map
	 * @param key
	 * @param value
	 * @return <code>true</code> if an element was removed
	 */
	public boolean remove(String key, String value) {
		Collection<String> values = backingMap.get(key);
		if (values!=null)
			return values.remove(value);
		return false;
	}
	@Override
	public void putAll(Map<? extends String, ? extends Collection<String>> m) {
		this.backingMap.putAll(m);
	}
	public void addAll(Map<String, ? extends Collection<String>> map) {
		for (Entry<String, ? extends Collection<String>> entry : map.entrySet()) {
			if (!backingMap.containsKey(entry.getKey())) {
				put(entry.getKey(), entry.getValue());
			} else {
				addAll(entry.getKey(), entry.getValue());
			}
		}
	}

	@Override
	public void clear() {
		this.backingMap.clear();
	}

	/**{@inheritDoc}*/
	@Override
	public Set<String> keySet() {
		return this.backingMap.keySet();
	}

	@Override
	public Collection<Collection<String>> values() {
		return this.backingMap.values();
	}

	@Override
	public Set<java.util.Map.Entry<String, Collection<String>>> entrySet() {
		return this.backingMap.entrySet();
	}
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		for (String key : backingMap.keySet())
			result.append(getHeader(key)).append("\r\n");
		return result.toString();
	}
}
