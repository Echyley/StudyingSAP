/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.modeling.controllers;

import de.hybris.platform.integrationbackoffice.widgets.common.controllers.AbstractIntegrationButtonPanelController;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;

import com.hybris.cockpitng.annotations.GlobalCockpitEvent;
import com.hybris.cockpitng.core.events.CockpitEvent;
import com.hybris.cockpitng.dataaccess.facades.object.ObjectCRUDHandler;

/**
 * Controller for integrationbackoffice modeling toolbar button panel.
 * Enables filtering, refreshing and saving of currently selected {@link IntegrationObjectModel}.
 */
public class IntegrationObjectButtonPanelController extends AbstractIntegrationButtonPanelController
{
	protected static final String REFRESHCOMFIRM_TITLE_LABEL_KEY = "integrationbackoffice.buttonPanel.warning.title.refreshConfirmation";
	protected static final String REFRESHCOMFIRM_MSG_LABEL_KEY = "integrationbackoffice.buttonPanel.warning.msg.refreshConfirmation";

	private static final Class<IntegrationObjectModel> INTEGRATION_OBJECT_MODEL_CLASS = IntegrationObjectModel.class;

	@GlobalCockpitEvent(eventName = ObjectCRUDHandler.OBJECT_CREATED_EVENT, scope = CockpitEvent.SESSION)
	public void objectCreatedEvent(final CockpitEvent event)
	{
		if (eventContainsIntegrationObject(event))
		{
			setSaveDefinitionsButtonState(true);
			setFilterButtonState(false);
		}
	}

	@GlobalCockpitEvent(eventName = ObjectCRUDHandler.OBJECTS_DELETED_EVENT, scope = CockpitEvent.SESSION)
	public void objectDeletedEvent(final CockpitEvent event)
	{
		if (eventContainsIntegrationObject(event))
		{
			setSaveDefinitionsButtonState(true);
			setFilterButtonState(true);
		}
	}

	@Override
	protected boolean unsavedChangesPresent()
	{
		return !isSaveDefinitionsButtonDisabled();
	}

	@Override
	protected void handleRefreshConfirmation()
	{
		showRefreshConfirmation(REFRESHCOMFIRM_TITLE_LABEL_KEY, REFRESHCOMFIRM_MSG_LABEL_KEY);
	}

	private boolean eventContainsIntegrationObject(final CockpitEvent event)
	{
		return INTEGRATION_OBJECT_MODEL_CLASS.isInstance(event.getData()) &&
				event.getDataAsCollection().stream().anyMatch(INTEGRATION_OBJECT_MODEL_CLASS::isInstance);
	}
}
