package com.weh.idgen.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.weh.idgen.model.GenerateUniqueID;
import com.weh.idgen.model.IDGeneratorConstant;
import com.weh.idgen.model.Selector;
import com.weh.idgen.model.Tracker;

/**
 * Controller class is the entry point for the rest call
 * 
 * @author BizRuntime
 */
@RestController
public class IDGeneratorController {

	// Logger
	protected static final Logger logger = Logger
			.getLogger(IDGeneratorController.class);

	public CharBuffer charBuffer;

	// AtomicInteger to Increment the Id for every new request
	protected AtomicLong atomicLong = new AtomicLong();

	// Caller from the rest url
	public String caller;

	// Selector from the rest url
	public String selector;

	// Holds the value of the previous ID
	public long previousID;

	// Holds the value of the latest ID
	public long latestID;

	// Selector from file
	public String selectorFromFile;

	// Gets the previous value of id
	public String idFromFile;

	// Loading Files from the properties file
	public Properties properties;

	// Holds the selector value
	private static final String SELECTORTEMPLATE = "%s";

	// Contractor of controller class
	public IDGeneratorController() {
		// Loading properties file<br>
		// Controller gets initialize once so as the property file.
		properties = new Properties();
		InputStream input = IDGeneratorController.class.getClassLoader()
				.getResourceAsStream(IDGeneratorConstant.PROPERTIES);
		try {
			properties.load(input);
		} catch (IOException e) {
			// Failed to initialize File path from the properties
			new IDGeneratorInitializationException(
					"Failed to intialize Filepath from the properties : ", e);
		}
	}

	/**
	 * 
	 * call from the rest url to Generating unique id
	 * 
	 * @param selector
	 *            = from the request url
	 * @return id = auto incremented id
	 * @throws IOException
	 */
	@RequestMapping(value = "/getID/{caller}", method = RequestMethod.GET)
	public GenerateUniqueID generateKey(
			@PathVariable("caller") String caller,
			@RequestParam(value = "selector", defaultValue = "NULL") String selector) {
		GenerateUniqueID generateID = null;
		this.selector = selector;
		this.caller = caller;

		if (selector.contains("?") || selector.contains("#")
				|| selector.contains("=")) {
			generateID = new GenerateUniqueID(
					"selector has Special characters which is not supported");
		} else {
			// if the name contains this special characters it will send error
			// response
			Pattern regex = Pattern
					.compile("[+!~`@$%&|}{'><.*/)(,\\[\\]\\\\^\\\"\\s]");
			Matcher matcher = regex.matcher(selector);
			if (matcher.find()) {
				generateID = new GenerateUniqueID(
						"selector has Special characters which is not supported");
			} else if (selector == null || selector.equalsIgnoreCase("null")
					|| selector.equalsIgnoreCase(selector)) {
				readTrackerFile();

				// Writing into Selector for ever new name
				if (latestID == 1) {
					String toSelector = selector + " " + caller
							+ System.getProperty("line.separator")
							+ System.lineSeparator();
					byte[] byteArray = toSelector.getBytes();
					ByteBuffer byteBufferWrite = ByteBuffer.wrap(byteArray);
					writeToSelectorFile(byteBufferWrite);
				}
				if (latestID > 9999999998L) {
					generateID = new GenerateUniqueID("Id has reached its max");
					return generateID;
				} else {

					// Adding Decimal value to ID
					DecimalFormat decimalFormat = new DecimalFormat(
							"0000000000");
					String decimalNumber = decimalFormat.format(latestID);
					String id = String.format(SELECTORTEMPLATE, selector)
							+ decimalNumber;

					// Returning generated ID to Browser
					generateID = new GenerateUniqueID(id);
					writeToLogFile(generateID.toString());
					return generateID;
				}
			}
		}
		return generateID;
	}

	/**
	 * call from the rest url to show the caller and selector from selector file
	 * 
	 * @return all the values of selector
	 * @throws IOException
	 */
	@RequestMapping("/ListIDSelectors")
	private Selector readFromSelector() {
		Selector selector = null;

		// Reading file with Read Write access
		RandomAccessFile randomAccessFile = null;
		try {
			randomAccessFile = new RandomAccessFile(
					properties.getProperty(IDGeneratorConstant.SELECTORFILE),
					"rw");
		} catch (FileNotFoundException e) {

			// Selector file not found
			new IDGeneratorInitializationException(
					"Selector file not found to read the selector : ", e);
		}

		// Using fileChannel to read or write into file
		FileChannel fileChannelSelector = randomAccessFile.getChannel();

		try {

			long fileSize = fileChannelSelector.size();
			ByteBuffer byteBuffer = ByteBuffer.allocate((int) fileSize);
			Charset charset = Charset.forName("UTF-8");
			while (fileChannelSelector.read(byteBuffer) > 0) {

				byteBuffer.rewind();
				charBuffer = charset.decode(byteBuffer);
				byteBuffer.flip();

			}
			fileChannelSelector.close();
			randomAccessFile.close();

		} catch (IOException e) {
			// Failed to connect to selector file
			new IDGeneratorInitializationException(
					"Failed to read selector from selector file : ", e);
		}
		String select = charBuffer.toString();

		String regexExpression = "\\w+[0-9-:_a-zA-Z]*?\\s\\w+[0-9-:_a-zA-Z]*?";

		Pattern pattern = Pattern.compile(regexExpression,
				Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(select);
		String json = null;
		StringBuilder sb = new StringBuilder();
		while (matcher.find()) {
			String firstsel = matcher.group().replaceAll("\\s(\\w+)", "");
			String lastsel = matcher.group().replaceAll("^([^\\s]+)\\s", "");

			json = firstsel + " : " + lastsel + " , ";
			ArrayList<String> arrlist = new ArrayList<String>();
			arrlist.add(json);
			for (int counter = 0; counter < arrlist.size(); counter++) {
				sb.append(arrlist.get(counter));
			}
		}
		String toSel = sb.toString().replaceAll(",\\s+$", "");
		selector = new Selector(toSel);
		return selector;

	}

	/**
	 * 
	 * Reads the data from the tracker file<br>
	 * In Tracker file we are maintaining the information about the selector and
	 * Corresponding IdGenerator
	 * 
	 */
	private void readTrackerFile() {

		logger.debug("Inside Read Tracker File Method");

		// Reads the file and access used here is read write.
		RandomAccessFile randomAccessFile = null;
		try {
			randomAccessFile = new RandomAccessFile(
					properties.getProperty(IDGeneratorConstant.TRACKERFILE),
					"rw");
		} catch (FileNotFoundException e) {
			// Tracker file not found
			new IDGeneratorInitializationException(
					"Tracker file not found to read the selector : ", e);
		}

		// Separate File channel.
		FileChannel fileChannelRead = randomAccessFile.getChannel();

		// The size of the ByteBuffer is set as the file size
		// byteBufferRead = null;
		try {
			ByteBuffer byteBufferRead = ByteBuffer
					.allocate((int) fileChannelRead.size());

			// Checking for the file is empty
			if (fileChannelRead.read(byteBufferRead) == -1) {

				logger.debug("The File is Empty");

				latestID = atomicLong.incrementAndGet();
				Tracker generateUniqueKey = new Tracker(selector, latestID);
				String uniqueKey = generateUniqueKey.toString();
				byte[] byteArrayNew = uniqueKey.getBytes();
				ByteBuffer byteBufferWriteNew = ByteBuffer.wrap(byteArrayNew);

				writeToTrackerFile(byteBufferWriteNew);

			} else {
				Charset charset = Charset.forName("US-ASCII");

				byteBufferRead.rewind();
				CharBuffer charBuffer = charset.decode(byteBufferRead);
				String buffStr = charBuffer.toString();
				// Gets all the data with previous id
				String regexFullExpression = "\\w+[0-9-:_a-zA-Z]*?\\s\\d+";
				String lineToBeReplaced = null;

				Pattern pattern = Pattern.compile(regexFullExpression,
						Pattern.CASE_INSENSITIVE);
				Matcher matcher = pattern.matcher(buffStr);
				while (matcher.find()) {
					lineToBeReplaced = matcher.group().replaceAll("", "")
							.trim();
					// Gets only the selector with allowed special characters
					String selectorFromFile = matcher.group().replaceAll(
							"([\\s]+)\\d*", "");
					// Gets only the selector's id from file
					idFromFile = matcher.group()
							.replaceAll("^([^\\s]+)\\s", "");

					if (selectorFromFile.equalsIgnoreCase(selector)) {

						logger.debug("selectorFromFile :-------------- "
								+ selectorFromFile);

						logger.debug("Id--------------- " + idFromFile);

						// To override the old selector ID with new ID
						replaceID(
								new File(
										properties
												.getProperty(IDGeneratorConstant.TRACKERFILE)),
								lineToBeReplaced);
						logger.debug("lineToBeReplaced----------------- : "
								+ lineToBeReplaced);
						previousID = Long.parseLong(idFromFile);

						break;
					}
				}
				if (previousID < 10000000000L) {
					latestID = previousID + atomicLong.incrementAndGet();

					previousID = atomicLong.decrementAndGet();

					Tracker generateUniqueKey = new Tracker(selector, latestID);
					String genUniqueKey = generateUniqueKey.toString();
					byte[] byteArray = genUniqueKey.getBytes();
					ByteBuffer byteBufferWrite = ByteBuffer.wrap(byteArray);
					writeToTrackerFile(byteBufferWrite);

				} else {
					Tracker generateUniqueKey = new Tracker(selector,
							9999999999L);
					String genUniqueKey = generateUniqueKey.toString();
					byte[] byteArray = genUniqueKey.getBytes();
					ByteBuffer byteBufferWrite = ByteBuffer.wrap(byteArray);
					writeToTrackerFile(byteBufferWrite);
				}
				fileChannelRead.close();
				randomAccessFile.close();
			}
		} catch (NumberFormatException e) {
			// Number formatException
			new IDGeneratorInitializationException(
					"Number format Execption : ", e);
		} catch (IOException e) {
			new IDGeneratorInitializationException(
					"Failed to read selector from Tracker file : ", e);
		}

	}

	/**
	 * 
	 * Writing the selector and ID to tracker file
	 * 
	 * @param byteBufferWrite
	 *            from the readTrackerFile
	 * @throws IOException
	 */
	private void writeToTrackerFile(ByteBuffer byteBufferWrite) {

		logger.debug("Inside Write To Tracker File Method");

		Set<StandardOpenOption> options = new HashSet<StandardOpenOption>();
		options.add(StandardOpenOption.CREATE);
		options.add(StandardOpenOption.APPEND);

		Path path = Paths.get(properties
				.getProperty(IDGeneratorConstant.TRACKERFILE));

		FileChannel fileChannelWrite = null;
		try {
			fileChannelWrite = FileChannel.open(path, options);
			FileLock lock = fileChannelWrite.lock();
			fileChannelWrite.write(byteBufferWrite);
			lock.release();
			fileChannelWrite.close();
		} catch (IOException e) {
			new IDGeneratorInitializationException(
					"Failed to write selector from Tracker file : ", e);
		}

	}

	/**
	 * 
	 * Writing the selector to Selector file
	 * 
	 * @param byteBufferWrite
	 *            from the generateKey()
	 * @throws IOException
	 */
	private void writeToSelectorFile(ByteBuffer byteBufferWrite) {

		logger.debug("Inside Write Selector To File Method");

		Set<StandardOpenOption> options = new HashSet<StandardOpenOption>();
		options.add(StandardOpenOption.CREATE);
		options.add(StandardOpenOption.APPEND);

		Path path = Paths.get(properties
				.getProperty(IDGeneratorConstant.SELECTORFILE));
		try {
			FileChannel fileChannelWrite = FileChannel.open(path, options);
			FileLock lock = fileChannelWrite.lock();
			fileChannelWrite.write(byteBufferWrite);
			lock.release();
			fileChannelWrite.close();
		} catch (IOException ioe) {
			new IDGeneratorInitializationException(
					"Failed to write selector from Selector file : ", ioe);

		}

	}

	/**
	 * Writing the selector and ID with time to logger file
	 * 
	 * @param byteBufferWrite
	 *            from the generateKey()
	 * @throws IOException
	 */
	private void writeToLogFile(String id) {

		logger.debug("Inside Write To Log File Method");

		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy.MM.dd 'at' hh:mm:ss a zzz E ");

		String logFile = dateFormat.format(date) + caller + " " + selector
				+ " " + id + System.getProperty("line.separator")
				+ System.lineSeparator();

		byte[] byteArray = logFile.getBytes();
		ByteBuffer byteBufferWrite = ByteBuffer.wrap(byteArray);

		Set<StandardOpenOption> options = new HashSet<StandardOpenOption>();
		options.add(StandardOpenOption.CREATE);
		options.add(StandardOpenOption.APPEND);

		Path path = Paths.get(properties
				.getProperty(IDGeneratorConstant.LOGFILE));
		try {
			FileChannel fileChannelWrite = FileChannel.open(path, options);
			FileLock lock = fileChannelWrite.lock();
			fileChannelWrite.write(byteBufferWrite);
			lock.release();
			fileChannelWrite.close();
		} catch (IOException e) {
			new IDGeneratorInitializationException(
					"Failed to write selector Log from Log : ", e);

		}
	}

	/**
	 * 
	 * Replace the selector previous ID with the Latest ID
	 * 
	 * @param targetFile
	 * @param replaceID
	 * @throws IOException
	 */
	private void replaceID(File targetFile, String replaceID) {
		logger.debug("Inside Replace ID Method");

		StringBuffer fileContents = null;
		String[] fileContentLines = null;
		try {
			fileContents = new StringBuffer(
					FileUtils.readFileToString(targetFile));

			fileContentLines = fileContents.toString().split(
					System.lineSeparator());

			RandomAccessFile randomAccessFile = new RandomAccessFile(
					targetFile, "rw");
			randomAccessFile.setLength(0);
			randomAccessFile.close();
		} catch (FileNotFoundException e) {
			// Selector file not found
			new IDGeneratorInitializationException(
					"file not found to read the selector details : ", e);
		} catch (IOException e) {
			new IDGeneratorInitializationException(
					"Failed to read selector details : ", e);
		}

		fileContents = new StringBuffer();

		for (int fileContentLinesIndex = 0; fileContentLinesIndex < fileContentLines.length; fileContentLinesIndex++) {
			if (fileContentLines[fileContentLinesIndex].contains(replaceID)) {
				continue;
			}

			fileContents.append(fileContentLines[fileContentLinesIndex]
					+ System.lineSeparator());
		}

		try {
			FileUtils.writeStringToFile(targetFile, fileContents.toString()
					.trim() + System.lineSeparator());
		} catch (IOException e) {
			new IDGeneratorInitializationException(
					"Failed to re-write selector from Tracker file : ", e);
		}
	}

}