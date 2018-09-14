package de.axxepta.services.interfaces;

import java.io.File;
import java.net.URL;
import java.util.List;

import org.jvnet.hk2.annotations.Contract;

import de.axxepta.models.FileDescriptionModel;

@Contract
public interface IFileResourceService {

	public boolean directUploadFile(URL fileURL, String nameFileUpload);

	public String calculateHashSum(File file);

	public List<File> listUploadedFiles();

	public boolean existFileStored(String fileName);

	public byte[] readingFile(String fileName);

	public boolean deleteFile(String fileName);
	
	public FileDescriptionModel uploadLocalFile(URL fileURL, boolean asTempFile);
}
