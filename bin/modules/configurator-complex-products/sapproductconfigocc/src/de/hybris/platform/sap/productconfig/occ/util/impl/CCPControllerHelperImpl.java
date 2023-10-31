/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.occ.util.impl;

import de.hybris.platform.commercewebservicescommons.dto.product.PriceWsDTO;
import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.facades.ConfigurationExpertModeFacade;
import de.hybris.platform.sap.productconfig.facades.ConfigurationFacade;
import de.hybris.platform.sap.productconfig.facades.CsticData;
import de.hybris.platform.sap.productconfig.facades.GroupType;
import de.hybris.platform.sap.productconfig.facades.KBKeyData;
import de.hybris.platform.sap.productconfig.facades.PriceDataPair;
import de.hybris.platform.sap.productconfig.facades.PriceValueUpdateData;
import de.hybris.platform.sap.productconfig.facades.PricingData;
import de.hybris.platform.sap.productconfig.facades.UiGroupData;
import de.hybris.platform.sap.productconfig.facades.UniqueUIKeyGenerator;
import de.hybris.platform.sap.productconfig.occ.ConfigurationSupplementsWsDTO;
import de.hybris.platform.sap.productconfig.occ.ConfigurationWsDTO;
import de.hybris.platform.sap.productconfig.occ.CsticSupplementsWsDTO;
import de.hybris.platform.sap.productconfig.occ.CsticValueSupplementsWsDTO;
import de.hybris.platform.sap.productconfig.occ.PriceSummaryWsDTO;
import de.hybris.platform.sap.productconfig.occ.util.CCPControllerHelper;
import de.hybris.platform.sap.productconfig.occ.util.ImageHandler;
import de.hybris.platform.sap.productconfig.occ.util.MessageHandler;
import de.hybris.platform.sap.productconfig.runtime.interf.ProviderFactory;
import de.hybris.platform.util.Config;
import de.hybris.platform.webservicescommons.mapping.DataMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;


public class CCPControllerHelperImpl implements CCPControllerHelper
{

	private static final Logger LOG = Logger.getLogger(CCPControllerHelperImpl.class);

	@Resource(name = "sapProductConfigFacade")
	private ConfigurationFacade configFacade;
	@Resource(name = "dataMapper")
	protected DataMapper dataMapper;
	@Resource(name = "sapProductConfigUiKeyGenerator")
	private UniqueUIKeyGenerator uniqueUiKeyGenerator;
	@Resource(name = "sapProductConfigImageHandler")
	private ImageHandler imageHandler;
	@Resource(name = "sapProductConfigExpertModeFacade")
	private ConfigurationExpertModeFacade configurationExpertModeFacade;
	@Resource(name = "sapProductConfigProviderFactory")
	private ProviderFactory providerFactory;
	@Resource(name = "sapProductConfigOccMessageHandler")
	private MessageHandler messageHandler;

	protected ConfigurationExpertModeFacade getConfigurationExpertModeFacade()
	{
		return configurationExpertModeFacade;
	}

	protected ProviderFactory getProviderFactory()
	{
		return providerFactory;
	}

	@Override
	public UniqueUIKeyGenerator getUniqueUIKeyGenerator()
	{
		return uniqueUiKeyGenerator;
	}

	protected DataMapper getDataMapper()
	{
		return dataMapper;
	}

	protected void setDataMapper(final DataMapper dataMapper)
	{
		this.dataMapper = dataMapper;
	}

	protected ConfigurationFacade getConfigFacade()
	{
		return configFacade;
	}

	protected void setConfigFacade(final ConfigurationFacade configFacade)
	{
		this.configFacade = configFacade;
	}

	@Override
	public ImageHandler getImageHandler()
	{
		return imageHandler;
	}

	@Override
	public String compileCsticKey(final CsticData cstic, final UiGroupData uiGroup)
	{
		return new StringBuilder(uiGroup.getId()).append(getUniqueUIKeyGenerator().getKeySeparator()).append(cstic.getName())
				.toString();
	}


	@Override
	public ConfigurationWsDTO mapDTOData(final ConfigurationData configData)
	{
		final ConfigurationWsDTO configurationWs = getDataMapper().map(configData, ConfigurationWsDTO.class);
		getImageHandler().convertImages(configData, configurationWs);
		configurationWs.setTotalNumberOfIssues(getConfigFacade().getNumberOfErrors(configData.getConfigId()));
		// Meaning of the 'asyncPriceMode' and 'pricingEnabled' is equivalent
		// for the configuration / pricing providers which are used in the underlying layer
		configurationWs.setPricingEnabled(configData.isAsyncPricingMode());


		configurationWs
				.setHideBasePriceAndSelectedOptions(!getProviderFactory().getPricingParameter().showBasePriceAndSelectedOptions());
		//The data related to the Kb key is automatically mapped since the respective attribute is named
		//identically in DTO and WsDTO. Still we only want to send it if expert mode is active,
		//otherwise it's not relevant and would just increase the size of the payload

		Preconditions.checkNotNull(configurationWs.getKbKey(), "At this point kb data must exist");
		configurationWs.getKbKey().setKbBuildNumber(configData.getKbBuildNumber());

		if (!getConfigurationExpertModeFacade().isExpertModeActive())
		{
			configurationWs.setKbKey(null);
		}
		getMessageHandler().processConfiguration(configurationWs);
		if (!isSupportGetDefaultConfigurationEnhancements())
		{
			configurationWs.setNewConfiguration(null);
		}
		return configurationWs;
	}

	protected boolean isSupportGetDefaultConfigurationEnhancements()
	{
		return Config.getBoolean("toggle.sapproductconfigservices.getDefaultConfigurationEnhancements.enabled", false);
	}

	@Override
	public ConfigurationData readDefaultConfiguration(final String productCode)
	{
		return this.readDefaultConfiguration(productCode, false);
	}

	@Override
	public ConfigurationData readDefaultConfiguration(final String productCode, final boolean forceReset)
	{
		final KBKeyData kbKey = new KBKeyData();
		kbKey.setProductCode(productCode);
		return getConfigFacade().getConfiguration(kbKey, forceReset);
	}

	@Override
	public ConfigurationSupplementsWsDTO compilePricingResult(final String configId, final PricingData priceSummary,
			final List<PriceValueUpdateData> valuePrices)
	{

		final ConfigurationSupplementsWsDTO pricingResult = new ConfigurationSupplementsWsDTO();
		final List<CsticSupplementsWsDTO> csticSupplementsWsDTOs = valuePrices.stream().map(this::createAttributeSupplementDTO)
				.collect(Collectors.toList());
		final PriceSummaryWsDTO priceSummaryResult = getDataMapper().map(priceSummary, PriceSummaryWsDTO.class);

		pricingResult.setAttributes(csticSupplementsWsDTOs);
		pricingResult.setPriceSummary(priceSummaryResult);
		pricingResult.setConfigId(configId);

		if (priceSummary.getCurrentTotal() == null)
		{
			pricingResult.setPricingError(true);
		}
		if (LOG.isDebugEnabled())
		{
			LOG.debug("compile pricing result, pricing error: " + pricingResult.isPricingError());
		}
		return pricingResult;
	}

	@Override
	public CsticSupplementsWsDTO createAttributeSupplementDTO(final PriceValueUpdateData valuePrice)
	{
		final CsticSupplementsWsDTO result = this.getDataMapper().map(valuePrice, CsticSupplementsWsDTO.class);
		result.setPriceSupplements(createPriceSupplements(valuePrice.getPrices()));
		return result;
	}


	@Override
	public void filterGroups(final ConfigurationData configData, final String requestedGroupId)
	{
		if (requestedGroupId != null)
		{
			filterGroups(configData.getGroups(), requestedGroupId);
		}
	}


	@Override
	public void filterGroups(final List<UiGroupData> groups, final String requestedGroupId)
	{
		if (requestedGroupId != null)
		{
			groups.stream().filter(group -> (isStandardGroup(group) && isNotRequestedGroup(group, requestedGroupId)))
					.forEach(this::deleteCstics);
			groups.stream().filter(this::hasSubGroups).forEach(group -> filterGroups(group.getSubGroups(), requestedGroupId));
		}
	}

	@Override
	public void deleteCstics(final UiGroupData group)
	{
		group.setCstics(new ArrayList<>());
	}

	@Override
	public boolean isNotRequestedGroup(final UiGroupData group, final String requestedGroupId)
	{
		if (requestedGroupId == null)
		{
			return false;
		}
		return !group.getId().equals(requestedGroupId);
	}

	protected boolean isStandardGroup(final UiGroupData group)
	{
		final var groupType = group.getGroupType();
		return groupType != GroupType.CONFLICT && groupType != GroupType.CONFLICT_HEADER;
	}

	@Override
	public boolean hasSubGroups(final UiGroupData group)
	{
		return CollectionUtils.isNotEmpty(group.getSubGroups());
	}

	private boolean hasCstics(final UiGroupData group)
	{
		return CollectionUtils.isNotEmpty(group.getCstics());
	}

	@Override
	public String determineFirstGroupId(final List<UiGroupData> uiGroups)
	{
		if (uiGroups != null)
		{
			final Optional<UiGroupData> result = uiGroups.stream().filter(group -> isStandardGroup(group) && hasCstics(group))
					.findFirst();
			if (result.isPresent())
			{
				return result.get().getId();
			}

			for (final UiGroupData uiGroup : uiGroups)
			{
				final String uiGroupResultId = determineFirstGroupId(uiGroup.getSubGroups());
				if (uiGroupResultId != null)
				{
					return uiGroupResultId;
				}
			}
		}

		return null;
	}

	@Override
	public List<CsticValueSupplementsWsDTO> createPriceSupplements(final Map<String, PriceDataPair> prices)
	{
		final Map<String, PriceDataPair> productInfoStatusMapConsiderNull = MoreObjects.firstNonNull(prices,
				Collections.emptyMap());

		return productInfoStatusMapConsiderNull.entrySet().stream().map(this::convertEntrytoWsDTO).collect(Collectors.toList());
	}

	@Override
	public CsticValueSupplementsWsDTO convertEntrytoWsDTO(final Entry<String, PriceDataPair> entry)
	{
		final CsticValueSupplementsWsDTO result = new CsticValueSupplementsWsDTO();
		result.setPriceValue(getDataMapper().map(entry.getValue().getPriceValue(), PriceWsDTO.class));
		result.setObsoletePriceValue(getDataMapper().map(entry.getValue().getObsoletePriceValue(), PriceWsDTO.class));
		result.setAttributeValueKey(entry.getKey());
		return result;
	}

	@Override
	public UiGroupData getUiGroup(final String configId, final String groupId)
	{
		final ConfigurationData configurationRequest = new ConfigurationData();
		configurationRequest.setConfigId(configId);
		configurationRequest.setGroupIdToDisplay(groupId);
		final ConfigurationData configurationData = getConfigFacade().getConfiguration(configurationRequest);
		return getUiGroup(configurationData.getGroups(), groupId);
	}

	@Override
	public Stream<UiGroupData> getFlattened(final UiGroupData uiGroup)
	{
		final List<UiGroupData> subGroups = uiGroup.getSubGroups();
		if (subGroups != null)
		{
			return Stream.concat(Stream.of(uiGroup), subGroups.stream().flatMap(this::getFlattened));
		}
		else
		{
			return Stream.of(uiGroup);
		}
	}

	@Override
	public UiGroupData getUiGroup(final List<UiGroupData> groupList, final String groupId)
	{

		final Optional<UiGroupData> optionalUiGroup = groupList.stream().flatMap(this::getFlattened)
				.filter(group -> groupId.equals(group.getId())).findAny();
		if (optionalUiGroup.isPresent())
		{
			return optionalUiGroup.get();
		}

		throw new IllegalStateException("No group present for: " + groupId);
	}

	@Override
	public List<String> compileValuePriceInput(final UiGroupData uiGroup)
	{
		final List<String> result = new ArrayList<>();
		final List<CsticData> cstics = uiGroup.getCstics();
		if (cstics != null)
		{
			cstics.forEach(cstic -> result.add(compileCsticKey(cstic, uiGroup)));
		}
		return result;
	}

	@Override
	public ConfigurationData getConfigurationFromTemplate(final String productCode, final String configIdTemplate)
	{
		final var kbKey = new KBKeyData();
		kbKey.setProductCode(productCode);
		return getConfigFacade().getConfigurationFromTemplate(kbKey, configIdTemplate);
	}

	protected MessageHandler getMessageHandler()
	{
		return messageHandler;
	}

}
