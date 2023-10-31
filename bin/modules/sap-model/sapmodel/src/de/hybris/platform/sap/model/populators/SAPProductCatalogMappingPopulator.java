/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.model.populators;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.sap.sapmodel.model.SAPProductSalesAreaToCatalogMappingModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Map;

import org.apache.log4j.Logger;

/**
 * Populator for additional fields specific for the SAPProductCatalog
 */
public class SAPProductCatalogMappingPopulator implements Populator<SAPProductSalesAreaToCatalogMappingModel, Map<String, Object>>
{
	protected static final Logger LOGGER = Logger.getLogger(SAPProductCatalogMappingPopulator.class);
	
	public void populate(final SAPProductSalesAreaToCatalogMappingModel source, final Map<String, Object> target) throws ConversionException
	{
		CountryModel countryModel = source.getTaxClassCountry();
		
		if (countryModel == null)	
		{
			LOGGER.error("Missing customizing for Product to Catalog Mapping!");
			return;
		}

		target.put("taxClassCountry", source.getTaxClassCountry().getIsocode());
	}
}
