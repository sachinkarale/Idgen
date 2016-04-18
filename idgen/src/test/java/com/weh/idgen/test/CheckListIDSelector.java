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
import com.weh.idgen.controller.exception.UnableToReadFileException;
import com.weh.idgen.helper.IDGenExceptionHelper;
import com.weh.idgen.model.IDGenConstant;

public class CheckListIDSelector {
	static Logger logger = Logger.getLogger(CheckListIDSelector.class);

	/**
	 * TestingIDSelector method to check whether the listIDSelector method gives<br>
	 * the, expected output on generating a new ID.<br>
	 * @throws UnableToGetSelectorListException 
	 */
	@Test
	public void TestingIDSelector() throws UnableToGetSelectorListException, UnableToGetSelectorIDException {
		// Creating an Object for IDGenController to call listIDSelectors method
		// to get the data already stored
		IDGenController idGenCtrl = new IDGenController();
		String previousSelector;
		try {
			previousSelector = idGenCtrl.listIDSelectors();
			String previousSelectorString = previousSelector.toString();
			StringBuffer expectedResult = new StringBuffer(previousSelectorString);
			// Appending to StringBuffer to set an expected result
			expectedResult.setLength(expectedResult.length()-1);
			expectedResult.append(",\"testing\":\"TEST\"}");
			// creating a new object to call getID to insert the data we expect
			// to
			// match with the expected output
			IDGenController guid = new IDGenController();
			guid.getID("TEST", "testing");
			// fetching the selector from the newly created key
			String actualResult = guid.listIDSelectors();
			assertNotNull(actualResult);
			assertEquals("Data Mismatch", expectedResult.toString(), actualResult.toString());
		} catch (UnableToGetSelectorListException e) {
			String message = IDGenExceptionHelper.exceptionFormat(IDGenConstant.BAD_REQUEST);
			throw new UnableToGetSelectorListException(message);
		} catch (UnableToGetSelectorIDException e) {
			String message = IDGenExceptionHelper.getErrorMessage() + " " + IDGenConstant.LOG_FILE_NAME;
			throw new UnableToGetSelectorIDException(message);
		}
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

		CheckListIDSelector.deleteEntry("src/main/resources/IDGen_Tracker.txt");
		CheckListIDSelector.deleteEntry("src/main/resources/IDGen_Log.txt");
		CheckListIDSelector.deleteEntry("src/main/resources/IDGen_Selector.txt");
	}
}
