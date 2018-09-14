package de.axxepta.models;

import java.net.URL;

public class FileDescriptionModel {

	private URL fileURL;
	private String initialFileName;
	private String typeFile;
	
	public FileDescriptionModel() {
		super();
	}
	
	public FileDescriptionModel(URL fileURL, String initialFileName, String typeFile) {
		super();
		this.fileURL = fileURL;
		this.initialFileName = initialFileName;
		this.typeFile = typeFile;
	}

	public URL getFileURL() {
		return fileURL;
	}

	public void setFileURL(URL fileURL) {
		this.fileURL = fileURL;
	}

	public String getInitialFileName() {
		return initialFileName;
	}

	public void setInitialFileName(String initialFileName) {
		this.initialFileName = initialFileName;
	}

	public String getTypeFile() {
		return typeFile;
	}

	public void setTypeFile(String typeFile) {
		this.typeFile = typeFile;
	}
}
