package de.axxepta.services.implementations;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.inject.Singleton;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.jvnet.hk2.annotations.Service;

import de.axxepta.services.interfaces.IDocumentsResourceService;
import de.axxepta.tools.ValidateURL;

@Service(name = "FilesResourceImplementation")
@Singleton
public class DocumentsResourceServiceImpl implements IDocumentsResourceService {

	private static final Logger LOG = Logger.getLogger(UserServiceImpl.class);

	@Override
	public boolean uploadFile(URL fileURL, String fileRef) {
		if (ValidateURL.validateURL.isURLValid(fileURL.toString())) {
			LOG.error(fileURL + " is not valid");
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

	@Override
	public boolean existFileStored(String fileName) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public char[] readingFile(String fileName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> listFiles() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean deleteFile(String fileName) {
		// TODO Auto-generated method stub
		return false;
	}
}
