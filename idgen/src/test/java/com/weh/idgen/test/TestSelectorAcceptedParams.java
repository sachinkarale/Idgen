package com.weh.idgen.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.CharBuffer;
import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.weh.idgen.controller.IDGenController;
import com.weh.idgen.controller.exception.UnableToGetSelectorIDException;
import com.weh.idgen.controller.exception.UnableToReadFileException;
import com.weh.idgen.helper.IDGenExceptionHelper;
import com.weh.idgen.model.GenerateUniqueID;
import com.weh.idgen.model.IDGenConstant;

/**
 * 
 * Junit Test Case for Testing generated IDs when valid inputs are provided.
 * 
 * @author BizRuntime
 * 
 */
@RunWith(Parameterized.class)
public class TestSelectorAcceptedParams {

	// Logger
	protected static Logger logger = Logger.getLogger(TestSelectorAcceptedParams.class);

	// Takes multiple selectors for the test
	private String selectors;

	// Takes multiple expected Results for the Tests
	private String expectedResults;

	// Loading Files from the properties file
	public Properties properties;

	// Buffer
	public static CharBuffer charBuffer;

	// Contractor of controller class
	public TestSelectorAcceptedParams(String expectedResults, String selectors) {
		// Loading properties file<br>
		// Controller gets initialize once so as the property file.
		properties = new Properties();
		InputStream input = IDGenController.class.getClassLoader().getResourceAsStream(
				IDGenConstant.IDGEN_CONFIG_PROPERTIES_FILE);
		try {
			properties.load(input);
		} catch (IOException e) {
			// Failed to initialize File path from the properties
			logger.error("Resources not found" + e.getMessage());
		}
		this.selectors = selectors;
		this.expectedResults = expectedResults;
	}

	/**
	 * 
	 * Parameters mentioned which are expected to be in this format<br>
	 * example [IMG-01, IMG_01, IMG:01] as selectors provided by passing the
	 * object as constructor inputs.
	 * 
	 */
	@Parameters
	public static Collection<Object[]> getSelectors() {
		return Arrays.asList(new Object[][] { { "TEST-010000000001", "TEST-01" }, { "TEST_010000000001", "TEST_01" },
				{ "TEST:010000000001", "TEST:01" }, { "TEST0000000001", "TEST" }, { "TEST010000000001", "TEST01" },
				{ "NULL0000000001", "NULL" } });
	}

	/**
	 * 
	 * Test Method to run the class GenerateUniqueID to generate the logs,
	 * provided<br>
	 * with the inputs of selectors mentioned in the parameters.
	 * @throws UnableToReadFileException 
	 * 
	 **/
	@Test
	public void testForRequiredSelector() throws UnableToReadFileException {
		// Object of controller class
		IDGenController idGeneratorController = new IDGenController();
		GenerateUniqueID generateUniqueID = null;

		if (selectors.trim() == "") {
			try {
				generateUniqueID = idGeneratorController.getID("image", "NULL");
			} catch (UnableToGetSelectorIDException e) {
				new UnableToGetSelectorIDException();
			}
		} else {
			// Passing the parameters to the selector.
			try {
				generateUniqueID = idGeneratorController.getID("image", selectors);
			} catch (UnableToGetSelectorIDException e) {
				String message = IDGenExceptionHelper.exceptionFormat(IDGenConstant.UNABLE_TO_READ)
						+ IDGenConstant.SELECTOR_FILE_NAME;
				throw new UnableToReadFileException(message);
			}
		}
		// Comparing with the required result.
		GenerateUniqueID assertingGenerateUniqueID = new GenerateUniqueID(expectedResults);
		// Checking for not null selector values.
		assertNotNull(generateUniqueID);
		// Comparing the input and output
		assertEquals("Generated ID is not coming as expected", assertingGenerateUniqueID.toString(),
				generateUniqueID.toString());
	}

	/**
	 * 
	 * deleteEntry(String filename) to delete the entry specific to the filename
	 * provided<br>
	 * filename - The path of the filename to be provided from the @After
	 * method.
	 * @throws UnableToReadFileException 
	 * 
	 **/
	public static void deleteEntry(String filename) throws UnableToReadFileException {
		try {
			// Fetching the last line number in lastLine
			BufferedReader br = new BufferedReader(new FileReader(filename));
			int lastLine = 0;
			while (br.readLine() != null) {
				lastLine++;
			}
			br.close();
			br = new BufferedReader(new FileReader(filename));
			// String buffer to store contents of the file
			StringBuffer sb = new StringBuffer("");
			// Keep track of the line number
			int linenumber = 1;
			String line;

			while ((line = br.readLine()) != null) {
				// Store each valid line in the string buffer
				if (linenumber < lastLine || linenumber >= lastLine + 1)
					sb.append(line + "\n");
				linenumber++;
			}
			if (lastLine + 1 > linenumber)
				logger.error("End of file reached.");
			br.close();

			FileWriter fw = new FileWriter(new File(filename));
			// Write entire string buffer into the file
			fw.write(sb.toString());
			fw.close();
		} catch (IOException e) {
			String message = IDGenExceptionHelper.exceptionFormat(IDGenConstant.UNABLE_TO_READ)
						+ filename;
				throw new UnableToReadFileException(message);
		}
	}

	/**
	 * 
	 * delete_entries_from_logs() to remove all the entries made while testing<br>
	 * by calling del methods specifically with the filenames.
	 * @throws UnableToReadFileException 
	 *
	 **/
	@After
	public void delete_entries_from_logs() throws UnableToReadFileException {
		TestSelectorAcceptedParams.deleteEntry("src/main/resources/IDGen_Log.txt");
		TestSelectorAcceptedParams.deleteEntry("src/main/resources/IDGen_Selector.txt");
		TestSelectorAcceptedParams.deleteEntry("src/main/resources/IDGen_Tracker.txt");
	}
}
