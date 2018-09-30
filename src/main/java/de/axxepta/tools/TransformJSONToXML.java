package de.axxepta.tools;

import org.json.JSONObject;
import org.json.XML;

import de.axxepta.tools.interfaces.ITransformJSONToXML;

public class TransformJSONToXML {

	public static ITransformJSONToXML transformJSON = (file) -> {
		JSONObject json = new JSONObject(file);
		return XML.toString(json);
	};
}
