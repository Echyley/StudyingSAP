/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.occ.controllers;

import de.hybris.platform.commerceservices.order.CommerceSaveCartException;
import de.hybris.platform.sap.productconfig.facades.ConfigurationOverviewFacade;
import de.hybris.platform.sap.productconfig.facades.ConfigurationSavedCartIntegrationFacade;
import de.hybris.platform.sap.productconfig.facades.overview.ConfigurationOverviewData;
import de.hybris.platform.sap.productconfig.occ.ConfigurationOverviewWsDTO;
import de.hybris.platform.sap.productconfig.occ.util.MessageHandler;
import de.hybris.platform.webservicescommons.errors.exceptions.NotFoundException;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdAndUserIdParam;
import de.hybris.platform.webservicescommons.util.YSanitizer;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Controller
@Tag(name = "Product Configurator CCP Saved Cart Integration")
@RequestMapping(value = "/{baseSiteId}/users/{userId}/carts/{cartId}")
public class ProductConfiguratorCCPSavedCartIntegrationController
{

	private static final Logger LOG = Logger.getLogger(ProductConfiguratorCCPSavedCartIntegrationController.class);

	@Resource(name = "sapProductConfigSavedCartIntegrationFacade")
	protected ConfigurationSavedCartIntegrationFacade configurationSavedCartIntegrationFacade;
	@Resource(name = "sapProductConfigOverviewFacade")
	protected ConfigurationOverviewFacade configurationOverviewFacade;
	@Resource(name = "dataMapper")
	protected DataMapper dataMapper;
	@Resource(name = "sapProductConfigOccMessageHandler")
	private MessageHandler messageHandler;

	public ProductConfiguratorCCPSavedCartIntegrationController(
			final ConfigurationSavedCartIntegrationFacade configurationSavedCartIntegrationFacade,
			final ConfigurationOverviewFacade configurationOverviewFacade, final DataMapper dataMapper)
	{
		super();
		this.configurationSavedCartIntegrationFacade = configurationSavedCartIntegrationFacade;
		this.configurationOverviewFacade = configurationOverviewFacade;
		this.dataMapper = dataMapper;
	}

	@GetMapping(value = "/entries/{entryNumber}" + "/" + SapproductconfigoccControllerConstants.CONFIGURATOR_TYPE_FOR_OCC_EXPOSURE
			+ SapproductconfigoccControllerConstants.CONFIG_OVERVIEW)
	@ResponseBody
	@Operation(operationId = "getConfigurationOverviewForSavedCart", summary = "Gets a product configuration overview of an saved cart entry", description = "Gets a configuration overview, a simplified, condensed read-only view on the product configuration of an saved cart entry. Only the selected attribute values are present here")
	@ApiBaseSiteIdAndUserIdParam
	public ConfigurationOverviewWsDTO getConfigurationOverviewForSavedCart(//
			@Parameter(required = true, description = "The cart id. Each cart has a unique identifier.") //
			@PathVariable("cartId") final String cartId, //
			@Parameter(required = true, description = "The entry number. Each entry in a saved cart has an entry number. Saved Cart entries are numbered in ascending order, starting with zero (0).") //
			@PathVariable("entryNumber") final int entryNumber) throws CommerceSaveCartException
	{
		ConfigurationOverviewData readConfigurationOverview;
		try
		{
			readConfigurationOverview = readConfigurationOverview(cartId, entryNumber);
		}
		catch (final CommerceSaveCartException ex)
		{

			LOG.error("getConfigurationOverviewForSavedCartEntry: cannot retrieve configuration information for '"
					+ logParam("cartId", sanitize(cartId)) + "," + logParam("entryNumber", String.valueOf(entryNumber)) + "'");
			throw new NotFoundException(
					"Cannot retrieve configuration information for cartId=" + sanitize(cartId) + ", entryNumber=" + entryNumber,
					ex.getMessage(), ex);
		}

		final ConfigurationOverviewWsDTO configurationOverviewWs = dataMapper.map(readConfigurationOverview,
				ConfigurationOverviewWsDTO.class);
		getMessageHandler().processConfigurationOverview(configurationOverviewWs);
		return configurationOverviewWs;
	}

	protected ConfigurationOverviewData readConfigurationOverview(final String cartId, final int entryNumber) throws CommerceSaveCartException
	{
		final ConfigurationOverviewData configuration = configurationSavedCartIntegrationFacade.getConfiguration(cartId, entryNumber);
		return configurationOverviewFacade.getOverviewForConfiguration(configuration.getId(), configuration);
	}

	protected static String logParam(final String paramName, final String paramValue)
	{
		return paramName + " = " + paramValue;
	}

	protected static String sanitize(final String input)
	{
		return YSanitizer.sanitize(input);
	}

	protected MessageHandler getMessageHandler()
	{
		return messageHandler;
	}
}
