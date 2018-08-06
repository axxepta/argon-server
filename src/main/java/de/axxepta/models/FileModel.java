package de.axxepta.models;

public class FileModel {

	private String fileNamePath;
	private String fileRef;
	
	public FileModel () {
		
	}

	public FileModel(String fileNamePath, String fileRef) {
		super();
		this.fileNamePath = fileNamePath;
		this.fileRef = fileRef;
	}

	public String getFileNamePath() {
		return fileNamePath;
	}

	public void setFileNamePath(String fileNamePath) {
		this.fileNamePath = fileNamePath;
	}

	public String getFileRef() {
		return fileRef;
	}

	public void setFileRef(String fileRef) {
		this.fileRef = fileRef;
	}
}
