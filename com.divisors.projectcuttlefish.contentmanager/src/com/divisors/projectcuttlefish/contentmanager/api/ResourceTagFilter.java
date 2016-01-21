package com.divisors.projectcuttlefish.contentmanager.api;

import java.util.function.Predicate;

@FunctionalInterface
public interface ResourceTagFilter extends Predicate<ResourceTag> {
	/**
	 * Filters out resource tags that have compression levels less than a certain value
	 * @author mailmindlin
	 */
	public class ResourceTagCmpGTEFilter implements ResourceTagFilter {
		protected final int minCmp;
		public ResourceTagCmpGTEFilter(int minCmp) {
			this.minCmp = minCmp;
		}
		@Override
		public boolean test(ResourceTag t) {
			return t.getCompression() >= this.minCmp;
		}
	}
}
