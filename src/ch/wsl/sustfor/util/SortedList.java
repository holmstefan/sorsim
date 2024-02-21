/*******************************************************************************
 * Copyright 2024 Stefan Holm
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package ch.wsl.sustfor.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import ch.wsl.sustfor.lang.SorSimLanguageManager;


/**
 * @author Stefan Holm
 *
 * @param <E> Class of the elements
 * @param <V> Class of the values to be compared (must implement Comparable-Interface)
 */
public abstract class SortedList<E, V extends Comparable<V>> implements Iterable<E> {

	protected List<E> listElements = new ArrayList<>();
	protected SortOrder sortOrder = SortOrder.Descending;

	public enum SortOrder {
		Ascending,
		Descending;

		@Override
		public String toString() {
			String text = SorSimLanguageManager.getInstance().getText("SortOrder." + this.name());

			if (text == null) {
				return this.name();
			}
			return text;
		}
	}

	public void add(E element) {
		listElements.add(element);
	}

	public E get(int i) {
		return listElements.get(i);
	}

	public void removeAll() {
		listElements.clear();
	}

	public int size() {
		return listElements.size();
	}

	public void sort(){
		Collections.sort(listElements, new Comparator<E>() {
			@Override
			public int compare(E o1, E o2) {
				V val1 = getElementWert(o1);
				V val2 = getElementWert(o2);
				if (SortedList.this.sortOrder == SortOrder.Ascending) {
					return val1.compareTo(val2);
				}
				else if (SortedList.this.sortOrder == SortOrder.Descending) {
					return val2.compareTo(val1);
				}
				else {
					throw new RuntimeException("Unknown sort order: " + SortedList.this.sortOrder);			
				}
			}
		});   	
	}

	protected abstract V getElementWert(E element);

	protected SortOrder getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(SortOrder sortOrder) {
		this.sortOrder = sortOrder;
	}

	@Override
	public Iterator<E> iterator() {
		return listElements.iterator();
	}
}
