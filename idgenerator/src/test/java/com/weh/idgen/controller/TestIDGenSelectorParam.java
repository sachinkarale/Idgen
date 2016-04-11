package com.weh.idgen.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.weh.idgen.controller.IDGeneratorController;
import com.weh.idgen.model.GenerateUniqueID;

/**
 * 
 * Junit Test cases for IDGen scenario
 * 
 * @author BizRuntime
 */
@RunWith(Parameterized.class)
public class TestIDGenSelectorParam {

	// Takes multiple selectors for the test
	private String selectors;

	// Constructor of the TestIDGenController class
	public TestIDGenSelectorParam(String selectors) {
		this.selectors = selectors;
	}

	// For the multiple selectors the input is given from the method.
	@Parameters
	public static Collection<Object[]> getSelectors() {
		return Arrays.asList(new Object[][] { { "IMG@" }, { "IMG+" },
				{ "IMG=" }, { "IMG)" }, { "IMG(" }, { "IMG*" }, { "IMG&" },
				{ "IMG^" }, { "IMG%" }, { "IMG$" }, { "IMG!" }, { "IMG~" },
				{ "IMG`" }, { "IMG/" }, { "IMG." }, { "IMG," }, { "IMG<" },
				{ "IMG>" }, { "IMG?" }, { "IMG\\" }, { "IMG]" }, { "IMG[" },
				{ "IMG{" }, { "IMG}" }, { "IMG|" }, {"IMG/"} });
	}

	/**
	 * Takes all the selector input as parameter from the getSelectors<br>
	 * and loops it til the final selector value.<br>
	 * if all the selector values meets the required output,<br>
	 * the test case is successfully done.
	 */
	@Test
	public void testForRequiredSelector() {
		// Object of controller class
		IDGeneratorController idGeneratorController = new IDGeneratorController();

		// Passing the parameters to the selector.
		GenerateUniqueID generateUniqueID = idGeneratorController.getID(
				"image", selectors);

		// Comparing with the required result.
		GenerateUniqueID assertingGenerateUniqueID = new GenerateUniqueID();
		assertingGenerateUniqueID
				.setId("selector has Special characters which is not supported");

		// Checking for not null selector values.
		assertNotNull(generateUniqueID);

		// Comparing the input and output
		assertEquals("Generated ID is not coming as expected",
				generateUniqueID.toString(),
				assertingGenerateUniqueID.toString());
	}
}