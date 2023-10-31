/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.gigya.gigyab2bwebservices.test.webservices;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.gigya.gigyab2bfacades.data.GigyaWebHookRequest;
import de.hybris.platform.gigya.gigyab2bwebservices.constants.Gigyab2bwebservicesConstants;
import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.webservicescommons.testsupport.client.WebservicesAssert;
import de.hybris.platform.webservicescommons.testsupport.client.WsRequestBuilder;
import de.hybris.platform.webservicescommons.testsupport.client.WsSecuredRequestBuilder;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;

@NeedsEmbeddedServer(webExtensions = { Gigyab2bwebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME })
@IntegrationTest
public class GigyaB2BWebServicesTest extends ServicelayerTest {
	public static final String OAUTH_CLIENT_ID = "trusted_client";
	public static final String OAUTH_CLIENT_PASS = "secret";

	private static final String BASE_SITE_ID = "wsB2BTest";
	private static final String WEBHOOK_URI = new StringBuilder("/v1/").append(BASE_SITE_ID)//
			.append("/b2bwebhook").toString();

	private static final String INVALID_TOKEN = "INVALID.JWT.TOKEN";
	private static final String VALID_TOKEN = "eyJhbGciOiJSUzI1NiIsImtpZCI6IlJFUTBNVVE1TjBOQ1JUSkVNemszTTBVMVJrTkRRMFUwUTBNMVJFRkJSamhETWpkRU5VRkJRZyIsInR5cCI6IkpXVCIsImRjIjoiZXUxIn0.eyJzdWIiOiJsaUlPVUpXeE1RaUpubU5fdEV5elBfeGs1MXhyamFVNmtZZEFXS3lvQ0ZjIiwianRpIjoiYTY5YzdmNGUtOTU1ZS00MTg5LTk3YjUtOGYzMTRkMDYwNjZmIiwiZXhwIjoxNjQ2MDIwNDQyLCJpYXQiOjE2NDYwMjAzODIsImlzcyI6Imh0dHBzOi8vZmlkbS5naWd5YS5jb20vand0LzNfQUJBNmYzVmprOHBhcGJWVDlTTjd4UzQwWldMWWY4TTB0SFNyejdxSmZ0NHJnV0ZVS1c0Q3lKa0JxZ2dTeENkMi8ifQ.R3AjxhNCDozdLueqpnzvFfd9v-naS7Y63cW9TfMXm7ScjtL9uRNmkfk2eTHHlyYkQUplB3n6AHWUUwn3Uz4_levjn1xzEqGQ5iVgdoFztQaDXLOBNIkv4hS12XnpYtiHJoT_yPiAH_Pjwl9QGg6ELWzSU55SmFjrzyIkmO7RhdbKlxK1imAmlEdADAM4vSS4OkF8V0NveVd4EuahvHaT_o5T9y6fsXJN8mh-Ay1RUoIKp1Px_P7B07FLD1sGOThZF4ssLX_74gRj0QjZf4j_d7cLjYt17XR8Iu3xge8nqTDUr2pKwS1xiYWomPgEmiaPrc-rjFP4H9zTzzntirKjOg";
	private static final String GIGYA_JWT_HEADER = "x-gigya-sig-jwt";
	private static final String ERROR = "error";
	private static final String SUCCESS = "success";

	private static final String NONCE = "a69c7f4e-955e-4189-97b5-8f314d06066f";
	private static final Long WEBHOOK_REQUEST_TIMESTAMP = 1646020382L;
	private static final String WEBHOOK_REQUEST_EVENT1_TYPE = "accountUpdated";
	private static final String WEBHOOK_REQUEST_EVENT1_ID = "0a8ba913-5c70-4654-988d-767c3fe50d5c";
	private static final Long WEBHOOK_REQUEST_EVENT1_TIMESTAMP = 1646020379L;
	private static final String WEBHOOK_REQUEST_EVENT1_CALL_ID = "865786b118bd43e9a27e8e1d12f40d5f";
	private static final String WEBHOOK_REQUEST_VERSION = "2.0";
	private static final String WEBHOOK_REQUEST_APIKEY = "3_ABA6f3Vjk8papbVT9SN7xS40ZWLYf8M0tHSrz7qJft4rgWFUKW4CyJkBqggSxCd2";
	private static final String WEBHOOK_REQUEST_ACCOUNT_TYPE = "full";
	private static final String WEBHOOK_REQUEST_EVENT1_UID = "82ef565b87c642c7b744b265fdcaf10a";

	private static final String WEBHOOK_REQUEST_EVENT2_TYPE = "accountCreated";
	private static final String WEBHOOK_REQUEST_EVENT2_ID = "062f96ac-cb0a-4736-a836-976a26440b3b";
	private static final Long WEBHOOK_REQUEST_EVENT2_TIMESTAMP = 1646020379L;
	private static final String WEBHOOK_REQUEST_EVENT2_CALL_ID = "865786b118bd43e9a27e8e1d12f40d5f";
	private static final String WEBHOOK_REQUEST_EVENT2_UID = "0e7896c274c54081a0d3067a23dd6874";

	private WsRequestBuilder wsRequestBuilder;
	private WsSecuredRequestBuilder wsSecuredRequestBuilder;

	@Before
	public void setUp() throws Exception {
		wsRequestBuilder = new WsRequestBuilder()//
				.extensionName(Gigyab2bwebservicesConstants.EXTENSIONNAME);

		wsSecuredRequestBuilder = new WsSecuredRequestBuilder()//
				.extensionName(Gigyab2bwebservicesConstants.EXTENSIONNAME)//
				.client(OAUTH_CLIENT_ID, OAUTH_CLIENT_PASS)//
				.grantClientCredentials();
		
		createCoreData();
		createDefaultUsers();
		importCsv("/gigyab2bwebservices/test/essential-data.impex", "utf-8");
	}

	@Test
	public void testGigyaWebHookWithoutAuthorization() {
		final Response result = wsRequestBuilder//
				.path(WEBHOOK_URI)//
				.build()//
				.accept(MediaType.APPLICATION_JSON)//
				.get();
		result.bufferEntity();
		WebservicesAssert.assertResponse(Status.UNAUTHORIZED, result);
	}

	@Test
	public void testGigyaWebHookWithInvalidSignature() {
		final GigyaWebHookRequest gigyawebhookRequest = new GigyaWebHookRequest();
		final Response result = wsSecuredRequestBuilder//
				.path(WEBHOOK_URI)//
				.build()//
				.header(GIGYA_JWT_HEADER, INVALID_TOKEN)//
				.accept(MediaType.APPLICATION_JSON)//
				.post(Entity.entity(gigyawebhookRequest, MediaType.APPLICATION_JSON));
		result.bufferEntity();
		WebservicesAssert.assertResponse(Status.BAD_REQUEST, result);
	}

	@Test
	public void testGigyaWebHookWithEmptyBody() {
		final GigyaWebHookRequest gigyawebhookRequest = new GigyaWebHookRequest();
		final Response result = wsSecuredRequestBuilder//
				.path(WEBHOOK_URI)//
				.build()//
				.header(GIGYA_JWT_HEADER, VALID_TOKEN)//
				.accept(MediaType.APPLICATION_JSON)//
				.post(Entity.entity(gigyawebhookRequest, MediaType.APPLICATION_JSON));
		result.bufferEntity();
		WebservicesAssert.assertResponse(Status.BAD_REQUEST, result);
	}

}
