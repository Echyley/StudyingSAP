/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.services.event;

import de.hybris.platform.core.servicelayer.data.SearchPageData;
import de.hybris.platform.cpq.productconfig.services.CloudCPQOrderEntryProductInfoDao;
import de.hybris.platform.cpq.productconfig.services.ConfigurationService;
import de.hybris.platform.cpq.productconfig.services.ConfigurationServiceLayerHelper;
import de.hybris.platform.cpq.productconfig.services.impl.DefaultConfigurationServiceLayerHelper;
import de.hybris.platform.cpq.productconfig.services.model.CloudCPQOrderEntryProductInfoModel;
import de.hybris.platform.servicelayer.event.events.AfterInitializationStartEvent;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;
import de.hybris.platform.servicelayer.internal.service.ServicelayerUtils;
import de.hybris.platform.site.BaseSiteService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;


/**
 * Event Handler to release CPQ runtime configurations just before the System is initialized.<br>
 * The Lifecycle of any CPQ runtime configuration created is managed by Hybris. When we initialize, any data on
 * persistet configurations are lost, hence we have to also inform the CPQ Cloud system, that the corresponding CPQ
 * runtime configurations can be deleted.
 */
public class CleanUpConfigurationAfterInitializationStartEventListener
		extends AbstractEventListener<AfterInitializationStartEvent>
{


	protected static final String PARAM_VALUE_INITIALIZE = "init";
	protected static final String PARAM_NAME_INITMETHOD = "initmethod";

	private static final Logger LOG = Logger.getLogger(CleanUpConfigurationAfterInitializationStartEventListener.class);
	private static final String ID_SEPARATOR = "; ";
	protected static final String PREFIX_FOR_MOCK = "MOCK";


	@Override
	protected void onEvent(final AfterInitializationStartEvent event)
	{
		if (ServicelayerUtils.isSystemInitialized())
		{
			onEventInternal(event);
		}
	}


	protected void onEventInternal(final AfterInitializationStartEvent event)
	{
		if (PARAM_VALUE_INITIALIZE.equals(event.getParams().get(PARAM_NAME_INITMETHOD)))
		{
			LOG.info("initialization started... releasing all created CPQ configurations");
			final StringBuilder builder = new StringBuilder();
			final Set<String> deletedConfigs = new HashSet<>();
			getConfigurationServiceLayerHelper().processPageWise(currentPage -> getProductInfos(currentPage),
					list -> deleteConfigs(list, builder, deletedConfigs));
			if (builder.length() > 0)
			{
				builder.delete(builder.length() - ID_SEPARATOR.length(), builder.length());
				LOG.error("The following CPQ configuration(s) could not be released: [" + builder.toString() + "]");
			}
			else
			{
				LOG.info("Successfully released all CPQ configurations found");
			}
		}
	}


	private void deleteConfigs(final List<CloudCPQOrderEntryProductInfoModel> list, final StringBuilder builder,
			final Set<String> deletedConfigs)
	{
		list.stream().forEach(p -> deleteConfig(p, builder, deletedConfigs));
	}


	protected SearchPageData<CloudCPQOrderEntryProductInfoModel> getProductInfos(final Integer currentPage)

	{
		final SearchPageData<CloudCPQOrderEntryProductInfoModel> result = getProductInfoDao()
				.getAllConfigurationProductInfos(DefaultConfigurationServiceLayerHelper.PAGE_SIZE, currentPage);
		if (currentPage == 0)
		{
			LOG.info(String.format("Found %d CloudCPQOrderEntryProductInfos to process for deletion.",
					result.getPagination().getTotalNumberOfResults()));
		}
		return result;
	}

	protected void deleteConfig(final CloudCPQOrderEntryProductInfoModel infoModel, final StringBuilder builder,
			final Set<String> deletedConfigs)
	{
		final String configId = infoModel.getConfigurationId();
		if (!configId.startsWith(PREFIX_FOR_MOCK) && deletedConfigs.add(configId))
		{
			try
			{
				getConfigurationServiceLayerHelper().ensureBaseSiteSetAndExecuteConfigurationAction(
						infoModel.getOrderEntry().getOrder(), baseSite -> getConfigurationService().deleteConfiguration(configId));
			}
			catch (final RuntimeException ex)
			{
				LOG.debug("Releasing CPQ configuration failed, due to " + ex.getMessage(), ex);
				LOG.error("Releasing CPQ configuration failed, due to '" + ex.getMessage() + "'. See debug log for details.");
				builder.append(configId).append(ID_SEPARATOR);
			}
		}
	}

	protected BaseSiteService getBaseSiteService()
	{
		throw new UnsupportedOperationException(
				"Please define in the spring configuration a <lookup-method> for getBaseSiteSerrvice().");
	}

	protected ConfigurationService getConfigurationService()
	{
		throw new UnsupportedOperationException(
				"Please define in the spring configuration a <lookup-method> for getConfigurationService().");
	}

	protected ConfigurationServiceLayerHelper getConfigurationServiceLayerHelper()
	{
		throw new UnsupportedOperationException(
				"Please define in the spring configuration a <lookup-method> for getConfigurationServiceLayerHelper().");
	}

	protected CloudCPQOrderEntryProductInfoDao getProductInfoDao()
	{
		throw new UnsupportedOperationException(
				"Please define in the spring configuration a <lookup-method> for getProductInfoDao().");
	}

}
