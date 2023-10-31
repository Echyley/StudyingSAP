/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saporderexchangebackoffice.widgets;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Label;

import de.hybris.platform.sap.saporderexchangebackoffice.services.SaporderexchangebackofficeService;

import com.hybris.cockpitng.util.DefaultWidgetController;


public class SaporderexchangebackofficeController extends DefaultWidgetController
{
	private static final long serialVersionUID = 1L;
	private Label label;

	@WireVariable
	private transient SaporderexchangebackofficeService saporderexchangebackofficeService;

	@Override
	public void initialize(final Component comp)
	{
		super.initialize(comp);
		label.setValue(saporderexchangebackofficeService.getHello() + " SaporderexchangebackofficeController");
	}
}
