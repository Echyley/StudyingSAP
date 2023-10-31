/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapcentralorderrcfacades.populator.impl;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.ConsignmentData;
import de.hybris.platform.commercefacades.order.data.ConsignmentEntryData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.sap.sapcentralorderservices.pojo.v1.CentralOrderDetailsResponse;
import de.hybris.platform.sap.sapcentralorderservices.pojo.v1.ExternalSystemReference;
import de.hybris.platform.sap.sapcentralorderservices.pojo.v1.ItemPrice;
import de.hybris.platform.sap.sapcentralorderservices.pojo.v1.ItemPriceAspectData;
import de.hybris.platform.sap.sapcentralorderservices.pojo.v1.Market;
import de.hybris.platform.sap.sapcentralorderservices.pojo.v1.OrderItem;
import de.hybris.platform.sap.sapcentralorderservices.pojo.v1.PriceTotals;
import de.hybris.platform.sap.sapcentralorderservices.pojo.v1.Product;
import de.hybris.platform.sap.sapcentralorderservices.pojo.v1.Quantity;
import de.hybris.platform.sap.sapcentralorderservices.pojo.v1.SubscriptionItemPrice;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;


/**
 *
 */

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCentralOrderRCDetailsPopulatorTest
{


	DefaultCentralOrderRCDetailsPopulator defaultCentralOrderRCDetailsPopulator = new DefaultCentralOrderRCDetailsPopulator();

	double originalAmount = 5500;

	double finalAmount = 5000;


	PriceDataFactory priceDataFactory = mock(PriceDataFactory.class);

	@Test
	public void test()
	{
		defaultCentralOrderRCDetailsPopulator.setPriceDataFactory(priceDataFactory);
		//setting up source and target.
		final CentralOrderDetailsResponse source = new CentralOrderDetailsResponse();
		final OrderData target = new OrderData();

		//set up
		final ProductData productData = new ProductData();
		productData.setSubscriptionCode("productId");

		final OrderEntryData orderEntryData = new OrderEntryData();
		orderEntryData.setProduct(productData);

		final ConsignmentEntryData consignmentEntryData = new ConsignmentEntryData();
		consignmentEntryData.setOrderEntry(orderEntryData);

		final List<ConsignmentEntryData> consignmentDataEntries = new ArrayList();
		consignmentDataEntries.add(consignmentEntryData);

		final ConsignmentData consignmentData = new ConsignmentData();
		consignmentData.setEntries(consignmentDataEntries);

		final List<ConsignmentData> consignmentDataList = new ArrayList();
		consignmentDataList.add(consignmentData);

		target.setConsignments(consignmentDataList);

		final PriceTotals priceTotal = new PriceTotals();
		priceTotal.setCategory("onetime");
		priceTotal.setOriginalAmount(originalAmount);
		priceTotal.setFinalAmount(finalAmount);

		final List<PriceTotals> priceTotals = new ArrayList();
		priceTotals.add(priceTotal);

		final SubscriptionItemPrice subscriptionItemPrice = new SubscriptionItemPrice();
		subscriptionItemPrice.setPriceTotals(priceTotals);

		final ItemPriceAspectData itemPriceAspectData = new ItemPriceAspectData();
		itemPriceAspectData.setSubscriptionItemPrice(subscriptionItemPrice);

		final ItemPrice itemPrice = new ItemPrice();
		itemPrice.setItemPriceAspectData(itemPriceAspectData);

		final ExternalSystemReference ref = new ExternalSystemReference();
		ref.setExternalId("productId");
		final List<ExternalSystemReference> refList = List.of(ref);

		final Product product = new Product();
		product.setExternalSystemReferences(refList);

		final Quantity quantity = new Quantity();
		quantity.setValue(2);

		final OrderItem orderItem = new OrderItem();
		orderItem.setProduct(product);
		orderItem.setItemPrice(itemPrice);
		orderItem.setQuantity(quantity);

		final List<OrderItem> orderItems = new ArrayList();
		orderItems.add(orderItem);

		source.setOrderItems(orderItems);

		final Market market = new Market();
		market.setCurrency("dollar");

		source.setMarket(market);

		final PriceData priceData = new PriceData();
		priceData.setValue(BigDecimal.valueOf(originalAmount));
		priceData.setCurrencyIso("currencyIso");
		priceData.setPriceType(PriceDataType.BUY);
		when(priceDataFactory.create(eq(PriceDataType.BUY), eq(BigDecimal.valueOf(originalAmount)), anyString()))
				.thenReturn(priceData);

		final PriceData priceDataForFinalAmount = new PriceData();
		priceDataForFinalAmount.setValue(BigDecimal.valueOf(finalAmount));
		priceDataForFinalAmount.setCurrencyIso("currencyIso");
		priceDataForFinalAmount.setPriceType(PriceDataType.BUY);
		when(priceDataFactory.create(eq(PriceDataType.BUY), eq(BigDecimal.valueOf(finalAmount)), anyString()))
				.thenReturn(priceDataForFinalAmount);

		defaultCentralOrderRCDetailsPopulator.setPriceDataFactory(priceDataFactory);

		//execute
		defaultCentralOrderRCDetailsPopulator.populate(source, target);

		//verify
		Assert.assertEquals(Long.valueOf(2), target.getConsignments().get(0).getEntries().get(0).getOrderEntry().getQuantity());
		Assert.assertEquals(BigDecimal.valueOf(finalAmount),
				target.getConsignments().get(0).getEntries().get(0).getOrderEntry().getTotalPrice().getValue());
		Assert.assertEquals(BigDecimal.valueOf(originalAmount),
				target.getConsignments().get(0).getEntries().get(0).getOrderEntry().getBasePrice().getValue());
	}

}
