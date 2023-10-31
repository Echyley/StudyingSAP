/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtservices.checkout.impl;

import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.DeliveryModeData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.order.model.AbstractOrderEntryProductInfoModel;
import de.hybris.platform.order.strategies.SubmitOrderStrategy;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.Basket;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.Order;
import de.hybris.platform.sap.sapordermgmtservices.bolfacade.BolOrderFacade;
import de.hybris.platform.sap.sapordermgmtservices.cart.CartRestorationService;
import de.hybris.platform.sap.sapordermgmtservices.cart.impl.DefaultCartCheckoutBaseService;
import de.hybris.platform.sap.sapordermgmtservices.checkout.CheckoutService;
import de.hybris.platform.sap.sapordermgmtservices.partner.SapPartnerService;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;


/**
 * Default service implementation for checkout with SAP Synchronous Order Management. <br>
 * The class synchronizes accesses to the BOL object representing the cart, as this is not thread safe. Multi-threaded
 * accesses can happen although we use request sequencing, since also filters might call cart facades.
 */
public class DefaultCheckoutService extends DefaultCartCheckoutBaseService implements CheckoutService
{
	@Autowired(required = false)
	@Qualifier("orderMetricsSubmitOrderStrategy")
	private SubmitOrderStrategy submitOrderStrategy;
	private static final Logger LOG = LoggerFactory.getLogger(DefaultCheckoutService.class);
	private BolOrderFacade bolOrderFacade;
	private Converter<Order, OrderData> orderConverter;
	private Converter<Entry<String, String>, DeliveryModeData> deliveryModeConverter;
	private CartRestorationService cartRestorationService;
	private SapPartnerService sapPartnerService;
	private ProductService productService;


	/**
	 * @return The converter for mapping the BOL representation of the delivery mode into the format needed for facade
	 *         layer
	 */
	public Converter<Entry<String, String>, DeliveryModeData> getDeliveryModeConverter()
	{
		return deliveryModeConverter;
	}

	/**
	 * @param deliveryModeConverter
	 *           The converter for mapping the BOL representation of the delivery mode into the format needed for the
	 *           facade layer
	 */
	public void setDeliveryModeConverter(final Converter<Entry<String, String>, DeliveryModeData> deliveryModeConverter)
	{
		this.deliveryModeConverter = deliveryModeConverter;
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.hybris.platform.sap.sapordermgmtservices.checkout.CheckoutService#placeOrder(de.hybris.platform.core.model.
	 * order.CartModel )
	 */
	@Override
	public OrderData placeOrder()
	{

		final Basket sapCart = getBolCartFacade().getCart();

		synchronized (sapCart)
		{

			final Order sapOrder = getBolCartFacade().placeOrderFromCart(sapCart);

			final OrderData orderData = getOrderConverter().convert(sapOrder);

			final OrderModel orderModel = getOrderModelfromOrderData(orderData);

			if (submitOrderStrategy != null)
			{
				LOG.info("Strategy for order metrics measurement is enabled");
				submitOrderStrategy.submitOrder(orderModel);
			}
			else
			{
				LOG.info("Strategy for order metrics measurement is disabled");
			}


			//reset search cache
			getBolOrderFacade().setSearchDirty();

			//reset cart
			getBolCartFacade().releaseCart();

			//reset hybris cart that is used for cross session cart restoration
			//initialize also hybris cart that is used for cross session cart restoration
			if (cartRestorationService != null)
			{
				cartRestorationService.removeInternalSessionCart();
			}

			return orderData;
		}
	}

	/**
	 * @param orderData
	 * @return orderModel converted order Model from the passed order Data
	 *
	 */
	protected OrderModel getOrderModelfromOrderData(final OrderData orderData)
	{
		if (orderData == null)
		{
			return null;
		}

		final OrderModel orderModel = new OrderModel();
		orderModel.setCode(orderData.getCode());
		orderModel.setDate(orderData.getCreated());
		orderModel.setCurrency(new CurrencyModel());
		orderModel.getCurrency().setIsocode(orderData.getTotalPrice() == null ? null : orderData.getTotalPrice().getCurrencyIso());
		orderModel.setDeliveryCost(orderData.getDeliveryCost() == null ? 0 : orderData.getDeliveryCost().getValue().doubleValue());
		orderModel.setSubtotal(orderData.getSubTotal() == null ? 0 : orderData.getSubTotal().getValue().doubleValue());
		orderModel.setNet(orderData.isNet());
		orderModel.setTotalPrice(orderData.getTotalPrice() == null ? 0 : orderData.getTotalPrice().getValue().doubleValue());
		orderModel.setTotalDiscounts(
				orderData.getTotalDiscounts() == null ? 0 : orderData.getTotalDiscounts().getValue().doubleValue());
		orderModel.setTotalTax(orderData.getTotalTax() == null ? 0 : orderData.getTotalTax().getValue().doubleValue());
		orderModel.setEntries(new ArrayList<>());
		if (orderData.getEntries() != null)
		{
			orderData.getEntries().forEach(e -> {
				final AbstractOrderEntryModel model = new AbstractOrderEntryModel();
				populateEntryMetrics(e, model);
				orderModel.getEntries().add(model);
			});

		}
		if (orderData.getTotalPrice() != null)
		{
			final String price = orderData.getTotalPrice().getValue().toString();
			final int numberOfDigits = (price.contains(".")) ? price.substring(price.indexOf(".") + 1, price.length()).length() : 0;
			orderModel.getCurrency().setDigits(numberOfDigits);
		}
		return orderModel;
	}


	/**
	 * @param e
	 * @param model
	 * @param orderData
	 */
	protected void populateEntryMetrics(final OrderEntryData e, final AbstractOrderEntryModel model)
	{
		final ProductModel productModel = this.getProductService().getProductForCode(e.getProduct().getCode());
		final List<AbstractOrderEntryProductInfoModel> infoList = new ArrayList<>();
		final AbstractOrderEntryProductInfoModel entryModel = new AbstractOrderEntryProductInfoModel();
		infoList.add(entryModel);
		model.setEntryNumber(e.getEntryNumber());
		model.setProduct(productModel);
		model.getProduct().setCode(e.getProduct().getCode());
		model.setQuantity(e.getQuantity());
		model.setTotalPrice(e.getTotalPrice().getValue().doubleValue());
		model.setUnit(productModel.getUnit());
		model.getUnit().setCode(productModel.getUnit().getCode());
		model.setProductInfos(infoList);
	}


	/**
	 * @return The converter we need to map the BOL representation of an order into the format needed in the facade layer
	 */
	public Converter<Order, OrderData> getOrderConverter()
	{
		return orderConverter;
	}

	/**
	 * @param orderConverter
	 *           The converter we need to map the BOL representation of an order into the format needed in the facade
	 *           layer
	 */
	public void setOrderConverter(final Converter<Order, OrderData> orderConverter)
	{
		this.orderConverter = orderConverter;
	}


	@Override
	public List<DeliveryModeData> getSupportedDeliveryModes()
	{
		final Map<String, String> allowedDeliveryTypes = getBolCartFacade().getAllowedDeliveryTypes();
		final List<DeliveryModeData> result = new ArrayList<>();

		//first add current one if existing
		final String currentCode = getCurrentDeliveryMode();
		if (currentCode != null && !currentCode.isEmpty())
		{
			result.add(getDeliveryModeConverter().convert(getEntry(currentCode, allowedDeliveryTypes)));
		}


		final Iterator<Entry<String, String>> iterator = allowedDeliveryTypes.entrySet().iterator();
		while (iterator.hasNext())
		{

			//now add the other ones
			final Entry<String, String> next = iterator.next();
			if (!next.getKey().equals(currentCode))
			{
				result.add(getDeliveryModeConverter().convert(next));
			}
		}

		return result;
	}


	@Override
	public boolean setDeliveryMode(final String deliveryModeCode)
	{
		final Basket cart = getBolCartFacade().getCart();
		synchronized (cart)
		{
			cart.getHeader().setShipCond(deliveryModeCode);
			getBolCartFacade().updateCart();
			return true;
		}
	}


	/**
	 * @return Current delivery mode from BOL header object
	 */
	public String getCurrentDeliveryMode()
	{
		final Basket cart = getBolCartFacade().getCart();
		return cart.getHeader().getShipCond();
	}

	Entry<String, String> getEntry(final String key, final Map<String, String> map)
	{
		Entry<String, String> result = null;
		final Iterator<Entry<String, String>> iterator = map.entrySet().iterator();
		while (iterator.hasNext())
		{
			final Entry<String, String> next = iterator.next();
			if (next.getKey().equals(key))
			{
				result = next;
				break;
			}
		}
		return result;
	}


	@Override
	public boolean setPurchaseOrderNumber(final String purchaseOrderNumber)
	{
		final Basket cart = getBolCartFacade().getCart();
		synchronized (cart)
		{
			cart.getHeader().setPurchaseOrderExt(purchaseOrderNumber);
			getBolCartFacade().updateCart();
			return true;
		}

	}

	/**
	 * @return the bolOrderFacade
	 */
	public BolOrderFacade getBolOrderFacade()
	{
		return bolOrderFacade;
	}

	/**
	 * @param bolOrderFacade
	 *           the bolOrderFacade to set
	 */
	public void setBolOrderFacade(final BolOrderFacade bolOrderFacade)
	{
		this.bolOrderFacade = bolOrderFacade;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.sap.sapordermgmtservices.checkout.CheckoutService#setDeliveryAddress(java.lang.String)
	 */
	@Override
	public boolean setDeliveryAddress(final String sapCustomerId)
	{
		final Basket cart = getBolCartFacade().getCart();
		synchronized (cart)
		{
			cart.getHeader().getShipTo().setId(sapCustomerId);
			getBolCartFacade().updateCart();
			return true;
		}
	}

	/**
	 * @param cartRestorationService
	 *           the cartRestorationService to set
	 */
	public void setCartRestorationService(final CartRestorationService cartRestorationService)
	{
		this.cartRestorationService = cartRestorationService;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.hybris.platform.sap.sapordermgmtservices.cart.CartService#updateCheckoutCart(de.hybris.platform.commercefacades
	 * .order.data.CartData)
	 */
	@Override
	public CartData updateCheckoutCart(final CartData cartData)
	{
		final Basket currentCart = getBolCartFacade().getCart();
		synchronized (currentCart)
		{

			if (cartData.getPurchaseOrderNumber() != null)
			{
				currentCart.getHeader().setPurchaseOrderExt(cartData.getPurchaseOrderNumber());
			}

			if (cartData.getDeliveryAddress() != null)
			{

				final Collection<AddressModel> allowedDeliveryAddresses = sapPartnerService.getAllowedDeliveryAddresses();
				AddressModel deliveryAddress = null;

				for (final AddressModel address : allowedDeliveryAddresses)
				{
					if (cartData.getDeliveryAddress().getId().equals(address.getPk().toString()))
					{
						deliveryAddress = address;
						break;
					}
				}

				if (deliveryAddress != null)
				{
					this.setDeliveryAddress(deliveryAddress.getSapCustomerID());
				}

			}


			getBolCartFacade().updateCart();


			return getSessionCart();


		}
	}

	/**
	 * @return the sapPartnerService
	 */
	public SapPartnerService getSapPartnerService()
	{
		return sapPartnerService;
	}

	/**
	 * @param sapPartnerService
	 *           the sapPartnerService to set
	 */
	public void setSapPartnerService(final SapPartnerService sapPartnerService)
	{
		this.sapPartnerService = sapPartnerService;
	}

	protected ProductService getProductService()
	{
		return productService;
	}

	public void setProductService(final ProductService productService)
	{
		this.productService = productService;
	}




}
