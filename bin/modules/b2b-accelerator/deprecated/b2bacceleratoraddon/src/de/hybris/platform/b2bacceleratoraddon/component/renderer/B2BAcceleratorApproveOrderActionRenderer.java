/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2bacceleratoraddon.component.renderer;

import de.hybris.platform.addonsupport.renderer.impl.DefaultAddOnCMSComponentRenderer;
import de.hybris.platform.b2bacceleratoraddon.constants.B2bacceleratoraddonConstants;
import de.hybris.platform.b2bacceleratorservices.model.actions.ApproveOrderActionModel;

import java.util.Map;

import javax.servlet.jsp.PageContext;

public class B2BAcceleratorApproveOrderActionRenderer<C extends ApproveOrderActionModel> extends
		DefaultAddOnCMSComponentRenderer<C>
{
	private static final String COMPONENT = "component";

	@Override
	protected Map<String, Object> getVariablesToExpose(final PageContext pageContext, final C component)
	{
		final Map<String, Object> model = super.getVariablesToExpose(pageContext, component);
		model.put(COMPONENT, component);
		return model;
	}

	@Override
	protected String getAddonUiExtensionName(final C component)
	{
		return B2bacceleratoraddonConstants.EXTENSIONNAME;
	}
}
