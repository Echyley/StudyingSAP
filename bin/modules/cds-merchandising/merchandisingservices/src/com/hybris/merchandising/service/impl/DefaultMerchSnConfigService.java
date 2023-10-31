/**
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.merchandising.service.impl;

import de.hybris.platform.commerceservices.impersonation.ImpersonationContext;
import de.hybris.platform.commerceservices.impersonation.ImpersonationService;
import de.hybris.platform.searchservices.admin.dao.SnIndexTypeDao;
import de.hybris.platform.searchservices.model.SnIndexTypeModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import de.hybris.platform.site.BaseSiteService;

import java.net.URL;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.hybris.charon.RawResponse;
import com.hybris.merchandising.client.MerchCatalogServiceProductDirectoryClient;
import com.hybris.merchandising.dao.MerchSnConfigDao;
import com.hybris.merchandising.model.MerchSnConfigModel;
import com.hybris.merchandising.model.ProductDirectory;
import com.hybris.merchandising.service.MerchSnConfigService;

/**
 * Default implementation of {@link MerchSnConfigService}.
 */
public class DefaultMerchSnConfigService implements MerchSnConfigService
{
	private static final Logger LOG = LoggerFactory.getLogger(DefaultMerchSnConfigService.class);

	private MerchSnConfigDao merchSnConfigDao;
	private MerchCatalogServiceProductDirectoryClient merchCatalogServiceProductDirectoryClient;
	private ModelService modelService;
	private BaseSiteService baseSiteService;
	private ImpersonationService impersonationService;
	private SnIndexTypeDao snIndexTypeDao;


	@Override
	public Optional<MerchSnConfigModel> getMerchConfigForIndexedType(final String indexType)
	{
		ServicesUtil.validateParameterNotNullStandardMessage("indexType", indexType);

		final Optional<SnIndexTypeModel> solrIndexedType = snIndexTypeDao.findIndexTypeById(indexType);
		return solrIndexedType.flatMap(it -> merchSnConfigDao.findByIndexedType(it));
	}

	@Override
	public Optional<MerchSnConfigModel> getMerchConfigForIndexedType(final SnIndexTypeModel indexType)
	{
		ServicesUtil.validateParameterNotNullStandardMessage("indexType", indexType);
		return merchSnConfigDao.findByIndexedType(indexType);
	}


	@Override
	public Optional<MerchSnConfigModel> getMerchConfigForCurrentBaseSite()
	{
		return merchSnConfigDao.findByBaseSite(baseSiteService.getCurrentBaseSite());
	}


	@Override
	public void updateMerchConfig(final MerchSnConfigModel merchSnConfig)
	{
		modelService.save(merchSnConfig);
	}

	@Override
	public void deleteProductDirectory(final MerchSnConfigModel merchSnConfig)
	{
		if (merchSnConfig.getCdsIdentifier() != null)
		{
			final ImpersonationContext context = new ImpersonationContext();
			context.setSite(merchSnConfig.getBaseSite());
			LOG.info("Deleting product directory with id : {}", merchSnConfig.getCdsIdentifier());
			impersonationService.executeInContext(context, () -> {
				merchCatalogServiceProductDirectoryClient.deleteProductDirectory(merchSnConfig.getCdsIdentifier());
				return merchSnConfig.getCdsIdentifier();
			});
		}
	}

	@Override
	public void createOrUpdateProductDirectory(final MerchSnConfigModel merchSnConfig, final boolean saveModel)
	{
		LOG.info("createUpdate invoked for merch config with index type: {}", merchSnConfig.getSnIndexType().getId());
		if (merchSnConfig.isEnabled())
		{
			final ImpersonationContext context = new ImpersonationContext();
			context.setSite(merchSnConfig.getBaseSite());
			final ProductDirectory productDirectory = ProductDirectory.fromMerchSnConfigModel(merchSnConfig);

			if (StringUtils.isBlank(productDirectory.getId()))
			{
				LOG.info("Creating product directory in catalog service");
				final String identifier = impersonationService.executeInContext(context,
						() -> getIdentifier(merchCatalogServiceProductDirectoryClient.createProductDirectory(productDirectory)));
				merchSnConfig.setCdsIdentifier(identifier);
				if (saveModel)
				{
					modelService.save(merchSnConfig);
				}
			}
			else
			{
				LOG.info("Updating product directory with ID:{}", productDirectory.getId());
				impersonationService.executeInContext(context,
						() -> merchCatalogServiceProductDirectoryClient.updateProductDirectory(productDirectory.getId(), productDirectory));
			}
		}
	}

	private String getIdentifier(final RawResponse response)
	{
		final Optional<URL> location = response.location();
		return location.map(this::getIdentifierFromPath).orElse(null);
	}

	private String getIdentifierFromPath(final URL location)
	{
		final String path = location.getPath();
		return path.substring(path.lastIndexOf('/') + 1);
	}

	//Getters and setters

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	protected BaseSiteService getBaseSiteService()
	{
		return baseSiteService;
	}

	@Required
	public void setBaseSiteService(final BaseSiteService baseSiteService)
	{
		this.baseSiteService = baseSiteService;
	}

	protected MerchSnConfigDao getMerchSnConfigDao()
	{
		return merchSnConfigDao;
	}

	@Required
	public void setMerchSnConfigDao(final MerchSnConfigDao merchSnConfigDao)
	{
		this.merchSnConfigDao = merchSnConfigDao;
	}

	protected MerchCatalogServiceProductDirectoryClient getMerchCatalogServiceProductDirectoryClient()
	{
		return merchCatalogServiceProductDirectoryClient;
	}

	@Required
	public void setMerchCatalogServiceProductDirectoryClient(final MerchCatalogServiceProductDirectoryClient merchCatalogServiceProductDirectoryClient)
	{
		this.merchCatalogServiceProductDirectoryClient = merchCatalogServiceProductDirectoryClient;
	}

	protected ImpersonationService getImpersonationService()
	{
		return impersonationService;
	}

	@Required
	public void setImpersonationService(final ImpersonationService impersonationService)
	{
		this.impersonationService = impersonationService;
	}

	protected SnIndexTypeDao getSnIndexTypeDao()
	{
		return snIndexTypeDao;
	}


	@Required
	public void setSnIndexTypeDao(final SnIndexTypeDao snIndexTypeDao)
	{
		this.snIndexTypeDao = snIndexTypeDao;
	}
}
