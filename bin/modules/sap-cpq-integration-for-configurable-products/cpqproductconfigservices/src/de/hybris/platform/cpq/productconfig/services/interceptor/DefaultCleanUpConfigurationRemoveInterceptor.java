/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.services.interceptor;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.cpq.productconfig.services.CloudCPQOrderEntryProductInfoDao;
import de.hybris.platform.cpq.productconfig.services.ConfigurationService;
import de.hybris.platform.cpq.productconfig.services.ConfigurationServiceLayerHelper;
import de.hybris.platform.cpq.productconfig.services.model.CloudCPQOrderEntryProductInfoModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.RemoveInterceptor;

import org.apache.log4j.Logger;



/**
 * Default CPQ implementation of the {@link RemoveInterceptor}.<br>
 * Will ensure that CPQ Configurations are deleted in the CPQ system if a entity containing a CPQ configuration is
 * deleted.
 */
public class DefaultCleanUpConfigurationRemoveInterceptor implements RemoveInterceptor<AbstractOrderEntryModel>
{
	private static final Logger LOG = Logger.getLogger(DefaultCleanUpConfigurationRemoveInterceptor.class);

	private final ConfigurationService cpqService;
	private final ConfigurationServiceLayerHelper serviceLayerHelper;
	private final CloudCPQOrderEntryProductInfoDao cpqOrderEntryProductInfoDao;

	/**
	 * @param cpqService
	 * @param serviceLayerHelper
	 * @param cpqOrderEntryProductInfoDao
	 */
	public DefaultCleanUpConfigurationRemoveInterceptor(final ConfigurationService cpqService,
			final ConfigurationServiceLayerHelper serviceLayerHelper,
			final CloudCPQOrderEntryProductInfoDao cpqOrderEntryProductInfoDao)
	{
		super();
		this.cpqService = cpqService;
		this.serviceLayerHelper = serviceLayerHelper;
		this.cpqOrderEntryProductInfoDao = cpqOrderEntryProductInfoDao;
	}


	@Override
	public void onRemove(final AbstractOrderEntryModel model, final InterceptorContext ctxt) throws InterceptorException
	{
		try
		{
			serviceLayerHelper.ensureBaseSiteSetAndExecuteConfigurationAction(model.getOrder(),
					baseSiteModel -> deleteIfExisting(model));
		}
		catch (final RuntimeException ex)
		{
			LOG.debug("Releasing CPQ configuration failed, due to " + ex.getMessage(), ex);
			LOG.error("Releasing CPQ configuration failed, due to '" + ex.getMessage() + "'. See debug log for details.");
		}
	}


	protected void deleteIfExisting(final AbstractOrderEntryModel model)
	{
		final CloudCPQOrderEntryProductInfoModel cpqInfo = serviceLayerHelper.getCPQInfo(model);

		if (null != cpqInfo && null != cpqInfo.getConfigurationId()
				&& cpqOrderEntryProductInfoDao.isOnlyRelatedToGivenEntry(model, cpqInfo.getConfigurationId()))
		{
			cpqService.deleteConfiguration(cpqInfo.getConfigurationId());
		}
	}

}
