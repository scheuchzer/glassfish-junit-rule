package com.ja.junit.rule.glassfish;

import org.glassfish.embeddable.GlassFish;

/**
 * No real Java Future but enables access to the GlassFish object at a later
 * time when it's available.
 * 
 * @author Thomas Scheuchzer, www.java-adventures.com
 * 
 */
public class GlassfishFuture {

	private GlassFish glassFish;

	public GlassFish get() {
		return glassFish;
	}

	public void setGlassFish(GlassFish glassFish) {
		this.glassFish = glassFish;
	}
}
