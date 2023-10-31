/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.yprofile.profiletagaddon.component.renderer;

import com.hybris.yprofile.profiletagaddon.model.ProfileTagScriptComponentModel;
import de.hybris.platform.addonsupport.renderer.impl.DefaultAddOnCMSComponentRenderer;
import de.hybris.platform.cms2.servicelayer.services.CMSComponentService;
import de.hybris.platform.servicelayer.exceptions.AttributeNotSupportedException;
import de.hybris.platform.servicelayer.model.ModelService;
import com.hybris.yprofile.profiletagaddon.constants.ProfiletagaddonConstants;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.jsp.PageContext;
import org.apache.log4j.Logger;

public class ProfileTagScriptComponentRenderer<C extends ProfileTagScriptComponentModel> extends
		DefaultAddOnCMSComponentRenderer<C>
{

	private static final Logger LOG = Logger.getLogger( ProfileTagScriptComponentRenderer.class.getName() );

	CMSComponentService cmsComponentService;
	ModelService modelService;

	@Override
	public void setCmsComponentService(final CMSComponentService cmsComponentService)
	{
		this.cmsComponentService = cmsComponentService;
	}

	@Override
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	//
	@Override
	protected Map<String, Object> getVariablesToExpose(final PageContext pageContext, final C component)
	{
		final Map<String, Object> variables = new HashMap<>();
		for (final String property : cmsComponentService.getEditorProperties(component))
		{
			try
			{
				final Object value = modelService.getAttributeValue(component, property);
				variables.put(property, value);

			}
			catch (final AttributeNotSupportedException ignore)
			{
				// ignore
			}
		}
		return variables;
	}

	@Override
	protected String getAddonUiExtensionName(final C component)
	{
		LOG.debug(String.format("Using extension name: %s", ProfiletagaddonConstants.EXTENSIONNAME));
		return ProfiletagaddonConstants.EXTENSIONNAME;
	}
}
