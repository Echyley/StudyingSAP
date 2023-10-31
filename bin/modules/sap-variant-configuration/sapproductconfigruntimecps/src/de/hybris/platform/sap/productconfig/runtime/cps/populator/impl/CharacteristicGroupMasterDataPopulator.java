/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.cps.populator.impl;

import de.hybris.platform.sap.productconfig.runtime.cps.constants.SapproductconfigruntimecpsConstants;
import de.hybris.platform.sap.productconfig.runtime.cps.masterdata.service.MasterDataContainerResolver;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataKnowledgeBaseContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSCharacteristicGroup;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSItem;
import de.hybris.platform.sap.productconfig.runtime.interf.ContextualPopulator;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticGroupModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;

import org.springframework.beans.factory.annotation.Required;


/**
 * Responsible to populate cstic groups from master data
 */
public class CharacteristicGroupMasterDataPopulator
		implements ContextualPopulator<CPSCharacteristicGroup, CsticGroupModel, MasterDataContext>
{
	private MasterDataContainerResolver masterDataResolver;

	@Override
	public void populate(final CPSCharacteristicGroup source, final CsticGroupModel target, final MasterDataContext ctxt)
	{
		final String groupId = source.getId();
		final CPSItem parentItem = source.getParentItem();
		final String productId = parentItem.getKey();
		if (groupId.equalsIgnoreCase(SapproductconfigruntimecpsConstants.CPS_GENERAL_GROUP_ID))
		{
			target.setName(InstanceModel.GENERAL_GROUP_NAME);
		}
		else
		{
			target.setName(groupId);
		}
		final CPSMasterDataKnowledgeBaseContainer kbContainer = ctxt.getKbCacheContainer();
		final MasterDataContainerResolver resolver = getMasterDataResolver();
		target.setDescription(resolver.getGroupNameWithFallback(ctxt, productId, parentItem.getType(), groupId));
		target.setCsticNames(resolver.getGroupCharacteristicIDs(kbContainer, productId, parentItem.getType(), groupId));
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
