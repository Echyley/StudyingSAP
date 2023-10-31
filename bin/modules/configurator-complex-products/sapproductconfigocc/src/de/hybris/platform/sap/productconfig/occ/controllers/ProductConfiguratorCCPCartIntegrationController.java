/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.occ.controllers;

import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commercewebservicescommons.dto.order.CartModificationWsDTO;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.CartEntryException;
import de.hybris.platform.sap.productconfig.facades.ConfigurationCartIntegrationFacade;
import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.facades.ConfigurationExpertModeFacade;
import de.hybris.platform.sap.productconfig.facades.ConfigurationFacade;
import de.hybris.platform.sap.productconfig.facades.KBKeyData;
import de.hybris.platform.sap.productconfig.occ.ConfigurationWsDTO;
import de.hybris.platform.sap.productconfig.occ.ProductConfigOrderEntryWsDTO;
import de.hybris.platform.sap.productconfig.occ.util.CCPControllerHelper;
import de.hybris.platform.sap.productconfig.occ.util.ImageHandler;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdUserIdAndCartIdParam;

import java.util.Optional;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;


@Controller
@Tag(name = "Product Configurator CCP Cart Integration")
@RequestMapping(value = "/{baseSiteId}/users/{userId}/carts")
public class ProductConfiguratorCCPCartIntegrationController
{
	private static final String PRODUCT_CODE = "', product code: '";
	private static final String CONFIG_ID = "configId: ";
	private static final Logger LOG = Logger.getLogger(ProductConfiguratorCCPCartIntegrationController.class);

	@Resource(name = "sapProductConfigCartIntegrationFacade")
	private ConfigurationCartIntegrationFacade configCartFacade;

	/**
	 * @deprecated since 2211. Not needed anymore.
	 */
	@Deprecated(since = "2211", forRemoval = true)
	@Resource(name = "sapProductConfigFacade")
	private ConfigurationFacade configFacade;

	@Resource(name = "commerceWebServicesCartFacade2")
	private CartFacade cartFacade;

	/**
	 * @deprecated since 2211. Not needed anymore.
	 */
	@Deprecated(since = "2211", forRemoval = true)
	@Resource(name = "sapProductConfigImageHandler")
	private ImageHandler imageHandler;

	@Resource(name = "sapProductConfigCCPControllerHelper")
	private CCPControllerHelper ccpControllerHelper;

	@Resource(name = "dataMapper")
	protected DataMapper dataMapper;

	@Resource(name = "sapProductConfigExpertModeFacade")
	private ConfigurationExpertModeFacade configExpertModeFacade;

	protected DataMapper getDataMapper()
	{
		return dataMapper;
	}


	@RequestMapping(value = "/{cartId}/entries/"
			+ SapproductconfigoccControllerConstants.CONFIGURATOR_TYPE_FOR_OCC_EXPOSURE, method = RequestMethod.POST)
	@ResponseBody
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(operationId = "createCartEntryConfiguration", summary = "Adds a product configuration to the cart", description = "Adds a product configuration to the cart. The root product of the configuration is added as a cart entry, in addition the configuration is attached to the new entry")
	@ApiBaseSiteIdUserIdAndCartIdParam
	public CartModificationWsDTO addCartEntry(//
			@Parameter(description = "Base site identifier") //
			@PathVariable final String baseSiteId, //
			@Parameter(description = "Request body parameter that contains attributes for creating the order entry, like quantity, product code and configuration identifier", required = true) //
			@RequestBody final ProductConfigOrderEntryWsDTO entry) throws CommerceCartModificationException
	{
		final CartModificationData cartModificationData = addCartEntryInternal(entry);
		return getDataMapper().map(cartModificationData, CartModificationWsDTO.class);
	}

	protected CartModificationData addCartEntryInternal(final ProductConfigOrderEntryWsDTO entry)
			throws CommerceCartModificationException
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug(
					"addCartEntry: '" + logParam(CONFIG_ID, entry.getConfigId()) + PRODUCT_CODE + entry.getProduct().getCode() + "'");
		}

		CartModificationData cartModificationData = null;
		cartModificationData = getConfigCartFacade().addProductConfigurationToCart(entry.getProduct().getCode(),
				entry.getQuantity(), entry.getConfigId());


		if (LOG.isDebugEnabled())
		{
			LOG.debug("addCartEntry: '" + logParam(CONFIG_ID, entry.getConfigId()) + PRODUCT_CODE + entry.getProduct().getCode()
					+ "' was successful");
		}

		return cartModificationData;
	}

	protected static String logParam(final String paramName, final String paramValue)
	{
		return paramName + " = " + paramValue;
	}


	@RequestMapping(value = "/{cartId}/entries/{entryNumber}/"
			+ SapproductconfigoccControllerConstants.CONFIGURATOR_TYPE_FOR_OCC_EXPOSURE, method = RequestMethod.PUT)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@Operation(operationId = "replaceCartEntryConfiguration", summary = "Updates the configuration of a cart entry", description = "Updates the configuration. The entire configuration attached to the cart entry is replaced by the configuration specified in the request body. Possible only if the configuration change has been initiated by the corresponding GET method before")
	@ApiBaseSiteIdUserIdAndCartIdParam
	public CartModificationWsDTO updateCartEntry(
			@Parameter(description = "Base site identifier.") @PathVariable final String baseSiteId,
			@Parameter(required = true, description = "The entry number. Each entry in a cart has an entry number. Cart entries are numbered in ascending order, starting with zero (0).") @PathVariable final int entryNumber,
			@Parameter(description = "Product configuration order entry with config id and order entry attributes") @RequestBody(required = true) final ProductConfigOrderEntryWsDTO entry)
	{
		entry.setEntryNumber(entryNumber);
		final CartModificationData cartModificationData = updateCartEntryInternal(entry);
		return getDataMapper().map(cartModificationData, CartModificationWsDTO.class);
	}

	protected CartModificationData updateCartEntryInternal(final ProductConfigOrderEntryWsDTO entry)

	{
		final String configId = entry.getConfigId();
		final String productCode = entry.getProduct().getCode();

		if (LOG.isDebugEnabled())
		{
			LOG.debug("updateCartEntry: '" + logParam(CONFIG_ID, configId) + PRODUCT_CODE + productCode + "'");
		}

		final CartModificationData cartModificationData = getConfigCartFacade().updateProductConfigurationInCart(productCode,
				configId);

		if (LOG.isDebugEnabled())
		{
			LOG.debug("updateCartEntry: '" + logParam(CONFIG_ID, configId) + PRODUCT_CODE + productCode + "' was successful");
		}
		return cartModificationData;
	}

	/**
	 * @deprecated since 2211.7 use {@link #configureCartEntry(String, boolean, int, boolean)}
	 */
	@Deprecated(since = "2211.7", forRemoval = true)
	public ConfigurationWsDTO configureCartEntry(final String baseSiteId, final int  entryNumber)
	{
		final ConfigurationData configData = configureCartEntryInternal(entryNumber);
		return getConfigurationHandler().mapDTOData(configData);
	}

	@RequestMapping(value = "/{cartId}/entries/{entryNumber}/"
			+ SapproductconfigoccControllerConstants.CONFIGURATOR_TYPE_FOR_OCC_EXPOSURE, method = RequestMethod.GET)
	@ResponseBody
	@Operation(operationId = "getCartEntryConfiguration", summary = "Gets the configuration of a cart entry", description = "Returns the configuration of a cart entry and ensures that the entry can later be updated with the configuration and its changes")
	@ApiBaseSiteIdUserIdAndCartIdParam
	public ConfigurationWsDTO configureCartEntry(//
			@Parameter(description = "Base site identifier.") //
			@PathVariable final String baseSiteId, //
			@Parameter(required = true, description = "The entry number. Each entry in a cart has an entry number. Cart entries are numbered in ascending order, starting with zero (0).") //
			@PathVariable final int entryNumber, //
			@Parameter(description = "If this parameter is provided and its value is true, the system tries to execute the request in expert mode (only possible if the current user belongs to group 'sapproductconfigexpmodegroup'") //
			@RequestParam(defaultValue = "false", required = false) //
			final boolean expMode)
	{
		if (expMode)
		{
			getConfigExpertModeFacade().enableExpertMode();
		}
		final ConfigurationData configData = configureCartEntryInternal(entryNumber);
		return getConfigurationHandler().mapDTOData(configData);
	}

	protected ConfigurationData configureCartEntryInternal(final long entryNumber)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("configureCartEntry with entryNumber '" + logParam("entryNumber", String.valueOf(entryNumber)) + "'");
		}
		final OrderEntryData orderEntry = getCartEntryForNumber(getSessionCart(), entryNumber);
		final ConfigurationData configData = getConfigCartFacade().configureCartItem(orderEntry.getItemPK());

		return configData;
	}

	protected KBKeyData getKbKey(final ProductData product)
	{
		final KBKeyData kbKeyData = new KBKeyData();
		kbKeyData.setProductCode(product.getCode());
		return kbKeyData;
	}

	protected OrderEntryData getCartEntryForNumber(final CartData sessionCart, final long entryNumber)
	{
		final Long requestedEntryNumberAsLong = Long.valueOf(entryNumber);
		final Integer requestedEntryNumber = Integer.valueOf(requestedEntryNumberAsLong.intValue());

		final Optional<OrderEntryData> cartEntry = sessionCart.getEntries()//
				.stream()//
				.filter(entry -> requestedEntryNumber.equals(entry.getEntryNumber()))//
				.findAny();
		if (cartEntry.isPresent())
		{
			return cartEntry.get();
		}
		throw new CartEntryException("Entry not found", CartEntryException.NOT_FOUND, String.valueOf(entryNumber));
	}


	protected CartData getSessionCart()
	{
		return getCartFacade().getSessionCart();
	}

	protected ConfigurationCartIntegrationFacade getConfigCartFacade()
	{
		return configCartFacade;
	}

	public void setConfigCartFacade(final ConfigurationCartIntegrationFacade configCartFacade)
	{
		this.configCartFacade = configCartFacade;
	}

	protected CartFacade getCartFacade()
	{
		return cartFacade;
	}

	/**
	 * @deprecated since 2211. Not needed anymore.
	 */
	@Deprecated(since = "2211", forRemoval = true)
	protected ImageHandler getImageHandler()
	{
		return imageHandler;
	}

	/**
	 * @deprecated since 2211. Not needed anymore.
	 */
	@Deprecated(since = "2211", forRemoval = true)
	protected ConfigurationFacade getConfigFacade()
	{
		return configFacade;
	}

	/**
	 * @deprecated since 2211. Not needed anymore.
	 */
	@Deprecated(since = "2211", forRemoval = true)
	protected void setConfigFacade(final ConfigurationFacade configFacade)
	{
		this.configFacade = configFacade;
	}

	protected CCPControllerHelper getConfigurationHandler()
	{
		return ccpControllerHelper;
	}

	protected ConfigurationExpertModeFacade getConfigExpertModeFacade()
	{
		return configExpertModeFacade;
	}

}
