/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapcentralorderfacades.populator.impl;

import de.hybris.platform.commercefacades.i18n.I18NFacade;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.ConsignmentData;
import de.hybris.platform.commercefacades.order.data.ConsignmentEntryData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commerceservices.strategies.CheckoutCustomerStrategy;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.sap.sapcentralorderservices.pojo.v1.Address;
import de.hybris.platform.sap.sapcentralorderservices.pojo.v1.CentralOrderDetailsResponse;
import de.hybris.platform.sap.sapcentralorderservices.pojo.v1.OrderItem;
import de.hybris.platform.sap.sapcentralorderservices.pojo.v1.PaymentData;
import de.hybris.platform.sap.sapcentralorderservices.pojo.v1.PhysicalItemPrice;
import de.hybris.platform.sap.sapcentralorderservices.pojo.v1.PriceTotals;
import de.hybris.platform.sap.sapmodel.enums.SAPOrderStatus;
import de.hybris.platform.sap.sapmodel.model.SAPOrderModel;
import de.hybris.platform.servicelayer.user.UserService;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;

import com.sap.sapcentralorderfacades.constants.SapcentralorderfacadesConstants;
import com.sap.sapcentralorderfacades.populator.SapCpiOrderDetailPopulator;
import com.sap.sapcentralorderservices.services.CentralOrderService;
import com.sap.sapcentralorderservices.services.config.CoConfigurationService;


/**
 *
 */
public class DefaultCentralOrderDetailsPopulator implements Populator<OrderModel, OrderData>
{
	private static final Logger LOG = LoggerFactory.getLogger(DefaultCentralOrderDetailsPopulator.class);

	private PriceDataFactory priceDataFactory;
	private Map orderStatusDisplayMap;
	private String dateFormat = SapcentralorderfacadesConstants.DEFAULT_DATE_FORMAT;
	@Resource(name = "i18NFacade")
	private I18NFacade i18NFacade;
	private List<SapCpiOrderDetailPopulator> sapCpiOrderDetailPopulators;


	private CentralOrderService centralOrderService;
	private Populator centralOrderListPopulator;
	private Map<String, String> orderListSortMap;
	private Populator<CentralOrderDetailsResponse, OrderData> centralOrderDetailsPopulator;
	private CoConfigurationService configurationService;
	private UserService userService;
	private CheckoutCustomerStrategy checkoutCustomerStrategy;

	@Override
	public void populate(final OrderModel source, final OrderData target)
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");
		try
		{
			final Optional<SAPOrderModel> sapOrderModel = source.getSapOrders().stream().findFirst();
			if (getConfigurationService().isCoActive()
					&& (sapOrderModel.isPresent()
					&& SAPOrderStatus.SENT_TO_ERP.equals(sapOrderModel.get().getSapOrderStatus())))
			{
				final CustomerModel currentCustomer = (CustomerModel) getUserService().getCurrentUser();
				ResponseEntity<CentralOrderDetailsResponse> centralOrderDetailsResponse = null;
				final String sourceSystemId = getConfigurationService().getCoSourceSystemId();

				if (getCheckoutCustomerStrategy().isAnonymousCheckout())
				{
					centralOrderDetailsResponse = getCentralOrderService().getCentalOrderDetailsForGuid(currentCustomer,
							source.getCode(), sourceSystemId);
				}
				else
				{
					centralOrderDetailsResponse = getCentralOrderService().getCentalOrderDetailsForCode(currentCustomer,
							source.getCode(), sourceSystemId);
				}

				if (centralOrderDetailsResponse != null)
				{
					final CentralOrderDetailsResponse centralOrderDetails = centralOrderDetailsResponse.getBody();
					this.populateOrderDetail(centralOrderDetails, target);

				}
			}
		}
		catch (final Exception e)
		{
			LOG.warn(String.format(SapcentralorderfacadesConstants.ORDER_NOT_FOUND_FOR_USER, e.getMessage()));
		}

	}

	private void populateOrderDetail(final CentralOrderDetailsResponse source, final OrderData target) throws ParseException
	{

		final String orderStatus = getStatusDisplay(source.getStatus(), getOrderStatusDisplayMap());
		target.setStatus(OrderStatus.valueOf(orderStatus));
		target.setStatusDisplay(orderStatus);
		target.setGuid(source.getId());
		if (source.getPrecedingDocument() != null)
		{
			target.setCode(source.getPrecedingDocument().getExternalSystemReference().getExternalNumber());
		}

		if (source.getMetadata() != null)
		{
			final DateFormat dateFormatter = new SimpleDateFormat(getDateFormat());
			Date createdDate;
			Date orderCreatedDate;

			createdDate = new SimpleDateFormat(SapcentralorderfacadesConstants.DATE_FORMAT)
					.parse(source.getMetadata().getCreatedAt().substring(0, SapcentralorderfacadesConstants.DATE_SIZE));
			orderCreatedDate = dateFormatter.parse(dateFormatter.format(createdDate));
			target.setCreated(orderCreatedDate);
		}
		populateOrderTotal(source, target);

		//Setting SHIP_TO Address
		for (final Address address : source.getCustomer().getAddresses())
		{

			if (address.getAddressType().equalsIgnoreCase(SapcentralorderfacadesConstants.ADDRESS_TYPE_SHIPTO))
			{
				populateShipAddress(address, source, target);
			}
			else if (address.getAddressType().equalsIgnoreCase(SapcentralorderfacadesConstants.ADDRESS_TYPE_BILLTO))
			{
				for (final PaymentData paymentData : source.getPayment())
				{
					populateBillAddress(paymentData, address, source, target);
				}
			}
		}

		final List consignments = new ArrayList<ConsignmentData>();
		for (final ConsignmentData consignmentData : target.getConsignments())
		{
			final ConsignmentData centralOrderConsignmentData = populateConsignmentData(consignmentData, source);
			consignments.add(centralOrderConsignmentData);
		}
		target.setConsignments(consignments);

		if (!CollectionUtils.isEmpty(getSapCpiOrderDetailPopulators()))
		{
			for (final SapCpiOrderDetailPopulator sapCpiOrderDetailPopulator : getSapCpiOrderDetailPopulators())
			{
				sapCpiOrderDetailPopulator.populate(source, target);
			}
		}
	}


	/**
	 * @param source
	 * @param consignments
	 * @return
	 *
	 */
	private ConsignmentData populateConsignmentData(final ConsignmentData consignmentData,
				final CentralOrderDetailsResponse source)
	{
		final List consignmentEntryDataList = new ArrayList<ConsignmentEntryData>();
		for (final ConsignmentEntryData consignmentEntryData : consignmentData.getEntries())
		{
			final OrderEntryData orderEntryData = consignmentEntryData.getOrderEntry();
			for (final OrderItem orderItem : source.getOrderItems())
			{
				final PhysicalItemPrice phyItemPrice = orderItem.getItemPrice().getItemPriceAspectData().getPhysicalItemPrice();

				if ((phyItemPrice != null
						&& (!phyItemPrice.getPriceTotals().isEmpty() && phyItemPrice.getPriceTotals().get(0) != null && orderItem.getProduct().getExternalSystemReferences().get(0).getExternalId()!=null))
						&& (orderItem.getProduct().getExternalSystemReferences().get(0).getExternalId()
								.equalsIgnoreCase(orderEntryData.getProduct().getCode())))
				{
					final PriceTotals priceTotal = phyItemPrice.getPriceTotals().get(0);
					orderEntryData.setBasePrice(getPriceDataFactory().create(PriceDataType.BUY,
							BigDecimal.valueOf(priceTotal.getOriginalAmount()), source.getMarket().getCurrency()));
					orderEntryData.setTotalPrice(getPriceDataFactory().create(PriceDataType.BUY,
							BigDecimal.valueOf(priceTotal.getFinalAmount()), source.getMarket().getCurrency()));
					orderEntryData.setQuantity(Long.valueOf(orderItem.getQuantity().getValue()));
				}

			}

			consignmentEntryData.setOrderEntry(orderEntryData);
			consignmentEntryDataList.add(consignmentEntryData);
		}
		consignmentData.setEntries(consignmentEntryDataList);
		return consignmentData;
	}

	/**
	 * @param paymentData
	 *
	 */
	private void populateBillAddress(final PaymentData paymentData, final Address address,
			final CentralOrderDetailsResponse source, final OrderData target)
	{
			if (paymentData.getMethod().equalsIgnoreCase(SapcentralorderfacadesConstants.PAYMENT_TYPE_CARD))
			{
				final AddressData addressData = new AddressData();
				if (source.getCustomer() != null && source.getCustomer().getPerson() != null)
				{
					addressData.setTitleCode(source.getCustomer().getPerson().getAcademicTitle());
					addressData.setFirstName(source.getCustomer().getPerson().getFirstName());
					addressData.setLastName(source.getCustomer().getPerson().getLastName());
				}
				if (address.getHouseNumber() != null)
				{
					addressData.setLine1(address.getHouseNumber());
				}
				if (address.getStreet() != null)
				{
					addressData.setLine2(address.getStreet());
				}
				addressData.setTown(address.getCity());
				addressData.setPostalCode(address.getPostalCode());
				addressData.setBillingAddress(true);
				addressData.setShippingAddress(false);
				addressData.setPhone(address.getPhone());
				if (address.getCountry() != null)
				{
					final CountryData countryData = getI18NFacade().getCountryForIsocode(address.getCountry());
					addressData.setCountry(countryData);
				}
				final CCPaymentInfoData paymentInfo = target.getPaymentInfo();
				paymentInfo.setBillingAddress(addressData);
				target.setPaymentInfo(paymentInfo);
			}

	}

	/**
	 * @param address
	 *
	 */
	private void populateShipAddress(final Address address, final CentralOrderDetailsResponse source, final OrderData target)
	{

		final AddressData addressData = new AddressData();
		if (source.getCustomer() != null && source.getCustomer().getPerson() != null)
		{
			addressData.setTitleCode(source.getCustomer().getPerson().getAcademicTitle());
			addressData.setFirstName(source.getCustomer().getPerson().getFirstName());
			addressData.setLastName(source.getCustomer().getPerson().getLastName());
		}
		if (address.getHouseNumber() != null)
		{
			addressData.setLine1(address.getHouseNumber());
		}
		if (address.getStreet() != null)
		{
			addressData.setLine2(address.getStreet());
		}
		addressData.setTown(address.getCity());
		addressData.setPostalCode(address.getPostalCode());
		addressData.setBillingAddress(false);
		addressData.setShippingAddress(true);
		addressData.setPhone(address.getPhone());

		if (address.getCountry() != null)
		{
			final CountryData countryData = getI18NFacade().getCountryForIsocode(address.getCountry());
			addressData.setCountry(countryData);
		}
		target.setDeliveryAddress(addressData);
		target.setDeliveryMode(null);


	}

	/**
	 *
	 */
	private void populateOrderTotal(final CentralOrderDetailsResponse source, final OrderData target)
	{
		for (final PriceTotals priceTotal : source.getPrices())
		{
			if (StringUtils.isNotBlank(priceTotal.getCategory())
					&& SapcentralorderfacadesConstants.CATEGORY_ONETIME.equalsIgnoreCase(priceTotal.getCategory()))
			{
				final BigDecimal totalPrice = BigDecimal.valueOf(priceTotal.getTotal().getFinalAmount());
				final BigDecimal subTotal = BigDecimal.valueOf(priceTotal.getTotal().getEffectiveAmount());
				final Double discount = priceTotal.getTotal().getOriginalAmount() - priceTotal.getTotal().getEffectiveAmount();


				final Double taxes = priceTotal.getTotal().getTax().getTotalAmount();

				target.setSubTotal(getPriceDataFactory().create(PriceDataType.BUY, subTotal, source.getMarket().getCurrency()));
				if (discount > 0)
				{
					target.setTotalDiscounts(getPriceDataFactory().create(PriceDataType.BUY, BigDecimal.valueOf(discount),
							source.getMarket().getCurrency()));
				}

				target.setDeliveryCost(null);

				target.setTotalTax(
						getPriceDataFactory().create(PriceDataType.BUY, BigDecimal.valueOf(taxes), source.getMarket().getCurrency()));

				target.setTotalPriceWithTax(
						getPriceDataFactory().create(PriceDataType.BUY, totalPrice, source.getMarket().getCurrency()));

				target.setTotalPrice(getPriceDataFactory().create(PriceDataType.BUY, totalPrice, source.getMarket().getCurrency()));


			}
		}

	}

	protected String getStatusDisplay(final String orderStatus, final Map<String, String> orderDisplayStatusMap)
	{
		return orderDisplayStatusMap.get(orderStatus);

	}


	/**
	 * @return the sapCpiOrderDetailPopulators
	 */
	public List<SapCpiOrderDetailPopulator> getSapCpiOrderDetailPopulators()
	{
		return sapCpiOrderDetailPopulators;
	}


	/**
	 * @param sapCpiOrderDetailPopulators
	 *           the sapCpiOrderDetailPopulators to set
	 */
	public void setSapCpiOrderDetailPopulators(final List<SapCpiOrderDetailPopulator> sapCpiOrderDetailPopulators)
	{
		this.sapCpiOrderDetailPopulators = sapCpiOrderDetailPopulators;
	}






	/**
	 * @return the orderStatusDisplayMap
	 */
	public Map getOrderStatusDisplayMap()
	{
		return orderStatusDisplayMap;
	}


	/**
	 * @param orderStatusDisplayMap
	 *           the orderStatusDisplayMap to set
	 */
	public void setOrderStatusDisplayMap(final Map orderStatusDisplayMap)
	{
		this.orderStatusDisplayMap = orderStatusDisplayMap;
	}


	/**
	 * @return the dateFormat
	 */
	public String getDateFormat()
	{
		return dateFormat;
	}


	/**
	 * @return the priceDataFactory
	 */
	public PriceDataFactory getPriceDataFactory()
	{
		return priceDataFactory;
	}


	/**
	 * @param priceDataFactory
	 *           the priceDataFactory to set
	 */
	public void setPriceDataFactory(final PriceDataFactory priceDataFactory)
	{
		this.priceDataFactory = priceDataFactory;
	}


	/**
	 * @param dateFormat
	 *           the dateFormat to set
	 */
	public void setDateFormat(final String dateFormat)
	{
		this.dateFormat = dateFormat;
	}

	public I18NFacade getI18NFacade()
	{
		return i18NFacade;
	}

	public void setI18NFacade(final I18NFacade i18nFacade)
	{
		i18NFacade = i18nFacade;
	}


	protected CentralOrderService getCentralOrderService()
	{
		return centralOrderService;
	}


	public void setCentralOrderService(final CentralOrderService centralOrderService)
	{
		this.centralOrderService = centralOrderService;
	}

	public Populator<CentralOrderDetailsResponse, OrderData> getCentralOrderDetailsPopulator()
	{
		return centralOrderDetailsPopulator;
	}


	public void setCentralOrderDetailsPopulator(
			final Populator<CentralOrderDetailsResponse, OrderData> centralOrderDetailsPopulator)
	{
		this.centralOrderDetailsPopulator = centralOrderDetailsPopulator;
	}

	protected Map<String, String> getOrderListSortMap()
	{
		return orderListSortMap;
	}

	public void setOrderListSortMap(final Map<String, String> orderListSortMap)
	{
		this.orderListSortMap = orderListSortMap;
	}

	protected Populator getCentralOrderListPopulator()
	{
		return centralOrderListPopulator;
	}

	public void setCentralOrderListPopulator(final Populator centralOrderListPopulator)
	{
		this.centralOrderListPopulator = centralOrderListPopulator;
	}

	/**
	 * @return the configurationService
	 */
	public CoConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	/**
	 * @param configurationService
	 *           the configurationService to set
	 */
	public void setConfigurationService(final CoConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

	protected CheckoutCustomerStrategy getCheckoutCustomerStrategy()
	{
		return checkoutCustomerStrategy;
	}


	public void setCheckoutCustomerStrategy(final CheckoutCustomerStrategy checkoutCustomerStrategy)
	{
		this.checkoutCustomerStrategy = checkoutCustomerStrategy;
	}

	protected UserService getUserService()
	{
		return userService;
	}

	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}








}
