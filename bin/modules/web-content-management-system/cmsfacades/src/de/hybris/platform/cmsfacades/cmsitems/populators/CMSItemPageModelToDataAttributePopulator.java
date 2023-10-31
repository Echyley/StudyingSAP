/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.cmsitems.populators;

import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_MASTER_TEMPLATE_ID;

import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Map;


/**
 * Populator that populates the masterTemplateId field for a given page.
 */
public class CMSItemPageModelToDataAttributePopulator extends AbstractCMSItemPopulator
{
	@Override
	public void populate(final ItemModel itemModel, final Map<String, Object> itemMap) throws ConversionException
	{
		if (isAttributeAllowed(itemModel, FIELD_MASTER_TEMPLATE_ID))
		{
			final AbstractPageModel page = (AbstractPageModel) itemModel;
			itemMap.put(FIELD_MASTER_TEMPLATE_ID, page.getMasterTemplate().getUid());
		}
	}
}
