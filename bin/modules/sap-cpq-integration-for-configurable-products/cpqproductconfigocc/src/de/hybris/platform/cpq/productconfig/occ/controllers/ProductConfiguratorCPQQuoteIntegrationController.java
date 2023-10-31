/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.occ.controllers;

import de.hybris.platform.cpq.productconfig.facades.QuoteIntegrationFacade;
import de.hybris.platform.cpq.productconfig.facades.data.ProductConfigData;
import de.hybris.platform.cpq.productconfig.occ.ProductConfigWsDTO;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdAndUserIdParam;

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
@Tag(name = "Product Configurator CPQ Quote Integration")
@RequestMapping(value = "/{baseSiteId}/users/{userId}/quotes")
public class ProductConfiguratorCPQQuoteIntegrationController
{
	private static final Logger LOG = Logger.getLogger(ProductConfiguratorCPQQuoteIntegrationController.class);

	@Resource(name = "cpqProductConfigQuoteIntegrationFacade")
	protected QuoteIntegrationFacade quoteIntegrationFacade;

	@Resource(name = "dataMapper")
	protected DataMapper dataMapper;

	@GetMapping(value = "/{quoteId}/entries/{entryNumber}/"
			+ CpqproductconfigoccControllerConstants.CONFIGURATOR_TYPE_FOR_OCC_EXPOSURE)
	@ResponseBody
	@Operation(operationId = "getCPQConfigurationIdForQuoteEntry", summary = "Gets a product configuration id of a quote entry", description = "Gets a configuration id of a quote entry which can be used to read the attached configuration.")
	@ApiBaseSiteIdAndUserIdParam
	public ProductConfigWsDTO getConfigurationIdForQuoteEntry(//
			@Parameter(required = true, description = "The quote id. Each quote has a unique identifier.") //
			@PathVariable("quoteId") final String quoteId, //
			@Parameter(required = true, description = "The entry number. Each entry in a quote has an entry number. Quote entries are numbered in ascending order, starting with zero (0).") //
			@PathVariable("entryNumber") final int entryNumber)
	{
		final ProductConfigData data = getQuoteEntryConfiguartionIdInternal(quoteId, entryNumber);
		
		return getDataMapper().map(data, ProductConfigWsDTO.class);
	}

	protected ProductConfigData getQuoteEntryConfiguartionIdInternal(final String quoteId, final int entryNumber)
	{
		final ProductConfigData data = new ProductConfigData();
		data.setConfigId(getQuoteIntegrationFacade().getConfigurationId(quoteId, entryNumber));
		if (LOG.isDebugEnabled())
		{
			LOG.debug(String.format("configuration '%s' is attached to quote entry number '%d'", data.getConfigId(), entryNumber));
		}
		return data;
	}

	protected QuoteIntegrationFacade getQuoteIntegrationFacade()
	{
		return quoteIntegrationFacade;
	}

	protected DataMapper getDataMapper()
	{
		return dataMapper;
	}
}
