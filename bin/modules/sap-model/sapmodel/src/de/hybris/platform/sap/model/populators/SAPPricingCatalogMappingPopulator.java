/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.model.populators;

import java.util.Map;

import org.apache.log4j.Logger;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.sap.sapmodel.model.SAPPricingSalesAreaToCatalogModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

/**
 * Populator for additional fields specific for the SAPPricingSalesAreaToCatalog
 */
public class SAPPricingCatalogMappingPopulator implements Populator<SAPPricingSalesAreaToCatalogModel, Map<String, Object>>
{
	protected static final Logger LOGGER = Logger
			.getLogger(SAPPricingCatalogMappingPopulator.class);
	
	public void populate(final SAPPricingSalesAreaToCatalogModel source, final Map<String, Object> target) throws ConversionException
	{
		final CatalogVersionModel catalogVersion = source.getCatalogVersion();
		if (catalogVersion == null)
		{
			LOGGER.error("Pricing Transfer: Catalog Version is not maintained");
			return;
		}
		final String version = catalogVersion.getVersion();
		final String catalogId = catalogVersion.getCatalog().getId();
		
		final StringBuilder sb = new StringBuilder(100);
		sb.append(catalogId);
		sb.append(":");
		sb.append(version);
		
		if (LOGGER.isDebugEnabled())
		{
			LOGGER.debug(sb.toString());
		}
		
		target.put("catalogVersion", sb.toString());
	}

}
