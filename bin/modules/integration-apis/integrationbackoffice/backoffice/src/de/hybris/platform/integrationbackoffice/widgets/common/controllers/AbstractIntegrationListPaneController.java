/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationbackoffice.widgets.common.controllers;

import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;

import com.hybris.cockpitng.util.DefaultWidgetController;

/**
 * Controller with common functionality across integrationbackoffice list pane controllers.
 * Contains a listbox of objects to navigate through.
 */
public abstract class AbstractIntegrationListPaneController extends DefaultWidgetController
{
	protected Listbox listPaneListbox;
	protected Listhead listPaneListhead;
	protected Listheader listPaneListheader;

	public abstract void populateListbox();

	public abstract void addListitemEvent(final Listitem listitem);

	protected void setListheaderLabel(final String label)
	{
		listPaneListheader.setLabel(label);
	}
}
