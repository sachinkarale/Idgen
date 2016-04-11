package com.weh.idgen.controller;

import static org.junit.Assert.assertTrue;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import org.junit.Test;


/**
 * 
 * TestFileExists<br>
 * Check for IDGenerator_Selector.txt , IDGenerator_Tracker.txt and
 * IDGenerator_Log.txt files, in the IDGENERATOR_CONFIG.properties.
 * 
 * @author BizRuntime
 *
 */
public class TestFileExists {

	/**
	 * Checks if IDGENERATOR_CONFIG.properties is in class path.
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	@Test
	public void testPropertyfileExists() throws FileNotFoundException,
			IOException {
		assertTrue(new File("src/main/resources/IDGENERATOR_CONFIG.properties")
				.exists());
	}

	/**
	 * In the IDGENERATOR_CONFIG.properties,<br>
	 * Check for the Selector file
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	@Test
	public void testTrackerFileExists() throws FileNotFoundException,
			IOException {
		Properties p = new Properties();
		p.load(new FileReader(new File(
				"src/main/resources/IDGENERATOR_CONFIG.properties")));
		assertTrue(p.containsKey("SelectorFile"));
	}

	/**
	 * In the IDGENERATOR_CONFIG.properties,<br>
	 * Check for the Tracker file
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	@Test
	public void testSelectorFileExists() throws FileNotFoundException,
			IOException {
		Properties p = new Properties();
		p.load(new FileReader(new File(
				"src/main/resources/IDGENERATOR_CONFIG.properties")));
		assertTrue(p.containsKey("TrackerFile"));
	}

	/**
	 * In the IDGENERATOR_CONFIG.properties,<br>
	 * Check for the Log file
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	@Test
	public void testLogFileExists() throws FileNotFoundException, IOException {
		Properties p = new Properties();
		p.load(new FileReader(new File(
				"src/main/resources/IDGENERATOR_CONFIG.properties")));
		assertTrue(p.containsKey("LogFile"));
	}

}
