package de.axxepta.rest.tests;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.eclipse.jetty.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.fail;

import java.net.HttpURLConnection;
import java.net.URL;

import de.axxepta.server.embedded.ServerEmbedded;
import de.axxepta.server.embedded.interfaces.IServerEmbedded;
import io.restassured.RestAssured;

public class RestServicesTest {

	private final static int PORT = 8830;
	private final static String CONTEXT = "";

	private IServerEmbedded serverEmbedded;
	
	@BeforeClass
	public static void setup() {
		String port = System.getProperty("server.port");
		if (port == null) {
			RestAssured.port = Integer.valueOf(PORT);
		} else {

			RestAssured.port = Integer.valueOf(port);

		}

		String basePath = System.getProperty("server.base");
		if (basePath == null) {
			basePath = CONTEXT;
		}
		RestAssured.basePath = basePath;

		String baseHost = System.getProperty("server.host");
		if (baseHost == null) {
			baseHost = "http://127.0.0.1";
		}
		RestAssured.baseURI = baseHost;

	}

	@Before
	public void startServer() {
		serverEmbedded = new ServerEmbedded(PORT);
		String startServerEmbedded = serverEmbedded.startServer();
		if(startServerEmbedded != null)
			fail(startServerEmbedded);
	}

	@Test
	public void test() {
		String test = "test";
		assertEquals(test, "test");
	}
	/*
	@Test
	public void testGet() throws Exception {
		String testURL = RestAssured.baseURI + ":" + RestAssured.port + RestAssured.basePath + "/testing/test";
		HttpURLConnection http = (HttpURLConnection) new URL(testURL)
				.openConnection();
		http.connect();
		assertThat("Response Code", http.getResponseCode(), is(HttpStatus.OK_200));
	}
	
	@Test
	public void testForTestServices() {
		String testURL = RestAssured.baseURI + ":" + RestAssured.port + RestAssured.basePath + "/testing/test";
		System.out.println("Test URL " + testURL);
		given().when().get(testURL).then().assertThat().statusCode(200);
	}
	*/
	
	@After
	public void stopServer() {
		String stopServerEmbedded = serverEmbedded.stopServer();
		if(stopServerEmbedded != null)
			fail(stopServerEmbedded);
	}
	
}
