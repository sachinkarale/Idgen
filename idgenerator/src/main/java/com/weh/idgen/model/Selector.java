package com.weh.idgen.model;

/**
 * 
 * Selector Class<br>
 * returns the all the selector from the SelectorFile
 * 
 * @author bizruntime
 */
public class Selector {
	
	private String selector;

	public Selector(String selector) {
		this.selector = selector;
	}

	public String getSelector() {
		return toString();
	}

	public void setSelector(String selector) {
		this.selector = selector;
	}

	@Override
	public String toString() {
		return selector;
	}

}
