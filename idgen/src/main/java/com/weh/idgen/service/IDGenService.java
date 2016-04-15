package com.weh.idgen.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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

import com.weh.idgen.controller.exception.UnableToGetSelectorIDException;
import com.weh.idgen.controller.exception.UnableToGetSelectorListException;
import com.weh.idgen.controller.exception.UnableToReadFileException;
import com.weh.idgen.controller.exception.UnableToWriteFileException;
import com.weh.idgen.helper.IDGenConfigHelper;
import com.weh.idgen.helper.IDGenExceptionHelper;
import com.weh.idgen.helper.IDGenInitializationException;
import com.weh.idgen.model.GenerateUniqueID;
import com.weh.idgen.model.IDGenConstant;
import com.weh.idgen.model.Selector;
import com.weh.idgen.model.Tracker;

public class IDGenService {

	// Logger
	protected static Logger logger = Logger.getLogger(IDGenService.class);

	// Caller from the rest url
	private String caller;

	// Selector from the rest url
	private String selector;

	// Holds the value of the previous ID
	private long previousID;

	// Holds the value of the latest ID
	private long latestID;

	// Add a line break while writing to files.
	private String lineBreaker = "\r\n";

	// Loading Files from the idGen_Config properties file
	private static Properties idGenConfigProperties;

	private static IDGenService idGenControllerService;

	// making service class Singleton
	private IDGenService() {
	}

	/**
	 * This method will create just one object of IDGenControllerService class
	 * @return Singleton object of IDGenControllerService
	 */
	public static IDGenService getInstance() {
		if (idGenControllerService == null) {
			idGenControllerService = new IDGenService();
		}
		return idGenControllerService;
	}

	/**
	 * This Method is to Loads the IDGen config properties file
	 * 
	 * @return Properties
	 */
	public static Properties loadIDGenConfigPropertiesFile() {
		try {
			idGenConfigProperties = IDGenConfigHelper.getIDGenConfigProperties();
		} catch (IDGenInitializationException e) {
			logger.error("Resources not found" + e.getMessage());
		}
		return idGenConfigProperties;
	}

	/**
	 * This method will read the selector file and get all the selector values.
	 * @return list of selector to controller
	 * @throws UnableToGetSelectorListException
	 */
	public Selector listOfSelector() throws UnableToGetSelectorListException {
		loadIDGenConfigPropertiesFile();
		Selector selector;
		CharBuffer charBuffer = null;
		try {
			RandomAccessFile randomAccessFile = new RandomAccessFile(
					idGenConfigProperties.getProperty(IDGenConstant.SELECTOR_FILE_NAME), IDGenConstant.FILE_ACCESS);
			FileChannel fileChannelSelector = randomAccessFile.getChannel();
			try {
				long fileSize = fileChannelSelector.size();
				ByteBuffer byteBuffer = ByteBuffer.allocate((int) fileSize);
				Charset charset = Charset.forName(IDGenConstant.UNICODE);
				while (fileChannelSelector.read(byteBuffer) > 0) {
					byteBuffer.rewind();
					charBuffer = charset.decode(byteBuffer);
					byteBuffer.flip();
				}
				selector = formatListIDSelector(charBuffer);
				fileChannelSelector.close();
				randomAccessFile.close();
			} catch (IOException e) {
				String message = IDGenExceptionHelper.exceptionFormat(IDGenConstant.UNABLE_TO_READ) + " "
						+ IDGenConstant.SELECTOR_FILE_NAME;
				throw new UnableToGetSelectorListException(message);
			}
		} catch (FileNotFoundException e) {
			String message = IDGenExceptionHelper.exceptionFormat(IDGenConstant.UNABLE_TO_READ) + " "
					+ IDGenConstant.SELECTOR_FILE_NAME;
			throw new UnableToGetSelectorListException(message);
		}

		return selector;
	}

	/**
	 * This method will return the generated id for selector.
	 * @param caller
	 *            : Takes the caller value
	 * @param selector
	 *            : Takes the selector value
	 * @return generateUniqueID
	 * @throws UnableToGetSelectorIDException
	 */
	public GenerateUniqueID GetSelectorID(String caller, String selector) throws UnableToGetSelectorIDException {
		GenerateUniqueID generateUniqueID = null;
		loadIDGenConfigPropertiesFile();
		this.caller = caller;
		this.selector = selector;
		// it will take special character (@,? etc) into variable specialChar
		String specialChar = selector.replaceAll(IDGenConstant.SPECIAL_CHAR_KEY, "");
		try {
			validateSelector(selector);
		} catch (UnableToGetSelectorListException e) {
			String message = IDGenExceptionHelper.getErrorMessage() + " " + specialChar;
			throw new UnableToGetSelectorIDException(message);
		}
		if (selector == null || selector.equalsIgnoreCase("null") || selector.equalsIgnoreCase(selector)) {
			try {
				readTrackerFile();
			} catch (UnableToReadFileException e) {
				String message = IDGenExceptionHelper.getErrorMessage() + IDGenConstant.TRACKER_FILE_NAME;
				throw new UnableToGetSelectorIDException(message);
			}
			// Writing into Selector for every new name
			if (latestID == 1 && previousID == 0) {
				String toSelector = selector + " " + caller + lineBreaker;
				byte[] byteArray = toSelector.getBytes();
				ByteBuffer byteBufferWrite = ByteBuffer.wrap(byteArray);
				try {
					writeToSelectorFile(byteBufferWrite);
				} catch (UnableToWriteFileException e) {
					String message = IDGenExceptionHelper.getErrorMessage() + " " + IDGenConstant.SELECTOR_FILE_NAME;
					throw new UnableToGetSelectorIDException(message);
				}
			}
			if (latestID > 9999999998L) {
				String message = IDGenExceptionHelper.exceptionFormat(IDGenConstant.MAX_ID_EXCEPTION) + " "
						+ IDGenConstant.MAX_ID;
				throw new UnableToGetSelectorIDException(message);

			} else {
				// Adding Decimal value to ID
				DecimalFormat decimalFormat = new DecimalFormat(IDGenConstant.DECIMAL_FORMAT);
				String decimalNumber = decimalFormat.format(latestID);
				String id = String.format(IDGenConstant.SELECTOR_TEMPLATE, selector) + decimalNumber;

				// Returning generated ID to Browser
				generateUniqueID = new GenerateUniqueID(id);
				try {
					logFileFormat(generateUniqueID.toString());
				} catch (UnableToWriteFileException e) {
					String message = IDGenExceptionHelper.getErrorMessage() + " " + IDGenConstant.LOG_FILE_NAME;
					throw new UnableToGetSelectorIDException(message);
				}
			}
		}
		return generateUniqueID;
	}

	/**
	 * Format the selectors List of data to json form.
	 * @param charBuffer
	 * @return ListIDSelector
	 */
	private Selector formatListIDSelector(CharBuffer charBuffer) {
		String select = charBuffer.toString();

		Pattern pattern = Pattern
				.compile(IDGenConstant.REGEX_EXPRESSION_FOR_LIST_OF_SELECTOR, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(select);
		String json;
		StringBuilder sb = new StringBuilder();
		while (matcher.find()) {
			String firstsel = matcher.group().replaceAll(IDGenConstant.FIRST_SELECTOR, "");
			String lastsel = matcher.group().replaceAll(IDGenConstant.LAST_SELECTOR, "");

			json = firstsel + " : " + lastsel + " , ";
			ArrayList<String> arrlist = new ArrayList<String>();
			arrlist.add(json);
			for (int counter = 0; counter < arrlist.size(); counter++) {
				sb.append(arrlist.get(counter));
			}
		}
		String toSel = sb.toString().replaceAll(IDGenConstant.REMOVE_AFTER_SPACE, "");
		Selector selector = new Selector(toSel);
		return selector;
	}
	
	/**
	 * This Method validates the given selector.
	 * @param selector
	 *            : selector value from rest url.
	 * @throws UnableToGetSelectorListException
	 */
	private void validateSelector(String selector) throws UnableToGetSelectorListException {
		// if the selector contains special characters (@,?,=) it will send
		// error response
		if (selector.contains("?") || selector.contains("&") || selector.contains("=")) {
			String message = IDGenExceptionHelper.exceptionFormat(IDGenConstant.BAD_REQUEST);
			throw new UnableToGetSelectorListException(message);
		} else {
			// if the selector contains special characters(!,@,$,...) it will
			// send error response
			Pattern regex = Pattern.compile(IDGenConstant.SPECIAL_CHAR_EXPRESSION);
			Matcher matcher = regex.matcher(selector);
			if (matcher.find()) {
				String message = IDGenExceptionHelper.exceptionFormat(IDGenConstant.BAD_REQUEST);
				throw new UnableToGetSelectorListException(message);
			}
		}
	}

	/**
	 * This method reads the previous id of selector from tracker file. If there
	 * is no selector its adds to tracker file as well as selector file.
	 * @throws UnableToReadFileException
	 */
	private void readTrackerFile() throws UnableToReadFileException {
		logger.debug("Inside Read Tracker File Method");
		// AtomicLong to Increment the Id for every new request
		AtomicLong autoIncrement = new AtomicLong();
		RandomAccessFile randomAccessFile = null;
		FileChannel fileChannelRead = null;
		try {
			// Read Tracker file if specified file access permission
			randomAccessFile = new RandomAccessFile(
					idGenConfigProperties.getProperty(IDGenConstant.TRACKER_FILE_NAME), IDGenConstant.FILE_ACCESS);
			fileChannelRead = randomAccessFile.getChannel();
		} catch (FileNotFoundException e) {
			String message = IDGenExceptionHelper.exceptionFormat(IDGenConstant.UNABLE_TO_READ);
			throw new UnableToReadFileException(message);
		}
		try {
			toTrackerFile(randomAccessFile, fileChannelRead, autoIncrement);
		} catch (UnableToReadFileException e) {
			String message = IDGenExceptionHelper.getErrorMessage();
			throw new UnableToReadFileException(message);
		}
	}

	/**
	 * This method will write the selector and id into tracker file.
	 * @param randomAccessFile
	 * @param fileChannelRead
	 * @param autoIncrement
	 * @throws UnableToReadFileException
	 */
	private void toTrackerFile(RandomAccessFile randomAccessFile, FileChannel fileChannelRead, AtomicLong autoIncrement)
			throws UnableToReadFileException {
		try {
			ByteBuffer byteBufferRead = ByteBuffer.allocate((int) fileChannelRead.size());
			// Checking for the file is empty
			if (fileChannelRead.read(byteBufferRead) == -1) {
				latestID = autoIncrement.incrementAndGet();
				Tracker generateUniqueKey = new Tracker(selector, latestID);
				String uniqueKey = generateUniqueKey.toString() + lineBreaker;
				byte[] byteArrayNew = uniqueKey.getBytes();
				ByteBuffer byteBufferWriteNew = ByteBuffer.wrap(byteArrayNew);
				try {
					writeToTrackerFile(byteBufferWriteNew);
				} catch (UnableToWriteFileException e) {
					String message = IDGenExceptionHelper.getErrorMessage();
					throw new UnableToReadFileException(message);
				}
			} else {
				Charset charset = Charset.forName(IDGenConstant.UNICODE);

				byteBufferRead.rewind();
				CharBuffer charBuffer = charset.decode(byteBufferRead);
				String buffStr = charBuffer.toString();
				// Gets all the data with previous id
				String lineToBeReplaced;

				Pattern pattern = Pattern.compile(IDGenConstant.REGEX_FULL_EXPRESSION_FOR_TRACKER_FILE,
						Pattern.CASE_INSENSITIVE);
				Matcher matcher = pattern.matcher(buffStr);
				while (matcher.find()) {
					lineToBeReplaced = matcher.group().replaceAll("", "").trim();
					// Gets only the selector with allowed special
					// characters
					String selectorFromFile = matcher.group().replaceAll(IDGenConstant.SELECTOR_FROM_TRACKER_FILE, "");
					// Gets only the selector's id from file
					String idFromFile = matcher.group().replaceAll(IDGenConstant.ID_OF_SELECTOR_FROM_TRACKER_FILE, "");

					if (selectorFromFile.equalsIgnoreCase(selector)) {

						// To override the old selector ID with new ID
						try {
							replaceID(new File(idGenConfigProperties.getProperty(IDGenConstant.TRACKER_FILE_NAME)),
									lineToBeReplaced);
						} catch (UnableToWriteFileException | UnableToReadFileException e) {
							String message = IDGenExceptionHelper.getErrorMessage();
							throw new UnableToReadFileException(message);
						}
						previousID = Long.parseLong(idFromFile);
						break;
					}
				}

				try {
					checkForMaxID(randomAccessFile, fileChannelRead, autoIncrement);
				} catch (UnableToWriteFileException e) {
					String message = IDGenExceptionHelper.getErrorMessage();
					throw new UnableToReadFileException(message);
				}
			}
		} catch (IOException e) {
			String message = IDGenExceptionHelper.exceptionFormat(IDGenConstant.UNABLE_TO_READ);
			throw new UnableToReadFileException(message);
		}
	}

	/**
	 * This method checks for Id less than the maximum number and write into
	 * selector
	 * @param randomAccessFile
	 *            : File access
	 * @param fileChannelRead
	 *            : read files using File channel.
	 * @param autoIncrement
	 *            : Increment id every time rest url send request.
	 * @throws UnableToWriteFileException
	 */
	private void checkForMaxID(RandomAccessFile randomAccessFile, FileChannel fileChannelRead, AtomicLong autoIncrement)
			throws UnableToWriteFileException {
		if (previousID < 9999999999L) {
			latestID = previousID + autoIncrement.incrementAndGet();

			previousID = autoIncrement.decrementAndGet();

			Tracker generateUniqueKey = new Tracker(selector, latestID);
			String genUniqueKey = generateUniqueKey.toString() + lineBreaker;
			byte[] byteArray = genUniqueKey.getBytes();
			ByteBuffer byteBufferWrite = ByteBuffer.wrap(byteArray);
			try {
				writeToTrackerFile(byteBufferWrite);
			} catch (UnableToWriteFileException e) {
				String message = IDGenExceptionHelper.getErrorMessage() + IDGenConstant.TRACKER_FILE_NAME;
				throw new UnableToWriteFileException(message);
			}

		} else if (previousID == IDGenConstant.MAX_ID || latestID == IDGenConstant.MAX_ID) {
			Tracker generateUniqueKey = new Tracker(selector, IDGenConstant.MAX_ID);
			String genUniqueKey = generateUniqueKey.toString();
			byte[] byteArray = genUniqueKey.getBytes();
			ByteBuffer byteBufferWrite = ByteBuffer.wrap(byteArray);
			try {
				writeToTrackerFile(byteBufferWrite);
			} catch (UnableToWriteFileException e) {
				String message = IDGenExceptionHelper.getErrorMessage() + IDGenConstant.TRACKER_FILE_NAME;
				throw new UnableToWriteFileException(message);
			}
		}
		try {
			fileChannelRead.close();
			randomAccessFile.close();
		} catch (IOException e) {
			String message = IDGenExceptionHelper.exceptionFormat(IDGenConstant.UNABLE_TO_READ);
			throw new UnableToWriteFileException(message);
		}

	}

	/**
	 * This method is used to write data into tracker file
	 * @param byteBufferWrite
	 *            : Data need to be written in tracker file.
	 * @throws UnableToWriteFileException
	 */
	private void writeToTrackerFile(ByteBuffer byteBufferWrite) throws UnableToWriteFileException {
		logger.debug(".writeToTrackerFile method of IDGenController");
		try {
			writeToFile(byteBufferWrite, IDGenConstant.TRACKER_FILE_NAME);
		} catch (UnableToWriteFileException e) {
			String message = IDGenExceptionHelper.getErrorMessage() + IDGenConstant.TRACKER_FILE_NAME;
			throw new UnableToWriteFileException(message);
		}
	}

	/**
	 * This method is used to write data into select file
	 * @param byteBufferWrite
	 *            : Data need to be written in selector file
	 * @throws UnableToWriteFileException
	 */
	private void writeToSelectorFile(ByteBuffer byteBufferWrite) throws UnableToWriteFileException {
		logger.debug(".writeToSelectorFile method of IDGenController");
		try {
			writeToFile(byteBufferWrite, IDGenConstant.SELECTOR_FILE_NAME);
		} catch (UnableToWriteFileException e) {
			String message = IDGenExceptionHelper.getErrorMessage() + IDGenConstant.SELECTOR_FILE_NAME;
			throw new UnableToWriteFileException(message);
		}
	}

	/**
	 * This method is used to define format in which data need to be written in
	 * log file
	 * @param genId
	 *            : generated Id in String
	 * @throws UnableToWriteFileException
	 */
	private void logFileFormat(String genId) throws UnableToWriteFileException {
		logger.debug(".logFileFormat method of IDGenController");
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat(IDGenConstant.DATE_FORMAT);
		String logFile = dateFormat.format(date) + caller + " " + selector + " " + genId + lineBreaker;
		byte[] byteArray = logFile.getBytes();
		ByteBuffer byteBufferWrite = ByteBuffer.wrap(byteArray);
		try {
			writeToLogFile(byteBufferWrite);
		} catch (UnableToWriteFileException e) {
			String message = IDGenExceptionHelper.getErrorMessage() + IDGenConstant.LOG_FILE_NAME;
			throw new UnableToWriteFileException(message);
		}
	}

	/**
	 * Writing the selector and ID with time to logger file
	 * @param byteBufferWrite
	 *            : data need to write on file
	 * @throws UnableToWriteFileException
	 */
	private void writeToLogFile(ByteBuffer byteBufferWrite) throws UnableToWriteFileException {
		logger.debug(".writeToLogFile method of IDGenController");
		try {
			writeToFile(byteBufferWrite, IDGenConstant.LOG_FILE_NAME);
		} catch (UnableToWriteFileException e) {
			String message = IDGenExceptionHelper.getErrorMessage() + IDGenConstant.LOG_FILE_NAME;
			throw new UnableToWriteFileException(message);
		}
	}

	/**
	 * This method is used to write file 
	 * @param byteBufferWrite
	 *            : Data to be written into file
	 * @param fileName
	 *            : file name where data to be written
	 * @throws UnableToWriteFileException
	 */
	private void writeToFile(ByteBuffer byteBufferWrite, String fileName) throws UnableToWriteFileException {
		synchronized (this) {
			logger.debug(".writeToFile method of IDGenController");
			Set<StandardOpenOption> options = new HashSet<StandardOpenOption>();
			options.add(StandardOpenOption.CREATE);
			options.add(StandardOpenOption.APPEND);
			Path path = Paths.get(idGenConfigProperties.getProperty(fileName));
			try {
				FileChannel fileChannelWrite = FileChannel.open(path, options);
				FileLock lock = fileChannelWrite.lock();
				fileChannelWrite.write(byteBufferWrite);
				lock.release();
				fileChannelWrite.close();
			} catch (IOException e) {
				String message = IDGenExceptionHelper.exceptionFormat(IDGenConstant.UNABLE_TO_WRITE)
						+ IDGenConstant.LOG_FILE_NAME;
				throw new UnableToWriteFileException(message);
			}
		}

	}

	/**
	 * This method removes the previous id of selector and write a new id for
	 * same selector 
	 * @param targetFile
	 *            : tracker file path
	 * @param replaceID
	 *            : previous id replaced with latest id
	 * @throws UnableToWriteFileException
	 * @throws UnableToReadFileException
	 */
	private void replaceID(File targetFile, String replaceID) throws UnableToWriteFileException,
			UnableToReadFileException {
		logger.debug("Inside Replace ID Method");

		StringBuffer fileContents;
		String[] fileContentLines = null;
		try {
			fileContents = new StringBuffer(FileUtils.readFileToString(targetFile));
			fileContentLines = fileContents.toString().split(lineBreaker);
			RandomAccessFile randomAccessFile = new RandomAccessFile(targetFile, IDGenConstant.FILE_ACCESS);
			randomAccessFile.setLength(0);
			randomAccessFile.close();
		} catch (FileNotFoundException e) {
			String message = IDGenExceptionHelper.exceptionFormat(IDGenConstant.UNABLE_TO_READ) + targetFile;
			throw new UnableToReadFileException(message);
		} catch (IOException e) {
			String message = IDGenExceptionHelper.exceptionFormat(IDGenConstant.UNABLE_TO_READ) + targetFile;
			throw new UnableToReadFileException(message);
		}
		fileContents = new StringBuffer();
		for (int fileContentLinesIndex = 0; fileContentLinesIndex < fileContentLines.length; fileContentLinesIndex++) {
			if (fileContentLines[fileContentLinesIndex].contains(replaceID)) {
				continue;
			}
			fileContents.append(fileContentLines[fileContentLinesIndex] + lineBreaker);
		}
		try {
			FileUtils.writeStringToFile(targetFile, fileContents.toString().trim() + lineBreaker);
		} catch (IOException e) {
			String message = IDGenExceptionHelper.exceptionFormat(IDGenConstant.UNABLE_TO_WRITE) + targetFile;
			throw new UnableToReadFileException(message);
		}
	}
}
