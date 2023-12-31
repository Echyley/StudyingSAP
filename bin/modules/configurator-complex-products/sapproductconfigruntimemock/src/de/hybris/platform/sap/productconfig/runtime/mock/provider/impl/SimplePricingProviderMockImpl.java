/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.mock.provider.impl;

import de.hybris.platform.sap.productconfig.runtime.interf.PricingConfigurationParameter;
import de.hybris.platform.sap.productconfig.runtime.interf.PricingEngineException;
import de.hybris.platform.sap.productconfig.runtime.interf.PricingProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.ProviderFactory;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.ConfigurationRetrievalOptions;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceSummaryModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceValueUpdateModel;
import de.hybris.platform.sap.productconfig.runtime.mock.persistence.ConfigurationMockPersistenceService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Required;


/**
 * Simple pricing provider that ensures that the mock can determine prices that are stored in the configuration model
 * and later taken from there in asynchronous pricing calls. This class is not meant for productive usage but just
 * show-cases the pricing integration into product configuration. It is also used for integration testing.
 */
public class SimplePricingProviderMockImpl implements PricingProvider
{
	private ProviderFactory providerFactory;
	private ConfigurationMockPersistenceService configurationMockPersistenceService;
	private boolean active;

	@Override
	public PriceSummaryModel getPriceSummary(final String configId, final ConfigurationRetrievalOptions options)
			throws PricingEngineException
	{
		return getPriceSummaryModelMocked(configId);
	}

	protected PriceSummaryModel getPriceSummaryModelMocked(final String configId)
	{
		final PriceSummaryModel prices = new PriceSummaryModel();
		final ConfigModel config = getPersistedConfig(configId);
		prices.setBasePrice(config.getBasePrice());
		prices.setCurrentTotalPrice(config.getCurrentTotalPrice());
		prices.setSelectedOptionsPrice(config.getSelectedOptionsPrice());
		prices.setCurrentTotalSavings(config.getCurrentTotalSavings());
		return prices;
	}


	/**
	 * See {@link SimplePricingProviderMockImpl#setActive(boolean)}
	 */
	@Override
	public boolean isActive()
	{
		return active;
	}

	@Override
	public void fillValuePrices(final ConfigModel configModel) throws PricingEngineException
	{
		//prices for assigned values are already part of the configuration model in mock mode.
		//Only the prices at _assignable_ values level might have been deleted
	}



	@Override
	public void fillValuePrices(final List<PriceValueUpdateModel> updateModels, final String configId)
			throws PricingEngineException
	{
		//this method deals with prices for the assignable values that might have been deleted
		final ConfigModel config = getPersistedConfig(configId);

		for (final PriceValueUpdateModel updateModel : updateModels)
		{
			final Map<String, PriceModel> valuePrices = new HashMap<>();

			fillValuePrices(config, updateModel, valuePrices);

			updateModel.setValuePrices(valuePrices);
			final boolean useDeltaPrices = getPricingConfigurationParameters().showDeltaPrices(config.getRootInstance().getName());
			updateModel.setShowDeltaPrices(useDeltaPrices);
		}
	}


	protected ConfigModel getPersistedConfig(final String configId)
	{
		return getConfigurationMockPersistenceService().readConfigModel(configId);
	}

	protected void fillValuePricesFromPossibleValues(final Map<String, PriceModel> valuePrices, final CsticModel csticModel,
			final String productId)
	{
		final boolean useDeltaPrices = getPricingConfigurationParameters().showDeltaPrices(productId);
		if (csticModel.getAssignableValues() != null)
		{
			for (final CsticValueModel value : csticModel.getAssignableValues())
			{
				if (useDeltaPrices)
				{
					valuePrices.put(value.getName(), value.getDeltaPrice());
				}
				else
				{
					valuePrices.put(value.getName(), value.getValuePrice());
				}
			}

		}
	}

	protected void fillValuePrices(final ConfigModel config, final PriceValueUpdateModel updateModel,
			final Map<String, PriceModel> valuePrices)
	{
		final String csticName = updateModel.getCsticQualifier().getCsticName();
		if (csticName != null)
		{
			final String requiredInstanceId = updateModel.getCsticQualifier().getInstanceId();
			final InstanceModel currentInstance = config.getRootInstance();
			final InstanceModel instance = retrieveRelatedInstance(requiredInstanceId, currentInstance);
			if (instance == null)
			{
				return;
			}

			final Optional<CsticModel> csticModel = instance.getCstics().stream()//
					.filter(c -> csticName.equals(c.getName())).findFirst();

			if (csticModel.isPresent())
			{
				fillValuePricesFromPossibleValues(valuePrices, csticModel.get(), config.getRootInstance().getName());
			}
		}
	}

	protected InstanceModel retrieveRelatedInstance(final String requiredInstanceId, final InstanceModel currentInstance)
	{
		if (currentInstance.getId().equalsIgnoreCase(requiredInstanceId))
		{
			return currentInstance;
		}

		for (final InstanceModel subInstance : currentInstance.getSubInstances())
		{
			final InstanceModel instance = retrieveRelatedInstance(requiredInstanceId, subInstance);
			if (instance != null)
			{
				return instance;
			}
		}

		return null;
	}



	protected PricingConfigurationParameter getPricingConfigurationParameters()
	{
		return getProviderFactory().getPricingParameter();
	}

	/**
	 * @param active
	 *           Asynchronous pricing is active. 'True' is default option for the accelerator, meaning price information
	 *           is not sent along with the configuration request. Note that the accelerator is not capable of digesting
	 *           pricing in configuration response and at the same time doing asynchronous requests. <br>
	 *           'True' also needed for OCC deployments. In this case, prices are just read from the cached state of the
	 *           configuration.
	 */
	public void setActive(final boolean active)
	{
		this.active = active;
	}

	protected ProviderFactory getProviderFactory()
	{
		return providerFactory;
	}

	@Required
	public void setProviderFactory(final ProviderFactory providerFactory)
	{
		this.providerFactory = providerFactory;
	}

	protected ConfigurationMockPersistenceService getConfigurationMockPersistenceService()
	{
		return configurationMockPersistenceService;
	}

	@Required
	public void setConfigurationMockPersistenceService(
			final ConfigurationMockPersistenceService configurationMockPersistenceService)
	{
		this.configurationMockPersistenceService = configurationMockPersistenceService;
	}

}
