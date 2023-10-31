/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.textfieldconfiguratortemplateoccaddon.controllers;

import de.hybris.platform.catalog.enums.ConfiguratorType;
import de.hybris.platform.catalog.enums.ProductInfoStatus;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.OrderFacade;
import de.hybris.platform.commercefacades.order.QuoteFacade;
import de.hybris.platform.commercefacades.order.SaveCartFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.CommerceSaveCartParameterData;
import de.hybris.platform.commercefacades.order.data.CommerceSaveCartResultData;
import de.hybris.platform.commercefacades.order.data.ConfigurationInfoData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.quote.data.QuoteData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceSaveCartException;
import de.hybris.platform.commercewebservicescommons.dto.order.CartModificationWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.ConfigurationInfoListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.ConfigurationInfoWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.OrderEntryWsDTO;
import de.hybris.platform.textfieldconfiguratortemplatefacades.TextFieldFacade;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.mapping.FieldSetLevelHelper;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdUserIdAndCartIdParam;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter; 
import io.swagger.v3.oas.annotations.media.Schema;


@Controller
@Tag(name = "Textfield Conifgurator Products")
@RequestMapping(value = "/{baseSiteId}")
public class ProductTextfieldConfiguratorController
{
	public static final String TEXTFIELDCONFIGURATOR_TYPE = "textfield";
	public static final String PAGE_LABEL = "configure" + TEXTFIELDCONFIGURATOR_TYPE;
	protected static final String DEFAULT_FIELD_SET = FieldSetLevelHelper.FULL_LEVEL;
	private static final Logger LOG = Logger.getLogger(ProductTextfieldConfiguratorController.class);
	@Resource(name = "cwsProductFacade")
	private ProductFacade productFacade;
	@Resource(name = "textFieldFacade")
	private TextFieldFacade textFieldFacade;
	@Resource(name = "cartFacade")
	private CartFacade cartFacade;
	@Resource(name = "dataMapper")
	private DataMapper dataMapper;
	@Resource(name = "orderFacade")
	private OrderFacade orderFacade;
	@Resource(name = "quoteFacade")
	private QuoteFacade quoteFacade;
	@Resource(name = "saveCartFacade")
	private SaveCartFacade saveCartFacade;

	protected DataMapper getDataMapper()
	{
		return dataMapper;
	}

	protected CartFacade getCartFacade()
	{
		return cartFacade;
	}

	@GetMapping(value = "/products/{productCode}/configurator/" + TEXTFIELDCONFIGURATOR_TYPE)
	@ResponseBody
	@Operation(summary = "Get textfield configuration", description = "Returns list of textfield configuration elements.")
	public ConfigurationInfoListWsDTO getConfigurationByProductCode(@Parameter(description = "Product identifier", required = true)
	@PathVariable
	final String productCode)
	{
		final List<ConfigurationInfoData> configInfoList = this.getProductFacade().getConfiguratorSettingsForCode(productCode);

		return mapToConfigurationInfoListWs(configInfoList);
	}

	@PostMapping(value = "/users/{userId}/carts/{cartId}/entries/configurator/" + TEXTFIELDCONFIGURATOR_TYPE, consumes =
	{ MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@ResponseBody
	@Operation(summary = "Adds a product to the cart.", description = "Adds a textfield configurator product to the cart.")
	@ApiBaseSiteIdUserIdAndCartIdParam
	public CartModificationWsDTO addCartEntry(@Parameter(description = "Base site identifier.")
	@PathVariable
	final String baseSiteId, @Parameter(description = "Request body parameter (DTO in xml or json format) which contains details like : "
			+ "product code (product.code), quantity of product (quantity), pickup store name (deliveryPointOfService.name)", required = true)
	@RequestBody
	final OrderEntryWsDTO entry,
			@Parameter(description = "Response configuration (list of fields, which should be returned in response)", schema = @Schema(allowableValues = {"BASIC", "DEFAULT", "FULL"}))
			@RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET)
			final String fields) throws CommerceCartModificationException
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("addCartEntry: " + entry.getProduct().getCode());
		}

		final CartModificationData cartModification = this.getCartFacade().addToCart(entry.getProduct().getCode(),
				entry.getQuantity());

		if (cartModification == null)
		{
			throw new CommerceCartModificationException("Null cart modification");
		}
		if (cartModification.getQuantityAdded() > 0)
		{
			this.getCartFacade().updateCartEntry(
					this.enrichOrderEntryWithConfigurationData(entry.getConfigurationInfos(), cartModification.getEntry()));
		}

		return this.getDataMapper().map(cartModification, CartModificationWsDTO.class, fields);
	}

	@GetMapping(value = "/users/{userId}/carts/{cartId}/entries/{entryNumber}/configurator/" + TEXTFIELDCONFIGURATOR_TYPE)
	@ResponseBody
	@Operation(summary = "Get configuration of cart entry.", description = "Get the textfield configuration for a cart entry.")
	public ConfigurationInfoListWsDTO getConfigurationInEntry(@Parameter(description = "Cart entry number.")
	@PathVariable
	final int entryNumber) throws CommerceCartModificationException
	{
		final CartData cart = getCartFacade().getSessionCart();
		final OrderEntryData entry = getTextFieldFacade().getAbstractOrderEntry(entryNumber, cart);

		return mapToConfigurationInfoListWs(entry.getConfigurationInfos());
	}

	@GetMapping(value = "/users/{userId}/orders/{orderId}/entries/{entryNumber}/configurator/" + TEXTFIELDCONFIGURATOR_TYPE)
	@ResponseBody
	@Operation(summary = "Get configuration for order entry.", description = "Get the textfield configuration for a order entry.")
	public ConfigurationInfoListWsDTO getConfigurationForOrderEntry(
			@Parameter(required = true, description = "The order id. Each order has a unique identifier.")
			@PathVariable("orderId")
			final String orderId,
			@Parameter(required = true, description = "The entry number. Each entry in an order has an entry number. Order entries are numbered in ascending order, starting with zero (0).") //
			@PathVariable("entryNumber")
			final int entryNumber) throws CommerceCartModificationException
	{
		final OrderData order = orderFacade.getOrderDetailsForCode(orderId);
		final OrderEntryData entry = getTextFieldFacade().getAbstractOrderEntry(entryNumber, order);
		return mapToConfigurationInfoListWs(entry.getConfigurationInfos());
	}

	@GetMapping(value = "/users/{userId}/quotes/{quoteId}/entries/{entryNumber}/configurator/" + TEXTFIELDCONFIGURATOR_TYPE)
	@ResponseBody
	@Operation(summary = "Get configuration for an quote entry.", description = "Get the textfield configuration for an quote entry.")
	public ConfigurationInfoListWsDTO getConfigurationForQuoteEntry(

			@Parameter(required = true, description = "The quote id. Each quote has a unique identifier.") //
			@PathVariable("quoteId")
			final String quoteId, //
			@Parameter(required = true, description = "The entry number. Each entry in a quote has an entry number. Quote entries are numbered in ascending order, starting with zero (0).") //
			@PathVariable("entryNumber")
			final int entryNumber) throws CommerceCartModificationException
	{
		final QuoteData quote = quoteFacade.getQuoteForCode(quoteId);
		final OrderEntryData entry = getTextFieldFacade().getAbstractOrderEntry(entryNumber, quote);
		return mapToConfigurationInfoListWs(entry.getConfigurationInfos());
	}

	@GetMapping(value = "/users/{userId}/savedCarts/{cartId}/entries/{entryNumber}/configurator/" + TEXTFIELDCONFIGURATOR_TYPE)
	@ResponseBody
	@Operation(summary = "Get configuration for an saved cart entry.", description = "Get the textfield configuration for a saved cart entry.")
	public ConfigurationInfoListWsDTO getConfigurationForSavedCartEntry(

			@Parameter(required = true, description = "The cart id. Each cart has a unique identifier.") //
			@PathVariable("cartId")
			final String cartId, //
			@Parameter(required = true, description = "The entry number. Each entry in a saved cart has an entry number. Saved Cart entries are numbered in ascending order, starting with zero (0).") //
			@PathVariable("entryNumber")
			final int entryNumber) throws CommerceCartModificationException, CommerceSaveCartException
	{

		final CommerceSaveCartParameterData parameters = new CommerceSaveCartParameterData();
		parameters.setCartId(cartId);
		final CommerceSaveCartResultData commerceSaveCartResultData = saveCartFacade.getCartForCodeAndCurrentUser(parameters);
		final OrderEntryData entry = getTextFieldFacade().getAbstractOrderEntry(entryNumber,
				commerceSaveCartResultData.getSavedCartData());
		return mapToConfigurationInfoListWs(entry.getConfigurationInfos());
	}

	@PostMapping(value = "/users/{userId}/carts/{cartId}/entries/{entryNumber}/configurator/" + TEXTFIELDCONFIGURATOR_TYPE)
	@ResponseBody
	@Operation(summary = "Update configuration of cart entry.", description = "Update the textfield configurtion for a cart entry.")
	public CartModificationWsDTO updateConfigurationInEntry(@Parameter(description = "Cart entry number.")
	@PathVariable
	final int entryNumber, @Parameter(description = "Request body parameter (DTO in xml or json format) which contains details like : "
			+ "product code (product.code), quantity of product (quantity), pickup store name (deliveryPointOfService.name)", required = true)
	@RequestBody
	final ConfigurationInfoListWsDTO configInfoList) throws CommerceCartModificationException
	{
		final CartData cart = getCartFacade().getSessionCart();
		final OrderEntryData entry = getTextFieldFacade().getAbstractOrderEntry(entryNumber, cart);
		final CartModificationData cartModification = getCartFacade()
				.updateCartEntry(enrichOrderEntryWithConfigurationData(configInfoList.getConfigurationInfos(), entry));

		return this.getDataMapper().map(cartModification, CartModificationWsDTO.class, "DEFAULT");
	}

	protected OrderEntryData enrichOrderEntryWithConfigurationData(final List<ConfigurationInfoWsDTO> configInfoListWsDto,
			final OrderEntryData orderEntry)
	{
		orderEntry.setConfigurationInfos(mapToConfigurationInfoList(configInfoListWsDto));

		return orderEntry;
	}

	protected ConfigurationInfoListWsDTO mapToConfigurationInfoListWs(final List<ConfigurationInfoData> configInfoList)
	{
		final List<ConfigurationInfoWsDTO> configInfoWsList = configInfoList.stream()
				.map(infoItem -> this.getDataMapper().map(infoItem, ConfigurationInfoWsDTO.class)).collect(Collectors.toList());

		final ConfigurationInfoListWsDTO configInfoListWsDto = new ConfigurationInfoListWsDTO();
		configInfoListWsDto.setConfigurationInfos(configInfoWsList);
		return configInfoListWsDto;
	}

	protected List<ConfigurationInfoData> mapToConfigurationInfoList(final List<ConfigurationInfoWsDTO> configInfoListWsDto)
	{
		return configInfoListWsDto.stream().map(infoData -> mapConfigInfo(infoData)).collect(Collectors.toList());
	}

	protected ConfigurationInfoData mapConfigInfo(final ConfigurationInfoWsDTO infoData)
	{
		final ConfigurationInfoData configInfoData = new ConfigurationInfoData();

		configInfoData.setConfigurationLabel(infoData.getConfigurationLabel());
		configInfoData.setConfigurationValue(infoData.getConfigurationValue());
		configInfoData.setConfiguratorType(ConfiguratorType.valueOf(infoData.getConfiguratorType()));
		configInfoData.setStatus(ProductInfoStatus.valueOf(infoData.getStatus()));

		return configInfoData;
	}



	public ProductFacade getProductFacade()
	{
		return productFacade;
	}

	protected TextFieldFacade getTextFieldFacade()
	{
		return textFieldFacade;
	}

	@Autowired
	public void setProductFacade(final ProductFacade productFacade)
	{
		this.productFacade = productFacade;
	}
}
