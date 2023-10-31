/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.ymkt.common.odata;

import de.hybris.bootstrap.annotations.UnitTest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.zip.GZIPOutputStream;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.hybris.ymkt.common.http.HttpURLConnectionService;


@UnitTest
public class ODataServiceTest
{
	ODataService oDataService = new ODataService();
	HttpURLConnectionService httpURLConnectionService = new HttpURLConnectionService();


	public static void disableCertificates() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchAlgorithmException, KeyManagementException
	{
		final TrustManager[] trustAllCerts =
		{ (TrustManager) Proxy.getProxyClass(X509TrustManager.class.getClassLoader(), X509TrustManager.class)
				.getConstructor(InvocationHandler.class).newInstance((InvocationHandler) (o, m, args) -> null) };

		final SSLContext sc = SSLContext.getInstance("TLSv1.2");
		sc.init(null, trustAllCerts, new SecureRandom());
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
	}

	@Before
	public void setUp() throws NoSuchAlgorithmException, InvocationTargetException, KeyManagementException, NoSuchMethodException, InstantiationException, IllegalAccessException
	{
		disableCertificates();

		oDataService.setHttpURLConnectionService(httpURLConnectionService);
		oDataService.setRootUrl("");
		oDataService.setUser("");
		oDataService.setPassword("");
	}

	//	@Test
	public void testGetEdm() throws IOException
	{
		oDataService.getEdm();
	}

	@Test
	public void testIsGZIP() throws IOException
	{
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		final GZIPOutputStream gzipOutputStream = new GZIPOutputStream(out);
		gzipOutputStream.write(56);
		gzipOutputStream.close();
		final byte[] decompressIfGZIP = oDataService.decompressIfGZIP(out.toByteArray());
		Assert.assertEquals(1, decompressIfGZIP.length);
		Assert.assertEquals(56, decompressIfGZIP[0]);
	}

	//	@Test
	public void testRefreshToken() throws IOException
	{
		oDataService.refreshToken(null);
	}

}
