package com.hybris.caas.test.wiremocking;

import com.hybris.caas.wiremocking.MockingServer;
import com.jayway.restassured.RestAssured;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;


public class MockingServerTest
{

	protected final static Logger logger = LoggerFactory.getLogger(MockingServerTest.class);
	protected final static MockingServer server = new MockingServer(new File("").getAbsolutePath()+"/src/test/resources/save-mappings-files");
	private static int REQUEST_PORT = 8084;
	private static String REQUEST_URL = "http://localhost:" + REQUEST_PORT;

	@BeforeClass
	public static void beforeClass() throws Exception
	{
	logger.info("start mocking...");
		server.startMockingServer();

	}

	@Test
	public void testmocking(){

		server.createStubWithUrlAndMethodAndHeaderAndBody();
		//server.resetToDefault();
		//WireMock.reset();
		//RestAssured.get(REQUEST_URL + "/site-mock-web-1.0.0-SNAPSHOT/cartcalcv1/sites/canada/payment/stripe");

		RestAssured.get(REQUEST_URL +"/hybris/site/v1/cartcalcv1/sites/canada/payment");
		System.out.println(server.verify(1));
		server.resetToDefault();
		RestAssured.get(REQUEST_URL +"/hybris/site/v1/cartcalcv1/sites/canada/payment");
		System.out.println(server.verify(2));




		//RestAssured.post(REQUEST_URL + "/hello/carts").then().assertThat().statusCode(200);

		//System.out.println(server.verify());
		//RestAssured.get(REQUEST_URL +"/thing/matching").then().assertThat().statusCode(200);


	}

	@AfterClass
	public static void afterClass() throws Exception
	{

		logger.info("stop mocking...");
		server.stopMockingServer();
	}

}