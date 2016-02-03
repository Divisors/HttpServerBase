package com.divisors.projectcuttlefish.httpserver.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Some nice constants.
 * @author mailmindlin
 */
public class Constants {
	/**
	 * Constant HTTP newline bytes, for hardcoding.
	 * @see #HTTP_NEWLINE_CHARS
	 */
	public static final byte[] HTTP_NEWLINE = new byte[]{'\r','\n'};
	/**
	 * Constant HTTP newline characters, for hardcoding.
	 * @see #HTTP_NEWLINE
	 */
	public static final char[] HTTP_NEWLINE_CHARS = new char[]{'\r','\n'};
	/**
	 * An identity function. Any input is returned, unmodified.
	 */
	public static final Function<?, ?> IDENTITY_FN = (x) -> (x);
	/**
	 * An empty set. This set is empty and unmodifiable. Whenever you're like, "well, I want to return
	 * null, because it doesn't make sense to make a new set to just return empty, but then returning null
	 * might make some exceptions, or I want to have returning null mean something else", this is the set
	 * for you.
	 * @see #EMPTY_MAP
	 * @author mailmindlin
	 */
	public static final Set<?> EMPTY_SET = new Set<Object>() {
		@Override
		public int size() {
			return 0;
		}
		@Override
		public boolean isEmpty() {
			return true;
		}
		@Override
		public boolean contains(Object o) {
			return false;
		}
		@Override
		public Iterator<Object> iterator() {
			return new Iterator<Object>() {
				@Override
				public boolean hasNext() {
					return false;
				}
				@Override
				public Object next() {
					return null;
				}
			};
		}
		@Override
		public Object[] toArray() {
			return new Object[0];
		}
		@Override
		@SuppressWarnings("unchecked")
		public Object[] toArray(Object[] a) {
			return a;
		}
		@Override
		public boolean add(Object e) {
			return false;
		}
		@Override
		public boolean remove(Object o) {
			return false;
		}
		@Override
		public boolean containsAll(Collection<?> c) {
			return c.isEmpty();
		}
		@Override
		public boolean addAll(Collection<?> c) {
			return false;
		}
		@Override
		public boolean retainAll(Collection<?> c) {
			return false;
		}
		@Override
		public boolean removeAll(Collection<?> c) {
			return false;
		}
		@Override
		public void clear() {
		}
	};
	/**
	 * An empty map. This map is not <i>just</i> empty, it's like a <b>black hole</b> of emptiness.
	 * The general idea is that you might not want to make new maps and take up RAM if you know that they
	 * will:
	 * <ol type="a">
	 *   <li>Start out empty</li>
	 *   <li>Remain empty</li>
	 *   <li>Never need to hold anything, ever</li>
	 * </ol>
	 * <p>
	 * Any calls to this map will do nothing, and trying to read from it will return null. The idea is, that
	 * sometimes, you want to have a map without anything in it, but not have it fillable, or trigger
	 * {@link NullPointerException}s when returned from a method. So this is a solution.
	 * </p>
	 * @see #EMPTY_SET
	 * @author mailmindlin
	 */
	public static final Map<?, ?> EMPTY_MAP = new Map<Object, Object>() {
		@Override
		public int size() {
			return 0;
		}
		@Override
		public boolean isEmpty() {
			return true;
		}
		@Override
		public boolean containsKey(Object key) {
			return false;
		}
		@Override
		public boolean containsValue(Object value) {
			return false;
		}
		@Override
		public Object get(Object key) {
			return null;
		}
		@Override
		public Object put(Object key, Object value) {
			return null;
		}
		@Override
		public Object remove(Object key) {
			return null;
		}
		@Override
		public void putAll(Map<?, ?> m) {
		}
		@Override
		public void clear() {
		}
		@Override
		@SuppressWarnings("unchecked")
		public Set<Object> keySet() {
			return (Set<Object>) EMPTY_SET;
		}
		@Override
		@SuppressWarnings("unchecked")
		public Collection<Object> values() {
			return (Collection<Object>) EMPTY_SET;
		}
		@Override
		@SuppressWarnings("unchecked")
		public Set<Map.Entry<Object, Object>> entrySet() {
			return (Set<Map.Entry<Object, Object>>) EMPTY_SET;
		}
	};
}
