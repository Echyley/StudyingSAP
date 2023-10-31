/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.cps.populator.impl;

import de.hybris.platform.sap.productconfig.runtime.cps.masterdata.service.MasterDataContainerResolver;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataKnowledgeBaseContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSValue;
import de.hybris.platform.sap.productconfig.runtime.interf.ContextualPopulator;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;

import org.springframework.beans.factory.annotation.Required;


/**
 * Responsible to populate characteristics
 */
public class ValueMasterDataPopulator implements ContextualPopulator<CPSValue, CsticValueModel, MasterDataContext>
{

	private MasterDataContainerResolver masterDataResolver;


	@Override
	public void populate(final CPSValue source, final CsticValueModel target, final MasterDataContext ctxt)
	{
		final String valueString = source.getValue();

		final String characteristicId = source.getParentCharacteristic().getId();

		final CPSMasterDataKnowledgeBaseContainer kbCacheContainer = ctxt.getKbCacheContainer();
		target.setLanguageDependentName(getMasterDataResolver().getValueNameWithFallback(ctxt, characteristicId, valueString));
		target.setNumeric(getMasterDataResolver().isCharacteristicNumeric(kbCacheContainer, characteristicId));

	}

	protected MasterDataContainerResolver getMasterDataResolver()
	{
		return masterDataResolver;
	}

	@Required
	public void setMasterDataResolver(final MasterDataContainerResolver masterDataResolver)
	{
		this.masterDataResolver = masterDataResolver;
	}

}
