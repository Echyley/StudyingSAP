/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationbackoffice.widgets.common.controllers;

import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;

import com.hybris.cockpitng.testing.AbstractWidgetUnitTest;

public abstract class AbstractIntegrationListPaneControllerUnitTest<T extends AbstractIntegrationListPaneController>
		extends AbstractWidgetUnitTest<AbstractIntegrationListPaneController>
{
	private AbstractIntegrationListPaneController controller;

	public void setup()
	{
		this.controller = getWidgetController();
		this.controller.listPaneListheader = new Listheader();
		this.controller.listPaneListbox = new Listbox();
		this.controller.listPaneListhead = new Listhead();
	}

	protected Listheader listheader()
	{
		return controller.listPaneListheader;
	}

	protected Listbox listbox()
	{
		return controller.listPaneListbox;
	}

	protected Listhead listhead()
	{
		return controller.listPaneListhead;
	}
}
