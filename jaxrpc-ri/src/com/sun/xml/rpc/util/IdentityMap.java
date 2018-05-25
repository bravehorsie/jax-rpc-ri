/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://oss.oracle.com/licenses/CDDL+GPL-1.1
 * or LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package com.sun.xml.rpc.util;

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/*
 * This class was lifted from JDK 1.4 (where it's called java.util.IdentityHashMap)
 * so that we can use it on 1.3.1.
 *
 * @author JAX-RPC RI Development Team
 */

/**
 * This class implements the <tt>Map</tt> interface with a hash table, using
 * reference-equality in place of object-equality when comparing keys (and
 * values).  In other words, in an <tt>IdentityMap</tt>, two keys
 * <tt>k1</tt> and <tt>k2</tt> are considered equal if and only if
 * <tt>(k1==k2)</tt>.  (In normal <tt>Map</tt> implementations (like
 * <tt>HashMap</tt>) two keys <tt>k1</tt> and <tt>k2</tt> are considered equal
 * if and only if <tt>(k1==null ? k2==null : k1.equals(k2))</tt>.)
 *
 * <p><b>This class is <i>not</i> a general-purpose <tt>Map</tt>
 * implementation!  While this class implements the <tt>Map</tt> interface, it
 * intentionally violates <tt>Map's</tt> general contract, which mandates the
 * use of the <tt>equals</tt> method when comparing objects.  This class is
 * designed for use only in the rare cases wherein reference-equality
 * semantics are required.</b>
 *
 * <p>A typical use of this class is <i>topology-preserving object graph
 * transformations</i>, such as serialization or deep-copying.  To perform such
 * a transformation, a program must maintain a "node table" that keeps track
 * of all the object references that have already been processed.  The node
 * table must not equate distinct objects even if they happen to be equal.
 * Another typical use of this class is to maintain <i>proxy objects</i>.  For
 * example, a debugging facility might wish to maintain a proxy object for
 * each object in the program being debugged.
 *
 * <p>This class provides all of the optional map operations, and permits
 * <tt>null</tt> values and the <tt>null</tt> key.  This class makes no
 * guarantees as to the order of the map; in particular, it does not guarantee
 * that the order will remain constant over time.
 *
 * <p>This class provides constant-time performance for the basic
 * operations (<tt>get</tt> and <tt>put</tt>), assuming the system
 * identity hash function ({@link System#identityHashCode(Object)})
 * disperses elements properly among the buckets.
 *
 * <p>This class has one tuning parameter (which affects performance but not
 * semantics): <i>expected maximum size</i>.  This parameter is the maximum
 * number of key-value mappings that the map is expected to hold.  Internally,
 * this parameter is used to determine the number of buckets initially
 * comprising the hash table.  The precise relationship between the expected
 * maximum size and the number of buckets is unspecified.
 *
 * <p>If the size of the map (the number of key-value mappings) sufficiently
 * exceeds the expected maximum size, the number of buckets is increased
 * Increasing the number of buckets ("rehashing") may be fairly expensive, so
 * it pays to create identity hash maps with a sufficiently large expected
 * maximum size.  On the other hand, iteration over collection views requires
 * time proportional to the the number of buckets in the hash table, so it
 * pays not to set the expected maximum size too high if you are especially
 * concerned with iteration performance or memory usage.
 *
 * <p><b>Note that this implementation is not synchronized.</b> If multiple
 * threads access this map concurrently, and at least one of the threads
 * modifies the map structurally, it <i>must</i> be synchronized externally.
 * (A structural modification is any operation that adds or deletes one or
 * more mappings; merely changing the value associated with a key that an
 * instance already contains is not a structural modification.)  This is
 * typically accomplished by synchronizing on some object that naturally
 * encapsulates the map.  If no such object exists, the map should be
 * "wrapped" using the <tt>Collections.synchronizedMap</tt> method.  This is
 * best done at creation time, to prevent accidental unsynchronized access to
 * the map: <pre>
 *     Map m = Collections.synchronizedMap(new HashMap(...));
 * </pre>
 *
 * <p>The iterators returned by all of this class's "collection view methods"
 * are <i>fail-fast</i>: if the map is structurally modified at any time after
 * the iterator is created, in any way except through the iterator's own
 * <tt>remove</tt> or <tt>add</tt> methods, the iterator will throw a
 * <tt>ConcurrentModificationException</tt>.  Thus, in the face of concurrent
 * modification, the iterator fails quickly and cleanly, rather than risking
 * arbitrary, non-deterministic behavior at an undetermined time in the
 * future.
 *
 * <p>Note that the fail-fast behavior of an iterator cannot be guaranteed
 * as it is, generally speaking, impossible to make any hard guarantees in the
 * presence of unsynchronized concurrent modification.  Fail-fast iterators
 * throw <tt>ConcurrentModificationException</tt> on a best-effort basis. 
 * Therefore, it would be wrong to write a program that depended on this
 * exception for its correctness: <i>fail-fast iterators should be used only
 * to detect bugs.</i>
 *
 * <p>Implementation note: This is a simple <i>linear-probe</i> hash table,
 * as described for example in texts by Sedgewick and Knuth.  The array
 * alternates holding keys and values.  (This has better locality for large
 * tables than does using separate arrays.)  For many JRE implementations
 * and operation mixes, this class will yield better performance than
 * {@link HashMap} (which uses <i>chaining</i> rather than linear-probing). 
 *
 * @see     System#identityHashCode(Object)
 * @see     Object#hashCode()
 * @see     Collection
 * @see	    Map
 * @see	    HashMap
 * @see	    TreeMap
 * @author  Doug Lea and Josh Bloch
 * @since   1.4
 */

public class IdentityMap
	extends MapBase
	implements Map, java.io.Serializable, Cloneable {
	/**
	 * The initial capacity used by the no-args constructor.
	 * MUST be a power of two.  The value 32 corresponds to the
	 * (specified) expected maximum size of 21, given a load factor
	 * of 2/3.
	 */
	private static final int DEFAULT_CAPACITY = 32;

	/**
	 * The minimum capacity, used if a lower value is implicitly specified
	 * by either of the constructors with arguments.  The value 4 corresponds
	 * to an expected maximum size of 2, given a load factor of 2/3.
	 * MUST be a power of two.
	 */
	private static final int MINIMUM_CAPACITY = 4;

	/**
	 * The maximum capacity, used if a higher value is implicitly specified
	 * by either of the constructors with arguments.
	 * MUST be a power of two <= 1<<29.
	 */
	private static final int MAXIMUM_CAPACITY = 1 << 29;

	/**
	 * The table, resized as necessary. Length MUST always be a power of two.
	 */
	private transient Object[] table;

	/**
	 * The number of key-value mappings contained in this identity hash map.
	 *
	 * @serial
	 */
	private int size;

	/**
	 * The number of modifications, to support fast-fail iterators
	 */
	private transient volatile int modCount;

	/**
	 * The next size value at which to resize (capacity * load factor).
	 */
	private transient int threshold;

	/**
	 * Value representing null keys inside tables.
	 */
	private static final Object NULL_KEY = new Object();

	/**
	 * Use NULL_KEY for key if it is null.
	 */

	private static Object maskNull(Object key) {
		return (key == null ? NULL_KEY : key);
	}

	/**
	 * Return internal representation of null key back to caller as null
	 */
	private static Object unmaskNull(Object key) {
		return (key == NULL_KEY ? null : key);
	}

	/**
	 * Constructs a new, empty identity hash map with a default expected
	 * maximum size (21).
	 */
	public IdentityMap() {
		init(DEFAULT_CAPACITY);
	}

	/**
	 * Constructs a new, empty map with the specified expected maximum size.
	 * Putting more than the expected number of key-value mappings into
	 * the map may cause the internal data structure to grow, which may be
	 * somewhat time-consuming.
	 *
	 * @param expectedMaxSize the expected maximum size of the map.
	 * @throws IllegalArgumentException if <tt>expectedMaxSize</tt> is negative
	 */
	public IdentityMap(int expectedMaxSize) {
		if (expectedMaxSize < 0)
			throw new IllegalArgumentException(
				"expectedMaxSize is negative: " + expectedMaxSize);
		init(capacity(expectedMaxSize));
	}

	/**
	 * Returns the appropriate capacity for the specified expected maximum
	 * size.  Returns the smallest power of two between MINIMUM_CAPACITY
	 * and MAXIMUM_CAPACITY, inclusive, that is greater than
	 * (3 * expectedMaxSize)/2, if such a number exists.  Otherwise
	 * returns MAXIMUM_CAPACITY.  If (3 * expectedMaxSize)/2 is negative, it
	 * is assumed that overflow has occurred, and MAXIMUM_CAPACITY is returned.
	 */
	private int capacity(int expectedMaxSize) {
		// Compute min capacity for expectedMaxSize given a load factor of 2/3
		int minCapacity = (3 * expectedMaxSize) / 2;

		// Compute the appropriate capacity
		int result;
		if (minCapacity > MAXIMUM_CAPACITY || minCapacity < 0) {
			result = MAXIMUM_CAPACITY;
		} else {
			result = MINIMUM_CAPACITY;
			while (result < minCapacity)
				result <<= 1;
		}
		return result;
	}

	/**
	 * Initialize object to be an empty map with the specified initial
	 * capacity, which is assumed to be a power of two between
	 * MINIMUM_CAPACITY and MAXIMUM_CAPACITY inclusive.
	 */
	private void init(int initCapacity) {
		// assert (initCapacity & -initCapacity) == initCapacity; // power of 2
		// assert initCapacity >= MINIMUM_CAPACITY;
		// assert initCapacity <= MAXIMUM_CAPACITY;

		threshold = (initCapacity * 2) / 3;
		table = new Object[2 * initCapacity];
	}

	/**
	 * Constructs a new identity hash map containing the keys-value mappings
	 * in the specified map.
	 *
	 * @param m the map whose mappings are to be placed into this map.
	 * @throws NullPointerException if the specified map is null.
	 */
	public IdentityMap(Map m) {
		// Allow for a bit of growth
		this((int) ((1 + m.size()) * 1.1));
		putAll(m);
	}

	/**
	 * Returns the number of key-value mappings in this identity hash map.
	 *
	 * @return the number of key-value mappings in this map.
	 */
	public int size() {
		return size;
	}

	/**
	 * Returns <tt>true</tt> if this identity hash map contains no key-value
	 * mappings.
	 *
	 * @return <tt>true</tt> if this identity hash map contains no key-value
	 *         mappings.
	 */
	public boolean isEmpty() {
		return size == 0;
	}

	/**
	 * Return index for Object x.
	 */
	private static int hash(Object x, int length) {
		int h = System.identityHashCode(x);
		// Multiply by -127, and left-shift to use least bit as part of hash
		return ((h << 1) - (h << 8)) & (length - 1);
	}

	/**
	 * Circularly traverse table of size len.
	 **/
	private static int nextKeyIndex(int i, int len) {
		return (i + 2 < len ? i + 2 : 0);
	}

	/**
	 * Returns the value to which the specified key is mapped in this identity
	 * hash map, or <tt>null</tt> if the map contains no mapping for
	 * this key.  A return value of <tt>null</tt> does not <i>necessarily</i>
	 * indicate that the map contains no mapping for the key; it is also
	 * possible that the map explicitly maps the key to <tt>null</tt>. The
	 * <tt>containsKey</tt> method may be used to distinguish these two
	 * cases.
	 *
	 * @param   key the key whose associated value is to be returned.
	 * @return  the value to which this map maps the specified key, or
	 *          <tt>null</tt> if the map contains no mapping for this key.
	 * @see #put(Object, Object)
	 */
	public Object get(Object key) {
		Object k = maskNull(key);
		Object[] tab = table;
		int len = tab.length;
		int i = hash(k, len);
		while (true) {
			Object item = tab[i];
			if (item == k)
				return tab[i + 1];
			if (item == null)
				return item;
			i = nextKeyIndex(i, len);
		}
	}

	/**
	 * Tests whether the specified object reference is a key in this identity
	 * hash map.
	 * 
	 * @param   key   possible key.
	 * @return  <code>true</code> if the specified object reference is a key
	 *          in this map. 
	 * @see     #containsValue(Object)
	 */
	public boolean containsKey(Object key) {
		Object k = maskNull(key);
		Object[] tab = table;
		int len = tab.length;
		int i = hash(k, len);
		while (true) {
			Object item = tab[i];
			if (item == k)
				return true;
			if (item == null)
				return false;
			i = nextKeyIndex(i, len);
		}
	}

	/**
	 * Tests whether the specified object reference is a value in this identity
	 * hash map.
	 *
	 * @param value value whose presence in this map is to be tested.
	 * @return <tt>true</tt> if this map maps one or more keys to the
	 *         specified object reference.
	 * @see     #containsKey(Object)
	 */
	public boolean containsValue(Object value) {
		Object[] tab = table;
		for (int i = 1; i < tab.length; i += 2)
			if (tab[i] == value)
				return true;

		return false;
	}

	/**
	 * Tests if the specified key-value mapping is in the map.
	 * 
	 * @param   key   possible key.
	 * @param   value possible value.
	 * @return  <code>true</code> if and only if the specified key-value
	 *          mapping is in map.
	 */
	private boolean containsMapping(Object key, Object value) {
		Object k = maskNull(key);
		Object[] tab = table;
		int len = tab.length;
		int i = hash(k, len);
		while (true) {
			Object item = tab[i];
			if (item == k)
				return tab[i + 1] == value;
			if (item == null)
				return false;
			i = nextKeyIndex(i, len);
		}
	}

	/**
	 * Associates the specified value with the specified key in this identity
	 * hash map.  If the map previously contained a mapping for this key, the
	 * old value is replaced.
	 *
	 * @param key the key with which the specified value is to be associated.
	 * @param value the value to be associated with the specified key.
	 * @return the previous value associated with <tt>key</tt>, or
	 *	       <tt>null</tt> if there was no mapping for <tt>key</tt>.  (A
	 *         <tt>null</tt> return can also indicate that the map previously
	 *         associated <tt>null</tt> with the specified key.)
	 * @see     Object#equals(Object)
	 * @see     #get(Object)
	 * @see     #containsKey(Object)
	 */
	public Object put(Object key, Object value) {
		Object k = maskNull(key);
		Object[] tab = table;
		int len = tab.length;
		int i = hash(k, len);

		Object item;
		while ((item = tab[i]) != null) {
			if (item == k) {
				Object oldValue = tab[i + 1];
				tab[i + 1] = value;
				return oldValue;
			}
			i = nextKeyIndex(i, len);
		}

		modCount++;
		tab[i] = k;
		tab[i + 1] = value;
		if (++size >= threshold)
			resize(len); // len == 2 * current capacity.
		return null;
	}

	/**
	 * Resize the table to hold given capacity.
	 *
	 * @param newCapacity the new capacity, must be a power of two.
	 */
	private void resize(int newCapacity) {
		// assert (newCapacity & -newCapacity) == newCapacity; // power of 2
		int newLength = newCapacity * 2;

		Object[] oldTable = table;
		int oldLength = oldTable.length;
		if (oldLength == 2 * MAXIMUM_CAPACITY) { // can't expand any further
			if (threshold == MAXIMUM_CAPACITY - 1)
				throw new IllegalStateException("Capacity exhausted.");
			threshold = MAXIMUM_CAPACITY - 1; // Gigantic map!
			return;
		}
		if (oldLength >= newLength)
			return;

		Object[] newTable = new Object[newLength];
		threshold = newLength / 3;

		for (int j = 0; j < oldLength; j += 2) {
			Object key = oldTable[j];
			if (key != null) {
				Object value = oldTable[j + 1];
				oldTable[j] = null;
				oldTable[j + 1] = null;
				int i = hash(key, newLength);
				while (newTable[i] != null)
					i = nextKeyIndex(i, newLength);
				newTable[i] = key;
				newTable[i + 1] = value;
			}
		}
		table = newTable;
	}

	/**
	 * Copies all of the mappings from the specified map to this map
	 * These mappings will replace any mappings that
	 * this map had for any of the keys currently in the specified map.<p>
	 *
	 * @param t mappings to be stored in this map.
	 * @throws NullPointerException if the specified map is null.
	 */
	public void putAll(Map t) {
		int n = t.size();
		if (n == 0)
			return;
		if (n > threshold) // conservatively pre-expand
			resize(capacity(n));

		for (Iterator it = t.entrySet().iterator(); it.hasNext();) {
			Entry e = (Entry) it.next();
			put(e.getKey(), e.getValue());
		}
	}

	/**
	 * Removes the mapping for this key from this map if present.
	 *
	 * @param key key whose mapping is to be removed from the map.
	 * @return previous value associated with specified key, or <tt>null</tt>
	 *	       if there was no entry for key.  (A <tt>null</tt> return can
	 *	       also indicate that the map previously associated <tt>null</tt>
	 *	       with the specified key.)
	 */
	public Object remove(Object key) {
		Object k = maskNull(key);
		Object[] tab = table;
		int len = tab.length;
		int i = hash(k, len);

		while (true) {
			Object item = tab[i];
			if (item == k) {
				modCount++;
				size--;
				Object oldValue = tab[i + 1];
				tab[i + 1] = null;
				tab[i] = null;
				closeDeletion(i);
				return oldValue;
			}
			if (item == null)
				return null;
			i = nextKeyIndex(i, len);
		}

	}

	/**
	 * Removes the specified key-value mapping from the map if it is present.
	 * 
	 * @param   key   possible key.
	 * @param   value possible value.
	 * @return  <code>true</code> if and only if the specified key-value
	 *          mapping was in map.
	 */
	private boolean removeMapping(Object key, Object value) {
		Object k = maskNull(key);
		Object[] tab = table;
		int len = tab.length;
		int i = hash(k, len);

		while (true) {
			Object item = tab[i];
			if (item == k) {
				if (tab[i + 1] != value)
					return false;
				modCount++;
				size--;
				tab[i] = null;
				tab[i + 1] = null;
				closeDeletion(i);
				return true;
			}
			if (item == null)
				return false;
			i = nextKeyIndex(i, len);
		}
	}

	/**
	 * Rehash all possibly-colliding entries following a
	 * deletion. This preserves the linear-probe 
	 * collision properties required by get, put, etc.
	 *
	 * @param d the index of a newly empty deleted slot
	 */
	private void closeDeletion(int d) {
		// Adapted from Knuth Section 6.4 Algorithm R
		Object[] tab = table;
		int len = tab.length;

		// Look for items to swap into newly vacated slot
		// starting at index immediately following deletion,
		// and continuing until a null slot is seen, indicating
		// the end of a run of possibly-colliding keys.
		Object item;
		for (int i = nextKeyIndex(d, len);
			(item = tab[i]) != null;
			i = nextKeyIndex(i, len)) {
			// The following test triggers if the item at slot i (which
			// hashes to be at slot r) should take the spot vacated by d.
			// If so, we swap it in, and then continue with d now at the
			// newly vacated i.  This process will terminate when we hit
			// the null slot at the end of this run.
			// The test is messy because we are using a circular table.
			int r = hash(item, len);
			if ((i < r && (r <= d || d <= i)) || (r <= d && d <= i)) {
				tab[d] = item;
				tab[d + 1] = tab[i + 1];
				tab[i] = null;
				tab[i + 1] = null;
				d = i;
			}
		}
	}

	/**
	 * Removes all mappings from this map.
	 */
	public void clear() {
		modCount++;
		Object[] tab = table;
		for (int i = 0; i < tab.length; i++)
			tab[i] = null;
		size = 0;
	}

	/**
	 * Compares the specified object with this map for equality.  Returns
	 * <tt>true</tt> if the given object is also a map and the two maps
	 * represent identical object-reference mappings.  More formally, this
	 * map is equal to another map <tt>m</tt> if and only if
	 * map <tt>this.entrySet().equals(m.entrySet())</tt>.
	 *
	 * <p><b>Owing to the reference-equality-based semantics of this map it is
	 * possible that the symmetry and transitivity requirements of the
	 * <tt>Object.equals</tt> contract may be violated if this map is compared
	 * to a normal map.  However, the <tt>Object.equals</tt> contract is
	 * guaranteed to hold among <tt>IdentityMap</tt> instances.</b>
	 *
	 * @param  o object to be compared for equality with this map.
	 * @return <tt>true</tt> if the specified object is equal to this map.
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		} else if (o instanceof IdentityMap) {
			IdentityMap m = (IdentityMap) o;
			if (m.size() != size)
				return false;

			Object[] tab = m.table;
			for (int i = 0; i < tab.length; i += 2) {
				Object k = tab[i];
				if (k != null && !containsMapping(k, tab[i + 1]))
					return false;
			}
			return true;
		} else if (o instanceof Map) {
			Map m = (Map) o;
			return entrySet().equals(m.entrySet());
		} else {
			return false; // o is not a Map
		}
	}

	/**
	 * Returns the hash code value for this map.  The hash code of a map
	 * is defined to be the sum of the hashcode of each entry in the map's
	 * entrySet view.  This ensures that <tt>t1.equals(t2)</tt> implies
	 * that <tt>t1.hashCode()==t2.hashCode()</tt> for any two 
	 * <tt>IdentityMap</tt> instances <tt>t1</tt> and <tt>t2</tt>, as
	 * required by the general contract of {@link Object#hashCode()}.
	 *
	 * <p><b>Owing to the reference-equality-based semantics of the
	 * <tt>Map.Entry</tt> instances in the set returned by this map's
	 * <tt>entrySet</tt> method, it is possible that the contractual
	 * requirement of <tt>Object.hashCode</tt> mentioned in the previous
	 * paragraph will be violated if one of the two objects being compared is
	 * an <tt>IdentityMap</tt> instance and the other is a normal map.</b>
	 *
	 * @return the hash code value for this map.
	 * @see Object#hashCode()
	 * @see Object#equals(Object)
	 * @see #equals(Object)
	 */
	public int hashCode() {
		int result = 0;
		Object[] tab = table;
		for (int i = 0; i < tab.length; i += 2) {
			Object key = tab[i];
			if (key != null) {
				Object k = unmaskNull(key);
				result += System.identityHashCode(k)
					^ System.identityHashCode(tab[i + 1]);
			}
		}
		return result;
	}

	/**
	 * Returns a shallow copy of this identity hash map: the keys and values
	 * themselves are not cloned.
	 *
	 * @return a shallow copy of this map.
	 */
	public Object clone() {
		try {
			IdentityMap t = (IdentityMap) super.clone();
			t.entrySet = null;
			t.table = (Object[]) (table.clone());
			return t;
		} catch (CloneNotSupportedException e) {
			throw new InternalError();
		}
	}

	private abstract class IdentityMapIterator implements Iterator {
		int index = (size != 0 ? 0 : table.length); // current slot. 
		int expectedModCount = modCount; // to support fast-fail
		int lastReturnedIndex = -1; // to allow remove()
		boolean indexValid; // To avoid unecessary next computation
		Object[] traversalTable = table; // reference to main table or copy

		public boolean hasNext() {
			Object[] tab = traversalTable;
			for (int i = index; i < tab.length; i += 2) {
				Object key = tab[i];
				if (key != null) {
					index = i;
					return indexValid = true;
				}
			}
			index = tab.length;
			return false;
		}

		protected int nextIndex() {
			if (modCount != expectedModCount)
				throw new ConcurrentModificationException();
			if (!indexValid && !hasNext())
				throw new NoSuchElementException();

			indexValid = false;
			lastReturnedIndex = index;
			index += 2;
			return lastReturnedIndex;
		}

		public void remove() {
			if (lastReturnedIndex == -1)
				throw new IllegalStateException();
			if (modCount != expectedModCount)
				throw new ConcurrentModificationException();

			expectedModCount = ++modCount;
			int deletedSlot = lastReturnedIndex;
			lastReturnedIndex = -1;
			size--;
			// back up index to revisit new contents after deletion
			index = deletedSlot;
			indexValid = false;

			// Removal code proceeds as in closeDeletion except that
			// it must catch the rare case where an element already
			// seen is swapped into a vacant slot that will be later
			// traversed by this iterator. We cannot allow future
			// next() calls to return it again.  The likelihood of
			// this occurring under 2/3 load factor is very slim, but
			// when it does happen, we must make a copy of the rest of
			// the table to use for the rest of the traversal. Since
			// this can only happen when we are near the end of the table,
			// even in these rare cases, this is not very expensive in
			// time or space.

			Object[] tab = traversalTable;
			int len = tab.length;

			int d = deletedSlot;
			Object key = tab[d];
			tab[d] = null; // vacate the slot
			tab[d + 1] = null;

			// If traversing a copy, remove in real table.
			// We can skip gap-closure on copy.
			if (tab != IdentityMap.this.table) {
				IdentityMap.this.remove(key);
				expectedModCount = modCount;
				return;
			}

			Object item;
			for (int i = nextKeyIndex(d, len);
				(item = tab[i]) != null;
				i = nextKeyIndex(i, len)) {
				int r = hash(item, len);
				// See closeDeletion for explanation of this conditional
				if ((i < r && (r <= d || d <= i)) || (r <= d && d <= i)) {

					// If we are about to swap an already-seen element
					// into a slot that may later be returned by next(),
					// then clone the rest of table for use in future
					// next() calls. It is OK that our copy will have
					// a gap in the "wrong" place, since it will never
					// be used for searching anyway.

					if (i < deletedSlot
						&& d >= deletedSlot
						&& traversalTable == IdentityMap.this.table) {
						int remaining = len - deletedSlot;
						Object[] newTable = new Object[remaining];
						System.arraycopy(
							tab,
							deletedSlot,
							newTable,
							0,
							remaining);
						traversalTable = newTable;
						index = 0;
					}

					tab[d] = item;
					tab[d + 1] = tab[i + 1];
					tab[i] = null;
					tab[i + 1] = null;
					d = i;
				}
			}
		}
	}

	private class KeyIterator extends IdentityMapIterator {
		public Object next() {
			return unmaskNull(traversalTable[nextIndex()]);
		}
	}

	private class ValueIterator extends IdentityMapIterator {
		public Object next() {
			return traversalTable[nextIndex() + 1];
		}
	}

	/**
	 * Since we don't use Entry objects, we use the Iterator
	 * itself as an entry.
	 */
	private class EntryIterator
		extends IdentityMapIterator
		implements Map.Entry {
		public Object next() {
			nextIndex();
			return this;
		}

		public Object getKey() {
			// Provide a better exception than out of bounds index
			if (lastReturnedIndex < 0)
				throw new IllegalStateException("Entry was removed");

			return unmaskNull(traversalTable[lastReturnedIndex]);
		}

		public Object getValue() {
			// Provide a better exception than out of bounds index
			if (lastReturnedIndex < 0)
				throw new IllegalStateException("Entry was removed");

			return traversalTable[lastReturnedIndex + 1];
		}

		public Object setValue(Object value) {
			// It would be mean-spirited to proceed here if remove() called 
			if (lastReturnedIndex < 0)
				throw new IllegalStateException("Entry was removed");
			Object oldValue = traversalTable[lastReturnedIndex + 1];
			traversalTable[lastReturnedIndex + 1] = value;
			// if shadowing, force into main table
			if (traversalTable != IdentityMap.this.table)
				put(traversalTable[lastReturnedIndex], value);
			return oldValue;
		}

		public boolean equals(Object o) {
			if (!(o instanceof Map.Entry))
				return false;
			Map.Entry e = (Map.Entry) o;
			return e.getKey() == getKey() && e.getValue() == getValue();
		}

		public int hashCode() {
			return System.identityHashCode(getKey())
				^ System.identityHashCode(getValue());
		}

		public String toString() {
			return getKey() + "=" + getValue();
		}
	}

	// Views

	/**
	 * This field is initialized to contain an instance of the entry set
	 * view the first time this view is requested.  The view is stateless,
	 * so there's no reason to create more than one.
	 */

	private transient Set entrySet = null;

	/**
	 * Returns an identity-based set view of the keys contained in this map.
	 * The set is backed by the map, so changes to the map are reflected in
	 * the set, and vice-versa.  If the map is modified while an iteration
	 * over the set is in progress, the results of the iteration are
	 * undefined.  The set supports element removal, which removes the
	 * corresponding mapping from the map, via the <tt>Iterator.remove</tt>,
	 * <tt>Set.remove</tt>, <tt>removeAll</tt> <tt>retainAll</tt>, and
	 * <tt>clear</tt> methods.  It does not support the <tt>add</tt> or
	 * <tt>addAll</tt> methods.
	 *
	 * <p><b>While the object returned by this method implements the
	 * <tt>Set</tt> interface, it does <i>not</i> obey <tt>Set's</tt> general
	 * contract.  Like its backing map, the set returned by this method
	 * defines element equality as reference-equality rather than
	 * object-equality.  This affects the behavior of its <tt>contains</tt>,
	 * <tt>remove</tt>, <tt>containsAll</tt>, <tt>equals</tt>, and
	 * <tt>hashCode</tt> methods.</b>
	 *
	 * <p>The <tt>equals</tt> method of the returned set returns <tt>true</tt>
	 * only if the specified object is a set containing exactly the same
	 * object references as the returned set.  The symmetry and transitivity
	 * requirements of the <tt>Object.equals</tt> contract may be violated if
	 * the set returned by this method is compared to a normal set.  However,
	 * the <tt>Object.equals</tt> contract is guaranteed to hold among sets
	 * returned by this method.</b>
	 *
	 * <p>The <tt>hashCode</tt> method of the returned set returns the sum of
	 * the <i>identity hashcodes</i> of the elements in the set, rather than
	 * the sum of their hashcodes.  This is mandated by the change in the
	 * semantics of the <tt>equals</tt> method, in order to enforce the
	 * general contract of the <tt>Object.hashCode</tt> method among sets
	 * returned by this method.
	 *
	 * @return an identity-based set view of the keys contained in this map.
	 * @see Object#equals(Object)
	 * @see System#identityHashCode(Object)
	 */
	public Set keySet() {
		Set ks = keySet;
		if (ks != null)
			return ks;
		else
			return keySet = new KeySet();
	}

	private class KeySet extends AbstractSet {
		public Iterator iterator() {
			return new KeyIterator();
		}
		public int size() {
			return size;
		}
		public boolean contains(Object o) {
			return containsKey(o);
		}
		public boolean remove(Object o) {
			int oldSize = size;
			IdentityMap.this.remove(o);
			return size != oldSize;
		}
		/*
		 * Must revert from AbstractSet's impl to AbstractCollection's, as
		 * the former contains an optimization that results in incorrect
		 * behavior when c is a smaller "normal" (non-identity-based) Set.
		 */
		public boolean removeAll(Collection c) {
			boolean modified = false;
			for (Iterator i = iterator(); i.hasNext();) {
				if (c.contains(i.next())) {
					i.remove();
					modified = true;
				}
			}
			return modified;
		}
		public void clear() {
			IdentityMap.this.clear();
		}
		public int hashCode() {
			int result = 0;
			for (Iterator i = iterator(); i.hasNext();)
				result += System.identityHashCode(i.next());
			return result;
		}
	}

	/**
	 * <p>Returns a collection view of the values contained in this map.  The
	 * collection is backed by the map, so changes to the map are reflected in
	 * the collection, and vice-versa.  If the map is modified while an
	 * iteration over the collection is in progress, the results of the
	 * iteration are undefined.  The collection supports element removal,
	 * which removes the corresponding mapping from the map, via the
	 * <tt>Iterator.remove</tt>, <tt>Collection.remove</tt>,
	 * <tt>removeAll</tt>, <tt>retainAll</tt> and <tt>clear</tt> methods.
	 * It does not support the <tt>add</tt> or <tt>addAll</tt> methods.
	 *
	 * <p><b>While the object returned by this method implements the
	 * <tt>Collection</tt> interface, it does <i>not</i> obey
	 * <tt>Collection's</tt> general contract.  Like its backing map,
	 * the collection returned by this method defines element equality as
	 * reference-equality rather than object-equality.  This affects the
	 * behavior of its <tt>contains</tt>, <tt>remove</tt> and
	 * <tt>containsAll</tt> methods.</b>
	 *
	 * @return a collection view of the values contained in this map.
	 */
	public Collection values() {
		Collection vs = values;
		if (vs != null)
			return vs;
		else
			return values = new Values();
	}

	private class Values extends AbstractCollection {
		public Iterator iterator() {
			return new ValueIterator();
		}
		public int size() {
			return size;
		}
		public boolean contains(Object o) {
			return containsValue(o);
		}
		public boolean remove(Object o) {
			for (Iterator i = iterator(); i.hasNext();) {
				if (i.next() == o) {
					i.remove();
					return true;
				}
			}
			return false;
		}
		public void clear() {
			IdentityMap.this.clear();
		}
	}

	/**
	 * Returns a set view of the mappings contained in this map.  Each element
	 * in the returned set is a reference-equality-based <tt>Map.Entry</tt>.
	 * The set is backed by the map, so changes to the map are reflected in
	 * the set, and vice-versa.  If the map is modified while an iteration
	 * over the set is in progress, the results of the iteration are
	 * undefined.  The set supports element removal, which removes the
	 * corresponding mapping from the map, via the <tt>Iterator.remove</tt>,
	 * <tt>Set.remove</tt>, <tt>removeAll</tt>, <tt>retainAll</tt> and
	 * <tt>clear</tt> methods.  It does not support the <tt>add</tt> or
	 * <tt>addAll</tt> methods.
	 *
	 * <p>Like the backing map, the <tt>Map.Entry</tt> objects in the set
	 * returned by this method define key and value equality as
	 * reference-equality rather than object-equality.  This affects the
	 * behavior of the <tt>equals</tt> and <tt>hashCode</tt> methods of these
	 * <tt>Map.Entry</tt> objects.  A reference-equality based <tt>Map.Entry
	 * e</tt> is equal to an object <tt>o</tt> if and only if <tt>o</tt> is a
	 * <tt>Map.Entry</tt> and <tt>e.getKey()==o.getKey() &&
	 * e.getValue()==o.getValue()</tt>.  To accommodate these equals
	 * semantics, the <tt>hashCode</tt> method returns
	 * <tt>System.identityHashCode(e.getKey()) ^
	 * System.identityHashCode(e.getValue())</tt>.
	 *
	 * <p><b>Owing to the reference-equality-based semantics of the
	 * <tt>Map.Entry</tt> instances in the set returned by this method,
	 * it is possible that the symmetry and transitivity requirements of
	 * the {@link Object#equals(Object)} contract may be violated if any of
	 * the entries in the set is compared to a normal map entry, or if
	 * the set returned by this method is compared to a set of normal map
	 * entries (such as would be returned by a call to this method on a normal
	 * map).  However, the <tt>Object.equals</tt> contract is guaranteed to
	 * hold among identity-based map entries, and among sets of such entries.
	 * </b>
	 *
	 * @return a set view of the identity-mappings contained in this map.
	 */
	public Set entrySet() {
		Set es = entrySet;
		if (es != null)
			return es;
		else
			return entrySet = new EntrySet();
	}

	private class EntrySet extends AbstractSet {
		public Iterator iterator() {
			return new EntryIterator();
		}
		public boolean contains(Object o) {
			if (!(o instanceof Map.Entry))
				return false;
			Map.Entry entry = (Map.Entry) o;
			return containsMapping(entry.getKey(), entry.getValue());
		}
		public boolean remove(Object o) {
			if (!(o instanceof Map.Entry))
				return false;
			Map.Entry entry = (Map.Entry) o;
			return removeMapping(entry.getKey(), entry.getValue());
		}
		public int size() {
			return size;
		}
		public void clear() {
			IdentityMap.this.clear();
		}
		/*
		 * Must revert from AbstractSet's impl to AbstractCollection's, as
		 * the former contains an optimization that results in incorrect
		 * behavior when c is a smaller "normal" (non-identity-based) Set.
		 */
		public boolean removeAll(Collection c) {
			boolean modified = false;
			for (Iterator i = iterator(); i.hasNext();) {
				if (c.contains(i.next())) {
					i.remove();
					modified = true;
				}
			}
			return modified;
		}

		public Object[] toArray() {
			Collection c = new ArrayList(size());
			for (Iterator i = iterator(); i.hasNext();)
				c.add(new MapBase.SimpleEntry((Map.Entry) i.next()));
			return c.toArray();
		}
		public Object[] toArray(Object a[]) {
			Collection c = new ArrayList(size());
			for (Iterator i = iterator(); i.hasNext();)
				c.add(new MapBase.SimpleEntry((Map.Entry) i.next()));
			return c.toArray(a);
		}

	}

	/**
	 * Save the state of the <tt>IdentityMap</tt> instance to a stream
	 * (i.e., serialize it).
	 *
	 * @serialData The <i>size</i> of the HashMap (the number of key-value
	 *	        mappings) (<tt>int</tt>), followed by the key (Object) and
	 *          value (Object) for each key-value mapping represented by the
	 *          IdentityMap.  The key-value mappings are emitted in no
	 *          particular order.
	 */
	private void writeObject(java.io.ObjectOutputStream s)
		throws java.io.IOException {
		// Write out and any hidden stuff
		s.defaultWriteObject();

		// Write out size (number of Mappings)
		s.writeInt(size);

		// Write out keys and values (alternating)
		Object[] tab = table;
		for (int i = 0; i < tab.length; i += 2) {
			Object key = tab[i];
			if (key != null) {
				s.writeObject(unmaskNull(key));
				s.writeObject(tab[i + 1]);
			}
		}
	}

	/**
	 * Reconstitute the <tt>IdentityMap</tt> instance from a stream (i.e.,
	 * deserialize it).
	 */
	private void readObject(java.io.ObjectInputStream s)
		throws java.io.IOException, ClassNotFoundException {
		// Read in any hidden stuff
		s.defaultReadObject();

		// Read in size (number of Mappings)
		int size = s.readInt();

		// Allow for 33% growth (i.e., capacity is >= 2* size()).
		init(capacity((size * 4) / 3));

		// Read the keys and values, and put the mappings in the table
		for (int i = 0; i < size; i++) {
			Object key = s.readObject();
			Object value = s.readObject();
			put(key, value);
		}
	}
}
