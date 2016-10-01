package com.zerofall.ezstorage.util;

import java.util.LinkedList;

/** An easier-to-construct linked list */
public class JointList<T> extends LinkedList<T> {

	public JointList<T> join(Iterable<T> add) {
		for (T a : add) {
			super.add(a);
		}
		return this;
	}

	public JointList<T> join(T... add) {
		for (T a : add) {
			super.add(a);
		}
		return this;
	}

	public JointList<T> join(T a, int amt) {
		for (int i = 0; i < amt; i++) {
			super.add(a);
		}
		return this;
	}

	public JointList<T> joinFirst(T first) {
		super.addFirst(first);
		return this;
	}

	public JointList<T> joinLast(T last) {
		super.addLast(last);
		return this;
	}

}
