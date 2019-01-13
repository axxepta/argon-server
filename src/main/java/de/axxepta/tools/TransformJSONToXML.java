package de.axxepta.tools;

import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.json.XML;

import de.axxepta.tools.interfaces.ITransformJSONToXML;

public class TransformJSONToXML {

	public static ITransformJSONToXML transformJSON = (file) -> {
		String contentFile = null;
		try {
			contentFile = FileUtils.readFileToString(file, "UTF-8");
		} catch (IOException e) {
			return null;
		}
		JSONObject json = new JSONObject(contentFile);
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?> <transform_JSON>" + XML.toString(json) + "</transform_JSON>" ;
	};
}
