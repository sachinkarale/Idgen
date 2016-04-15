package com.weh.idgen.test;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.weh.idgen.controller.exception.UnableToReadFileException;
import com.weh.idgen.helper.IDGenExceptionHelper;
import com.weh.idgen.model.IDGenConstant;

/**
 * TestFileExists<br>
 * Check for IDGenerator_Selector.txt , IDGenerator_Tracker.txt and
 * IDGenerator_Log.txt files, in the IDGENERATOR_CONFIG.properties.
 * 
 * @author BizRuntime
 */
public class TestFileExists {
	Logger logger = Logger.getLogger(TestFileExists.class);

	/**
	 * Checks if IDGENERATOR_CONFIG.properties is in class path.
	 * 
	 */
	@Test
	public void testPropertyfileExists() {
		assertTrue(new File("src/main/resources/IDGEN_CONFIG.properties").exists());
	}

	/**
	 * In the IDGENERATOR_CONFIG.properties,<br>
	 * Check for the Selector file's path properties
	 * 
	 * @throws UnableToReadFileException
	 * 
	 */
	@Test
	public void testTrackerFileExists() throws UnableToReadFileException {
		Properties p = new Properties();
		try {
			p.load(new FileReader(new File("src/main/resources/IDGEN_CONFIG.properties")));
			assertTrue(p.containsKey("SelectorFile"));
		} catch (IOException e) {
			String message = IDGenExceptionHelper.exceptionFormat(IDGenConstant.UNABLE_TO_READ)
					+ IDGenConstant.TRACKER_FILE_NAME;
			throw new UnableToReadFileException(message);
		}
	}

	/**
	 * In the IDGENERATOR_CONFIG.properties,<br>
	 * Check for the Tracker file's path properties
	 * 
	 * @throws UnableToReadFileException
	 */
	@Test
	public void testSelectorFileExists() throws UnableToReadFileException {
		Properties p = new Properties();
		try {
			p.load(new FileReader(new File("src/main/resources/IDGEN_CONFIG.properties")));
			assertTrue(p.containsKey("TrackerFile"));
		} catch (IOException e) {
			String message = IDGenExceptionHelper.exceptionFormat(IDGenConstant.UNABLE_TO_WRITE)
					+ IDGenConstant.SELECTOR_FILE_NAME;
			throw new UnableToReadFileException(message);
		}
	}

	/**
	 * In the IDGENERATOR_CONFIG.properties,<br>
	 * Check for the Log file's path properties
	 * 
	 * @throws UnableToReadFileException
	 */
	@Test
	public void testLogFileExists() throws UnableToReadFileException {
		Properties p = new Properties();
		try {
			p.load(new FileReader(new File("src/main/resources/IDGEN_CONFIG.properties")));
			assertTrue(p.containsKey("LogFile"));
		} catch (IOException e) {
			String message = IDGenExceptionHelper.exceptionFormat(IDGenConstant.UNABLE_TO_WRITE)
					+ IDGenConstant.LOG_FILE_NAME;
			throw new UnableToReadFileException(message);
		}
	}

}
