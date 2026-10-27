//import model.CryptogramModel;

import java.io.FileNotFoundException;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;

//import controller.CryptogramController;
/**
 * 
 * Name: Samyak Gurung
 * Description: 
 * - User Interface and interaction are all done here.
 * - Gets input from user
 * - Sends the data to Crypto model and container or user thier functions
 * - Presents the current status of the users interaction
 * @param <K> the type of keys
 * @param <V> the type of mapped values
 * 
 */
public class ArrayMap<K, V> extends AbstractMap<K,V>{
	private Object[] keys, vals;
	private int size, maxSize;
	public ArrayMap(){
		this.keys = new Object[10];
		this.vals = new Object[10];
		this.size = 0;
		this.maxSize = 10;
	}
	/**
	 * This method adds key and value to your map. 
	 * If key already exists, the new value replaces the old one, 
	 * and the old one is returned.
	 * @param K key
	 * @param V Value
	 * @return Value
	 */
	@Override
	public V put(K key, V value) {
		if (this.size == this.maxSize) {
			this.resize();
		}
		for (int i = 0; i<size; i++) {
			if (Objects.equals(keys[i], key)) {
				V oldVal = (V) vals[i];
				vals[i] = value;
				return oldVal;
			}
		}
		keys[size] = key;
		vals[size] = value;
		size++;
		return null;
	}
	
	/**
	 * Returns value
	 * @param Objects
	 * @return values related to list or null
	 */
	@Override
	public V get(Object key) {
		for (int i = 0; i< size; i++) {
			if (Objects.equals(keys[i], key)) {
				return (V) vals[i];
			}
		}
		return null;
	}
	/**
	 * This method returns the number of mappings that the object contains.
	 * @return integer size
	 */
	@Override
	public int size() {
		return size;
	}
	
	@Override
	public Set<Entry<K, V>> entrySet(){
		return new ArrayMapEntrySet();
	}
	/**
	 * resize():
	 * Increases the size of the Arrays
	 */
	public void resize() {
		int newCap = keys.length *2;
		Object[] newKeys = new Object[newCap];
		Object[] newVals = new Object[newCap];
		for (int i = 0; i< size; i++) {
			newKeys[i] = keys[i];
			newVals[i] = vals[i];
		}
		this.keys = newKeys;
		this.vals = newVals;
		this.maxSize = newCap;

	}
	/**
	 * Returns a Set of key, value pairs contained in an Entry object. 
	 * The AbstractMap class provides a concrete SimpleEntry class that we can use to hold them.
	 */
	private class ArrayMapEntrySet extends AbstractSet<Entry<K,V>>{
		
		/**
		 * This method returns the size of the set (and of the Map).
		 * @return integer size
		 */
		@Override
		public int size() {
			return size;
		}
		
		/**
		 * This method should return true if the Set contains an Entry equal to the the one represented
		 * by the parameter. If o is not an Entry, this is trivially false. If it is, validate that the 
		 * key and the value are actually part of the Map.
		 * @param object to be checkedd
		 * @return boolean if it exists
		 */
		@Override
		public boolean contains(Object o) {
			if (!(o instanceof Entry<?, ?> t)) {
				return false;
			}
			for (int i = 0; i < size; i++) {
				if (Objects.equals(keys[i], t.getKey()) && 
					Objects.equals(vals[i], t.getValue())){
					return true;
				}
			} 
			return false;
		}
		
		/**
		 * This returns an iterator that walks over the Set of Entries in the Map. 
		 * This iterator will also be implemented as a private inner class:
		 * @return ArrayMap entry set iterator
		 */
		@Override
		public Iterator<Entry<K,V>> iterator() {
			return new ArrayMapEntrySetIterator();
		}
	}
	/**
	 * This returns an iterator that walks over the Set of Entries in the Map. 
	 * This iterator will also be implemented as a private inner class:
	 * @param <T>
	 */
	private class ArrayMapEntrySetIterator implements Iterator<Entry<K, V>>{
		private int cur = 0;
		private int lastRet = -1;
		/**
		 *  Returns true if there are more items in the Set of Entries being iterated over.
		 */
		@Override
		public boolean hasNext() {
			return cur < size;
		}
		
		/**
		 * Checks if the Set 
		 * @return true if there are more items in the Set of Entries being iterated over.
		 */
		@Override
		public Entry<K, V> next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			lastRet = cur;
			Entry<K, V> entry = new SimpleEntry<>((K) keys[cur], (V) vals[cur]);
			cur++;
			return entry;
		}
		
		/**
		 * Removes from the underlying collection the last 
		 * element returned by this iterator (optional operation). 
		 * This method can be called only once per call to next(). The behavior of
		 * an iterator is unspecified if the underlying collection is modified while the
		 * iteration is in progress in any way other than by calling this method.
		 *  
		 * @throws IllegalStateException - if the next method has not yet been called, or the remove method has already been called after the last call to the next method
		 */
		@Override
		public void remove() {
			if (lastRet < 0) {
				throw new IllegalStateException();
			}
			
			for (int i= lastRet; i < size-1; i++){
				keys[i] = keys[i+1];
				vals[i] = vals[i+1];
			}
			keys[size - 1] = null;
			vals[size - 1] = null;
			size--;
			cur = lastRet;
			lastRet = -1;
		}
		
	}
	public static void main(String[] args) throws FileNotFoundException {
		
	}
	

}

