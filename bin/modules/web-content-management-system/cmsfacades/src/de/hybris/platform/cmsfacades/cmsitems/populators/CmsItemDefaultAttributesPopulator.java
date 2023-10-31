/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.cmsitems.populators;

import de.hybris.platform.cms2.common.functions.Converter;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.springframework.beans.factory.annotation.Required;

import java.util.Date;
import java.util.Map;

import static de.hybris.platform.core.model.ItemModel.CREATIONTIME;
import static de.hybris.platform.core.model.ItemModel.MODIFIEDTIME;


/**
 * This populator sets the default CmsItem properties, such as item type, creation time, and modified time.
 */
public class CmsItemDefaultAttributesPopulator extends AbstractCMSItemPopulator
{
	// --------------------------------------------------------------------------
	// Variables
	// --------------------------------------------------------------------------
	private Converter<Date, String> dateConverter;

	// --------------------------------------------------------------------------
	// Public Methods
	// --------------------------------------------------------------------------
	@Override
	public void populate(ItemModel source, Map<String, Object> targetMap) throws ConversionException
	{
		if (isAttributeAllowed(source, ItemModel.ITEMTYPE))
		{
			targetMap.put(ItemModel.ITEMTYPE, source.getItemtype());
		}
		if (isAttributeAllowed(source, CREATIONTIME))
		{
			targetMap.put(CREATIONTIME, getDateConverter().convert(source.getCreationtime()));
		}
		if (isAttributeAllowed(source, MODIFIEDTIME))
		{
			targetMap.put(MODIFIEDTIME, getDateConverter().convert(source.getModifiedtime()));
		}
	}

	// --------------------------------------------------------------------------
	// Getters/Setters
	// --------------------------------------------------------------------------
	protected Converter<Date, String> getDateConverter()
	{
		return dateConverter;
	}

	@Required
	public void setDateConverter(final Converter<Date, String> dateConverter)
	{
		this.dateConverter = dateConverter;
	}
}
