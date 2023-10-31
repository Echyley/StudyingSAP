/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.cps.masterdata.service.impl;

import de.hybris.platform.sap.productconfig.runtime.cps.constants.SapproductconfigruntimecpsConstants;
import de.hybris.platform.sap.productconfig.runtime.cps.masterdata.service.ConfigurationMasterDataService;
import de.hybris.platform.sap.productconfig.runtime.cps.masterdata.service.MasterDataContainerResolver;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.CPSMasterDataCharacteristicGroup;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.CPSMasterDataPossibleValue;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.CPSMasterDataPossibleValueSpecific;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataCharacteristicContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataCharacteristicSpecificContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataClassContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataKnowledgeBaseContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataProductContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.populator.impl.MasterDataContext;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;


/**
 * Utility class to extract specific data from {@link CPSMasterDataKnowledgeBaseContainer}.
 */
public class MasterDataContainerResolverImpl implements MasterDataContainerResolver
{

	private static final String COULD_NOT_FIND_CHARACTERISTIC = "Could not find characteristic for: ";
	private static final String COULD_NOT_FIND_GROUP = "Could not find group for: ";
	private static final String COULD_NOT_FIND_CLASS = "Could not find class for: ";
	private static final String COULD_NOT_FIND_PRODUCT = "Could not find product for: ";
	private static final String TYPE_IS_NULL = "Type is null.";
	private static final String UNKNOWN_ITEM_TYPE = "Unknown item type: ";
	private static final String INVALID_TYPE = "Invalid type: ";
	private static final String ITEM_TYPE_NOT_ALLOWED = "Item type not allowed: ";
	private static final String CSTIC_TYPE_STRING = "string";
	protected static final String CSTIC_TYPE_FLOAT = "float";
	protected static final String CSTIC_TYPE_INTEGER = "integer";

	private ConfigurationMasterDataService masterDataService;

	@Override
	public CPSMasterDataCharacteristicContainer getCharacteristic(final CPSMasterDataKnowledgeBaseContainer kBContainer,
			final String characteristicId)
	{
		final CPSMasterDataCharacteristicContainer csticContainer = kBContainer.getCharacteristics().get(characteristicId);
		if (csticContainer == null)
		{
			throw new IllegalStateException(COULD_NOT_FIND_CHARACTERISTIC + characteristicId);
		}
		return csticContainer;
	}

	@Override
	public Set<String> getPossibleValueIds(final CPSMasterDataKnowledgeBaseContainer kbContainer, final String characteristicId)
	{
		final CPSMasterDataCharacteristicContainer cstic = kbContainer.getCharacteristics().get(characteristicId);
		if (cstic == null)
		{
			return Collections.emptySet();
		}
		return cstic.getPossibleValueGlobals().keySet();
	}

	@Override
	public String getValueName(final CPSMasterDataKnowledgeBaseContainer kBContainer, final String characteristicId,
			final String valueId)
	{
		final CPSMasterDataCharacteristicContainer cstic = getCharacteristic(kBContainer, characteristicId);
		if (!this.isCsticStringType(cstic))
		{
			// For cstics with a type different from "string", the value names are formatted in facade-layer.
			return null;
		}
		final CPSMasterDataPossibleValue value = cstic.getPossibleValueGlobals().get(valueId);
		if (value == null)
		{
			return valueId;
		}
		return value.getName();
	}

	@Override
	public String getValueNameWithFallback(final MasterDataContext ctxt, final String characteristicId, final String valueId)
	{
		final CPSMasterDataCharacteristicContainer cstic = getCharacteristic(ctxt.getKbCacheContainer(), characteristicId);
		if (!this.isCsticStringType(cstic))
		{
			// For cstics with a type different from "string", the value names are formatted in facade-layer.
			return null;
		}
		final CPSMasterDataPossibleValue value = cstic.getPossibleValueGlobals().get(valueId);
		if (value == null)
		{
			return valueId;
		}
		return handleValueNameFallback(ctxt, characteristicId, valueId, value);
	}

	protected String handleValueNameFallback(final MasterDataContext ctxt, final String characteristicId, final String valueId,
			final CPSMasterDataPossibleValue value)
	{
		final String valueName = value.getName();
		if (valueName != null)
		{
			return valueName;
		}
		else
		{
			return getValueNameFromFallbackKbContainer(ctxt, characteristicId, valueId);
		}
	}

	protected String getValueNameFromFallbackKbContainer(final MasterDataContext ctxt, final String characteristicId,
			final String valueId)
	{
		final CPSMasterDataKnowledgeBaseContainer fallbackKbCacheContainer = getFallbackKbCacheContainer(ctxt);
		if (fallbackKbCacheContainer != null)
		{
			return getValueName(fallbackKbCacheContainer, characteristicId, valueId);
		}
		else
		{
			return null;
		}
	}

	protected boolean isCsticNumericType(final CPSMasterDataCharacteristicContainer csticContainer)
	{
		final String type = csticContainer.getType();
		return CSTIC_TYPE_FLOAT.equals(type) || CSTIC_TYPE_INTEGER.equals(type);
	}

	protected boolean isCsticStringType(final CPSMasterDataCharacteristicContainer cstic)
	{
		return CSTIC_TYPE_STRING.equals(cstic.getType());
	}

	@Override
	public boolean isCharacteristicNumeric(final CPSMasterDataKnowledgeBaseContainer kbContainer, final String csticId)
	{
		final CPSMasterDataCharacteristicContainer cstic = kbContainer.getCharacteristics().get(csticId);
		return isCsticNumericType(cstic);
	}

	@Override
	public List<String> getGroupCharacteristicIDs(final CPSMasterDataKnowledgeBaseContainer kbMasterData, final String itemKey,
			final String itemType, final String groupId)
	{
		if (SapproductconfigruntimecpsConstants.ITEM_TYPE_MARA.equals(itemType))
		{
			return getGroup(kbMasterData, itemKey, groupId).getCharacteristicIDs();
		}
		else if (SapproductconfigruntimecpsConstants.ITEM_TYPE_KLAH.equals(itemType))
		{
			return getClass(kbMasterData, itemKey).getCharacteristicSpecifics().entrySet().stream().map(entry -> entry.getKey())
					.collect(Collectors.toList());
		}
		else
		{
			throw new IllegalArgumentException(ITEM_TYPE_NOT_ALLOWED + itemType);
		}
	}

	protected CPSMasterDataCharacteristicGroup getGroup(final CPSMasterDataKnowledgeBaseContainer kbMasterData,
			final String itemKey, final String groupId)
	{
		final CPSMasterDataProductContainer product = getProduct(kbMasterData, itemKey);
		final CPSMasterDataCharacteristicGroup group = product.getGroups().get(groupId);
		if (group == null)
		{
			throw new IllegalStateException(COULD_NOT_FIND_GROUP + groupId);
		}
		return group;
	}

	protected CPSMasterDataClassContainer getClass(final CPSMasterDataKnowledgeBaseContainer kbMasterData, final String id)
	{
		final CPSMasterDataClassContainer classContainer = kbMasterData.getClasses().get(id);
		if (classContainer == null)
		{
			throw new IllegalStateException(COULD_NOT_FIND_CLASS + id);
		}
		return classContainer;
	}

	protected String getClassName(final CPSMasterDataKnowledgeBaseContainer kbMasterData, final String id)
	{
		return getClass(kbMasterData, id).getName();
	}

	protected CPSMasterDataProductContainer getProduct(final CPSMasterDataKnowledgeBaseContainer kbMasterData, final String id)
	{
		final CPSMasterDataProductContainer productContainer = kbMasterData.getProducts().get(id);
		if (productContainer == null)
		{
			throw new IllegalStateException(COULD_NOT_FIND_PRODUCT + id);
		}
		return productContainer;
	}

	protected String getProductName(final CPSMasterDataKnowledgeBaseContainer kbMasterData, final String id)
	{
		return getProduct(kbMasterData, id).getName();
	}

	protected String getProductNameFromFallbackKbContainer(final MasterDataContext ctxt, final String itemKey)
	{
		final CPSMasterDataKnowledgeBaseContainer fallbackKbCacheContainer = getFallbackKbCacheContainer(ctxt);
		if (fallbackKbCacheContainer != null)
		{
			return getProductName(fallbackKbCacheContainer, itemKey);
		}
		else
		{
			return null;
		}
	}

	@Override
	public String getItemName(final CPSMasterDataKnowledgeBaseContainer kbMasterData, final String id, final String type)
	{
		if (type == null)
		{
			throw new IllegalStateException(TYPE_IS_NULL);
		}
		if (SapproductconfigruntimecpsConstants.ITEM_TYPE_MARA.equals(type))
		{
			return getProductName(kbMasterData, id);
		}
		if (SapproductconfigruntimecpsConstants.ITEM_TYPE_KLAH.equals(type))
		{
			return getClassName(kbMasterData, id);
		}
		throw new IllegalStateException(INVALID_TYPE + type);
	}

	@Override
	public String getItemNameWithFallback(final MasterDataContext ctxt, final String id, final String type)
	{
		if (type == null)
		{
			throw new IllegalStateException(TYPE_IS_NULL);
		}
		if (SapproductconfigruntimecpsConstants.ITEM_TYPE_MARA.equals(type))
		{
			return handleProductNameFallback(ctxt, id);
		}
		if (SapproductconfigruntimecpsConstants.ITEM_TYPE_KLAH.equals(type))
		{
			return handleClassNameFallback(ctxt, id);
		}
		throw new IllegalStateException(INVALID_TYPE + type);
	}

	@Override
	public String getGroupName(final CPSMasterDataKnowledgeBaseContainer kbMasterData, final String itemKey, final String itemType,
			final String groupId)
	{
		if (SapproductconfigruntimecpsConstants.ITEM_TYPE_MARA.equals(itemType))
		{
			return getGroup(kbMasterData, itemKey, groupId).getName();
		}
		else if (SapproductconfigruntimecpsConstants.ITEM_TYPE_KLAH.equals(itemType))
		{
			return getClassName(kbMasterData, itemKey);
		}
		else
		{
			throw new IllegalArgumentException(ITEM_TYPE_NOT_ALLOWED + itemType);
		}
	}

	@Override
	public String getGroupNameWithFallback(final MasterDataContext ctxt, final String itemKey, final String itemType,
			final String groupId)
	{
		if (SapproductconfigruntimecpsConstants.ITEM_TYPE_MARA.equals(itemType))
		{
			return handleGroupNameFallback(ctxt, itemKey, groupId);
		}
		else if (SapproductconfigruntimecpsConstants.ITEM_TYPE_KLAH.equals(itemType))
		{
			return handleClassNameFallback(ctxt, itemKey);
		}
		else
		{
			throw new IllegalArgumentException(ITEM_TYPE_NOT_ALLOWED + itemType);
		}
	}

	protected String getGroupNameFromFallbackKbContainer(final MasterDataContext ctxt, final String itemKey, final String groupId)
	{
		final CPSMasterDataKnowledgeBaseContainer fallbackKbCacheContainer = getFallbackKbCacheContainer(ctxt);
		if (fallbackKbCacheContainer != null)
		{
			return getGroup(fallbackKbCacheContainer, itemKey, groupId).getName();
		}
		else
		{
			return null;
		}
	}

	protected String handleGroupNameFallback(final MasterDataContext ctxt, final String itemKey, final String groupId)
	{
		final String groupName = getGroup(ctxt.getKbCacheContainer(), itemKey, groupId).getName();
		if (groupName != null)
		{
			return groupName;
		}
		else
		{
			return getGroupNameFromFallbackKbContainer(ctxt, itemKey, groupId);
		}
	}

	protected String getClassNameFromFallbackKbContainer(final MasterDataContext ctxt, final String itemKey)
	{
		final CPSMasterDataKnowledgeBaseContainer fallbackKbCacheContainer = getFallbackKbCacheContainer(ctxt);
		if (fallbackKbCacheContainer != null)
		{
			return getClassName(fallbackKbCacheContainer, itemKey);
		}
		else
		{
			return null;
		}
	}

	protected String handleClassNameFallback(final MasterDataContext ctxt, final String itemKey)
	{
		final String className = getClassName(ctxt.getKbCacheContainer(), itemKey);
		if (className != null)
		{
			return className;
		}
		else
		{
			return getClassNameFromFallbackKbContainer(ctxt, itemKey);
		}
	}

	protected String handleProductNameFallback(final MasterDataContext ctxt, final String itemKey)
	{
		final String productName = getProductName(ctxt.getKbCacheContainer(), itemKey);
		if (productName != null)
		{
			return productName;
		}
		else
		{
			return getProductNameFromFallbackKbContainer(ctxt, itemKey);
		}
	}

	@Override
	public String getValuePricingKey(final CPSMasterDataKnowledgeBaseContainer masterData, final String productId,
			final String csticId, final String valueId)
	{
		final CPSMasterDataProductContainer product = masterData.getProducts().get(productId);
		if (product == null)
		{
			return null;
		}
		final CPSMasterDataCharacteristicSpecificContainer csticSpecific = product.getCstics().get(csticId);
		if (csticSpecific == null)
		{
			return null;
		}
		final CPSMasterDataPossibleValueSpecific value = csticSpecific.getPossibleValueSpecifics().get(valueId);
		if (value == null)
		{
			return null;
		}
		return value.getVariantConditionKey();
	}

	@Override
	public Set<String> getSpecificPossibleValueIds(final CPSMasterDataKnowledgeBaseContainer masterData, final String productId,
			final String itemType, final String characteristicId)
	{
		if (SapproductconfigruntimecpsConstants.ITEM_TYPE_MARA.equals(itemType))
		{
			final CPSMasterDataProductContainer cpsMasterDataProductContainer = masterData.getProducts().get(productId);
			final CPSMasterDataCharacteristicSpecificContainer csticSpecific = cpsMasterDataProductContainer.getCstics()
					.get(characteristicId);
			if (csticSpecific == null)
			{
				return Collections.emptySet();
			}
			return retrieveIdsForPossibleValueSpecificsWithVariantCondition(csticSpecific);
		}
		else if (SapproductconfigruntimecpsConstants.ITEM_TYPE_KLAH.equals(itemType))
		{
			return Collections.emptySet();
		}
		else
		{
			throw new IllegalArgumentException(UNKNOWN_ITEM_TYPE + itemType);
		}
	}

	protected Set<String> retrieveIdsForPossibleValueSpecificsWithVariantCondition(
			final CPSMasterDataCharacteristicSpecificContainer csticSpecific)
	{
		final Set<String> possibleValueIds = new HashSet<>();
		final Map<String, CPSMasterDataPossibleValueSpecific> possibleValueSpecifics = csticSpecific.getPossibleValueSpecifics();
		if (MapUtils.isNotEmpty(possibleValueSpecifics))
		{
			for (final Map.Entry<String, CPSMasterDataPossibleValueSpecific> entry : possibleValueSpecifics.entrySet())
			{
				if (StringUtils.isNotEmpty(entry.getValue().getVariantConditionKey()))
				{
					possibleValueIds.add(entry.getKey());
				}
			}
		}
		return possibleValueIds;
	}

	@Override
	public boolean isProductMultilevel(final CPSMasterDataKnowledgeBaseContainer masterData, final String id)
	{
		return getProduct(masterData, id).isMultilevel();
	}

	@Override
	public Integer getKbBuildNumber(final CPSMasterDataKnowledgeBaseContainer kbContainer)
	{
		return kbContainer.getHeaderInfo().getBuild();
	}

	@Override
	public CPSMasterDataKnowledgeBaseContainer getFallbackKbCacheContainer(final MasterDataContext ctxt)
	{
		CPSMasterDataKnowledgeBaseContainer fallbackKbCacheContainer = ctxt.getFallbackKbCacheContainer();
		if (fallbackKbCacheContainer == null)
		{
			fallbackKbCacheContainer = masterDataService
					.getFallbackMasterData(ctxt.getKbCacheContainer().getHeaderInfo().getId().toString());
			ctxt.setFallbackKbCacheContainer(fallbackKbCacheContainer);
		}
		return fallbackKbCacheContainer;
	}

	protected ConfigurationMasterDataService getMasterDataService()
	{
		return masterDataService;
	}

	public void setMasterDataService(final ConfigurationMasterDataService configurationMasterDataService)
	{
		this.masterDataService = configurationMasterDataService;
	}

	@Override
	public String getCharacteristicNameWithFallback(final MasterDataContext ctxt, final String csticId)
	{
		final CPSMasterDataCharacteristicContainer characteristicMasterData = getCharacteristic(ctxt.getKbCacheContainer(),
				csticId);
		if (characteristicMasterData.getName() != null)
		{
			return characteristicMasterData.getName();
		}
		else
		{
			final CPSMasterDataKnowledgeBaseContainer fallbackKbCacheContainer = getFallbackKbCacheContainer(ctxt);
			if (fallbackKbCacheContainer != null)
			{
				final CPSMasterDataCharacteristicContainer fallbackCharacteristicMasterData = getCharacteristic(
						fallbackKbCacheContainer, csticId);
				if (fallbackCharacteristicMasterData != null)
				{
					return fallbackCharacteristicMasterData.getName();
				}
			}
			return null;
		}
	}

}
