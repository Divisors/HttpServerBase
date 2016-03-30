package com.divisors.projectcuttlefish.contentmanager.api.plugins;

import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

import org.javatuples.Pair;

import com.divisors.projectcuttlefish.httpserver.api.NeedsTesting;

/**
 * Class for permissions. Permission names are in the form of <code>foo.bar.baz</code>.
 * @author mailmindlin
 */
public class Permissions {
	/**
	 * Root node
	 */
	protected final PermissionNode root = new PermissionMiddleNode(null, PermissionValue.UNSET);
	
	public boolean setPermission(String permission, PermissionValue value) {
		return setPermission(Arrays.asList(permission.split("\\.")), value);
	}
	public boolean setPermission(List<String> segments, PermissionValue value) {
		Pair<PermissionNode, Integer> end = root.seekEndOfPath(segments.listIterator());
		int endDist = end.getValue1();
		PermissionNode endNode = end.getValue0();
		//TODO finish
		return true;
	}
	/**
	 * Permission node
	 * @author mailmindlin
	 */
	protected static interface PermissionNode {
		String getName();
		abstract PermissionValue hasPermission(ListIterator<String> nodes);
		PermissionValue getValue();
		void setValue(PermissionValue value);
		PermissionNode addChild(PermissionNode child);
		default Pair<PermissionNode, Integer> seekEndOfPath(ListIterator<String> nodes) {
			return this.seekEndOfPath(nodes, 0);
		}
		Pair<PermissionNode, Integer> seekEndOfPath(ListIterator<String> nodes, int steps);
		boolean isMutable();
	}
	
	/**
	 * 
	 * @author mailmindlin
	 *
	 */
	protected static class PermissionEndNode implements PermissionNode {
		protected final String name;
		protected PermissionValue value;
		public PermissionEndNode(String name, PermissionValue value) {
			this.name = name;
			this.value = value;
		}
		@Override
		public String getName() {
			return this.name;
		}

		@Override
		public PermissionValue hasPermission(ListIterator<String> nodes) {
			// TODO Auto-generated method stub
			return PermissionValue.UNSET;
		}

		@Override
		public PermissionValue getValue() {
			return this.value;
		}

		@Override
		public PermissionNode addChild(PermissionNode child) {
			return null;
		}
		@Override
		public boolean isMutable() {
			return false;
		}
		@Override
		public void setValue(PermissionValue value) {
			this.value = value;
		}
		@Override
		public Pair<PermissionNode, Integer> seekEndOfPath(ListIterator<String> nodes, int steps) {
			return Pair.with(this, steps);
		}
	}
	protected static class PermissionMiddleNode implements PermissionNode {
		protected final String name;
		protected PermissionValue value;
		protected boolean matchAll = false;
		PermissionNode[] children = new PermissionNode[0];
		public PermissionMiddleNode(String name, PermissionValue value) {
			this.name = name;
			this.value = value;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public PermissionValue hasPermission(ListIterator<String> nodes) {
			if (!nodes.hasNext())
				return this.value;
			if (this.children.length == 0)
				return PermissionValue.UNSET;
			String nextNode = nodes.next();
			//binary search
			int max = this.children.length - 1, min = 0;
			while (max - min > 1) {
				int test = (max - min)/2 + min;
				String otherNode = children[test].getName();
				int cmp = nextNode.compareTo(otherNode);
				if (cmp > 0)
					max = test;
				else if (cmp < 0)
					min = test;
				else if (cmp == 0)
					return children[test].hasPermission(nodes); 
			}
			if (matchAll)
				return this.value;
			return PermissionValue.UNSET;
		}

		@Override
		public PermissionValue getValue() {
			return value;
		}

		@Override
		public PermissionNode addChild(PermissionNode child) {
			int max = children.length - 1, min = 0;
			while (max - min > 1) {
				int test = (max - min)/2 + min;
				String otherNode = children[test].getName();
				int cmp = child.getName().compareTo(otherNode);
				if (cmp > 0)
					max = test;
				else if (cmp < 0)
					min = test;
				else if (cmp == 0) {
					//TODO merge with duplicate names
					return this;
				}
			}
			synchronized (this) {
				PermissionNode[] newChildren = new PermissionNode[children.length + 1];
				//TODO check that the math is right
				System.arraycopy(this.children, 0, newChildren, 0, min);
				newChildren[min] = child;
				System.arraycopy(this.children, min, newChildren, min + 1, this.children.length - min);
				this.children = newChildren;
			}
			return this;
		}

		@Override
		public boolean isMutable() {
			return true;
		}
		
		public PermissionMiddleNode setWildcard(boolean aflag) {
			this.matchAll = aflag;
			return this;
		}

		@Override
		public void setValue(PermissionValue value) {
			this.value = value;
		}

		@Override
		public Pair<PermissionNode, Integer> seekEndOfPath(ListIterator<String> nodes, int steps) {
			if (nodes.hasNext() && this.children.length > 0) {
				String nextNode = nodes.next();
				nextNode.compareTo(null);
				//binary search
				int max = this.children.length - 1, min = 0;
				while (max - min > 1) {
					int test = (max - min)/2 + min;
					String otherNode = children[test].getName();
					int cmp = nextNode.compareTo(otherNode);
					if (cmp > 0)
						max = test;
					else if (cmp < 0)
						min = test;
					else if (cmp == 0)
						return children[test].seekEndOfPath(nodes, steps + 1);
				}
			}
			return Pair.with(this, steps);
		}
	}
	public static enum PermissionValue {
		ALLOWED,
		BLOCKED,
		UNSET;
	}
}
