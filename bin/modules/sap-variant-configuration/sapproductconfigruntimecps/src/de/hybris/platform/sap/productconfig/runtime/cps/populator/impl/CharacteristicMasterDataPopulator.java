/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.cps.populator.impl;

import de.hybris.platform.sap.productconfig.runtime.cps.masterdata.service.ConfigurationMasterDataService;
import de.hybris.platform.sap.productconfig.runtime.cps.masterdata.service.MasterDataContainerResolver;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataCharacteristicContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSCharacteristic;
import de.hybris.platform.sap.productconfig.runtime.interf.ContextualPopulator;
import de.hybris.platform.sap.productconfig.runtime.interf.TextConverterBase;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;


/**
 * Responsible to populate characteristics
 */
public class CharacteristicMasterDataPopulator implements ContextualPopulator<CPSCharacteristic, CsticModel, MasterDataContext>
{
	private MasterDataContainerResolver masterDataResolver;
	private ConfigurationMasterDataService configurationMasterDataService;
	private TextConverterBase textConverterBase;

	@Override
	public void populate(final CPSCharacteristic source, final CsticModel target, final MasterDataContext ctxt)
	{
		final CPSMasterDataCharacteristicContainer characteristicMasterData = getMasterDataResolver()
				.getCharacteristic(ctxt.getKbCacheContainer(), source.getId());
		target.setLanguageDependentName(getMasterDataResolver().getCharacteristicNameWithFallback(ctxt, source.getId()));
		final String description = getTextConverterBase().convertLongText(characteristicMasterData.getDescription());
		target.setLongText(description);
		target.setMultivalued(characteristicMasterData.isMultiValued());
		target.setValueType(getValueType(characteristicMasterData.getType()));
		target.setEntryFieldMask(characteristicMasterData.getEntryFieldMask());
		final Integer numberDecimals = characteristicMasterData.getNumberDecimals();
		if (numberDecimals != null)
		{
			target.setNumberScale(numberDecimals.intValue());
		}

		final Integer length = characteristicMasterData.getLength();
		if (length != null)
		{
			target.setTypeLength(length.intValue());
		}
	}

	protected int getValueType(final String type)
	{
		if (type == null)
		{
			return CsticModel.TYPE_STRING;
		}

		switch (type)
		{
			case "float":
				return CsticModel.TYPE_FLOAT;
			case "int":
				return CsticModel.TYPE_INTEGER;
			case "string":
				return CsticModel.TYPE_STRING;
			case "date":
				return CsticModel.TYPE_DATE;

			default:
				return CsticModel.TYPE_STRING;
		}
	}


	protected MasterDataContainerResolver getMasterDataResolver()
	{
		return masterDataResolver;
	}

	public void setMasterDataResolver(final MasterDataContainerResolver masterDataResolver)
	{
		this.masterDataResolver = masterDataResolver;
	}

	protected TextConverterBase getTextConverterBase()
	{
		return textConverterBase;
	}

	public void setTextConverterBase(final TextConverterBase textConverterBase)
	{
		this.textConverterBase = textConverterBase;
	}


	protected ConfigurationMasterDataService getConfigurationMasterDataService()
	{
		return configurationMasterDataService;
	}

	public void setConfigurationMasterDataService(final ConfigurationMasterDataService configurationMasterDataService)
	{
		this.configurationMasterDataService = configurationMasterDataService;
	}
}
