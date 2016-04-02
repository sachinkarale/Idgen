package com.weh.idgen.model;

/**
 * 
 * Selector Class<br>
 * returns the all the App name from the SelectorFile
 * 
 * @author bizruntime
 */
public class Selector {

	private String app;

	public Selector(String app) {
		this.app = app;
	}

	public String getApp() {
		return app;
	}

	public void setApp(String app) {
		this.app = app;
	}

	@Override
	public String toString() {
		return "appName = " + app + "\n";
	}

}
