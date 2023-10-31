/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.occ.util;

import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.facades.CsticData;
import de.hybris.platform.sap.productconfig.facades.PriceDataPair;
import de.hybris.platform.sap.productconfig.facades.PriceValueUpdateData;
import de.hybris.platform.sap.productconfig.facades.PricingData;
import de.hybris.platform.sap.productconfig.facades.UiGroupData;
import de.hybris.platform.sap.productconfig.facades.UniqueUIKeyGenerator;
import de.hybris.platform.sap.productconfig.occ.ConfigurationSupplementsWsDTO;
import de.hybris.platform.sap.productconfig.occ.ConfigurationWsDTO;
import de.hybris.platform.sap.productconfig.occ.CsticSupplementsWsDTO;
import de.hybris.platform.sap.productconfig.occ.CsticValueSupplementsWsDTO;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

import org.apache.commons.lang.NotImplementedException;


/**
 * Provides tools for configurator OCC controllers
 */
public interface CCPControllerHelper
{

	/**
	 * Provides default configuration for product code
	 *
	 * @param productCode
	 *           Code of CCP configurable product
	 * @return Default configuration
	 */
	ConfigurationData readDefaultConfiguration(String productCode);

	/**
	 * Provides default configuration for product code. If parameter forceReset is true it will always return a new
	 * configuration independent of an existing configuration for the given product code.
	 *
	 * @param productCode
	 *           Code of CCP configurable product
	 * @param forceReset
	 *           If true it returns a new configuration
	 *
	 * @return Default configuration
	 */
	default ConfigurationData readDefaultConfiguration(final String productCode, final boolean forceReset)
	{
		throw new NotImplementedException(
				"Method readDefaultConfiguration(final String productCode, final boolean forceReset) not implemented");
	}

	/**
	 * Searches for the first standard group with visible characteristics. It first searches in the groups of the given
	 * list before searching in subgroups. Groups of type {@code GroupType.CONFLICT} and
	 * {@code GroupType.CONFLICT_HEADER} are ignored.
	 *
	 * @param uiGroups
	 *           List of UI groups
	 * @return Id of first group
	 */
	String determineFirstGroupId(List<UiGroupData> uiGroups);

	/**
	 * Filters group data of given configuration by removing characteristic data from groups that are not matching the
	 * requested group Id. No filtering if requested group id is null.
	 *
	 * @param configData
	 *           Configuration data to filter
	 * @param requestedGroupId
	 *           Group id for which data should be completely returned
	 */
	void filterGroups(ConfigurationData configData, String requestedGroupId);

	/**
	 * Maps configuration from DTO to WS representation
	 *
	 * @param configData
	 *           Configuration in DTO format
	 * @return Configuration in WS format
	 */
	ConfigurationWsDTO mapDTOData(ConfigurationData configData);

	/**
	 * Compiles pricing supplement in WS format, which represents value prices and price summary), from the pricing DTO
	 * data
	 *
	 * @param configId
	 *           Configuration id
	 * @param priceSummary
	 *           Price summary in DTO format
	 * @param valuePrices
	 *           Value prices in DTO format
	 * @return Configuration supplement in WS format
	 */
	ConfigurationSupplementsWsDTO compilePricingResult(String configId, PricingData priceSummary,
			List<PriceValueUpdateData> valuePrices);

	/**
	 * Gets UI group for provided id
	 *
	 * @param configId
	 *           Configuration id
	 * @param groupId
	 *           Group id
	 * @return Ui group in DTO representation
	 */
	UiGroupData getUiGroup(String configId, String groupId);

	/**
	 * Compiles the facade layer input for the call for value prices from the provided UI group
	 *
	 * @param uiGroup
	 *           UI group
	 * @return List of characteristic keys for the facade layer call
	 */
	List<String> compileValuePriceInput(UiGroupData uiGroup);

	/**
	 * Compiles characteristic key
	 *
	 * @param cstic
	 *           Characteristic
	 * @param uiGroup
	 *           Group the characteristic belongs to
	 * @return Key
	 */
	String compileCsticKey(CsticData cstic, UiGroupData uiGroup);

	/**
	 * Converts price data into the DTO structure we use for OCC exposal
	 *
	 * @param entry
	 *           Price data, with characteristic value id as key
	 * @return DTO holding the supplementary price data per value
	 */
	CsticValueSupplementsWsDTO convertEntrytoWsDTO(Entry<String, PriceDataPair> entry);

	/**
	 * Creates characteristic supplements DTO from the bean representing value prices for a characteristic
	 *
	 * @param valuePrices
	 *           Value prices
	 * @return Price supplements on characteristic level
	 */
	CsticSupplementsWsDTO createAttributeSupplementDTO(PriceValueUpdateData valuePrices);

	/**
	 * Creates a list of characteristic value supplements DTO from value prices
	 *
	 * @param prices
	 *           Map of value prices
	 * @return List of characteristic value supplements
	 */
	List<CsticValueSupplementsWsDTO> createPriceSupplements(Map<String, PriceDataPair> prices);

	/**
	 * Deletes characteristics that form a UI group (by initializing the respective list)
	 *
	 * @param group
	 *           UI group
	 */
	void deleteCstics(UiGroupData group);

	/**
	 * Filters the given group list by removing characteristic data from groups that are not matching the requested group
	 * Id and are not a conflict group. Sub-groups are also taken into account. No filtering if requested group id is
	 * null.
	 *
	 * @param groups
	 *           List of groups to filter
	 * @param requestedGroupId
	 *           The group id for which data should be completely returned
	 */
	void filterGroups(List<UiGroupData> groups, String requestedGroupId);

	/**
	 * Flattens the UI group hierarchy, i.e. brings the tree of UI groups in a stream consisting of all UI groups
	 *
	 * @param uiGroup
	 *           Root group
	 * @return Strem of UI groups
	 */
	Stream<UiGroupData> getFlattened(UiGroupData uiGroup);

	/**
	 * Returns image handler
	 *
	 * @return Image handler
	 */
	ImageHandler getImageHandler();

	/**
	 * Finds UI group in a list of groups
	 *
	 * @param groupList
	 *           List of UI groups we want to search
	 * @param groupId
	 *           Group id (which is unique)
	 * @return Group that matches provided groupId
	 */
	UiGroupData getUiGroup(List<UiGroupData> groupList, String groupId);

	/**
	 * Returns UI key generator
	 *
	 * @return UI key generator
	 */
	UniqueUIKeyGenerator getUniqueUIKeyGenerator();

	/**
	 * Checks if a UI groups has sub groups
	 *
	 * @param group
	 *           UI group
	 * @return Does UI group have sub groups?
	 */
	boolean hasSubGroups(UiGroupData group);

	/**
	 * Checks if UI group does not match the provided group ID
	 *
	 * @param group
	 *           UI group
	 * @param requestedGroupId
	 *           Group ID that we expect to find
	 * @return Group does not match ID?
	 */
	boolean isNotRequestedGroup(UiGroupData group, String requestedGroupId);

	/**
	 * Copies the values of another configuration that is already existing into a new configuration that is managed by
	 * commerce. This API does not support product variants
	 *
	 * @param productCode
	 *           Code of CCP configurable product
	 * @param configIdTemplate
	 *           ID of configuration template
	 * @return Configuration carrying attribute values of the configuration template
	 */
	default ConfigurationData getConfigurationFromTemplate(final String productCode, final String configIdTemplate)
	{
		throw new IllegalStateException("Not supported");
	}





}
