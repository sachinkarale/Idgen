package com.weh.idgen.test;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Test;

import com.weh.idgen.controller.IDGenController;
import com.weh.idgen.controller.exception.UnableToGetSelectorIDException;
import com.weh.idgen.controller.exception.UnableToGetSelectorListException;
import com.weh.idgen.controller.exception.UnableToReadFileException;
import com.weh.idgen.helper.IDGenExceptionHelper;
import com.weh.idgen.model.IDGenConstant;

public class CheckListIDSelector {
	private static Logger logger = Logger.getLogger(CheckListIDSelector.class);
	
	static String lineBreaker = "\r\n";

	/**
	 * TestingIDSelector method to check whether the listIDSelector method gives<br>
	 * the, expected output on generating a new ID.<br>
	 * @throws UnableToGetSelectorListException 
	 */
	@Test
	public void TestingIDSelector() throws UnableToGetSelectorListException, UnableToGetSelectorIDException {
		// Creating an Object for IDGenController to call listIDSelectors method to get the data already stored
		try {
			IDGenController guid = new IDGenController();
			guid.getID("testing", "TEST");
			String actualResult = guid.listIDSelectors();
			JSONObject actualJSONObject=new JSONObject(actualResult);
			assertTrue("TEST doesn't exist in the list of keys",actualJSONObject.has("TEST"));			
		} catch (UnableToGetSelectorListException e) {
			String message = IDGenExceptionHelper.exceptionFormat(IDGenConstant.BAD_REQUEST);
			throw new UnableToGetSelectorListException(message);
		} catch (UnableToGetSelectorIDException e) {
			String message = IDGenExceptionHelper.getErrorMessage() + " " + IDGenConstant.LOG_FILE_NAME;
			throw new UnableToGetSelectorIDException(message);
		}
	}

	/**
	 * deleteEntry(String filename) to delete the entry specific to the filename
	 * provided<br>
	 * filename - The path of the filename to be provided from the @After
	 * method.
	 * @throws UnableToReadFileException 
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
					sb.append(line + lineBreaker);
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
	 * delete_entries_from_logs() to remove all the entries made while testing<br>
	 * by calling del methods specifically with the filenames.
	 * @throws UnableToReadFileException 
	 **/
	@After
	public void delete_entries_from_logs() throws UnableToReadFileException {

		CheckListIDSelector.deleteEntry("src/main/resources/IDGen_Tracker.txt");
		CheckListIDSelector.deleteEntry("src/main/resources/IDGen_Log.txt");
		CheckListIDSelector.deleteEntry("src/main/resources/IDGen_Selector.txt");
	}
}
