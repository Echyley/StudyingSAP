/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.occ.controllers;

import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.facades.ConfigurationExpertModeFacade;
import de.hybris.platform.sap.productconfig.facades.ConfigurationFacade;
import de.hybris.platform.sap.productconfig.facades.ConfigurationOverviewFacade;
import de.hybris.platform.sap.productconfig.facades.ConfigurationPricingFacade;
import de.hybris.platform.sap.productconfig.facades.CsticData;
import de.hybris.platform.sap.productconfig.facades.GroupType;
import de.hybris.platform.sap.productconfig.facades.PriceDataPair;
import de.hybris.platform.sap.productconfig.facades.PriceValueUpdateData;
import de.hybris.platform.sap.productconfig.facades.PricingData;
import de.hybris.platform.sap.productconfig.facades.UiGroupData;
import de.hybris.platform.sap.productconfig.facades.UniqueUIKeyGenerator;
import de.hybris.platform.sap.productconfig.facades.overview.ConfigurationOverviewData;
import de.hybris.platform.sap.productconfig.facades.overview.FilterEnum;
import de.hybris.platform.sap.productconfig.occ.ConfigurationOverviewWsDTO;
import de.hybris.platform.sap.productconfig.occ.ConfigurationSupplementsWsDTO;
import de.hybris.platform.sap.productconfig.occ.ConfigurationWsDTO;
import de.hybris.platform.sap.productconfig.occ.CsticSupplementsWsDTO;
import de.hybris.platform.sap.productconfig.occ.CsticValueSupplementsWsDTO;
import de.hybris.platform.sap.productconfig.occ.FilterDataWsDTO;
import de.hybris.platform.sap.productconfig.occ.util.CCPControllerHelper;
import de.hybris.platform.sap.productconfig.occ.util.ImageHandler;
import de.hybris.platform.sap.productconfig.occ.util.MessageHandler;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdParam;
import de.hybris.platform.webservicescommons.util.YSanitizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Stream;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;



/**
 * Web Services Controller to expose the functionality of the
 * {@link de.hybris.platform.sap.productconfig.facades.ConfigurationFacade}.
 */
@Controller
@Tag(name = "Product Configurator CCP")
public class ProductConfiguratorCCPController
{

	private static final Logger LOG = Logger.getLogger(ProductConfiguratorCCPController.class);
	@Resource(name = "sapProductConfigFacade")
	private ConfigurationFacade configFacade;
	@Resource(name = "sapProductConfigPricingFacade")
	private ConfigurationPricingFacade configPricingFacade;
	@Resource(name = "sapProductConfigOverviewFacade")
	private ConfigurationOverviewFacade configOverviewFacade;
	@Resource(name = "dataMapper")
	protected DataMapper dataMapper;
	@Resource(name = "sapProductConfigCCPControllerHelper")
	private CCPControllerHelper ccpControllerHelper;
	@Resource(name = "sapProductConfigExpertModeFacade")
	private ConfigurationExpertModeFacade configExpertModeFacade;
	@Resource(name = "sapProductConfigOccMessageHandler")
	private MessageHandler messageHandler;

	protected ConfigurationOverviewFacade getConfigOverviewFacade()
	{
		return configOverviewFacade;
	}

	protected void setConfigOverviewFacade(final ConfigurationOverviewFacade configOverviewFacade)
	{
		this.configOverviewFacade = configOverviewFacade;
	}

	protected ConfigurationFacade getConfigFacade()
	{
		return configFacade;
	}

	protected void setConfigFacade(final ConfigurationFacade configFacade)
	{
		this.configFacade = configFacade;
	}

	protected ConfigurationPricingFacade getConfigPricingFacade()
	{
		return configPricingFacade;
	}

	protected CCPControllerHelper getConfigurationHandler()
	{
		return ccpControllerHelper;
	}

	protected DataMapper getDataMapper()
	{
		return dataMapper;
	}

	protected static String sanitize(final String input)
	{
		return YSanitizer.sanitize(input);
	}

	/**
	 * @deprecated since 2211.4 use {@link #getDefaultConfiguration(String, boolean, String, boolean, boolean)}
	 */
	@Deprecated(since = "2211.4", forRemoval = true)
	public ConfigurationWsDTO getDefaultConfiguration(final String productCode, final boolean provideAllAttributes,
			final String configIdTemplate, final boolean expMode)

	{
		return this.getDefaultConfiguration(productCode, provideAllAttributes, configIdTemplate, expMode, false);
	}

	@RequestMapping(value = SapproductconfigoccControllerConstants.GET_CONFIG_FOR_PRODUCT, method = RequestMethod.GET)
	@ResponseBody
	@Operation(operationId = "getDefaultProductConfiguration", summary = "Gets the default product configuration for a complex product", description = "Returns the default product configuration for a given complex product. This means that a new instance of the configuration runtime object is created that is equipped with the default values from the configuration model. This API always returns the _entire_ group hierarchy, whereas it's capable of both including all attributes or only those for the first group. This is controlled by query attribute provideAllAttributes")
	@ApiBaseSiteIdParam
	public ConfigurationWsDTO getDefaultConfiguration(//
			@Parameter(description = "Product code", required = true) //
			@PathVariable final String productCode,
			@Parameter(description = "If this parameter is provided and its value is true, attributes for all groups are returned. Otherwise, attributes only for the first group are considered.") //
			@RequestParam(defaultValue = "false", required = false) final boolean provideAllAttributes,
			@Parameter(description = "If this parameter is provided, the template configuration values will be applied to the default configuration") //
			@RequestParam(defaultValue = "", required = false) final String configIdTemplate,
			@Parameter(description = "If this parameter is provided and its value is true, the system tries to execute the request in expert mode (only possible if the current user belongs to group 'sapproductconfigexpmodegroup'") //
			@RequestParam(defaultValue = "false", required = false) final boolean expMode,
			@Parameter(description = "If this parameter is provided and its value is true, the system will force the creation of a new configuration independent of an existing configuration") //
			@RequestParam(defaultValue = "false", required = false) final boolean forceReset)


	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("getDefaultConfiguration: pCode=" + sanitize(productCode));
		}

		ConfigurationData configData;
		if (expMode)
		{
			getConfigExpertModeFacade().enableExpertMode();
		}
		if (StringUtils.isEmpty(configIdTemplate))
		{
			configData = getConfigurationHandler().readDefaultConfiguration(productCode, forceReset);
		}
		else
		{
			configData = getConfigurationHandler().getConfigurationFromTemplate(productCode, sanitize(configIdTemplate));
		}
		if (!provideAllAttributes)
		{
			final String firstGroupId = getConfigurationHandler().determineFirstGroupId(configData.getGroups());
			getConfigurationHandler().filterGroups(configData, firstGroupId);
		}
		return getConfigurationHandler().mapDTOData(configData);
	}

	@RequestMapping(value = SapproductconfigoccControllerConstants.CONFIGURE_URL, method = RequestMethod.PATCH)
	@ResponseBody
	@Operation(operationId = "updateProductConfiguration", summary = "Updates a product configuration", description = "Updates a product configuration. It's possible to send only the changed parts of the configuration, for example a single value change for an attribute. These changes must include their entire path through the configuration (the group they belong to and its parent groups)")
	@ApiBaseSiteIdParam
	public ConfigurationWsDTO updateConfiguration(//
			@Parameter(description = "Configuration identifier", required = true) //
			@PathVariable("configId") final String configId, //
			@RequestBody(required = true) final ConfigurationWsDTO updatedConfiguration,
			@Parameter(description = "If this parameter is provided and its value is true, the system tries to execute the request in expert mode (only possible if the current user belongs to group 'sapproductconfigexpmodegroup'") //
			@RequestParam(required = false) final boolean expMode)
	{
		if (expMode)
		{
			getConfigExpertModeFacade().enableExpertMode();
		}

		final ConfigurationData configurationData = getDataMapper().map(updatedConfiguration, ConfigurationData.class);
		configurationData.setConfigId(configId);
		final String requestedGroupId = getConfigurationHandler().determineFirstGroupId(configurationData.getGroups());
		configurationData.setGroupIdToDisplay(requestedGroupId);
		getConfigFacade().updateConfiguration(configurationData);

		final ConfigurationData updatedBackendConfiguration = getConfigFacade().getConfiguration(configurationData);
		getConfigurationHandler().filterGroups(updatedBackendConfiguration, requestedGroupId);
		return getConfigurationHandler().mapDTOData(updatedBackendConfiguration);
	}

	@RequestMapping(value = SapproductconfigoccControllerConstants.CONFIGURE_URL, method = RequestMethod.GET)
	@ResponseBody
	@Operation(operationId = "getProductConfiguration", summary = "Gets a product configuration", description = "Returns a product configuration, specified by its id. In case this call is done in the context of a logged-in session, the call ensures that the configuration is only returned if the user is authorized to view the configuration")
	@ApiBaseSiteIdParam
	public ConfigurationWsDTO getConfiguration(//
			@Parameter(description = "Configuration identifier", required = true) //
			@PathVariable("configId") final String configId,
			@Parameter(description = "If the parameter is provided only the attributes of the requested group are returned. If the parameter is not provided, attributes for all groups are returned.") //
			@RequestParam(required = false) final String groupId,
			@Parameter(description = "If this parameter is provided and its value is true, the system tries to execute the request in expert mode (only possible if the current user belongs to group 'sapproductconfigexpmodegroup'") //
			@RequestParam(defaultValue = "false", required = false) final boolean expMode)
	{
		if (expMode)
		{
			getConfigExpertModeFacade().enableExpertMode();
		}
		final ConfigurationData configurationData = new ConfigurationData();
		configurationData.setConfigId(configId);
		configurationData.setGroupIdToDisplay(groupId);

		final ConfigurationData backendConfiguration = getConfigFacade().getConfiguration(configurationData);
		if (groupId != null && !groupId.isEmpty())
		{
			getConfigurationHandler().filterGroups(backendConfiguration, groupId);
		}
		return getConfigurationHandler().mapDTOData(backendConfiguration);
	}

	@RequestMapping(value = SapproductconfigoccControllerConstants.GET_CONFIGURE_OVERVIEW_URL, method = RequestMethod.PATCH)
	@ResponseBody
	@Operation(operationId = "updateProductConfigurationOverview", summary = "Updates the product configuration overview", description = "Updates the configuration overview, a simplified, condensed read-only view on the product configuration. Only the selected attribute values are present here")
	@ApiBaseSiteIdParam
	public ConfigurationOverviewWsDTO updateConfigurationOverview(//
			@Parameter(description = "Configuration identifier", required = true) //
			@PathVariable("configId") final String configId, @RequestBody(required = true) //
			final ConfigurationOverviewWsDTO updatedConfigurationOverview)
	{
		final List<FilterEnum> appliedCsticFilters = createCsticFilterList(updatedConfigurationOverview.getAppliedCsticFilter());
		final Set<String> appliedGroupFilters = createGroupFilterList(updatedConfigurationOverview.getGroupFilterList());
		final ConfigurationOverviewData overviewDataWithFilters = new ConfigurationOverviewData();
		overviewDataWithFilters.setAppliedCsticFilters(appliedCsticFilters);
		overviewDataWithFilters.setAppliedGroupFilters(appliedGroupFilters);
		final ConfigurationOverviewData overviewData = getConfigOverviewFacade().getOverviewForConfiguration(configId,
				overviewDataWithFilters);
		return getDataMapper().map(overviewData, ConfigurationOverviewWsDTO.class);
	}

	protected Set<String> createGroupFilterList(final List<FilterDataWsDTO> inputGroupFilterList)
	{
		final Set<String> finalGroupFilterSet = new HashSet<>();
		if (inputGroupFilterList != null)
		{
			for (final FilterDataWsDTO inputFilterData : inputGroupFilterList)
			{
				if (inputFilterData.isSelected())
				{
					finalGroupFilterSet.add(inputFilterData.getKey());
				}
			}
		}
		return finalGroupFilterSet;
	}

	protected List<FilterEnum> createCsticFilterList(final List<FilterDataWsDTO> inputCsticFilterList)
	{
		final List<FilterEnum> finalCsticFilterList = new ArrayList<>();
		if (inputCsticFilterList != null)
		{
			inputCsticFilterList.stream().filter(this::checkSelected).forEach(element -> {
				switch (element.getKey())
				{
					case "VISIBLE":
						finalCsticFilterList.add(FilterEnum.VISIBLE);
						break;
					case "USER_INPUT":
						finalCsticFilterList.add(FilterEnum.USER_INPUT);
						break;
					case "PRICE_RELEVANT":
						finalCsticFilterList.add(FilterEnum.PRICE_RELEVANT);
						break;
					//if the key is unknown, we raise an exception
					default:
						throw new IllegalStateException(String.format("filter key not known: %s", element.getKey()));
				}
			});
		}
		return finalCsticFilterList;
	}

	protected boolean checkSelected(final FilterDataWsDTO filterDataWsDTO)
	{
		return filterDataWsDTO.isSelected();
	}

	@RequestMapping(value = SapproductconfigoccControllerConstants.GET_CONFIGURE_OVERVIEW_URL, method = RequestMethod.GET)
	@ResponseBody
	@Operation(operationId = "getProductConfigurationOverview", summary = "Gets a product configuration overview", description = "Gets a configuration overview, a simplified, condensed read-only view on the product configuration. Only the selected attribute values are present here")
	@ApiBaseSiteIdParam
	public ConfigurationOverviewWsDTO getConfigurationOverview(//
			@Parameter(description = "Configuration identifier", required = true) //
			@PathVariable("configId") final String configId)
	{
		final ConfigurationOverviewData overviewData = getConfigOverviewFacade().getOverviewForConfiguration(configId, null);

		final ConfigurationOverviewWsDTO configurationOverviewWs = getDataMapper().map(overviewData,
				ConfigurationOverviewWsDTO.class);
		configurationOverviewWs.setTotalNumberOfIssues(getConfigFacade().getNumberOfErrors(configId));
		configurationOverviewWs.setNumberOfIncompleteCharacteristics(getConfigFacade().getNumberOfIncompleteCstics(configId));
		configurationOverviewWs.setNumberOfConflicts(getConfigFacade().getNumberOfSolvableConflicts(configId));
		getMessageHandler().processConfigurationOverview(configurationOverviewWs);
		return configurationOverviewWs;
	}

	@RequestMapping(value = SapproductconfigoccControllerConstants.GET_PRICING_URL, method = RequestMethod.GET)
	@ResponseBody
	@Operation(operationId = "getProductConfigurationPricing", summary = "Gets prices for a product configuration", description = "Gets price elements on configuration level and on attribute value level if present. Those price elements include e.g. the configuration base price and the sum of selected options")
	@ApiBaseSiteIdParam
	public ConfigurationSupplementsWsDTO getPricing(//
			@Parameter(description = "Configuration identifier", required = true) //
			@PathVariable("configId") final String configId, //
			@Parameter(description = "Specifies the group for which the value prices are requested. In case not specified, no value prices are returned") //
			@RequestParam(required = false) final String groupId)
	{
		final PricingData priceSummary = getConfigPricingFacade().getPriceSummary(configId);

		//do not request value prices for conflict groups 
		//(as we then cannot easily determine instances and groups the attributes belong to)
		final List<String> valuePricingInput = groupId != null && (!groupId.startsWith(GroupType.CONFLICT.toString()))
				? getConfigurationHandler().compileValuePriceInput(getConfigurationHandler().getUiGroup(configId, groupId))
				: Collections.emptyList();
		final List<PriceValueUpdateData> valuePrices = getConfigPricingFacade().getValuePrices(valuePricingInput, configId);

		return getConfigurationHandler().compilePricingResult(configId, priceSummary, valuePrices);
	}

	/**
	 * @deprecated since 22.05 use {@link #getConfigurationHandler()} and delegate
	 */
	@Deprecated(since = "2205", forRemoval = true)
	protected String compileCsticKey(final CsticData cstic, final UiGroupData uiGroup)
	{
		return getConfigurationHandler().compileCsticKey(cstic, uiGroup);
	}

	/**
	 * @deprecated since 22.05 use {@link #getConfigurationHandler()} and delegate
	 */
	@Deprecated(since = "2205", forRemoval = true)
	protected ConfigurationSupplementsWsDTO compilePricingResult(final String configId, final PricingData priceSummary,
			final List<PriceValueUpdateData> valuePrices)
	{
		return getConfigurationHandler().compilePricingResult(configId, priceSummary, valuePrices);
	}

	/**
	 * @deprecated since 22.05 use {@link #getConfigurationHandler()} and delegate
	 */
	@Deprecated(since = "2205", forRemoval = true)
	protected List<String> compileValuePriceInput(final UiGroupData uiGroup)
	{
		return getConfigurationHandler().compileValuePriceInput(uiGroup);
	}

	/**
	 * @deprecated since 22.05 use {@link #getConfigurationHandler()} and delegate
	 */
	@Deprecated(since = "2205", forRemoval = true)
	protected CsticValueSupplementsWsDTO convertEntrytoWsDTO(final Entry<String, PriceDataPair> entry)
	{
		return getConfigurationHandler().convertEntrytoWsDTO(entry);
	}

	/**
	 * @deprecated since 22.05 use {@link #getConfigurationHandler()} and delegate
	 */
	@Deprecated(since = "2205", forRemoval = true)
	protected CsticSupplementsWsDTO createAttributeSupplementDTO(final PriceValueUpdateData valuePrice)
	{
		return getConfigurationHandler().createAttributeSupplementDTO(valuePrice);
	}

	/**
	 * @deprecated since 22.05 use {@link #getConfigurationHandler()} and delegate
	 */
	@Deprecated(since = "2205", forRemoval = true)
	protected List<CsticValueSupplementsWsDTO> createPriceSupplements(final Map<String, PriceDataPair> prices)
	{
		return getConfigurationHandler().createPriceSupplements(prices);
	}

	/**
	 * @deprecated since 22.05 use {@link #getConfigurationHandler()} and delegate
	 */
	@Deprecated(since = "2205", forRemoval = true)
	protected void deleteCstics(final UiGroupData group)
	{
		getConfigurationHandler().deleteCstics(group);
	}

	/**
	 * @deprecated since 22.05 use {@link #getConfigurationHandler()} and delegate
	 */
	@Deprecated(since = "2205", forRemoval = true)
	protected String determineFirstGroupId(final List<UiGroupData> uiGroups)
	{
		return getConfigurationHandler().determineFirstGroupId(uiGroups);
	}

	/**
	 * @deprecated since 22.05 use {@link #getConfigurationHandler()} and delegate
	 */
	@Deprecated(since = "2205", forRemoval = true)
	protected void filterGroups(final ConfigurationData configData, final String requestedGroupId)
	{
		getConfigurationHandler().filterGroups(configData, requestedGroupId);
	}

	/**
	 * @deprecated since 22.05 use {@link #getConfigurationHandler()} and delegate
	 */
	@Deprecated(since = "2205", forRemoval = true)
	protected void filterGroups(final List<UiGroupData> groups, final String requestedGroupId)
	{
		getConfigurationHandler().filterGroups(groups, requestedGroupId);
	}

	/**
	 * @deprecated since 22.05 use {@link #getConfigurationHandler()} and delegate
	 */
	@Deprecated(since = "2205", forRemoval = true)
	protected Stream<UiGroupData> getFlattened(final UiGroupData uiGroup)
	{
		return getConfigurationHandler().getFlattened(uiGroup);
	}

	/**
	 * @deprecated since 22.05 use {@link #getConfigurationHandler()} and delegate
	 */
	@Deprecated(since = "2205", forRemoval = true)
	protected ImageHandler getImageHandler()
	{
		return getConfigurationHandler().getImageHandler();
	}

	/**
	 * @deprecated since 22.05 use {@link #getConfigurationHandler()} and delegate
	 */
	@Deprecated(since = "2205", forRemoval = true)
	protected UiGroupData getUiGroup(final String configId, final String groupId)
	{
		return getConfigurationHandler().getUiGroup(configId, groupId);
	}

	/**
	 * @deprecated since 22.05 use {@link #getConfigurationHandler()} and delegate
	 */
	@Deprecated(since = "2205", forRemoval = true)
	protected UiGroupData getUiGroup(final List<UiGroupData> groupList, final String groupId)
	{
		return getConfigurationHandler().getUiGroup(groupList, groupId);
	}

	/**
	 * @deprecated since 22.05 use {@link #getConfigurationHandler()} and delegate
	 */
	@Deprecated(since = "2205", forRemoval = true)
	protected UniqueUIKeyGenerator getUniqueUIKeyGenerator()
	{
		return getConfigurationHandler().getUniqueUIKeyGenerator();
	}

	/**
	 * @deprecated since 22.05 use {@link #getConfigurationHandler()} and delegate
	 */
	@Deprecated(since = "2205", forRemoval = true)
	protected boolean hasSubGroups(final UiGroupData group)
	{
		return getConfigurationHandler().hasSubGroups(group);
	}

	/**
	 * @deprecated since 22.05 use {@link #getConfigurationHandler()} and delegate
	 */
	@Deprecated(since = "2205", forRemoval = true)
	protected boolean isNotRequestedGroup(final UiGroupData group, final String requestedGroupId)
	{
		return getConfigurationHandler().isNotRequestedGroup(group, requestedGroupId);
	}

	/**
	 * @deprecated since 22.05 use {@link #getConfigurationHandler()} and delegate
	 */
	@Deprecated(since = "2205", forRemoval = true)
	protected ConfigurationWsDTO mapDTOData(final ConfigurationData configData)
	{
		return getConfigurationHandler().mapDTOData(configData);
	}

	/**
	 * @deprecated since 22.05 use {@link #getConfigurationHandler()} and delegate
	 */
	@Deprecated(since = "2205", forRemoval = true)
	protected ConfigurationData readDefaultConfiguration(final String productCode)
	{
		return getConfigurationHandler().readDefaultConfiguration(productCode);
	}

	protected ConfigurationExpertModeFacade getConfigExpertModeFacade()
	{
		return configExpertModeFacade;
	}

	protected MessageHandler getMessageHandler()
	{
		return messageHandler;
	}
}
