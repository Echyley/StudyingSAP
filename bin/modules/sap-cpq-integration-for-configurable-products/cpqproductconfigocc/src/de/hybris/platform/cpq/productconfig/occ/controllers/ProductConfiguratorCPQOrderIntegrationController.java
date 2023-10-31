/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.occ.controllers;

import de.hybris.platform.cpq.productconfig.facades.OrderIntegrationFacade;
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

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@Controller
@Tag(name = "Product Configurator CPQ Order Integration")
@RequestMapping(value = "/{baseSiteId}/users/{userId}/orders")
public class ProductConfiguratorCPQOrderIntegrationController
{
	private static final Logger LOG = Logger.getLogger(ProductConfiguratorCPQOrderIntegrationController.class);

	@Resource(name = "cpqProductConfigOrderIntegrationFacade")
	protected OrderIntegrationFacade orderIntegrationFacade;

	@Resource(name = "dataMapper")
	protected DataMapper dataMapper;

	@GetMapping(value = "/{orderId}/entries/{entryNumber}/"
			+ CpqproductconfigoccControllerConstants.CONFIGURATOR_TYPE_FOR_OCC_EXPOSURE)
	@ResponseBody
	@Operation(operationId = "getCPQConfigurationIdForOrderEntry", summary = "Gets a product configuration id of an order entry", description = "Gets a configuration id of an order entry which can be used to read the attached configuration.")
	@ApiBaseSiteIdAndUserIdParam
	public ProductConfigWsDTO getConfigurationIdForOrderEntry(//
			@Parameter(required = true, description = "The order id. Each order has a unique identifier.") //
			@PathVariable("orderId") final String orderId, //
			@Parameter(required = true, description = "The entry number. Each entry in an order has an entry number. Order entries are numbered in ascending order, starting with zero (0).") //
			@PathVariable("entryNumber") final int entryNumber)
	{
		final ProductConfigData data = getOrderEntryConfiguartionIdInternal(orderId, entryNumber);
		
		return getDataMapper().map(data, ProductConfigWsDTO.class);
	}

	protected ProductConfigData getOrderEntryConfiguartionIdInternal(final String orderId, final int entryNumber)
	{
		final ProductConfigData data = orderIntegrationFacade.getConfigurationId(orderId, entryNumber);
		if (LOG.isDebugEnabled())
		{
			LOG.debug(String.format("configuration '%s' is attached to order entry number '%d'", data.getConfigId(), entryNumber));
		}
		return data;
	}

	protected OrderIntegrationFacade getOrderIntegrationFacade()
	{
		return orderIntegrationFacade;
	}

	protected DataMapper getDataMapper()
	{
		return dataMapper;
	}
}
