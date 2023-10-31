/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapcentralorderfacades.populator.impl;

import static com.sap.sapcentralorderfacades.constants.SapcentralorderfacadesConstants.CATEGORY_ONETIME;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.OrderHistoryData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.sap.sapcentralorderservices.pojo.v1.CentralOrderListResponse;
import de.hybris.platform.sap.sapcentralorderservices.pojo.v1.ExternalSystemReference;
import de.hybris.platform.sap.sapcentralorderservices.pojo.v1.Market;
import de.hybris.platform.sap.sapcentralorderservices.pojo.v1.Metadata;
import de.hybris.platform.sap.sapcentralorderservices.pojo.v1.PrecedingDocument;
import de.hybris.platform.sap.sapcentralorderservices.pojo.v1.PriceTotals;
import de.hybris.platform.sap.sapcentralorderservices.pojo.v1.Total;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

/**
 *
 */

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCentralOrderHistoryPopulatorTest
{


	PriceDataFactory priceDataFactory = mock(PriceDataFactory.class);

	DefaultCentralOrderHistoryPopulator defaultCentralOrderHistoryPopulator = new DefaultCentralOrderHistoryPopulator();

	final String status = OrderStatus.COMPLETED.toString();



	String precedingDocumentNumber = "precedingDocumentNumber";

	Metadata metadata = new Metadata();

	double finalAmount = 50000.0;

	BigDecimal amount = BigDecimal.valueOf(finalAmount);

	String dateNow = "1970-01-01";



	String currencyISO = "currencyISO";

	@Test
	public void shouldPopulateTest()
	{
		final OrderHistoryData target = new OrderHistoryData();

		final CentralOrderListResponse source = new CentralOrderListResponse();
		source.setStatus(status);
		source.setId("sourceId");

		final Map orderStatusDisplayMap = new HashMap<String, String>();
		orderStatusDisplayMap.put(status, status);

		final PrecedingDocument precedingDocument = new PrecedingDocument();
		final ExternalSystemReference systemRef = new ExternalSystemReference();
		systemRef.setExternalNumber(precedingDocumentNumber);
		precedingDocument.setExternalSystemReference(systemRef);
		source.setPrecedingDocument(precedingDocument);

		metadata.setCreatedAt(dateNow);
		source.setMetadata(metadata);

		final List<PriceTotals> priceTotals = new ArrayList();

		final PriceTotals priceTotal = new PriceTotals();
		priceTotal.setCategory(CATEGORY_ONETIME);
		priceTotal.setFinalAmount(finalAmount);
		final Total total = new Total();
		total.setFinalAmount(finalAmount);
		priceTotal.setTotal(total);
		priceTotals.add(priceTotal);

		source.setPrices(priceTotals);

		final Market market = new Market();
		market.setCurrency("currency");

		source.setMarket(market);

		final PriceData response = mock(PriceData.class);
		Mockito.lenient().when(response.getCurrencyIso()).thenReturn(currencyISO);
		Mockito.lenient().when(response.getValue()).thenReturn(BigDecimal.valueOf(finalAmount));
		Mockito.lenient().when(priceDataFactory.create(eq(PriceDataType.BUY), any(BigDecimal.class), anyString()))
				.thenReturn(response);

		defaultCentralOrderHistoryPopulator.setOrderStatusDisplayMap(orderStatusDisplayMap);
		defaultCentralOrderHistoryPopulator.setPriceDataFactory(priceDataFactory);

		//execute
		defaultCentralOrderHistoryPopulator.populate(source, target);

		//verify
		Assert.assertEquals(precedingDocumentNumber, target.getCode());
		Assert.assertNotNull(target.getPlaced());
		Assert.assertEquals(amount.setScale(1), (target.getTotal().getValue()));
	}

}
