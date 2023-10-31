/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.services.impl;

import de.hybris.platform.catalog.enums.ConfiguratorType;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.cpq.productconfig.services.AbstractOrderIntegrationService;
import de.hybris.platform.cpq.productconfig.services.model.CloudCPQOrderEntryProductInfoModel;
import de.hybris.platform.order.model.AbstractOrderEntryProductInfoModel;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.List;

import com.google.common.base.Preconditions;


/**
 * Default implementation of {@link AbstractOrderIntegrationService}
 */
public class DefaultAbstractOrderIntegrationService implements AbstractOrderIntegrationService
{

	/**
	 * Default Constructor. Expects all required dependencies as constructor args
	 *
	 * @param modelService
	 *           model service
	 * @deprecated Since 22.05 not needed anymore          
	 */
	@Deprecated(since = "2205", forRemoval = true)
	public DefaultAbstractOrderIntegrationService(final ModelService modelService)
	{
		// Attribute modelService has been removed as it is not needed anymore. Therefore no need to set it in the constructor.
	}

	@Override
	public String getConfigIdForAbstractOrderEntry(final AbstractOrderEntryModel entry)
	{
		final CloudCPQOrderEntryProductInfoModel cpqProductInfo = getCloudCPQOrderEntryProductInfoModel(entry);
		final String configurationId = cpqProductInfo.getConfigurationId();
		Preconditions.checkState(configurationId != null, "We expect a configuration ID");
		return configurationId;
	}

	@Override
	public void setConfigIdForAbstractOrderEntry(final AbstractOrderEntryModel entry, final String newConfigId)
	{
		final CloudCPQOrderEntryProductInfoModel productInfo = getCloudCPQOrderEntryProductInfoModel(entry);
		productInfo.setConfigurationId(newConfigId);
		productInfo.setConfiguratorType(ConfiguratorType.CLOUDCPQCONFIGURATOR);
		productInfo.setOrderEntry(entry);
	}

	protected CloudCPQOrderEntryProductInfoModel getCloudCPQOrderEntryProductInfoModel(final AbstractOrderEntryModel entry)
	{
		Preconditions.checkNotNull(entry, "We expect to have an entry");
		final List<AbstractOrderEntryProductInfoModel> productInfos = entry.getProductInfos();
		Preconditions.checkNotNull(productInfos, "We expect to have product infos attached to the entry");
		Preconditions.checkState(productInfos.size() == 1, "We expect exactly one product info instance");
		final AbstractOrderEntryProductInfoModel abstractOrderEntryProductInfoModel = productInfos.get(0);
		Preconditions.checkState(abstractOrderEntryProductInfoModel instanceof CloudCPQOrderEntryProductInfoModel);
		return (CloudCPQOrderEntryProductInfoModel) abstractOrderEntryProductInfoModel;
	}

}
