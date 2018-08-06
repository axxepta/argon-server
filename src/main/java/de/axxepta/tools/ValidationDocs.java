package de.axxepta.tools;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.codehaus.stax2.XMLStreamReader2;
import org.codehaus.stax2.validation.XMLValidationSchema;
import org.codehaus.stax2.validation.XMLValidationSchemaFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.SAXException;

import de.axxepta.tools.interfaces.IValidationDocs;

public class ValidationDocs {

	private static final Logger LOG = Logger.getLogger(ValidationDocs.class);

	private final static String URL_SCHEMA_VALIDATION = "https://www.w3.org/2009/XMLSchema/XMLSchema.xsd";
	
	public static IValidationDocs validateXMLWithDOM = (file) -> {

		String fileContent = null;
		try {
			fileContent = FileUtils.readFileToString(file);
		} catch (IOException e) {
			LOG.error(e.getClass() + ": " + e.getMessage());
			return false;
		}

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;

		try {
			dBuilder = dbFactory.newDocumentBuilder();
			dBuilder.parse(fileContent);
			return true;
		} catch (SAXException | IOException | ParserConfigurationException e) {
			LOG.error(e.getClass() + ": " + e.getMessage());
		}

		return false;
	};

	public static IValidationDocs validateXMLSchema = (file) -> {

		InputStream xmlInputStream = null;
		
		try {
			xmlInputStream = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			LOG.error("File " + file.toString() + " not exist");
		}
		XMLValidationSchemaFactory schemaFactory = XMLValidationSchemaFactory
				.newInstance(XMLValidationSchema.SCHEMA_ID_W3C_SCHEMA);
		XMLValidationSchema validationSchema = null;
		
		try {
			validationSchema = schemaFactory.createSchema(new URL(URL_SCHEMA_VALIDATION));
		} catch (MalformedURLException | XMLStreamException e) {
			LOG.error(e.getMessage());
			return false;
		}
		XMLStreamReader2 streamReader = null;
		try {
			streamReader = (XMLStreamReader2) XMLInputFactory.newInstance().createXMLStreamReader(xmlInputStream);
			streamReader.validateAgainst(validationSchema);
		} catch (XMLStreamException | FactoryConfigurationError e) {
			LOG.error(e.getMessage());
			return false;
		}
			
		try {
			while (streamReader.hasNext()) {
				streamReader.next();
			}
		} catch (XMLStreamException e) {
			LOG.error(e.getMessage());
			return false;
		}
		try {
			streamReader.close();
		} catch (XMLStreamException e) {
			return false;
		}
		
		return true;
	};
	
	public static IValidationDocs validateJSONDocs = (file) -> {

		String fileContent = null;
		try {
			fileContent = FileUtils.readFileToString(file);
		} catch (IOException e) {
			LOG.error(e.getClass() + ": " + e.getMessage());
			return false;
		}
		try {
			new JSONObject(fileContent);
		} catch (JSONException e) {
			try {
				new JSONArray(fileContent);
			} catch (JSONException ex) {
				LOG.error(ex.getClass() + ": " + ex.getMessage());
				return false;
			}
		}
		return true;
	};

}
