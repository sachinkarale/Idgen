package com.weh.idgen.model;

/**
 * IDGenConstant Class<br>
 * Reads the files, from file path using IDGENERATOR_CONFIG.properties file
 * @author BizRuntime
 */
public class IDGenConstant {

	public static final String IDGEN_CONFIG_PROPERTIES_FILE = "IDGEN_CONFIG.properties";

	// Files from class path.
	public static final String SELECTOR_FILE_NAME = "SelectorFile";
	public static final String TRACKER_FILE_NAME = "TrackerFile";
	public static final String LOG_FILE_NAME = "LogFile";

	// Constants of controller class.
	public static final String UNICODE = "UTF-8";
	public static final String SPECIAL_CHAR_KEY = "[A-Za-z0-9\\-:_]";
	public static final String SPECIAL_CHAR_EXPRESSION = "[+%!~`@$&|}{;'><.*/)(,\\[\\]\\\\^\\\"\\s]";
	public static final String DECIMAL_FORMAT = "0000000000";

	// Holds the selector value.
	public static final String SELECTOR_TEMPLATE = "%s";

	// ListOfSelector.
	public static final String REGEX_EXPRESSION_FOR_LIST_OF_SELECTOR = "\\w+[0-9-:_a-zA-Z]*?\\s\\w+[0-9-:_a-zA-Z]*?";
	public static final String GET_SELECTOR = "\\s(\\w+)";
	public static final String GET_CALLER = "^([^\\s]+)\\s";
	
	// ReadTrackerFile.
	public static final String REGEX_FULL_EXPRESSION_FOR_TRACKER_FILE = "\\w+[0-9-:_a-zA-Z]*?\\s\\d+";
	public static final String SELECTOR_FROM_TRACKER_FILE = "([\\s]+)\\d*";
	public static final String ID_OF_SELECTOR_FROM_TRACKER_FILE = "^([^\\s]+)\\s";
	public static final Long MAX_ID = 9999999999L;

	// Log Date format.
	public static final String DATE_FORMAT = "yyyy.MM.dd 'at' hh:mm:ss a zzz E ";

	// Read write access.
	public static final String FILE_ACCESS = "rw";

	// ErrorCode
	public static final String MAX_ID_EXCEPTION = "222";
	public static final String BAD_REQUEST = "400";
	public static final String RESOURCE_NOT_FOUND = "404";
	public static final String INTERNAL_SERVER_ERROR = "500";
	public static final String UNABLE_TO_READ = "506";
	public static final String UNABLE_TO_WRITE = "507";

}
