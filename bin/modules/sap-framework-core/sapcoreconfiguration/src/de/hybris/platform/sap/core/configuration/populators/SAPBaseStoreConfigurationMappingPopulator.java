/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.configuration.populators;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.store.BaseStoreModel;

import java.util.Map;

import org.apache.log4j.Logger;


/**
 * Populator for transferring the mapping from Base Store to Base Store Configuration.
 */
public class SAPBaseStoreConfigurationMappingPopulator implements Populator<BaseStoreModel, Map<String, Object>>
{

	private static final Logger LOG = Logger.getLogger(SAPBaseStoreConfigurationMappingPopulator.class.getName());

	@Override
	public void populate(final BaseStoreModel baseStoreModel, final Map<String, Object> propertyMap) throws ConversionException
	{
		propertyMap.put("baseStoreUid", baseStoreModel.getUid());
		final SAPConfigurationModel sapBaseStoreConfiguration = baseStoreModel.getSAPConfiguration();
		// Fill base store configuration name and PK
		if (sapBaseStoreConfiguration == null)
		{
			propertyMap.put("baseStoreConfigurationName", "");
			propertyMap.put("baseStoreConfigurationPK", "");
		}
		else
		{
			propertyMap.put("baseStoreConfigurationName", sapBaseStoreConfiguration.getCore_name());
			propertyMap.put("baseStoreConfigurationPK", sapBaseStoreConfiguration.getPk());
		}
		if (LOG.isDebugEnabled())
		{
			LOG.debug("SAPBaseStoreConfigurationMappingPopulator: Populating the following value from Base Store Uid '"
					+ baseStoreModel.getUid() + "' :" + propertyMap);
		}
	}

}
