/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.services.job;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.servicelayer.data.SearchPageData;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.sap.productconfig.services.enums.ProductConfigurationPersistenceCleanUpMode;
import de.hybris.platform.sap.productconfig.services.impl.ProductConfigurationPagingUtil;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationPersistenceService;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationService;
import de.hybris.platform.sap.productconfig.services.model.ProductConfigurationModel;
import de.hybris.platform.sap.productconfig.services.model.ProductConfigurationPersistenceCleanUpCronJobModel;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.site.BaseSiteService;

import java.util.List;
import java.util.function.Function;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.google.common.base.Preconditions;



/**
 * Release of orphaned or obsolete configurations
 */

public class PersistenceCleanUpJob extends AbstractJobPerformable<ProductConfigurationPersistenceCleanUpCronJobModel>
{
	private static final Logger LOG = Logger.getLogger(PersistenceCleanUpJob.class);


	private ProductConfigurationPersistenceService productConfigurationPersistenceService;
	private ProductConfigurationService productConfigurationService;
	private ProductConfigurationPagingUtil productConfigurationPagingUtil;
	private BaseSiteService baseSiteService;


	protected BaseSiteService getBaseSiteService()
	{
		return baseSiteService;
	}

	/**
	 * @param baseSiteService
	 *           the baseSiteService to set
	 */
	@Required
	public void setBaseSiteService(final BaseSiteService baseSiteService)
	{
		this.baseSiteService = baseSiteService;
	}

	@Override
	public PerformResult perform(
			final ProductConfigurationPersistenceCleanUpCronJobModel productConfigurationPersistenceCleanUpCronJobModel)
	{
		ensureBaseSiteAvailable(productConfigurationPersistenceCleanUpCronJobModel);

		if (isRequestedCleanUpProductRelated(productConfigurationPersistenceCleanUpCronJobModel))
		{
			final Integer thresholdDays = productConfigurationPersistenceCleanUpCronJobModel.getThresholdDays();
			LOG.info("Clean up of product related configurations requested for threshold: " + thresholdDays);
			cleanUpConfigs(currentPage -> searchProductRelated(thresholdDays, currentPage));
		}
		if (isRequestedCleanUpOrphaned(productConfigurationPersistenceCleanUpCronJobModel))
		{
			LOG.info("Clean up of orphaned configurations");
			cleanUpConfigs(currentPage -> searchOrphaned(currentPage));
		}
		final PerformResult result = new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
		return result;
	}

	/**
	 * Ensures that the current base site is set to enable apiregistry to find the correct connection to the
	 * configuration engine. <br>
	 *
	 * @param productConfigurationPersistenceCleanUpCronJobModel
	 */
	protected void ensureBaseSiteAvailable(
			final ProductConfigurationPersistenceCleanUpCronJobModel productConfigurationPersistenceCleanUpCronJobModel)
	{
		final BaseSiteModel baseSite = productConfigurationPersistenceCleanUpCronJobModel.getBaseSite();
		Preconditions.checkNotNull(baseSite, "Base site on persistence cronjob must be specified for configuration clean up");
		getBaseSiteService().setCurrentBaseSite(baseSite, false);
	}


	protected SearchPageData<ProductConfigurationModel> searchProductRelated(final Integer thresholdDays,
			final Integer currentPage)
	{
		final SearchPageData<ProductConfigurationModel> result = getProductConfigurationPersistenceService()
				.getProductRelatedByThreshold(thresholdDays, ProductConfigurationPagingUtil.PAGE_SIZE, currentPage);
		if (0 == currentPage)
		{
			LOG.info(String.format("Found %s product related CPS configurations, which will be released.",
					result.getPagination().getTotalNumberOfResults()));
		}
		return result;
	}

	protected SearchPageData<ProductConfigurationModel> searchOrphaned(final Integer currentPage)
	{
		final SearchPageData<ProductConfigurationModel> result = getProductConfigurationPersistenceService()
				.getOrphaned(ProductConfigurationPagingUtil.PAGE_SIZE, currentPage);

		if (0 == currentPage)
		{
			LOG.info(String.format("Found %s CPS configurations not bound to products, which will be checked for deletion.",
					result.getPagination().getTotalNumberOfResults()));
		}
		return result;
	}

	protected boolean isRequestedCleanUpProductRelated(
			final ProductConfigurationPersistenceCleanUpCronJobModel productConfigurationPersistenceCleanUpCronJobModel)
	{
		final ProductConfigurationPersistenceCleanUpMode cleanUpMode = productConfigurationPersistenceCleanUpCronJobModel
				.getCleanUpMode();
		return cleanUpMode == ProductConfigurationPersistenceCleanUpMode.ALL
				|| cleanUpMode == ProductConfigurationPersistenceCleanUpMode.ONLYPRODUCTRELATED;
	}

	protected boolean isRequestedCleanUpOrphaned(
			final ProductConfigurationPersistenceCleanUpCronJobModel productConfigurationPersistenceCleanUpCronJobModel)
	{
		final ProductConfigurationPersistenceCleanUpMode cleanUpMode = productConfigurationPersistenceCleanUpCronJobModel
				.getCleanUpMode();
		return cleanUpMode == ProductConfigurationPersistenceCleanUpMode.ALL
				|| cleanUpMode == ProductConfigurationPersistenceCleanUpMode.ONLYORPHANED;
	}

	protected void cleanUpConfigs(final Function<Integer, SearchPageData<ProductConfigurationModel>> searchService)
	{
		getProductConfigurationPagingUtil().processPageWise(searchService, list -> cleanUpProductConfigurations(list));
	}

	protected void cleanUpProductConfigurations(final List<ProductConfigurationModel> results)
	{
		results.stream().forEach(model -> cleanUpProductConfiguration(model));
	}

	protected void cleanUpProductConfiguration(final ProductConfigurationModel model)
	{
		final String configurationId = model.getConfigurationId();
		try
		{
			if (!productConfigurationPersistenceService.isRelatedToAbstractOrderEntry(model))
			{
				LOG.info(String.format("Configuration %s is unbound and will be deleted", configurationId));
				getProductConfigurationService().releaseSession(configurationId);
			}
		}
		catch (final RuntimeException ex)
		{
			LOG.debug(String.format("Could not release configuration '%s'", configurationId), ex);
			LOG.error(
					"Runtime exception catched during release session, due to '" + ex.getMessage() + "'. See debug log for details.");
		}
	}

	protected ProductConfigurationService getProductConfigurationService()
	{
		return productConfigurationService;
	}

	@Required
	public void setProductConfigurationService(final ProductConfigurationService productConfigurationService)
	{
		this.productConfigurationService = productConfigurationService;
	}

	protected ProductConfigurationPersistenceService getProductConfigurationPersistenceService()
	{
		return productConfigurationPersistenceService;
	}

	@Required
	public void setProductConfigurationPersistenceService(
			final ProductConfigurationPersistenceService productConfigurationPersistenceService)
	{
		this.productConfigurationPersistenceService = productConfigurationPersistenceService;
	}

	protected ProductConfigurationPagingUtil getProductConfigurationPagingUtil()
	{
		return productConfigurationPagingUtil;
	}

	@Required
	public void setProductConfigurationPagingUtil(final ProductConfigurationPagingUtil productConfigurationPagingUtil)
	{
		this.productConfigurationPagingUtil = productConfigurationPagingUtil;
	}

}
