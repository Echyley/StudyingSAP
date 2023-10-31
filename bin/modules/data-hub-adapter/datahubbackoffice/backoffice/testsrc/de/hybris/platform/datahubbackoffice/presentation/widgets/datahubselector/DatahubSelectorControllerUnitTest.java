/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.datahubbackoffice.presentation.widgets.datahubselector;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import de.hybris.platform.datahubbackoffice.service.datahub.DataHubServer;
import de.hybris.platform.datahubbackoffice.service.datahub.DataHubServerContextService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Div;
import org.zkoss.zul.ListModelList;

import com.hybris.backoffice.navigation.NavigationNode;
import com.hybris.cockpitng.testing.AbstractWidgetUnitTest;
import com.hybris.cockpitng.testing.annotation.DeclaredInput;
import com.hybris.cockpitng.testing.annotation.DeclaredViewEvent;
import com.hybris.cockpitng.util.notifications.NotificationService;
import com.hybris.cockpitng.util.notifications.event.NotificationEvent;

@DeclaredViewEvent(componentID = "datahubSelectorList", eventName = Events.ON_CHANGE)
@DeclaredInput(socketType = NavigationNode.class, value = DatahubSelectorControllerUnitTest.PERSPECTIVE_CHANGE_INPUT)
public class DatahubSelectorControllerUnitTest extends AbstractWidgetUnitTest<DatahubSelectorController>
{
	static final String PERSPECTIVE_CHANGE_INPUT = "perspectiveChanged";
	private static final Div parent = Mockito.mock(Div.class);
	private static final String SOURCE = "DataHubSelectorSource";
	private static final String DATAHUB_PERSPECTIVE = "datahubBackofficePerspective";

	@InjectMocks
	private DatahubSelectorController controller;

	@Mock
	private DataHubServerContextService dataHubServerContext;
	@Mock
	private Combobox datahubSelectorList;
	@Mock
	private ListModelList<DataHubServer> datahubSelectorModel;
	@Mock
	private NotificationService notificationService;


	@Before
	public void setup()
	{
		doReturn(SOURCE).when(notificationService).getWidgetNotificationSource(widgetInstanceManager);
	}

	@Test
	public void testSelectorPopulatedUponInitialization()
	{
		simulateDataHubInstances("Local", "Remote");
		controller.initialize(parent);
		verifySelectorContent("Local", "Remote");
	}

	@Test
	public void testContextServerIsResetUponInitialization()
	{
		controller.initialize(parent);

		verify(dataHubServerContext).reset();
	}

	@Test
	public void testExplicitDataHubInstanceSelection()
	{
		simulateDataHubInstances("Local", "Remote");
		final DataHubServer selected = setCurrentSelection("Remote");

		executeViewEvent("datahubSelectorList", Events.ON_CHANGE, new InputEvent("On-Change", null, "Remote", ""));

		assertSocketOutput("datahubSelected", selected);
		reset(datahubSelectorModel);
	}

	@Test
	public void testNotifiesUserWhenDataHubPerspectiveActivatedAndContextDataHubIsNotAccessible()
	{
		final var dataHubServer = inaccessibleDataHubServer();
		doReturn(dataHubServer).when(dataHubServerContext).getContextDataHubServer();

		executeInputSocketEvent(PERSPECTIVE_CHANGE_INPUT, navigationNode(DATAHUB_PERSPECTIVE));

		verify(notificationService).notifyUser(SOURCE, "datahub.selector.error.no.instances", NotificationEvent.Level.FAILURE);
	}

	@Test
	public void testDoesNotNotifyUserWhenDataHubPerspectiveActivatedButContextDataHubIsAccessible()
	{
		final var dataHubServer = dataHubServer("Online");
		doReturn(dataHubServer).when(dataHubServerContext).getContextDataHubServer();

		executeInputSocketEvent(PERSPECTIVE_CHANGE_INPUT, navigationNode("datahubBackofficePerspective"));

		verifyZeroInteractions(notificationService);
	}

	@Test
	public void testDoesNotNotifyUserWhenPerspectiveOtherThanDataHubIsActivated()
	{
		final var dataHubServer = inaccessibleDataHubServer();
		doReturn(dataHubServer).when(dataHubServerContext).getContextDataHubServer();

		executeInputSocketEvent(PERSPECTIVE_CHANGE_INPUT, navigationNode("someNonDataHubPerspective"));

		verifyZeroInteractions(notificationService);
	}

	private NavigationNode navigationNode(final String value)
	{
		final var node = mock(NavigationNode.class);
		doReturn(value).when(node).getId();
		return node;
	}

	private DataHubServer setCurrentSelection(final String name)
	{
		final DataHubServer server = findServerInstance(name);
		when(datahubSelectorModel.getSelection()).thenReturn(Collections.singleton(server));
		return server;
	}

	private DataHubServer findServerInstance(final String name)
	{
		return dataHubServerContext.getAllServers()
								   .stream()
								   .filter(server -> name.equals(server.getName()))
								   .findFirst()
								   .orElse(null);
	}

	private void verifySelectorContent(final String... names)
	{
		final String[] currentNames = controller.getDatahubSelectorModel()
												.getInnerList()
												.stream()
												.map(DataHubServer::getName)
												.toArray(String[]::new);

		assertThat(names).isEqualTo(currentNames);
	}

	private void simulateDataHubInstances(final String... hubs)
	{
		final List<DataHubServer> servers = Arrays.stream(hubs).map(this::dataHubServer).collect(Collectors.toList());
		doReturn(servers).when(dataHubServerContext).getAllServers();
	}

	private DataHubServer dataHubServer(final String name)
	{
		final DataHubServer server = mock(DataHubServer.class);
		doReturn(name).when(server).getName();
		doReturn(true).when(server).isAccessibleWithTimeout();
		return server;
	}

	private DataHubServer inaccessibleDataHubServer()
	{
		final DataHubServer server = mock(DataHubServer.class);
		doReturn(false).when(server).isAccessibleWithTimeout();
		return server;
	}

	@Override
	protected DatahubSelectorController getWidgetController()
	{
		return controller;
	}
}
