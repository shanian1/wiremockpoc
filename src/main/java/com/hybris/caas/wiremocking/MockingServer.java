package com.hybris.caas.wiremocking;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.VerificationException;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.common.FatalStartupException;
import com.github.tomakehurst.wiremock.common.SingleRootFileSource;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.jayway.restassured.RestAssured;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;

public class MockingServer

{

	protected final static Logger logger = LoggerFactory.getLogger(MockingServer.class);
	static private List<WireMockServer> mockingServers = new ArrayList<WireMockServer>();
	private WireMockServer mockManager;
	WireMockConfiguration options;
	private int portNumber = 8084;
	private String bindAddress = "0.0.0.0";
	private int httpsPort = -1;
	private String fileSourceRoot=System.getProperty("java.io.tmpdir")+ File.separator+"save-mappings-files";

	public MockingServer()

	{

		this.options = new WireMockConfiguration();
		this.options.port(this.portNumber);
		this.options.httpsPort(this.httpsPort);
		this.options.bindAddress(this.bindAddress);
		this.options.fileSource(new SingleRootFileSource(this.fileSourceRoot));
		this.mockManager = new WireMockServer(options);
		mockManager.enableRecordMappings(new SingleRootFileSource(fileSourceRoot+"/mappings"), new SingleRootFileSource("src/test/resources/"+"save-mappings-files"+"/_files"));

	}

	public MockingServer(String fileSourceRoot){
		this.options = new WireMockConfiguration();
		this.options.port(this.portNumber);
		this.options.httpsPort(this.httpsPort);
		this.options.bindAddress(this.bindAddress);
		this.options.fileSource(new SingleRootFileSource(fileSourceRoot));
		this.mockManager = new WireMockServer(options);
		this.mockManager.enableRecordMappings(new SingleRootFileSource(fileSourceRoot+"/mappings"), new SingleRootFileSource(fileSourceRoot+"/_files"));

	}

	public MockingServer(String fileSourceRoot,int portNumber,int httpsPort,String bindAddress){

		this.options = new WireMockConfiguration();
		this.options.port(portNumber);
		this.options.httpsPort(httpsPort);
		this.options.bindAddress(bindAddress);
		this.options.fileSource(new SingleRootFileSource(fileSourceRoot));
		this.mockManager = new WireMockServer(options);
		this.mockManager.enableRecordMappings(new SingleRootFileSource(fileSourceRoot+"/mappings"), new SingleRootFileSource(fileSourceRoot+"/_files"));


	}

	public boolean verify(){

		try
		{
			this.mockManager.verify(2, getRequestedFor(urlMatching("/hello/test")));
		}catch(VerificationException e){
			return false;
		}
	return true;

	}

	public void createStubWithUrlAndMethodAndHeaderAndBody(){


		//this.mockManager.stubFor(get(urlMatching("/site-mock-web-1.0.0-SNAPSHOT/cartcalcv1/sites/canada/payment/stripe"))
		//		.willReturn(aResponse()
		//				.withStatus(200).withBodyFile("sara.json")));

		//http://api.yaas.io/hybris/site/v1/cartcalcv1/sites/canada/payment



		this.mockManager.stubFor(get(urlMatching("/hybris/site/v1/cartcalcv1/sites/canada/payment"))
				.willReturn(aResponse()
						.withStatus(200).proxiedFrom("https://api.yaas.io")));




		this.mockManager.saveMappings();

	}



	public void resetToDefault(){
		mockManager.resetToDefaultMappings();
	}

		public boolean startMockingServer()

        {
			boolean success = false;
			try {
			mockManager.start();

				mockingServers.add(mockManager);
				WireMock.configureFor(this.options.bindAddress(),this.portNumber);

				success = true;

			} catch (Exception e) {
				throw new FatalStartupException(e);
			}


			return success;
		}


	public boolean stopMockingServer(){

		for (WireMockServer server : mockingServers)
		{
			server.stop();


		}
		return true;
	}


	}





