/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.subscriptionbundlecockpits.session.impl;

import de.hybris.platform.cockpit.session.BrowserModel;
import de.hybris.platform.cockpit.session.PopupEditorAreaController;
import de.hybris.platform.cockpit.session.impl.DefaultEditorAreaController;

import java.util.HashMap;
import java.util.Map;



/**
 * Subscriptionbundlecockpits popup editor area controller.
 */
public class SubscriptionbundlecockpitsPopupEditorAreaControllerImpl extends DefaultEditorAreaController implements PopupEditorAreaController
{

	private transient BrowserModel contextEditorBrowser = null;
	private final Map<String, Object> attributes = new HashMap<String, Object>();

	public Map<String, Object> getAttributesMap()
	{
		return attributes;
	}

	@Override
	public void setContextEditorBrowser(final BrowserModel model)
	{
		this.contextEditorBrowser = model;
	}

	public BrowserModel getContextEditorBrowserModel()
	{
		return contextEditorBrowser;
	}
}
