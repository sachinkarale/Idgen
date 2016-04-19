package com.weh.idgen.test;

import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.weh.idgen.controller.IDGenController;
import com.weh.idgen.controller.exception.UnableToGetSelectorIDException;
import com.weh.idgen.model.GenerateUniqueID;

/**
 * Junit Test cases for IDGen scenario<br>
 * If the selector has the values of getSelectors()<br>
 * the returned id will be resulting an error message.
 * @author BizRuntime
 */
@RunWith(Parameterized.class)
public class TestIDGenRejectedParams {

	// Logger
	protected static Logger logger = Logger.getLogger(TestIDGenRejectedParams.class);

	// Takes multiple selectors for the test
	private String selectors;

	// Constructor of the TestIDGenController class
	public TestIDGenRejectedParams(String selectors) {
		this.selectors = selectors;
	}

	// For the multiple selectors the input is given from the method.
	@Parameters
	public static Collection<Object[]> getSelectors() {
		return Arrays.asList(new Object[][] { { "@" }, { "+" }, { "=" }, { ")" }, { "(" }, { "*" }, { "&" }, { "^" },
				{ "%" }, { "$" }, { "!" }, { "~" }, { "`" }, { "/" }, { "." }, { "," }, { "<" }, { ">" }, { "?" },
				{ "\\" }, { "]" }, { "[" }, { "{" }, { "}" }, { "|" }, { "/" } });
	}

	/**
	 * Takes all the selector input as parameter from the getSelectors<br>
	 * and loops it till the final selector's value.<br>
	 * if all the selector values throws GetIDFailedException then,<br>
	 * the test case is successfully done.
	 * @throws GetIDFailedException
	 */
	@Test(expected = com.weh.idgen.controller.exception.UnableToGetSelectorIDException.class)
	public void testForRequiredSelector() throws UnableToGetSelectorIDException {
		// Object of controller class
		IDGenController idGeneratorController = new IDGenController();

		// Passing the parameters to the generate a key whose inputs are taken
		// from parameters defined.
		GenerateUniqueID actualUniqueID = idGeneratorController.getID("test", "TEST" + selectors);

		// Checking for not null selector values.
		assertNotNull(actualUniqueID);
	}
}