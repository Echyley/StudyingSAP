/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.cps.masterdata.service;

import de.hybris.platform.sap.productconfig.runtime.cps.constants.SapproductconfigruntimecpsConstants;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataCharacteristicContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataKnowledgeBaseContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.populator.impl.MasterDataContext;

import java.util.List;
import java.util.Set;


/**
 * Resolver for reading specific master data from a {@link CPSMasterDataKnowledgeBaseContainer}.
 */
public interface MasterDataContainerResolver
{

	/**
	 * Gets localized master data for characteristic
	 *
	 * @param kbContainer
	 *           container with cached master data
	 * @param csticId
	 *           id of the characteristic
	 * @return localized master data of the characteristic
	 */
	CPSMasterDataCharacteristicContainer getCharacteristic(CPSMasterDataKnowledgeBaseContainer kbContainer, String csticId);

	/**
	 * Gets localized name for characteristic. If it is not maintained it retrieves the localized name from the fallback
	 * language (if maintained). Otherwise null is returned
	 *
	 * @param ctxt
	 *           master data context
	 * @param csticId
	 *           id of the characteristic
	 * @return Localized name for characteristic
	 */
	String getCharacteristicNameWithFallback(MasterDataContext ctxt, String csticId);

	/**
	 * Get all possible value ids for a given characteristic
	 *
	 * @param kbContainer
	 *           container with cached master data
	 * @param productId
	 *           id of the product
	 * @param csticId
	 *           id of the characteristic
	 * @return list of possible value ids
	 */
	Set<String> getPossibleValueIds(CPSMasterDataKnowledgeBaseContainer kbContainer, String csticId);

	/**
	 * Gets localized name for value. If it is not maintained it retrieves the localized name from the fallback language
	 * (if maintained). Otherwise null is returned
	 *
	 * @param ctxt
	 *           master data context
	 * @param csticId
	 *           id of the characteristic
	 * @param valueId
	 *           id of the value
	 * @return localized name of the value (or localized name from fallback language)
	 */
	String getValueNameWithFallback(MasterDataContext ctxt, String csticId, String valueId);

	/**
	 * Gets localized name for value
	 *
	 * @param kbContainer
	 *           container with cached master data
	 * @param csticId
	 *           id of the characteristic
	 * @param valueId
	 *           id of the value
	 * @return localized name of the value
	 */
	String getValueName(CPSMasterDataKnowledgeBaseContainer kbContainer, String csticId, String valueId);

	/**
	 * Checks if a characteristic is numeric
	 *
	 * @param kbContainer
	 *           container with cached master data
	 * @param csticId
	 *           Id of the characteristic
	 * @return whether characteristic is numeric
	 */
	boolean isCharacteristicNumeric(CPSMasterDataKnowledgeBaseContainer kbContainer, String csticId);

	/**
	 * Gets characteristic ids for group. In case we are requesting the characteristics for class nodes, all
	 * characteristic assigned to the class node will be returned as the class node does not carry any group besides the
	 * 'general' dummy group
	 *
	 * @param kbContainer
	 *           container with cached master data
	 * @param itemKey
	 *           id of the product or a class node
	 * @param itemType
	 *           Either {@link SapproductconfigruntimecpsConstants#ITEM_TYPE_MARA} or
	 *           {@link SapproductconfigruntimecpsConstants#ITEM_TYPE_KLAH}
	 * @param groupId
	 *           id of the group. Not relevant for class nodes
	 * @return list of characteristic ids belonging to the group
	 */
	List<String> getGroupCharacteristicIDs(CPSMasterDataKnowledgeBaseContainer kbContainer, String itemKey, String itemType,
			String groupId);

	/**
	 * Gets localized name for item
	 *
	 * @param kbContainer
	 *           container with cached master data
	 * @param itemKey
	 *           id of the item
	 * @param itemType
	 *           type of the item
	 * @return localized name of the item
	 *
	 */
	String getItemName(CPSMasterDataKnowledgeBaseContainer kbContainer, String itemKey, String itemType);

	/**
	 * Gets localized name for item. If it is not maintained it retrieves the localized name from the fallback language
	 * (if maintained). Otherwise null is returned
	 *
	 * @param kbContainer
	 *           container with cached master data
	 * @param itemKey
	 *           id of the item
	 * @param itemType
	 *           type of the item
	 * @return localized name of the item
	 *
	 */
	String getItemNameWithFallback(MasterDataContext ctxt, String itemKey, String itemType);

	/**
	 * Gets localized name for group. In case we are requesting the group name for class nodes, the class name will be
	 * returned as the class node doesn't carry UI groups, except for the 'general' dummy group
	 *
	 * @param kbContainer
	 *           container with cached master data
	 * @param itemKey
	 *           id of the product or class
	 * @param itemType
	 *           Either {@link SapproductconfigruntimecpsConstants#ITEM_TYPE_MARA} or
	 *           {@link SapproductconfigruntimecpsConstants#ITEM_TYPE_KLAH}
	 * @param groupId
	 *           id of the group. Not relevant for class nodes
	 * @return localized name of the group
	 */
	String getGroupName(CPSMasterDataKnowledgeBaseContainer kbContainer, String itemKey, String itemType, String groupId);

	/**
	 * Gets localized name for group. In case we are requesting the group name for class nodes, the class name will be
	 * returned as the class node doesn't carry UI groups, except for the 'general' dummy group. If a localized name is
	 * not maintained it retrieves the localized name from the fallback language (if maintained). Otherwise null is
	 * returned
	 *
	 * @param ctxt
	 *           master data context
	 * @param itemKey
	 *           id of the product or class
	 * @param itemType
	 *           Either {@link SapproductconfigruntimecpsConstants#ITEM_TYPE_MARA} or
	 *           {@link SapproductconfigruntimecpsConstants#ITEM_TYPE_KLAH}
	 * @param groupId
	 *           id of the group. Not relevant for class nodes
	 * @return localized name of the group (of localized name from fallback language)
	 */
	String getGroupNameWithFallback(MasterDataContext ctxt, String itemKey, String itemType, String groupId);

	/**
	 * Get pricing key for value
	 *
	 * @param kbContainer
	 *           container with cached master data
	 * @param productId
	 *           id of the product
	 * @param csticId
	 *           id of the characteristic
	 * @param valueId
	 *           id of the value
	 * @return pricing key for the given value
	 */
	String getValuePricingKey(CPSMasterDataKnowledgeBaseContainer kbContainer, String productId, String csticId, String valueId);

	/**
	 * Get all specific (that have a variant condition) possible value ids for a given characteristic. For class nodes,
	 * which don't carry variant key assignments, an empty Set is returned
	 *
	 * @param kbContainer
	 *           container with cached master data
	 * @param productId
	 *           id of the product of of a class node
	 * @param itemType
	 *           Either {@link SapproductconfigruntimecpsConstants#ITEM_TYPE_MARA} or
	 *           {@link SapproductconfigruntimecpsConstants#ITEM_TYPE_KLAH}
	 * @param csticId
	 *           id of the characteristic
	 * @return list of possible value ids
	 */
	Set<String> getSpecificPossibleValueIds(CPSMasterDataKnowledgeBaseContainer kbContainer, String productId, String itemType,
			String csticId);

	/**
	 * Gives information whether product is multilevel
	 *
	 * @param kbContainer
	 *           container with cached master data
	 * @param productId
	 *           id of the product
	 * @return whether product is multilevel
	 */
	boolean isProductMultilevel(CPSMasterDataKnowledgeBaseContainer kbContainer, String productId);

	/**
	 * Retrieves knowledgebase build number for given master data container
	 *
	 * @param kbContainer
	 *           container with cached master data
	 * @return build number of the knowledgebase
	 */
	Integer getKbBuildNumber(CPSMasterDataKnowledgeBaseContainer kbContainer);

	/**
	 * Returns the fallback knowledgebase cache container
	 *
	 * @param ctxt
	 * @return knowledgebase cache container
	 */
	CPSMasterDataKnowledgeBaseContainer getFallbackKbCacheContainer(final MasterDataContext ctxt);
}
