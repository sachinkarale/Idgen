package com.weh.idgen.test;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;

import org.apache.log4j.Logger;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;

/**
 * JUnit class to test the regular expression provided which will test the
 * selector's <br>
 * accepted inputs.
 * 
 * @author BizRuntime
 **/
public class TestRegexExp {

	// Logger
	protected static Logger logger = Logger.getLogger(TestRegexExp.class);

	/**
	 * isValidInput() to validate the regular expression return type of
	 * TypeSafeMatcher has 2 overrider methods -matchesSafely(String entry)
	 * invoked when called -describeTo(String org.hamcrest.Description arg0 )
	 * invoked when fails
	 **/
	public static TypeSafeMatcher<String> isValidInput() {
		return new TypeSafeMatcher<String>() {
			@Override
			protected boolean matchesSafely(String entry) {
				return entry.matches("\\w+[0-9-:_a-zA-Z]*?\\s\\w+[0-9-:_a-zA-Z]*?");
			}

			@Override
			public void describeTo(org.hamcrest.Description arg0) {
				logger.info("Selector Mismatch");
			}
		};
	}

	// Test Matcher for asserting the inputs by validating with isValidInput()
	@Test
	public void testMatcherwithValidInput1() {
		assertThat("TEST test", isValidInput());
	}

	// Test Matcher for asserting the inputs by validating with isValidInput()
	@Test
	public void testMatcherwithValidInput2() {
		assertThat("TEST test123", isValidInput());
	}

	// Test method to check the rejection of the input as per the regex
	// expression
	@Test
	public void testMatcherwithInvalidInput() {
		assertNotEquals("TEST test@#", isValidInput());
	}
}