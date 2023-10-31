/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.test.charon;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManagerFactory;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.SocketUtils;

import com.hybris.charon.Charon;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsParameters;
import com.sun.net.httpserver.HttpsServer;

import io.netty.handler.ssl.SslHandshakeTimeoutException;

@IntegrationTest
public class CharonTest extends ServicelayerBaseTest
{
	private static final String TRUSTSTORE_PATH = "test/test_charon_truststore_client.keystore";
	private static final String KEYSTORE_PATH = "test/test_charon_keystore_server.keystore";

	private static final String PROPERTY_TRUSTSTORE = "javax.net.ssl.trustStore";
	private static final String PROPERTY_TRUSTSTORE_PASSWORD = "javax.net.ssl.trustStorePassword";

	private static final int PORT_HTTP = SocketUtils.findAvailableTcpPort();
	private static final int PORT_HTTPS = SocketUtils.findAvailableTcpPort();

	private static final String ENDPOINT_PATH = "/test-endpoint";
	private static final String RESPONSE_MESSAGE = "Simple response body message";

	@Test
	public void shouldCommunicateWithServerUsingHttp() throws Exception
	{
		final HttpClient client = Charon.from(HttpClient.class)
		                                .url("http://localhost:" + PORT_HTTP)
		                                .build();

		assertThat(isServerReachable(PORT_HTTP)).isFalse();
		assertThatThrownBy(client::getResponse).hasMessageContaining("Connection refused");

		final HttpServer server = HttpServerFactory.newHttpServer();
		try
		{
			server.start();
			assertThat(isServerReachable(PORT_HTTP)).isTrue();
			assertThat(client.getResponse()).isEqualTo(RESPONSE_MESSAGE);
		}
		finally
		{
			server.stop(0);
			assertThat(isServerReachable(PORT_HTTP)).isFalse();
		}
	}

	@Test
	public void shouldCommunicateWithServerUsingHttps() throws Exception
	{
		final String initialTrustStore = System.setProperty(PROPERTY_TRUSTSTORE, retrieveNewTruststoreAbsolutePath());
		final String initialTrustStorePassword = System.setProperty(PROPERTY_TRUSTSTORE_PASSWORD, "password");

		assertThat(isServerReachable(PORT_HTTPS)).isFalse();
		final HttpServer server = HttpServerFactory.newHttpsServer(new ClassPathResource(KEYSTORE_PATH).getFile());
		try
		{
			final HttpClient client = Charon.from(HttpClient.class)
			                                .url("https://localhost:" + PORT_HTTPS)
			                                .build();

			assertThatThrownBy(client::getResponse).hasCauseInstanceOf(SslHandshakeTimeoutException.class);

			server.start();
			assertThat(isServerReachable(PORT_HTTPS)).isTrue();
			assertThat(client.getResponse()).isEqualTo(RESPONSE_MESSAGE);
		}
		finally
		{
			restoreSystemProperties(initialTrustStore, initialTrustStorePassword);
			server.stop(0);
			assertThat(isServerReachable(PORT_HTTPS)).isFalse();
		}
	}

	private String retrieveNewTruststoreAbsolutePath() throws IOException
	{
		final java.nio.file.Path truststoreAbsolutePath = new ClassPathResource(TRUSTSTORE_PATH).getFile()
		                                                                                        .toPath()
		                                                                                        .toAbsolutePath();

		assertThat(truststoreAbsolutePath).exists().isAbsolute().isRegularFile();

		return truststoreAbsolutePath.toString();
	}

	private void restoreSystemProperties(final String initialTrustStore, final String initialTrustStorePassword)
	{
		if (initialTrustStore == null)
		{
			System.clearProperty(PROPERTY_TRUSTSTORE);
		}
		else
		{
			System.setProperty(PROPERTY_TRUSTSTORE, initialTrustStore);
		}
		assertThat(System.getProperty(PROPERTY_TRUSTSTORE)).isEqualTo(initialTrustStore);

		if (initialTrustStorePassword == null)
		{
			System.clearProperty(PROPERTY_TRUSTSTORE_PASSWORD);
		}
		else
		{
			System.setProperty(PROPERTY_TRUSTSTORE_PASSWORD, initialTrustStorePassword);
		}
		assertThat(System.getProperty(PROPERTY_TRUSTSTORE_PASSWORD)).isEqualTo(initialTrustStorePassword);
	}

	private boolean isServerReachable(final int port)
	{
		try (final Socket socket = new Socket())
		{
			socket.connect(new InetSocketAddress("localhost", port), 1000);
			return true;
		}
		catch (final IOException ex)
		{
			return false; // Timeout, unreachable or failed DNS lookup.
		}
	}

	interface HttpClient
	{
		@GET
		@Path(ENDPOINT_PATH)
		String getResponse();
	}

	static class ServerHttpHandler implements HttpHandler
	{
		@Override
		public void handle(final HttpExchange exchange) throws IOException
		{
			exchange.sendResponseHeaders(200, RESPONSE_MESSAGE.getBytes().length);
			try (final OutputStream os = exchange.getResponseBody())
			{
				os.write(RESPONSE_MESSAGE.getBytes());
			}
		}
	}

	static class HttpServerFactory
	{
		static HttpServer newHttpServer() throws Exception
		{
			final HttpServer server = HttpServer.create(new InetSocketAddress(PORT_HTTP), 0);
			server.createContext(ENDPOINT_PATH, new ServerHttpHandler());
			server.setExecutor(null);
			return server;
		}

		static HttpServer newHttpsServer(final File serverKeystoreFile) throws Exception
		{
			final HttpsServer server = HttpsServer.create(new InetSocketAddress(PORT_HTTPS), 0);
			server.createContext(ENDPOINT_PATH, new ServerHttpHandler());
			server.setExecutor(null);

			server.setHttpsConfigurator(new HttpsConfigurator(createSSLContext(serverKeystoreFile))
			{
				@Override
				public void configure(final HttpsParameters params)
				{
					final SSLContext ctx = getSSLContext();
					final SSLEngine engine = ctx.createSSLEngine();
					params.setNeedClientAuth(false);
					params.setCipherSuites(engine.getEnabledCipherSuites());
					params.setProtocols(engine.getEnabledProtocols());

					final SSLParameters sslParameters = ctx.getSupportedSSLParameters();
					params.setSSLParameters(sslParameters);
				}
			});
			return server;
		}

		private static SSLContext createSSLContext(final File serverKeystoreFile) throws Exception
		{
			final SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
			final char[] password = "password".toCharArray();

			final KeyStore ks = KeyStore.getInstance("PKCS12");
			try (final FileInputStream fis = new FileInputStream(serverKeystoreFile))
			{
				ks.load(fis, password);
			}

			final KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
			kmf.init(ks, password);

			final TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
			tmf.init(ks);

			sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

			return sslContext;
		}
	}
}
