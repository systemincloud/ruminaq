package org.ruminaq.tasks.javatask.client.data;

import java.util.Iterator;
import java.util.List;

public abstract class Data {
	
	protected List<Integer> dims;
	public    List<Integer> getDimensions() { return dims; }

	protected int nElements = 1;
	public    int getNumberOfElements() { return nElements; }

	public Data(List<Integer> dims) {
		this.dims = dims;
		for(Integer i : dims) nElements *= i;
	}
	
	public boolean equalDimensions(Data data) {
		if(data == null) return false;
		Iterator<Integer> it = data.getDimensions().iterator();
		for(Integer i : dims) 
			if(!it.hasNext() || !it.next().equals(i)) return false;
		if(it.hasNext()) return false;
		return true;
	}
}
