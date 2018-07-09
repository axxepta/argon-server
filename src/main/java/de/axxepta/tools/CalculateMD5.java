package de.axxepta.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.SequenceInputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;

public class CalculateMD5 {
	
	public static String calcMD5Hash(File dir) throws IOException {

		assert (dir.isDirectory());
		List<FileInputStream> fileStreams = new LinkedList<>();

		addInputStreams(dir, fileStreams);

		SequenceInputStream seqStream = new SequenceInputStream(Collections.enumeration(fileStreams));

		String md5Hash = DigestUtils.md5Hex(seqStream);
		seqStream.close();
		return md5Hash;

	}

	private static void addInputStreams(File dir, List<FileInputStream> foundStreams) throws FileNotFoundException {

		File[] fileList = dir.listFiles();
		Arrays.sort(fileList, new Comparator<File>() {
					public int compare(File file1, File file2) {
						return file1.getName().compareTo(file2.getName());
					}
				});

		for (File file : fileList) {
			if (file.isDirectory()) {
				addInputStreams(file, foundStreams);
			} else {
				foundStreams.add(new FileInputStream(file));
			}
		}

	}
}