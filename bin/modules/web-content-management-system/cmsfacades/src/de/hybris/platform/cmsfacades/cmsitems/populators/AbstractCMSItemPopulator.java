/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.cmsitems.populators;

import de.hybris.platform.cmsfacades.cmsitems.CMSItemAttributeFilterEnablerService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.ItemModel;

import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Required;


/**
 * Abstract populator that validates whether the attribute is allowed by {@link CMSItemAttributeFilterEnablerService} or not.
 */
public abstract class AbstractCMSItemPopulator implements Populator<ItemModel, Map<String, Object>>
{
	private CMSItemAttributeFilterEnablerService cmsItemAttributeFilterEnablerService;

	/**
	 * Verifies whether the attribute can be returned.
	 * @param source the source object
	 * @param attribute the attribute
	 * @return true if possible, false otherwise.
	 */
	protected boolean isAttributeAllowed(ItemModel source, String attribute)
	{
		if (Objects.isNull(source))
		{
			return true;
		}
		return getCmsItemAttributeFilterEnablerService().isAttributeAllowed(source.getItemtype(), attribute);
	}

	public CMSItemAttributeFilterEnablerService getCmsItemAttributeFilterEnablerService()
	{
		return cmsItemAttributeFilterEnablerService;
	}

	@Required
	public void setCmsItemAttributeFilterEnablerService(CMSItemAttributeFilterEnablerService cmsItemAttributeFilterEnablerService)
	{
		this.cmsItemAttributeFilterEnablerService = cmsItemAttributeFilterEnablerService;
	}
}
