/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.facades.impl;


import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.productconfig.facades.ConfigConsistenceChecker;
import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.facades.ConfigurationFacade;
import de.hybris.platform.sap.productconfig.facades.CsticData;
import de.hybris.platform.sap.productconfig.facades.GroupType;
import de.hybris.platform.sap.productconfig.facades.KBKeyData;
import de.hybris.platform.sap.productconfig.facades.PricingData;
import de.hybris.platform.sap.productconfig.facades.UiGroupData;
import de.hybris.platform.sap.productconfig.facades.UiType;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationNotFoundException;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.KBKeyImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationAbstractOrderEntryLinkStrategy;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationProductLinkStrategy;
import de.hybris.platform.util.Config;
import de.hybris.platform.variants.model.VariantProductModel;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of the {@link ConfigurationFacade}.<br>
 */
public class ConfigurationFacadeImpl extends ConfigurationBaseFacadeImpl implements ConfigurationFacade
{
	private static final Logger LOG = Logger.getLogger(ConfigurationFacadeImpl.class);

	private ConfigConsistenceChecker configConsistenceChecker;
	private boolean conflictGroupProcessing = true;
	private ConfigurationProductLinkStrategy configurationProductLinkStrategy;
	private ConfigurationAbstractOrderEntryLinkStrategy configurationAbstractOrderEntryLinkStrategy;

	/**
	 * @param configurationProductLinkStrategy
	 *           the configurationProductLinkStrategy to set
	 */
	public void setProductLinkStrategy(final ConfigurationProductLinkStrategy configurationProductLinkStrategy)
	{
		this.configurationProductLinkStrategy = configurationProductLinkStrategy;
	}


	/**
	 * This setting is active per default but can be deactivated to ease an upgrade from previous versions.
	 *
	 * @return Are we processing conflict groups (which have been introduced in 6.0)?
	 */
	public boolean isConflictGroupProcessing()
	{
		return conflictGroupProcessing;
	}


	/**
	 * @param configConsistenceChecker
	 *           injects the consistency checker
	 */
	@Required
	public void setConfigConsistenceChecker(final ConfigConsistenceChecker configConsistenceChecker)
	{
		this.configConsistenceChecker = configConsistenceChecker;
	}

	@Override
	public ConfigurationData getConfiguration(final ConfigurationData configData)
	{
		final String configId = configData.getConfigId();
		String productCode = null != configData.getKbKey() ? configData.getKbKey().getProductCode() : null;
		final long startTime = logFacadeCallStart("GET configuration [CONFIG_ID='%s'; PRODUCT_CODE='%s']", configId, productCode);

		final ConfigModel configModel = getConfigurationService().retrieveConfigurationModel(configId,
				configData.getGroupIdToDisplay());

		if (productCode == null)
		{
			productCode = configModel.getKbKey().getProductCode();
		}
		configData.setKbBuildNumber(configModel.getKbBuildNumber());
		configData.setKbKey(convertKbKey(configModel.getKbKey(), productCode));
		populateConfigDataFromModel(configData, configModel);
		getConfigConsistenceChecker().checkConfiguration(configData);


		logFacadeCallDone("GET configuration ", startTime);
		return configData;
	}


	@Override
	public ConfigurationData getConfiguration(final KBKeyData kbKey)
	{
		return this.getConfiguration(kbKey, false);
	}

	@Override
	public ConfigurationData getConfiguration(final KBKeyData kbKey, final boolean forceReset)
	{
		final long startTime = logFacadeCallStart("GET configuration [PRODUCT_CODE='%s']", kbKey.getProductCode());

		ConfigurationData configurationDataResult = null;
		String configId = null;
		if (!forceReset || !isSupportGetDefaultConfigurationEnhancements())
		{
			configId = getProductLinkStrategy().getConfigIdForProduct(kbKey.getProductCode());
		}
		if (configId != null)
		{
			if (null != getConfigurationAbstractOrderEntryLinkStrategy().getCartEntryForConfigId(configId))
			{
				final String msg = String.format(
						"Inconsistent state detected. Configuration '%s' is linked to cart and product at the same time. Creating new configuration instead.",
						configId);
				LOG.warn(msg);
				configurationDataResult = createConfiguration(kbKey);
			}
			else
			{
				configurationDataResult = getConfigurationWithFallback(kbKey, configId);
			}
		}
		else
		{
			configurationDataResult = createConfiguration(kbKey);
		}

		logFacadeCallDone("GET configuration", startTime);
		return configurationDataResult;
	}

	@Override
	public boolean isConfigurationAvailable(final String configId)
	{
		try
		{
			final ConfigModel configModel = getConfigurationService().retrieveConfigurationOverview(configId);

			return configModel != null;
		}
		catch (final IllegalStateException ex)
		{
			LOG.warn("Configuration not found by engine", ex);
			return false;
		}
	}

	protected ConfigurationData getConfigurationWithFallback(final KBKeyData kbKey, final String configId)
	{
		ConfigurationData configurationDataResult;
		try
		{
			configurationDataResult = getConfiguration(kbKey, configId);
		}
		catch (final IllegalStateException ex)
		{
			if (ex.getCause() instanceof ConfigurationNotFoundException)
			{
				LOG.info(String.format(
						"Configuration '%s' currently linked to product '%s' not found anymore. Creating default configuration instead.",
						configId, kbKey.getProductCode()));
				configurationDataResult = createConfiguration(kbKey);
			}
			else
			{
				throw ex;
			}
		}
		return configurationDataResult;
	}


	protected ConfigurationData createConfiguration(final KBKeyData kbKey)
	{
		final ConfigModel configModel;
		final ConfigurationData configurationDataResult;
		configModel = getConfigurationModel(kbKey);
		replaceProductForNotChangeableVariant(kbKey);
		getProductLinkStrategy().setConfigIdForProduct(kbKey.getProductCode(), configModel.getId());
		configurationDataResult = convert(kbKey, configModel);
		if (isSupportGetDefaultConfigurationEnhancements())
		{
			configurationDataResult.setNewConfiguration(true);
		}
		return configurationDataResult;
	}

	protected void replaceProductForNotChangeableVariant(final KBKeyData kbKey)
	{
		final ProductModel productModel = getProductService().getProductForCode(kbKey.getProductCode());
		if (getConfigurationVariantUtil().isCPQNotChangeableVariantProduct(productModel))
		{
			final String baseProductCode = ((VariantProductModel) productModel).getBaseProduct().getCode();
			kbKey.setProductCode(baseProductCode);
		}
	}

	protected ConfigurationData getConfiguration(final KBKeyData kbKey, final String configId)
	{
		final ConfigurationData configurationDataResult;
		final ConfigurationData configurationDataInput = new ConfigurationData();
		configurationDataInput.setConfigId(configId);
		configurationDataInput.setKbKey(kbKey);
		configurationDataResult = getConfiguration(configurationDataInput);
		return configurationDataResult;
	}

	protected ConfigurationProductLinkStrategy getProductLinkStrategy()
	{
		return this.configurationProductLinkStrategy;
	}


	@Override
	protected ConfigurationData convert(final KBKeyData kbKey, final ConfigModel configModel)
	{
		final ConfigurationData config = super.convert(kbKey, configModel);
		getConfigConsistenceChecker().checkConfiguration(config);

		return config;
	}

	@Override
	public void updateConfiguration(final ConfigurationData configContent)
	{
		final String configId = configContent.getConfigId();
		final String productCode = null != configContent.getKbKey() ? configContent.getKbKey().getProductCode() : null;
		final long startTime = logFacadeCallStart("UPDATE configuration [CONFIG_ID='%s'; PRODUCT_CODE='%s']", configId,
				productCode);

		final ConfigModel configModel = getConfigurationService().retrieveConfigurationModel(configId,
				configContent.getGroupIdToDisplay());
		final PricingData pricingData = getConfigPricing().getPricingData(configModel);
		configContent.setPricing(pricingData);

		final InstanceModel rootInstance = configModel.getRootInstance();

		if (configContent.getGroups() != null)
		{
			for (final UiGroupData uiGroup : configContent.getGroups())
			{
				updateUiGroup(rootInstance, uiGroup);
			}
		}
		getConfigurationService().updateConfiguration(configModel);

		logFacadeCallDone("UPDATE configuration", startTime);
	}


	protected void updateUiGroup(final InstanceModel instance, final UiGroupData uiGroup)
	{

		final GroupType groupType = uiGroup.getGroupType() != null ? uiGroup.getGroupType() : GroupType.INSTANCE;

		switch (groupType)
		{
			case CSTIC_GROUP:
				// cstic group
				updateCsticGroup(instance, uiGroup);
				break;
			case INSTANCE:
				// (sub)instance
				updateSubInstances(instance, uiGroup);
				break;
			case CONFLICT:
				updateConflictGroup(instance, uiGroup);
				break;
			case CONFLICT_HEADER:
				updateConflictHeader(instance, uiGroup);
				break;
			default:
				throw new IllegalArgumentException("Group type not supported: " + groupType);
		}
	}

	protected void updateConflictHeader(final InstanceModel instance, final UiGroupData uiGroup)
	{
		final List<UiGroupData> conflictGroups = uiGroup.getSubGroups();

		if (instance != null && conflictGroups != null)
		{
			for (final UiGroupData uiSubGroup : conflictGroups)
			{
				updateUiGroup(instance, uiSubGroup);
			}
		}
	}

	protected void updateSubInstances(final InstanceModel instance, final UiGroupData uiGroup)
	{
		final InstanceModel subInstance = retrieveRelatedInstanceModel(instance, uiGroup);
		updateConflictHeader(subInstance, uiGroup);
	}


	protected void updateConflictGroup(final InstanceModel instance, final UiGroupData uiGroup)
	{
		//conflict groups might carry no cstics at all in case conflict solver cannot find the conflicting
		//assumptions
		if (!isConflictGroupProcessing() || uiGroup.getCstics() == null)
		{
			return;
		}


		for (final CsticData cstic : uiGroup.getCstics())
		{
			if (cstic.getType() != UiType.NOT_IMPLEMENTED)
			{
				final InstanceModel instanceCarryingTheConflict = getSubInstance(instance, cstic.getInstanceId());
				if (instanceCarryingTheConflict == null)
				{
					throw new IllegalStateException("No instance found for id: " + cstic.getInstanceId());
				}
				updateCsticModelFromCsticData(instanceCarryingTheConflict, cstic);
			}
		}
	}



	InstanceModel getSubInstance(final InstanceModel instance, final String instanceId)
	{
		final String id = instance.getId();
		if (id != null && id.equals(instanceId))
		{
			return instance;
		}
		for (final InstanceModel subInstance : instance.getSubInstances())
		{
			final InstanceModel foundInstance = getSubInstance(subInstance, instanceId);
			if (foundInstance != null)
			{
				return foundInstance;
			}
		}
		return null;
	}



	protected InstanceModel retrieveRelatedInstanceModel(final InstanceModel instance, final UiGroupData uiSubGroup)
	{
		InstanceModel instToReturn = null;
		final String uiGroupId = uiSubGroup.getId();
		if (uiGroupId != null)
		{
			final String instanceId = getUiKeyGenerator().retrieveInstanceId(uiGroupId);
			final List<InstanceModel> subInstances = instance.getSubInstances();
			for (final InstanceModel subInstance : subInstances)
			{
				if (subInstance.getId().equals(instanceId))
				{
					instToReturn = subInstance;
					break;
				}
			}
		}
		return instToReturn;
	}

	protected void updateCsticGroup(final InstanceModel instance, final UiGroupData csticGroup)
	{
		// we need this check for null, in the model the empty lists will be changed to null
		if (csticGroup != null && csticGroup.getCstics() != null)
		{
			for (final CsticData csticData : csticGroup.getCstics())
			{
				if (csticData.getType() != UiType.NOT_IMPLEMENTED)
				{
					updateCsticModelFromCsticData(instance, csticData);
				}
			}
		}
	}

	protected void updateCsticModelFromCsticData(final InstanceModel instance, final CsticData csticData)
	{
		final String csticName = csticData.getName();
		final CsticModel cstic = instance.getCstic(csticName);
		if (cstic == null)
		{
			throw new IllegalStateException("No cstic available at instance " + instance.getId() + " : " + csticName);
		}
		if (cstic.isChangedByFrontend())
		{
			return;
		}
		getCsticTypeMapper().updateCsticModelValuesFromData(csticData, cstic);
	}


	protected ConfigConsistenceChecker getConfigConsistenceChecker()
	{
		return configConsistenceChecker;
	}

	/**
	 * @param b
	 *           Is conflict group processing active?
	 */
	public void setConflictGroupProcessing(final boolean b)
	{
		this.conflictGroupProcessing = b;
	}

	@Override
	public int getNumberOfErrors(final String configId)
	{
		return getConfigurationService().calculateNumberOfIncompleteCsticsAndSolvableConflicts(configId);
	}

	@Override
	public int getNumberOfIncompleteCstics(final String configId)
	{
		final ConfigModel configModel = getConfigurationService().retrieveConfigurationOverview(configId);
		final InstanceModel rootInstance = configModel.getRootInstance();
		return getConfigurationService().countNumberOfIncompleteCstics(rootInstance);
	}

	@Override
	public int getNumberOfSolvableConflicts(final String configId)
	{
		final ConfigModel configModel = getConfigurationService().retrieveConfigurationOverview(configId);
		return getConfigurationService().countNumberOfSolvableConflicts(configModel);
	}


	protected ConfigurationAbstractOrderEntryLinkStrategy getConfigurationAbstractOrderEntryLinkStrategy()
	{
		return configurationAbstractOrderEntryLinkStrategy;
	}

	@Required
	public void setConfigurationAbstractOrderEntryLinkStrategy(
			final ConfigurationAbstractOrderEntryLinkStrategy configurationAbstractOrderEntryLinkStrategy)
	{
		this.configurationAbstractOrderEntryLinkStrategy = configurationAbstractOrderEntryLinkStrategy;
	}


	@Override
	public ConfigurationData getConfigurationFromTemplate(final KBKeyData kbKey, final String configIdTemplate)
	{
		final String templateExternal = getConfigurationService().retrieveExternalConfiguration(configIdTemplate);
		final var configModel = getConfigurationService().createConfigurationFromExternal(
				new KBKeyImpl(kbKey.getProductCode(), kbKey.getKbName(), kbKey.getKbLogsys(), kbKey.getKbVersion()),
				templateExternal);

		getProductLinkStrategy().setConfigIdForProduct(kbKey.getProductCode(), configModel.getId());
		return convert(kbKey, configModel);
	}

	protected boolean isSupportGetDefaultConfigurationEnhancements()
	{
		return Config.getBoolean("toggle.sapproductconfigservices.getDefaultConfigurationEnhancements.enabled", false);
	}

}
