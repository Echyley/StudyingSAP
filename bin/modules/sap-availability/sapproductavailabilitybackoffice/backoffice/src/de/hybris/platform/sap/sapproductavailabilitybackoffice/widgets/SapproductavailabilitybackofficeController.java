/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapproductavailabilitybackoffice.widgets;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Label;

import de.hybris.platform.sap.sapproductavailabilitybackoffice.services.SapproductavailabilitybackofficeService;

import com.hybris.cockpitng.util.DefaultWidgetController;


public class SapproductavailabilitybackofficeController extends DefaultWidgetController
{
	private static final long serialVersionUID = 1L;
	private Label label;

	@SuppressWarnings("squid:S1948")
	@WireVariable
	private SapproductavailabilitybackofficeService sapproductavailabilitybackofficeService;

	@Override
	public void initialize(final Component comp)
	{
		super.initialize(comp);
		label.setValue(sapproductavailabilitybackofficeService.getHello() + " sapproductavailabilitybackofficeController");
	}
}
