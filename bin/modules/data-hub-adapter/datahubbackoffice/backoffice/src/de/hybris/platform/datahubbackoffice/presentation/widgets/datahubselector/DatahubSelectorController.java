/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.datahubbackoffice.presentation.widgets.datahubselector;

import de.hybris.platform.datahubbackoffice.WidgetConstants;
import de.hybris.platform.datahubbackoffice.service.datahub.DataHubServer;
import de.hybris.platform.datahubbackoffice.service.datahub.DataHubServerContextService;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.ComboitemRenderer;
import org.zkoss.zul.ListModelList;

import com.hybris.backoffice.navigation.NavigationNode;
import com.hybris.cockpitng.annotations.SocketEvent;
import com.hybris.cockpitng.annotations.ViewEvent;
import com.hybris.cockpitng.util.DefaultWidgetController;
import com.hybris.cockpitng.util.notifications.NotificationService;
import com.hybris.cockpitng.util.notifications.event.NotificationEvent;

public class DatahubSelectorController extends DefaultWidgetController
{
	private static final long serialVersionUID = 4698586679589347152L;
	private static final String NO_DATAHUB_INSTANCES_AVAILABLE = "datahub.selector.error.no.instances";
	private static final String DATAHUB_INSTANCE_ENABLED_SCLASS = "datahubSelector_instance_enabled";
	private static final String DATAHUB_INSTANCE_DISABLED_SCLASS = "datahubSelector_instance_disabled";
	private static final String COMPONENT_DATAHUB_SELECTOR_LIST = "datahubSelectorList";
	private static final String SOCKET_OUT_ID = "datahubSelected";
	private static final String ON_DEFER_CREATION = "onDeferCreation";
	private static final String PERSPECTIVE_CHANGED = "perspectiveChanged";

	@WireVariable
	private transient DataHubServerContextService dataHubServerContext;
	@WireVariable
	private transient NotificationService notificationService;

	private Combobox datahubSelectorList;
	private ListModelList<DataHubServer> datahubSelectorModel;

	@Override
	public void initialize(final Component comp)
	{
		comp.addEventListener(Events.ON_CREATE, event -> Events.echoEvent(ON_DEFER_CREATION, comp, null));
		comp.addEventListener(ON_DEFER_CREATION, event -> selectDefaultDataHubInstance());
		datahubSelectorList.setItemRenderer(createComboItemRenderer());
		populateSelectorComponent();
		dataHubServerContext.reset();
	}

	protected ComboitemRenderer<Object> createComboItemRenderer()
	{
		return (comboitem, data, i) -> {
			if (data instanceof DataHubServer)
			{
				final DataHubServer server = (DataHubServer) data;
				final boolean enabled = server.isAccessibleWithTimeout();
				comboitem.setDisabled(!enabled);
				
				String serverStatus = enabled ? DATAHUB_INSTANCE_ENABLED_SCLASS : DATAHUB_INSTANCE_DISABLED_SCLASS;
				if(StringUtils.isNotEmpty(comboitem.getSclass()))
				{
					serverStatus = String.join(" ", comboitem.getSclass(), serverStatus);
				}
				comboitem.setSclass(serverStatus);

				comboitem.setLabel(server.getName());
				comboitem.setValue(server);
			}
		};
	}

	protected void populateSelectorComponent()
	{
		datahubSelectorModel = new ListModelList<>(dataHubServerContext.getAllServers());
		datahubSelectorList.setModel(datahubSelectorModel);
	}

	protected void selectDefaultDataHubInstance()
	{
		final var server = dataHubServerContext.getContextDataHubServer();
		if (server != null && server.isAccessibleWithTimeout())
		{
			datahubSelectorModel.setSelection(Collections.singleton(server));
			sendOutput(SOCKET_OUT_ID, server);
		}
	}

	@SocketEvent(socketId = PERSPECTIVE_CHANGED)
	public void perspectiveChanged(final NavigationNode node)
	{
		if (node != null
				&& WidgetConstants.DATAHUB_BACKOFFICE_MAIN_WIDGET.equals(node.getId())
				&& !dataHubServerContext.getContextDataHubServer().isAccessibleWithTimeout())
 		{
			notificationService.notifyUser(notificationService.getWidgetNotificationSource(getWidgetInstanceManager()),
					NO_DATAHUB_INSTANCES_AVAILABLE, NotificationEvent.Level.FAILURE);
		}
	}

	@ViewEvent(eventName = Events.ON_CHANGE, componentID = COMPONENT_DATAHUB_SELECTOR_LIST)
	public void selectDataHubInstance(final InputEvent event)
	{
		DataHubServer server = null;
		final Set<DataHubServer> selectedInstances = datahubSelectorModel.getSelection();
		if (CollectionUtils.isNotEmpty(selectedInstances))
		{
			server = datahubSelectorModel.getSelection().iterator().next();
		}
		if (server != null)
		{
			sendOutput(SOCKET_OUT_ID, server);
		}
	}

	protected List<String> getSelectorContent()
	{
		return datahubSelectorList.getItems()
								  .stream()
								  .map(Comboitem::getLabel)
								  .collect(Collectors.toList());
	}

	protected Combobox getDatahubSelectorList()
	{
		return datahubSelectorList;
	}

	protected ListModelList<DataHubServer> getDatahubSelectorModel()
	{
		return datahubSelectorModel;
	}

	public void setNotificationService(final NotificationService notificationService)
	{
		this.notificationService = notificationService;
	}

	public NotificationService getNotificationService()
	{
		return this.notificationService;
	}
}
