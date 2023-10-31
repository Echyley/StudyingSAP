/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saps4omservices.services.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.sap.core.module.ModuleConfigurationAccess;
import de.hybris.platform.sap.saps4omservices.services.SapS4OrderManagementConfigService;
import de.hybris.platform.servicelayer.exceptions.AttributeNotSupportedException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.store.BaseStoreModel;

/**
 * Order management configuration service 
 */
public class DefaultSapS4OrderManagementConfigService implements SapS4OrderManagementConfigService {

	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultSapS4OrderManagementConfigService.class);
	private ModuleConfigurationAccess moduleConfigurationAccess;
	private ModelService modelService;

	@Override
	public boolean isCartPricingEnabled() {
		
		if (getModuleConfigurationAccess().isSAPConfigurationActive()) {
			boolean isCartPricingEnabled = getConfiguration(SAPConfigurationModel.SAPS4OMCARTPRICING);
			LOGGER.debug("Cart Pricing Enabled: {}", isCartPricingEnabled);
			return isCartPricingEnabled;
		} else {
			logNoBaseStoreConfigrued();
			return false;
		}
	}

	@Override
	public boolean isCatalogPricingEnabled() {
		
		if (getModuleConfigurationAccess().isSAPConfigurationActive()) {
			boolean isCatalogPricingEnabled = getConfiguration(SAPConfigurationModel.SAPS4OMCATALOGPRICING);
			LOGGER.debug("Catalog Pricing Enabled: {}", isCatalogPricingEnabled);
			return isCatalogPricingEnabled;
		} else {
			logNoBaseStoreConfigrued();
			return false;
		}	
	}

	@Override
	public boolean isCreditCheckActive() {
		if (getModuleConfigurationAccess().isSAPConfigurationActive()) {
			boolean isCreditCheckEnabled = getConfiguration(SAPConfigurationModel.SAPS4OMCREDITCHECKACTIVE);
			LOGGER.debug("Catalog credit check Enabled: {}", isCreditCheckEnabled);
			return isCreditCheckEnabled;
		} else {
			logNoBaseStoreConfigrued();
			return false;
		}	

	}

	@Override
	public boolean isATPCheckActive() {
		if (getModuleConfigurationAccess().isSAPConfigurationActive()) {
			boolean isATPCheckEnabled = getConfiguration(SAPConfigurationModel.SAPS4OMATPCHECKACTIVE);
			LOGGER.debug("Catalog ATP check Enabled: {}", isATPCheckEnabled);
			return isATPCheckEnabled;
		} else {
			logNoBaseStoreConfigrued();
			return false;
		}
	}

	@Override
	public boolean isS4SynchronousOrderEnabled() {

		if (getModuleConfigurationAccess().isSAPConfigurationActive()) {
			Boolean isS4SynchronousOrderEnabled = getConfiguration(SAPConfigurationModel.SAPS4SYNCHRONOUSORDERANDHISTORYENABLED);
			LOGGER.debug("S4 Synchronous Order Enabled : {}", isS4SynchronousOrderEnabled);

			return isS4SynchronousOrderEnabled;
		} else {
			logNoBaseStoreConfigrued();
			return false;
		}
	}

	@Override
	public boolean isS4SynchronousOrderHistoryEnabled() {

		if (getModuleConfigurationAccess().isSAPConfigurationActive()) {
			Boolean isS4SynchronousOrderHistoryEnabled = getConfiguration(SAPConfigurationModel.SAPS4SYNCHRONOUSORDERHISTORYENABLED);
			LOGGER.debug("S4 Synchronous Order History Enabled : {}", isS4SynchronousOrderHistoryEnabled);
			
			return isS4SynchronousOrderHistoryEnabled;
		} else {
			logNoBaseStoreConfigrued();
			return false;
		}
	}
	
	@Override
	public boolean isRequestedRetrievalDateFeatureEnabled(final BaseStoreModel baseStore)
	{
		if (baseStore != null)
		{
			try
			{
				final Boolean featureEnabled = getModelService().getAttributeValue(baseStore,
						BaseStoreModel.REQUESTEDRETRIEVALDATEENABLED);
				return featureEnabled != null && featureEnabled.booleanValue();
			}
			catch (final AttributeNotSupportedException e)
			{
				LOGGER.debug("Requested retrieval date feature is not enabled, please update the system", e);
			}
		}
		return false;
	}
	
	private boolean getConfiguration(String property) {
		if (getModuleConfigurationAccess().getProperty(property) == null) {
			LOGGER.debug("Configuration for property is NULL : {}", property);
			return false;
		}
		return getModuleConfigurationAccess().getProperty(property);
	}

	private void logNoBaseStoreConfigrued() {
		LOGGER.debug("No Base Store Configuration Assigned");
	}

	public ModuleConfigurationAccess getModuleConfigurationAccess() {
		return moduleConfigurationAccess;
	}

	public void setModuleConfigurationAccess(ModuleConfigurationAccess moduleConfigurationAccess) {
		this.moduleConfigurationAccess = moduleConfigurationAccess;
	}

	public ModelService getModelService() {
		return modelService;
	}

	public void setModelService(ModelService modelService) {
		this.modelService = modelService;
	}

}
