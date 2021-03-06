package com.weh.idgen.model;

/**
 * GenerateUniqueID Class<br>
 * Generates unique ID for every rest call
 * @author BizRuntime
 */
public class GenerateUniqueID {

	private String id;

	public GenerateUniqueID(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return id;
	}
}