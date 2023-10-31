/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.occ.controllers;

import de.hybris.platform.sap.productconfig.facades.ConfigurationOverviewFacade;
import de.hybris.platform.sap.productconfig.facades.ConfigurationQuoteIntegrationFacade;
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
@Tag(name = "Product Configurator CCP Quote Integration")
@RequestMapping(value = "/{baseSiteId}/users/{userId}/quotes/{quoteId}")
public class ProductConfiguratorCCPQuoteIntegrationController
{

	private static final Logger LOG = Logger.getLogger(ProductConfiguratorCCPQuoteIntegrationController.class);

	@Resource(name = "sapProductConfigQuoteIntegrationFacade")
	protected ConfigurationQuoteIntegrationFacade configurationQuoteIntegrationFacade;
	@Resource(name = "sapProductConfigOverviewFacade")
	protected ConfigurationOverviewFacade configurationOverviewFacade;
	@Resource(name = "dataMapper")
	protected DataMapper dataMapper;
	@Resource(name = "sapProductConfigOccMessageHandler")
	private MessageHandler messageHandler;


	public ProductConfiguratorCCPQuoteIntegrationController(
			final ConfigurationQuoteIntegrationFacade configurationQuoteIntegrationFacade,
			final ConfigurationOverviewFacade configurationOverviewFacade, final DataMapper dataMapper)
	{
		super();
		this.configurationQuoteIntegrationFacade = configurationQuoteIntegrationFacade;
		this.configurationOverviewFacade = configurationOverviewFacade;
		this.dataMapper = dataMapper;
	}


	@GetMapping(value = "/entries/{entryNumber}" + "/" + SapproductconfigoccControllerConstants.CONFIGURATOR_TYPE_FOR_OCC_EXPOSURE
			+ SapproductconfigoccControllerConstants.CONFIG_OVERVIEW)
	@ResponseBody
	@Operation(operationId = "getConfigurationOverviewForQuote", summary = "Gets a product configuration overview of an quote entry", description = "Gets a configuration overview, a simplified, condensed read-only view on the product configuration of an quote entry. Only the selected attribute values are present here")
	@ApiBaseSiteIdAndUserIdParam
	public ConfigurationOverviewWsDTO getConfigurationOverviewForQuotes(//
			@Parameter(required = true, description = "The quote id. Each quote has a unique identifier.") //
			@PathVariable("quoteId") final String quoteId, //
			@Parameter(required = true, description = "The entry number. Each entry in a quote has an entry number. Quote entries are numbered in ascending order, starting with zero (0).") //
			@PathVariable("entryNumber") final int entryNumber) throws NotFoundException
	{
		final ConfigurationOverviewData readConfigurationOverview;
		try
		{
			readConfigurationOverview = readConfigurationOverview(quoteId, entryNumber);
		}
		catch (final IllegalArgumentException ex)
		{
			LOG.error("getConfigurationOverviewForQuoteEntry: cannot retrieve configuration information for '"
					+ logParam("quoteId", sanitize(quoteId)) + "," + logParam("entryNumber", String.valueOf(entryNumber)) + "'");
			throw new NotFoundException(
					"Cannot retrieve configuration information for quoteId=" + sanitize(quoteId) + ", entryNumber=" + entryNumber,
					ex.getMessage(), ex);
		}


		final ConfigurationOverviewWsDTO configurationOverviewWs = dataMapper.map(readConfigurationOverview,
				ConfigurationOverviewWsDTO.class);
		getMessageHandler().processConfigurationOverview(configurationOverviewWs);
		return configurationOverviewWs;
	}

	protected DataMapper getDataMapper()
	{
		return dataMapper;
	}

	protected ConfigurationOverviewData readConfigurationOverview(final String quoteId, final int entryNumber)
	{
		final ConfigurationOverviewData configuration = configurationQuoteIntegrationFacade.getConfiguration(quoteId, entryNumber);
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
