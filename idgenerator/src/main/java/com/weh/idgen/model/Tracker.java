package com.weh.idgen.model;

/**
 * 
 * Tracker class<br>
 * Gets the Application Name and generated ID.
 * 
 * @author bizruntime
 */

public class Tracker {

	private String name;
	private int id;

	public Tracker() {
	}

	public Tracker(String name, int id) {
		super();
		this.name = name;
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "\n" + name + " " + id + "\n";
	}

}