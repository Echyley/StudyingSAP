/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.datahubbackoffice.service.datahub.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

import de.hybris.platform.datahubbackoffice.service.datahub.DataHubNameService;
import de.hybris.platform.datahubbackoffice.service.datahub.DataHubServer;
import de.hybris.platform.datahubbackoffice.service.datahub.DataHubServerInfo;
import de.hybris.platform.datahubbackoffice.service.datahub.InaccessibleDataHubServer;
import de.hybris.platform.dataimportcommons.TestApplicationContext;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DataHubServerContextServiceImplUnitTest
{
	@Rule
	public TestApplicationContext applicationContext = new TestApplicationContext();

	@Mock
	private DataHubNameService nameService;
	@InjectMocks
	private DataHubServerContextServiceImpl serverService;

	@Test(expected = IllegalArgumentException.class)
	public void testSetDataHubConfigurationWhenNull()
	{
		serverService.setDataHubServer(null);
	}

	@Test
	public void testGetContextServerReturnsSelectedServer()
	{
		final DataHubServer server = dataHubServer();

		serverService.setDataHubServer(server);

		assertThat(serverService.getContextDataHubServer()).isEqualTo(server);
	}

	@Test
	public void testGetContextServerReturnsFirstAccessibleServerWhenItWasNotSelected()
	{
		final DataHubServer[] servers = { dataHubServer(), accessibleDataHubServer(), accessibleDataHubServer() };
		final var service = spy(serverService);
		doReturn(Arrays.asList(servers)).when(service).getAllServers();

		assertThat(service.getContextDataHubServer()).isSameAs(servers[1]);
	}

	@Test
	public void testGetContextServerReturnsInaccessibleServerWhenNoServerInfoAvailable()
	{
		doReturn(Collections.emptyList()).when(nameService).getAllServers();

		assertThat(serverService.getContextDataHubServer()).isInstanceOf(InaccessibleDataHubServer.class);
	}

	@Test
	public void testGetContextServerReturnsInaccessibleServerWhenNoneOfTheServersIsAccessible()
	{
		final var service = spy(serverService);
		doReturn(Arrays.asList(dataHubServer(), dataHubServer())).when(service).getAllServers();

		assertThat(service.getContextDataHubServer()).isInstanceOf(InaccessibleDataHubServer.class);
	}

	@Test
	public void testGetAllServersIsEmptyWhenNameServiceDoesNotHaveServerInfoConfigured()
	{
		doReturn(Collections.emptyList()).when(nameService).getAllServers();

		assertThat(serverService.getAllServers()).isEmpty();
	}

	@Test
	public void testGetAllServersConvertsAvailableServerInfoToServers()
	{
		final String[] serverNames = {"Server 1", "Server 2"};
		final var availableInfo = Arrays.stream(serverNames).sequential()
		                                .map(this::serverInfo)
		                                .collect(Collectors.toList());
		doReturn(availableInfo).when(nameService).getAllServers();

		assertThat(serverService.getAllServers())
				.extracting("name")
				.containsExactly(serverNames);
	}

	@Test
	public void testResetUnsetsCurrentAllServersAndTheContextServer()
	{
		final var server = dataHubServer();
		serverService.setDataHubServer(server);
		doReturn(List.of(serverInfo("s1")))
				.doReturn(List.of(serverInfo("s2"), serverInfo("s3")))
				.when(nameService).getAllServers();
		final var serversBeforeReset = serverService.getAllServers();

		serverService.reset();

		assertThat(serverService.getContextDataHubServer())
				.isNotNull()
				.isNotEqualTo(server);
		assertThat(serverService.getAllServers()).isNotEqualTo(serversBeforeReset);
	}

	@Test
	public void testUsesDataHubNameServiceFromTheAppContextWhenItWasNotInjected()
	{
		final var contextNameService = mock(DataHubNameService.class);
		doReturn(List.of(serverInfo("server1"), serverInfo("server2"))).when(contextNameService).getAllServers();
		applicationContext.addBean("dataHubNameService", contextNameService);
		serverService.setNameService(null);

		final Collection<DataHubServer> allServers = serverService.getAllServers();

		assertThat(allServers).extracting("name")
		                      .containsExactly("server1", "server2");
	}

	private DataHubServer accessibleDataHubServer()
	{
		final var server = mock(DataHubServer.class);
		doReturn(true).when(server).isAccessibleWithTimeout();
		return server;
	}

	private DataHubServer dataHubServer()
	{
		return mock(DataHubServer.class);
	}

	private DataHubServerInfo serverInfo(final String name)
	{
		return new DataHubServerInfo(name, "", "", "");
	}
}
