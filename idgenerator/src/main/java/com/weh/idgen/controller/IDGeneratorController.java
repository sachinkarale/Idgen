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
import java.util.Date;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
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
	protected AtomicInteger atomicInteger = new AtomicInteger();

	// Application Name from the rest url
	public String appName;

	// Holds the value of the pervious ID
	public int perviousID;

	// Holds the value of the latest ID
	public int latestID;

	// Application Name from file
	public String appNameFromFile;

	// Gets the pervious value of id
	public String idFromFile;

	// Loading Files from the properties file
	public Properties properties;

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
			// Failed to intialize Filepath from the properties
			new IDGeneratorInitializationException(
					"Failed to intialize Filepath from the properties : ", e);
		}
	}

	/**
	 * 
	 * call from the rest url to Generating unique id
	 * 
	 * @param appName
	 *            = from the request url
	 * @return id = auto incremented id
	 * @throws IOException
	 */
	@RequestMapping("/generateID")
	public GenerateUniqueID generateKey(String appName) {
		GenerateUniqueID generateID = null;
		this.appName = appName;
		logger.info("YoYo " + appName);

		// If the name is null it return a message
		if (appName == null || appName.trim().equalsIgnoreCase("")) {
			generateID = new GenerateUniqueID("Application name is null");
		} else if (appName.contains("?")) {
			generateID = new GenerateUniqueID(
					"Application name has Special characters which is not supported");
		} else if (appName.contains("#") || appName.contains("=")) {
			generateID = new GenerateUniqueID(
					"Application name has Special characters which is not supported");
		} else {
			// if the name contains this special characters it will send error
			// response
			Pattern regex = Pattern
					.compile("[+!~`@$%&|}{'><.*/)(,\\[\\]\\\\^\\\"\\s]");
			Matcher matcher = regex.matcher(appName);
			if (matcher.find()) {
				generateID = new GenerateUniqueID(
						"Application name has Special characters which is not supported");
			} else {
				readTrackerFile();

				// Writing into Selector for ever new name
				if (latestID == 1) {
					String selector = "App : " + appName + "\n";
					byte[] byteArray = selector.getBytes();
					ByteBuffer byteBufferWrite = ByteBuffer.wrap(byteArray);
					writeToSelectorFile(byteBufferWrite);
				}

				// Adding Decimal value to ID
				DecimalFormat decimalFormat = new DecimalFormat("00000");
				String decimalNumber = decimalFormat.format(latestID);
				String id = appName + decimalNumber;

				// Returning generated ID to Browser
				generateID = new GenerateUniqueID(id);
				writeToLogFile(generateID.toString());
				return generateID;
			}
		}
		return generateID;
	}

	/**
	 * call from the rest url to show the app from selector file
	 * 
	 * @return all the values of application name
	 * @throws IOException
	 */
	@RequestMapping("/selector")
	public Selector readFromSelector() {

		// Reading file with Read Write access
		RandomAccessFile randomAccessFile = null;
		try {
			randomAccessFile = new RandomAccessFile(
					properties.getProperty(IDGeneratorConstant.SELECTORFILE),
					"rw");
		} catch (FileNotFoundException e) {

			// Selector file not found
			new IDGeneratorInitializationException(
					"Selector file not found to read the Application name : ",
					e);
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
					"Failed to read Application name from selector : ", e);
		}

		String select = charBuffer.toString();

		Selector selector = new Selector(select.replace("\n", " "));

		return selector;
	}

	/**
	 * 
	 * Reads the data from the tracker file<br>
	 * In Tracker file we are maintaing the information about the appName and
	 * corosponding IdGenerator
	 * 
	 */
	public void readTrackerFile() {

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
					"Tracker file not found to read the Application name : ", e);
		}

		// Seprate File channel.
		FileChannel fileChannelRead = randomAccessFile.getChannel();

		// The size of the ByteBuffer is set as the file size
		// byteBufferRead = null;
		try {
			ByteBuffer byteBufferRead = ByteBuffer
					.allocate((int) fileChannelRead.size());

			// Checking for the file is empty
			if (fileChannelRead.read(byteBufferRead) == -1) {

				logger.debug("The File is Empty");

				latestID = atomicInteger.incrementAndGet();
				Tracker generateUniqueKey = new Tracker(appName, latestID);
				String uniqueKey = generateUniqueKey.toString();
				byte[] byteArrayNew = uniqueKey.getBytes();
				ByteBuffer byteBufferWriteNew = ByteBuffer.wrap(byteArrayNew);

				writeToTrackerFile(byteBufferWriteNew);

			} else {
				Charset charset = Charset.forName("US-ASCII");

				byteBufferRead.rewind();
				CharBuffer charBuffer = charset.decode(byteBufferRead);
				String buffStr = charBuffer.toString();
				// Gets all the data with pervious id
				String regexFullExpression = "\\w+[0-9-:_]*?\\s\\d+";
				String lineToBeReplaced = null;

				Pattern pattern = Pattern.compile(regexFullExpression,
						Pattern.CASE_INSENSITIVE);
				Matcher matcher = pattern.matcher(buffStr);
				while (matcher.find()) {
					lineToBeReplaced = matcher.group().replaceAll("", "")
							.trim();
					// Gets only the appName with allowed special characters
					String appNameFromFile = matcher.group().replaceAll(
							"([\\s]+)\\d*", "");
					// Gets only the appName's id from file
					idFromFile = matcher.group()
							.replaceAll("^([^\\s]+)\\s", "");

					if (appNameFromFile.equalsIgnoreCase(appName)) {

						logger.debug("appNameFromFile :-------------- "
								+ appNameFromFile);

						logger.debug("Id--------------- " + idFromFile);

						// To override the old appName ID with new ID
						replaceID(
								new File(
										properties
												.getProperty(IDGeneratorConstant.TRACKERFILE)),
								lineToBeReplaced);
						logger.debug("lineToBeReplaced----------------- : "
								+ lineToBeReplaced);
						perviousID = Integer.parseInt(idFromFile);
						break;
					}
				}

				latestID = perviousID + atomicInteger.incrementAndGet();

				perviousID = atomicInteger.decrementAndGet();

				Tracker generateUniqueKey = new Tracker(appName, latestID);
				String genUniqueKey = generateUniqueKey.toString();
				byte[] byteArray = genUniqueKey.getBytes();
				ByteBuffer byteBufferWrite = ByteBuffer.wrap(byteArray);
				writeToTrackerFile(byteBufferWrite);
				fileChannelRead.close();
				randomAccessFile.close();

			}
		} catch (NumberFormatException e) {
			// Number formatException
			new IDGeneratorInitializationException(
					"Number format Execption : ", e);
		} catch (IOException e) {
			new IDGeneratorInitializationException(
					"Failed to read Application dara from Tracker file : ", e);
		}

	}

	/**
	 * 
	 * Writing the App name and ID to tracker file
	 * 
	 * @param byteBufferWrite
	 *            from the readTrackerFile
	 * @throws IOException
	 */
	public void writeToTrackerFile(ByteBuffer byteBufferWrite) {

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
					"Failed to write Application data from Tracker file : ", e);
		}

	}

	/**
	 * 
	 * Writing the App name to Selector file
	 * 
	 * @param byteBufferWrite
	 *            from the generateKey()
	 * @throws IOException
	 */
	public void writeToSelectorFile(ByteBuffer byteBufferWrite) {

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
					"Failed to write Application name from Selector file : ",
					ioe);

		}

	}

	/**
	 * Writing the App name and ID with time to logger file
	 * 
	 * @param byteBufferWrite
	 *            from the generateKey()
	 * @throws IOException
	 */
	public void writeToLogFile(String id) {

		logger.debug("Inside Write To Log File Method");

		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy.MM.dd 'at' hh:mm:ss a zzz E ");

		String logFile = dateFormat.format(date) + " app1 " + appName + " "
				+ id;

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
					"Failed to write Application Log from Log : ", e);

		}
	}

	/**
	 * 
	 * Replace the AppName pervious ID with the Latest ID
	 * 
	 * @param targetFile
	 * @param replaceID
	 * @throws IOException
	 */
	public void replaceID(File targetFile, String replaceID) {
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
					"file not found to read the Application details : ", e);
		} catch (IOException e) {
			new IDGeneratorInitializationException(
					"Failed to read Application details : ", e);
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
					.trim());
		} catch (IOException e) {
			new IDGeneratorInitializationException(
					"Failed to re-write Application data from Tracker file : ",
					e);
		}
	}

}