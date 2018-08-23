package de.axxepta.database;

import org.basex.core.BaseXException;
import org.basex.core.Context;
import org.basex.core.cmd.Close;
import org.basex.core.cmd.Open;
import org.basex.core.cmd.XQuery;

public class ConnectionBaseXEmbedded {

	private String nameDB;
	private String resourcePath;
	private Context context;
	
	public ConnectionBaseXEmbedded(String nameDB, String resourcePath) {
		this.nameDB = nameDB;
		this.resourcePath = resourcePath;
	}
	
	public void connect() throws BaseXException {
		new Open(resourcePath + nameDB).execute(context);
		
	}
	
	public String executeQuery(String query) throws BaseXException {
		return new XQuery(query).execute(context);
		
	}
	
	public void close() throws BaseXException {
		new Close().execute(context);
	}
}
