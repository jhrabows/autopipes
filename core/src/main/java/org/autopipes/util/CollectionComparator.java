package org.autopipes.util;

import java.util.Collection;
import java.util.Comparator;

public class CollectionComparator<T> implements Comparator<Collection<T>> {
    private boolean ascending = true;
    public void setAscending(final boolean ascending){
    	this.ascending = ascending;
    }
	public int compare(Collection<T> o1, Collection<T> o2) {
		return ascending ? o1.size() - o2.size() : o2.size() - o1.size();
	}
}
