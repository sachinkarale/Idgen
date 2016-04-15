package com.weh.idgen.helper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.weh.idgen.model.IDGenConstant;

/**
 * <h3>IDGenConfigHelper class</h3> Used to load the required files from
 * properties file.
 * 
 * @author BizRuntime
 */
public class IDGenConfigHelper {

	/**
	 * <h3>getIDGenConfigProperties()</h3> Loads all the files from properties
	 * file.
	 * 
	 * @return properties
	 * @throws IDGenInitializationException
	 */
	public static Properties getIDGenConfigProperties() throws IDGenInitializationException {
		Properties properties = new Properties();
		InputStream input = IDGenConfigHelper.class.getClassLoader().getResourceAsStream(
				IDGenConstant.IDGEN_CONFIG_PROPERTIES_FILE);
		try {
			properties.load(input);
			return properties;
		} catch (IOException e) {
			System.out.println("3");
			// Failed to initialize File path from the properties
			String message = IDGenExceptionHelper.exceptionFormat("404");
			throw new IDGenInitializationException(message);
		}
	}

	/**
	 * <h3>getErrorCodeProperties()</h3> Loads the errorCode properties
	 * file.
	 * from classpath
	 * @return properties
	 * @throws IDGenInitializationException
	 */
	public static Properties getErrorCodeProperties() throws IDGenInitializationException {
		Properties properties = new Properties();
		InputStream input = IDGenConfigHelper.class.getClassLoader().getResourceAsStream("errorCode.properties");
		try {
			properties.load(input);
			return properties;
		} catch (IOException e) {
			// Failed to initialize File path from the properties
			String message = IDGenExceptionHelper.exceptionFormat("404");
			throw new IDGenInitializationException(message);
		}
	}

}
