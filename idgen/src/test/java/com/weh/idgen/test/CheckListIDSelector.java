package com.weh.idgen.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Test;

import com.weh.idgen.controller.IDGenController;
import com.weh.idgen.controller.exception.UnableToGetSelectorIDException;
import com.weh.idgen.controller.exception.UnableToGetSelectorListException;
import com.weh.idgen.model.Selector;

public class CheckListIDSelector {
	static Logger logger = Logger.getLogger(CheckListIDSelector.class);

	/**
	 * TestingIDSelector method to check whether the listIDSelector method gives<br>
	 * the, expected output on generating a new ID.<br>
	 * 
	 * @throws UnableToGetSelectorIDException
	 * @throws UnableToGetSelectorListException
	 * 
	 */
	@Test
	public void TestingIDSelector() throws UnableToGetSelectorIDException,
			UnableToGetSelectorListException {
		File file = new File("src/main/resources/IDGen_Selector.txt");
		if (file.length() == 0) {
			IDGenController guid = new IDGenController();
			guid.getID("TEST", "testing");
			Selector actualResult = guid.listIDSelectors();
			assertNotNull(actualResult);
			StringBuffer sb = new StringBuffer("testing : TEST ");
			assertEquals("Data Mismatch", sb.toString(),
					actualResult.toString());

		} else {
			IDGenController idGenCtrl = new IDGenController();
			Selector previousSelector;
			try {
				previousSelector = idGenCtrl.listIDSelectors();
				String previousSelectorString = previousSelector.toString();
				StringBuffer expectedResult = new StringBuffer(previousSelectorString);
				// Appending to StringBuffer to set an expected result
				expectedResult.append(", testing : TEST ");
				// creating a new object to call getID to insert the data we expect
				// to
				// match with the expected output
				IDGenController guid = new IDGenController();
				guid.getID("TEST", "testing");
				// fetching the selector from the newly created key
				Selector actualResult = guid.listIDSelectors();
				assertNotNull(actualResult);
				assertEquals("Data Mismatch", expectedResult.toString(), actualResult.toString());
			} catch (UnableToGetSelectorListException e) {
				new UnableToGetSelectorListException();
			} catch (UnableToGetSelectorIDException e) {
				new UnableToGetSelectorIDException();
			}
		}
	}

	/**
	 * 
	 * deleteEntry(String filename) to delete the entry specific to the filename
	 * provided<br>
	 * filename - The path of the filename to be provided from the @After
	 * method.
	 * 
	 **/
	public static void deleteEntry(String filename) {
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
			logger.error("Error Occured while attempting to read the file: "
					+ e);
		}
	}

	/**
	 * 
	 * delete_entries_from_logs() to remove all the entries made while testing<br>
	 * by calling del methods specifically with the filenames.
	 * 
	 **/
	@After
	public void delete_entries_from_logs() {

		CheckListIDSelector.deleteEntry("src/main/resources/IDGen_Tracker.txt");
		CheckListIDSelector.deleteEntry("src/main/resources/IDGen_Log.txt");
		CheckListIDSelector
				.deleteEntry("src/main/resources/IDGen_Selector.txt");
	}
}
