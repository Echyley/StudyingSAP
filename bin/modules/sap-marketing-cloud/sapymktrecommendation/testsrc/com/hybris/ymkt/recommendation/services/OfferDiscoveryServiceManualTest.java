/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.ymkt.recommendation.services;

import de.hybris.bootstrap.annotations.ManualTest;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.hybris.ymkt.common.http.HttpURLConnectionService;
import com.hybris.ymkt.common.odata.ODataConvertEdmService;
import com.hybris.ymkt.common.odata.ODataService;
import com.hybris.ymkt.recommendation.dao.OfferRecommendationScenario;
import com.hybris.ymkt.recommendation.dao.OfferRecommendationScenario.ContextParam;


/* This is a manual test.
 * Correct URL and credentials are required.
 * Test data in SAP Marketing must be setup for results to be returned
 */
@ManualTest
public class OfferDiscoveryServiceManualTest
{
	static OfferDiscoveryService offerDiscoveryService = new OfferDiscoveryService();
	static ODataService oDataService = new ODataService();
	static HttpURLConnectionService httpURLConnectionService = new HttpURLConnectionService();
	static ODataConvertEdmService oDataConvertEdmService = new ODataConvertEdmService();

	public static void disableCertificates() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchAlgorithmException, KeyManagementException
	{
		final TrustManager[] trustAllCerts =
		{ (TrustManager) Proxy.getProxyClass(X509TrustManager.class.getClassLoader(), X509TrustManager.class)
				.getConstructor(InvocationHandler.class).newInstance((InvocationHandler) (o, m, args) -> null) };

		final SSLContext sc = SSLContext.getInstance("TLSv1.2");
		sc.init(null, trustAllCerts, new SecureRandom());
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
	}

	@BeforeClass
	public static void setUpBeforeClass() throws NoSuchAlgorithmException, InvocationTargetException, KeyManagementException, NoSuchMethodException, InstantiationException, IllegalAccessException
	{
		disableCertificates();
		oDataService.setHttpURLConnectionService(httpURLConnectionService);
		oDataService.setODataConvertEdmService(oDataConvertEdmService);

		oDataService.setRootUrl("https://localhost:11100/sap/opu/odata/sap/PROD_RECO_RUNTIME_SRV/");
		oDataService.setUser("");
		oDataService.setPassword("");

		offerDiscoveryService.setODataService(oDataService);
	}

	@Test
	public void testWithResult() throws IOException
	{
		final OfferRecommendationScenario offerRecoScenario = new OfferRecommendationScenario("6de4ae57e795a737", "COOKIE_ID",
				"PHX_OFFER_SCENARIO_2");
		offerRecoScenario.getContextParams().add(new ContextParam(1, "P_LANGUAGE", "EN"));
		offerRecoScenario.getContextParams().add(new ContextParam(2, "P_POSITION", "Home"));
		offerRecoScenario.getContextParams().add(new ContextParam(3, "P_COMM_MEDIUM", "ONLINE_SHOP"));

		offerDiscoveryService.executeOfferRecommendation(offerRecoScenario);
		Assert.assertNotNull(offerRecoScenario);
		Assert.assertFalse(offerRecoScenario.getResults().isEmpty());
	}

}
