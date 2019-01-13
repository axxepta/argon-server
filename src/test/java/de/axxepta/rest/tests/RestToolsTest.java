package de.axxepta.rest.tests;

import org.junit.Test;

import de.axxepta.tools.TransformJSONToXML;
import de.axxepta.tools.ValidateURL;
import de.axxepta.tools.ValidationDocs;
import de.axxepta.tools.ValidationString;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class RestToolsTest {

	private File resourcesDirectory = new File("src/test/resources");

	@Test
	public void test() {
		String test = "test";
		assertEquals(test, "test");
	}

	@Test
	public void existenceDirectoryResource() {
		if (!resourcesDirectory.exists())
			fail("Resource directory not exists");
	}

	@Test
	public void existenceFileURLResource() {
		final String fileURLResource = resourcesDirectory.getAbsolutePath() + File.separator + "TestResourceURL.txt";
		if (!new File(fileURLResource).exists())
			fail("Resource file with URLs not exists " + fileURLResource);
	}

	@Test
	public void testValidationURL() {
		final List<String> listURL = getStringLineFromFile("TestResourceURL.txt");
		for (String url : listURL) {
			if(url.startsWith("#"))
				continue;
			if (!ValidateURL.validateURL.isURLValid(url)) {
				fail(url + " is not valid");
				break;
			}
		}
	}

	@Test
	public void testValidationXML() {
		final List<String> listXMLFileName = getStringLineFromFile("TestResourceXML.txt");
		List<File> listFile = new ArrayList<>();
		for (String fileName : listXMLFileName) {
			File file = new File(resourcesDirectory.getAbsolutePath() + File.separator + fileName);
			if (!file.exists()) {
				fail(file + " not exist");
				return;
			}
			listFile.add(file);
		}
		
		for(File file : listFile) {
			if(!ValidationDocs.validateXMLWithDOM.isDocTypeValid(file))
				fail(file.getPath() + " is not an XML file valid");
		}
	}

	@Test
	public void testValidationJSON() {
		List<String> listJSONFileName = getStringLineFromFile("TestResourceJson.txt");
		List<File> listFile = new ArrayList<>();
		for (String fileName : listJSONFileName) {
			File file = new File(resourcesDirectory.getAbsolutePath() + File.separator + fileName);
			if (!file.exists()) {
				fail(file + " not exist");
				return;
			}
			listFile.add(file);
		}
		
		for(File file : listFile) {
			if(!ValidationDocs.validateJSONDocs.isDocTypeValid(file))
				fail(file.getPath() + " is not an XML file valid");
		}
	}
	
	@Test
	public void convertJsonToXML() {
		final List<String> listJSONFileName = getStringLineFromFile("TestResourceJson.txt");
		List<File> listFile = new ArrayList<>();
		for (String fileName : listJSONFileName) {
			File file = new File(resourcesDirectory.getAbsolutePath() + File.separator + fileName);
			if (!file.exists()) {
				fail(file + " not exist");
				return;
			}
			listFile.add(file);
		}
		
		for(File file : listFile) {
			String xmlContent = TransformJSONToXML.transformJSON.transformToXML(file);
			System.out.println(xmlContent);
			try {
				File tempFileXML = File.createTempFile("XMLTestFile", ".tmp");
				PrintWriter out = new PrintWriter(tempFileXML);
				out.println(xmlContent);
				out.close();
				
				if(!ValidationDocs.validateXMLWithDOM.isDocTypeValid(tempFileXML))
					fail("Generated  file from " + file.getPath() + " is not an XML file valid");
			
				if(!tempFileXML.delete()) {
					fail("Generated  file " + file.getPath() + " cannot be deleted");//<-- fail here
				}
			} catch (IOException e) {
				fail("Cannot create tempt file " + e.getMessage());
			}
		}
	}

	@Test
	public void validationStrings() {
		final String simple = "abcdefgh";
		if(!ValidationString.validationString(simple, "simple")) {
			fail("simple string cannot be validate");
		}
		
		if(ValidationString.validationString("", "")) {
			fail("that cannot be nether valid");
		}
	}
	
	private List<String> getStringLineFromFile(String fileName) {
		final String fileURLResource = resourcesDirectory.getAbsolutePath() + File.separator + fileName;
		File fileURL = new File(fileURLResource);
		Scanner sc;
		List<String> listLines = new ArrayList<>();
		try {
			sc = new Scanner(fileURL);
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				if(line.startsWith("#"))
					continue;
				listLines.add(line);
			}
		} catch (FileNotFoundException e) {
			fail(e.getMessage());
		}

		return listLines;
	}

}
