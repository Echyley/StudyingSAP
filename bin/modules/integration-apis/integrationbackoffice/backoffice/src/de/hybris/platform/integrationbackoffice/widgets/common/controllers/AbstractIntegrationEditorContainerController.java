/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.common.controllers;

import com.hybris.cockpitng.util.DefaultWidgetController;

/**
 * Controller with common functionality across integrationbackoffice editor container controllers
 */
public abstract class AbstractIntegrationEditorContainerController extends DefaultWidgetController
{
	public static final String REFRESH_CONTAINER_IN_SOCKET = "refreshEvent";
	public static final String FILTER_STATE_CHANGE_IN_SOCKET = "filterStateChangeInput";
	public static final String FILTER_STATE_CHANGE_OUT_SOCKET = "filterStateChangeOutput";
	public static final String ENABLE_SAVE_BUTTON_OUT_SOCKET = "enableSaveButton";
}
