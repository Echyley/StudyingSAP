/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.configuration.controllers;

import de.hybris.platform.integrationbackoffice.constants.IntegrationbackofficeConstants;
import de.hybris.platform.integrationbackoffice.exceptions.ExportConfigurationModelNotFoundException;
import de.hybris.platform.integrationbackoffice.widgets.common.controllers.AbstractIntegrationEditorContainerController;
import de.hybris.platform.integrationbackoffice.widgets.configuration.data.ExportConfigurationEditorPresentation;
import de.hybris.platform.integrationbackoffice.widgets.configuration.generators.ExportConfigurationGenerator;
import de.hybris.platform.integrationbackoffice.widgets.modeling.services.ExportConfigurationEditorPresentationPopulator;
import de.hybris.platform.odata2services.dto.ConfigurationBundleEntity;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;

import org.zkoss.lang.Strings;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.annotation.WireVariable;

import com.hybris.backoffice.widgets.notificationarea.event.NotificationEvent.Level;
import com.hybris.cockpitng.annotations.SocketEvent;
import com.hybris.cockpitng.util.notifications.NotificationService;

/**
 * Controller containing the selection pane and listbox of the export configuration editor.
 */
public class ExportConfigurationEditorContainerController extends AbstractIntegrationEditorContainerController
{
	public static final String EXPORT_CONFIG_EVENT_IN_SOCKET = "exportConfigEvent";
	public static final String REFRESH_EDITOR_OUT_SOCKET = "refreshEditor";

	@WireVariable
	private transient ExportConfigurationEditorPresentationPopulator exportConfigPresentationPopulator;
	private transient ExportConfigurationEditorPresentation exportConfigPresentation;
	@WireVariable
	private transient ExportConfigurationGenerator exportConfigGenerator;
	@WireVariable
	private transient NotificationService notificationService;

	@Override
	public void initialize(final Component component)
	{
		super.initialize(component);
		exportConfigPresentation = exportConfigPresentationPopulator.getExportConfigEditorPresentation();
	}

	@SocketEvent(socketId = EXPORT_CONFIG_EVENT_IN_SOCKET)
	public void handleExportConfigDownload(final String message)
	{
		final ConfigurationBundleEntity configurationBundleEntity = exportConfigPresentation.generateConfigurationBundleEntity();
		if (configurationBundleEntity.getIntegrationObjectBundles().isEmpty())
		{
			notificationService.notifyUser(Strings.EMPTY, IntegrationbackofficeConstants.NOTIFICATION_TYPE, Level.WARNING,
					getLabel("integrationbackoffice.exportConfigurationEditorContainer.noInstancesSelected"));
		}
		else
		{
			downloadExportConfig(configurationBundleEntity);
		}
	}

	private void downloadExportConfig(final ConfigurationBundleEntity configurationBundleEntity)
	{
		try
		{
			exportConfigGenerator.downloadExportConfig(configurationBundleEntity);
		}
		catch (final ModelNotFoundException e)
		{
			notificationService.notifyUser(Strings.EMPTY, IntegrationbackofficeConstants.NOTIFICATION_TYPE, Level.WARNING,
					new ExportConfigurationModelNotFoundException(e));
		}
	}

	@SocketEvent(socketId = REFRESH_CONTAINER_IN_SOCKET)
	public void refreshButtonOnClick(final String message)
	{
		exportConfigPresentation.clearSelection();
		sendOutput(REFRESH_EDITOR_OUT_SOCKET, "");
	}
}
