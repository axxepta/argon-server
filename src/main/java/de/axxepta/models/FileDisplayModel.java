package de.axxepta.models;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.apache.commons.io.FileUtils;

class FilePermission{

	private boolean readable;
	private boolean writable;
	private boolean executable;
	
	public FilePermission() {
		super();
	}

	public FilePermission(boolean readable, boolean writable, boolean executable) {
		super();
		this.readable = readable;
		this.writable = writable;
		this.executable = executable;
	}

	public boolean isWritable() {
		return writable;
	}

	public void setWritable(boolean writable) {
		this.writable = writable;
	}

	public boolean isReadable() {
		return readable;
	}

	public void setReadable(boolean readable) {
		this.readable = readable;
	}

	public boolean isExecutable() {
		return executable;
	}

	public void setExecutable(boolean executable) {
		this.executable = executable;
	}
}
public class FileDisplayModel {

	private String name;
	private String filePath;
	private String size;
	private long sumCRC32;
	private LocalDateTime lastModified;
	private FilePermission filePermission;
	private boolean isHidden;
	
	public FileDisplayModel() {
		super();
	}

	public FileDisplayModel(File file) throws IOException {
		super();
		this.name = file.getName();
		long sizeB = file.length();
		this.size = FileUtils.byteCountToDisplaySize(sizeB);
		this.sumCRC32 = FileUtils.checksumCRC32(file);
		this.lastModified = LocalDateTime.ofInstant(Instant.ofEpochMilli(file.lastModified()), ZoneId.systemDefault());
		boolean isWriteble = file.canWrite();
		boolean isReadable = file.canRead();
		boolean isExecutable = file.canExecute();
		FilePermission filePermission = new FilePermission(isReadable, isWriteble, isExecutable);
		this.filePermission = filePermission;
		this.isHidden = file.isHidden();	
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public long getSumCRC32() {
		return sumCRC32;
	}

	public void setSumCRC32(long sumCRC32) {
		this.sumCRC32 = sumCRC32;
	}

	public LocalDateTime getLastModified() {
		return lastModified;
	}

	public void setLastModified(LocalDateTime lastModified) {
		this.lastModified = lastModified;
	}

	public FilePermission getFilePermission() {
		return filePermission;
	}

	public void setFilePermission(FilePermission filePermission) {
		this.filePermission = filePermission;
	}

	public boolean isHidden() {
		return isHidden;
	}

	public void setHidden(boolean isHidden) {
		this.isHidden = isHidden;
	}
}
