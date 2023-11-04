/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author shangyu
 */
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;

/**
 *
 * @author shangyu
 * @param <E>
 */
public class SortedArrayList<E> extends ArrayList<E> {
 
    private Comparator<E> _cmp;
    
    public SortedArrayList(Comparator<E> cmp) {
	super();
	_cmp = cmp;
    }
    
    public SortedArrayList() {
		_cmp = (E o1, E o2) -> ((Comparable<E>) o1).compareTo(o2);
    }
    
    @Override
    public boolean add(E e) {

	int j = size() - 1;

	// Start from the end, and look for the first
	// element that is smaller than or equal to e
	while (j >= 0 && _cmp.compare(get(j), e) == 1) {
		j--;
	}

	super.add(j + 1,e);

	return true;
	}

    @Override
    public boolean addAll(Collection<? extends E> c) {
	super.addAll(c);
	for (E e : c) {
		add(e);
	}
	return true;
        }
	
}
