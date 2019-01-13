package de.axxepta.services.implementations;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Singleton;
import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.jvnet.hk2.annotations.Service;

import de.axxepta.models.FileDescriptionModel;
import de.axxepta.services.interfaces.IFileResourceService;

@Service(name = "FileServiceImplementation")
@Singleton
public class FileResourceServiceImpl implements IFileResourceService {

	private static final Logger LOG = Logger.getLogger(FileResourceServiceImpl.class);

	@Context
	private ServletContext servletContext;

	private File dataDir;

	private Map<String, List<File>> tmpFileMap = new HashMap<>();

	@PostConstruct
	private void initFileResourceService() {
		File workDir = (File) servletContext.getAttribute("OXYGEN_WEBAPP_DATA_DIR");
		dataDir = new File(workDir, "uploads");
		LOG.info("Data dir is " + dataDir.getAbsolutePath());
		if (!dataDir.exists()) {
			LOG.info("Upload directory not exist " + dataDir.getPath() + " and will be try to be created");
			boolean isCreated = dataDir.mkdir();
			if (!isCreated) {
				LOG.error("Cannot be created upload dir");
				throw new RuntimeException("Not created upload dir");
			}
		}
	}

	@Override
	public boolean directUploadFile(URL fileURL, String nameFileUpload) {
		LOG.info("Is uploaded file from " + fileURL.toString());
		File fileUpload = new File(fileURL.toString());
		File locationUpload = new File(dataDir + File.separator + nameFileUpload);
		FileInputStream inputStream = null;
		try {
			inputStream = new FileInputStream(fileUpload);
		} catch (FileNotFoundException e) {
			LOG.error(e.getMessage());
			return false;
		}
		OutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(locationUpload);
		} catch (FileNotFoundException e) {
			LOG.error(e.getMessage());
			try {
				inputStream.close();
			} catch (IOException e1) {
			}
			return false;
		}
		int bytes;
		byte[] dataBuffer = new byte[1024];
		try {
			while ((bytes = inputStream.read(dataBuffer)) != -1) {
				outputStream.write(dataBuffer, 0, bytes);
			}
			outputStream.flush();
			outputStream.close();
			inputStream.close();
		} catch (IOException e) {
			LOG.error(e.getMessage());
			return false;
		}
		return true;
	}

	@Override
	public String calculateHashSum(File file) {
		String contentFile = null;
		try {
			contentFile = FileUtils.readFileToString(file, "UTF-8");
		} catch (IOException e) {
			LOG.error(e.getClass() + ": " + e.getMessage());
			return null;
		}
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
			md.update(contentFile.getBytes());
			byte[] digest = md.digest();
			return DatatypeConverter.printHexBinary(digest).toUpperCase();
		} catch (NoSuchAlgorithmException e) {
			LOG.error(e.getClass() + ": " + e.getMessage());
			return null;
		}
	}

	private String getFileNameWithSHA(String fileName, String content) {
		return fileName + DigestUtils.sha256(content);
	}
	
	@Override
	public List<File> listUploadedFiles() {
		LOG.info("Number of uploaded files " + dataDir.listFiles().length);
		List<File> filesFromUploadDirList = new ArrayList<>();
		for (File entry : dataDir.listFiles()) {
			if (entry.isFile()) {
				filesFromUploadDirList.add(entry);
			} else {
				LOG.error(entry + "is not a file");
			}
		}

		return filesFromUploadDirList;
	}

	@Override
	public boolean existFileStored(String fileName) {
		List<File> fileList = listUploadedFiles();
		if (fileList == null)
			return false;
		List<String> fileNameList = fileList.stream().map(f -> f.getName()).collect(Collectors.toList());
		if (fileNameList.contains(fileName)) {
			LOG.info("File with name " + fileName + " exist");
			return true;
		} else {
			LOG.info("File with name " + fileName + " not exist");
			return false;
		}
	}

	@Override
	public byte[] readingFile(String fileName) {
		boolean fileExist = existFileStored(fileName);
		String filePathString = dataDir.getPath() + File.separator + fileName;
		LOG.info("Path and file that would be reading is " + filePathString);
		if (fileExist) {
			Path path = Paths.get(filePathString);
			try {
				return Files.readAllBytes(path);
			} catch (IOException e) {
				LOG.error(e.getMessage());
				return null;
			}
		}
		LOG.info("File with name " + fileName + " not exist to be read");
		return null;
	}

	@Override
	public boolean deleteFile(String fileName) {
		boolean fileExist = existFileStored(fileName);
		if (fileExist) {
			File file = new File(dataDir.getPath() + File.separator + fileName);
			boolean isDeleted = file.delete();
			if (isDeleted) {
				LOG.info("File with name " + fileName + " has been deleted");
				return true;
			} else {
				LOG.info("File with name " + fileName + "  has not been deleted");
				return false;
			}
		}
		LOG.info("File with name " + fileName + " not exist to be deleted");
		return false;
	}

	public FileDescriptionModel uploadLocalFile(URL fileURL, boolean asTempFile) {
		LOG.info("Try to upload file from " + fileURL.toString() + " as a temporal one");
		URLConnection connection;
		try {
			connection = fileURL.openConnection();
		} catch (IOException e) {
			LOG.error(e.getMessage());
			return null;
		}

		if (connection.getContentType() == null) {
			LOG.info("URL " + fileURL + " can not be recognized as a type");
			return null;
		}

		String fileURLString = fileURL.toString();
		String initialFileName = fileURLString.substring(fileURLString.lastIndexOf('/') + 1, fileURLString.length());
		LOG.info("Initial file name " + initialFileName);
		File fileUpload = null;
		String prefixFile = FilenameUtils.removeExtension(initialFileName);
		long startCopy = 0, endCopy = 0;
		if (asTempFile) {
			LOG.info("Try to upload file " + fileURL.toString() + " as a temporal one");

			try {
				fileUpload = File.createTempFile(prefixFile, ".tmp");
			} catch (IOException e) {
				LOG.error("Temp file cannot be created " + e.getMessage());
				return null;
			}
			
			startCopy = System.nanoTime();
			try {
				FileUtils.copyURLToFile(fileURL, fileUpload);
			} catch (IOException e) {
				LOG.error("Exception " + e.getClass().getName() + " " + e.getMessage());
				return null;
			}
			endCopy = System.nanoTime();
			List<File> fileNameList;
			if (tmpFileMap.containsKey(initialFileName)) {
				fileNameList = tmpFileMap.get(initialFileName);
				LOG.info("Version number " + (fileNameList.size() + 1) + " for file "+ initialFileName);
			} else {
				fileNameList = new ArrayList<>();
			}
			fileNameList.add(fileUpload);
			tmpFileMap.put(initialFileName, fileNameList);
		} else {
			LOG.info("Try to upload file " + fileURL.toString() + " in upload directory");
			if (existFileStored(initialFileName)) {
				LOG.error("File already exist in upload directory");
				return null;
			}
			fileUpload = new File(dataDir + File.separator + initialFileName);
			try {
				FileUtils.copyURLToFile(fileURL, fileUpload);
			} catch (IOException e) {
				LOG.error("Exception " + e.getClass().getName() + " " + e.getMessage());
				return null;
			}
			endCopy = System.nanoTime();
		}
		LOG.info("Time for effectively copy is " + (endCopy - startCopy) + " nanoseconds");

		URL tempFileURL = null;
		try {
			tempFileURL = fileUpload.toURI().toURL();
		} catch (MalformedURLException e) {
			LOG.error(e.getMessage());
			return null;
		}

		String fileType;
		try {
			fileType = Files.probeContentType((new File(initialFileName).toPath()));
		} catch (IOException e) {
			LOG.error("IOException" + e.getMessage());
			fileType = "unkown";
		}

		if (fileType == null)
			fileType = "unkown";
		LOG.info("Uploaded file is of type " + fileType);

		FileDescriptionModel fileDescription = new FileDescriptionModel(tempFileURL, initialFileName, fileType);
		return fileDescription;
	}

	
	@PreDestroy
	private void clear() {
		for (Map.Entry<String, List<File>> entry : tmpFileMap.entrySet()) {
			for (File file : entry.getValue()) {
				file.deleteOnExit();
				LOG.info(file.getName() + " put on deleted");
			}
		}
	}
}
