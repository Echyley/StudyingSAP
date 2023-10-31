/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.mock.persistence.impl;

import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.mock.model.ProductConfigurationMockModel;
import de.hybris.platform.sap.productconfig.runtime.mock.persistence.ConfigurationMockPersistenceService;
import de.hybris.platform.sap.productconfig.runtime.mock.util.ModelCloneFactory;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.Collections;

import org.apache.log4j.Logger;


/**
 * Default implementation of {@link ConfigurationMockPersistenceService}.<br>
 * Provides a simple persistence by serializing the whole configuration model and save in the commerce database.
 */
public class DefaultConfigurationMockPersistenceService implements ConfigurationMockPersistenceService
{
	private static final Logger LOG = Logger.getLogger(DefaultConfigurationMockPersistenceService.class);
	private static final String GET_MODEL_BY_ID = "GET { ProductConfigurationMock } WHERE {configId} = ?configId";

	private final ModelService modelService;
	private final FlexibleSearchService flexibleSearchService;

	/**
	 * Default constructor accepting dependencies as parameters.
	 *
	 * @param modelService
	 *           model service to save data
	 * @param flexibleSearchService
	 *           flexible search service to read data
	 */
	public DefaultConfigurationMockPersistenceService(final ModelService modelService,
			final FlexibleSearchService flexibleSearchService)
	{
		super();
		this.modelService = modelService;
		this.flexibleSearchService = flexibleSearchService;
	}

	@Override
	public void writeConfigModel(final ConfigModel model)
	{
		ProductConfigurationMockModel persistenceModel = readPersistenceModel(model.getId());
		if (persistenceModel == null)
		{
			persistenceModel = modelService.create(ProductConfigurationMockModel.class);
			persistenceModel.setConfigId(model.getId());
		}
		persistenceModel.setCurrentConfigState(model);
		LOG.debug(String.format("Saving config with id='%s'and version='%s'", model.getId(), model.getVersion()));
		getModelService().save(persistenceModel);

	}

	@Override
	public void writeExtConfigModel(final ConfigModel extConfigModel)
	{
		ProductConfigurationMockModel persistenceModel = readPersistenceModel(extConfigModel.getId());
		if (persistenceModel == null)
		{
			persistenceModel = modelService.create(ProductConfigurationMockModel.class);
			persistenceModel.setConfigId(extConfigModel.getId());
		}
		persistenceModel.setExternalConfigState(extConfigModel);
		LOG.debug(String.format("Saving external config with id='%s'and version='%s'", extConfigModel.getId(),
				extConfigModel.getVersion()));
		getModelService().save(persistenceModel);
	}

	protected ProductConfigurationMockModel readPersistenceModel(final String configId)
	{
		final SearchResult<ProductConfigurationMockModel> result = getFlexibleSearchService().search(GET_MODEL_BY_ID,
				Collections.singletonMap("configId", configId));
		if (result.getCount() == 0)
		{
			return null;
		}
		else if (result.getCount() == 1)
		{
			return result.getResult().get(0);
		}
		else
		{
			throw new AmbiguousIdentifierException(String.format("multiple entries for configId '%s' found!", configId));
		}
	}

	@Override
	public ConfigModel readConfigModel(final String configId)
	{
		final ProductConfigurationMockModel persistenceModel = readPersistenceModel(configId);
		if (persistenceModel != null && persistenceModel.getCurrentConfigState() != null)
		{
			final ConfigModel config = (ConfigModel) persistenceModel.getCurrentConfigState();
			LOG.debug(String.format("Retrieved config with id='%s'and verison='%s'", configId, config.getVersion()));
		}
		return persistenceModel != null ? getCloneModelOrNull(persistenceModel.getCurrentConfigState()) : null;
	}

	protected ConfigModel getCloneModelOrNull(final Object fromPersistence)
	{
		final ConfigModel model = (ConfigModel) fromPersistence;
		return model!= null ? ModelCloneFactory.cloneConfigModel(model) : null;
	}

	@Override
	public ConfigModel readExtConfigModel(final String id)
	{
		final ProductConfigurationMockModel persistenceModel = readPersistenceModel(id);
		if (persistenceModel != null)
		{
			final ConfigModel config = (ConfigModel) persistenceModel.getExternalConfigState();
			LOG.debug(String.format("Retrieved external config with id='%s'and verison='%s'", id, config.getVersion()));
		}
		return persistenceModel != null ? getCloneModelOrNull(persistenceModel.getExternalConfigState()) : null;
	}

	@Override
	public void deleteConfigModel(final String configId)
	{
		final ProductConfigurationMockModel persistenceModel = readPersistenceModel(configId);
		if (persistenceModel != null)
		{

			if (persistenceModel.getExternalConfigState() == null)
			{
				modelService.remove(persistenceModel);
			}
			else
			{
				persistenceModel.setCurrentConfigState(null);
				modelService.save(persistenceModel);
			}
		}
	}

	protected FlexibleSearchService getFlexibleSearchService()
	{
		return flexibleSearchService;
	}


	protected ModelService getModelService()
	{
		return modelService;
	}



}
