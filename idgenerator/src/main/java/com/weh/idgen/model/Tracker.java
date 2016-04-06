package com.weh.idgen.model;

/**
 * 
 * Tracker class<br>
 * Gets the Selector and generated ID.
 * 
 * @author bizruntime
 */

public class Tracker {

	private String selector;
	private long id;

	public Tracker() {
	}

	public Tracker(String selector, long latestID) {
		super();
		this.selector = selector;
		this.id = latestID;
	}

	public String getSelector() {
		return selector;
	}

	public void setSelector(String selector) {
		this.selector = selector;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "\n" + selector + " " + id + "\n";
	}

}