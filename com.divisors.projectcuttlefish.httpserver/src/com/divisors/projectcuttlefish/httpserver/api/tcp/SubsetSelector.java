package com.divisors.projectcuttlefish.httpserver.api.tcp;

import java.util.Map;

import reactor.bus.selector.Selector;
import reactor.fn.Function;
import reactor.fn.tuple.Tuple;
import reactor.fn.tuple.TupleN;

/**
 * Selector to select from tuples or part of tuples
 * @author mailmindlin
 */
public class SubsetSelector implements Selector<Object>{
	/**
	 * Wildcard object 
	 */
	public static final Object MATCH_ANY = new Object() {
		@Override
		public boolean equals(Object other) {
			return true;
		}
	};
	public static SubsetSelector $t(Object...args) {
		return new SubsetSelector(args);
	}
	protected final Object[] args;
	public SubsetSelector (Object...args) {
		//trim the end of the array
		int size;
		for (size = args.length; size > 0; size--)
			if (args[size - 1] != MATCH_ANY)
				break;
		if (size != args.length) {
			this.args = new Object[size];
			System.arraycopy(args, 0, this.args, 0, size);
		} else {
			this.args = args;
		}
	}
	@Override
	public Function<Object, Map<String, Object>> getHeaderResolver() {
		return null;// not entirely (at all) sure what this method is for...
	}

	@Override
	public Object getObject() {
		return TupleN.of(args);
	}
	@Override
	public boolean matches(Object key) {
		if (!(key instanceof Tuple)) {
			//if this empty (matching anything) or is a size of 1, and the entry is the same as the key
			if (this.args.length == 0 || (this.args.length == 1 && this.args[0].equals(key)))
				return true;
			System.err.println("Key is not tuple: "+key.toString());
			return false;
		}
		Tuple tupl = (Tuple) key;
		if (this.args.length > tupl.size())
			return false;
		for (int i=0; i < tupl.size(); i++)
			if (!(args[i] == MATCH_ANY || args[i].equals(tupl.get(i))))
				return false;
		return true;
	}
}
