/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saps4omservices.services.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.order.strategies.calculation.FindDeliveryCostStrategy;
import de.hybris.platform.order.strategies.calculation.FindPaymentCostStrategy;
import de.hybris.platform.order.strategies.calculation.FindPriceStrategy;
import de.hybris.platform.order.strategies.calculation.OrderRequiresCalculationStrategy;
import de.hybris.platform.sap.sapmodel.enums.SAPProductType;
import de.hybris.platform.sap.saps4omservices.services.SapS4OMPricingService;
import de.hybris.platform.sap.saps4omservices.services.SapS4OrderManagementConfigService;
import de.hybris.platform.sap.saps4omservices.services.SapS4SalesOrderSimulationService;
import de.hybris.platform.util.DiscountValue;
import de.hybris.platform.util.PriceValue;
import de.hybris.platform.util.TaxValue;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SapCartCalculationServiceTest {

	@Spy
	@InjectMocks
	SapCartCalculationService calculationService;

	@Mock
	SapS4SalesOrderSimulationService sapS4SalesOrderSimulationService;

	@Mock
	SapS4OrderManagementConfigService sapS4OrderManagementConfigService;

	@Mock
	SapS4OMPricingService sapS4OMPricingService;

	@Mock
	OrderRequiresCalculationStrategy orderRequiresCalculationStrategy;

	@Mock
	FindDeliveryCostStrategy findDeliveryCostStrategy;

	@Mock
	FindPriceStrategy findPriceStrategy;

	@Mock
	FindPaymentCostStrategy findPaymentCostStrategy;

	@Test
	public void testCalculate() {

		OrderModel order = spy(OrderModel.class);
		OrderEntryModel entry = spy(OrderEntryModel.class);
		order.setEntries(Collections.singletonList(entry));

		when(sapS4OrderManagementConfigService.isCartPricingEnabled()).thenReturn(true);
		doNothing().when(sapS4SalesOrderSimulationService).setCartDetails(order);
		when(orderRequiresCalculationStrategy.requiresCalculation(order)).thenReturn(false);

		try {
			calculationService.calculate(order);
		} catch (CalculationException e) {
			e.printStackTrace();
		}

		verify(sapS4SalesOrderSimulationService).setCartDetails(order);

	}

	@Test
	public void testCalculateTotals() {

		OrderModel order = spy(OrderModel.class);
		OrderEntryModel entry = spy(OrderEntryModel.class);
		order.setEntries(Collections.singletonList(entry));

		when(sapS4OrderManagementConfigService.isCartPricingEnabled()).thenReturn(true);
		doNothing().when(sapS4SalesOrderSimulationService).setCartDetails(order);
		when(orderRequiresCalculationStrategy.requiresCalculation(order)).thenReturn(false);

		try {
			calculationService.calculateTotals(order, false);
		} catch (CalculationException e) {
			e.printStackTrace();
		}

		verify(sapS4SalesOrderSimulationService).setCartDetails(order);

	}

	@Test
	public void testResetAllValuesOrderEntry() {

		OrderEntryModel entry = spy(OrderEntryModel.class);

		when(sapS4OrderManagementConfigService.isCartPricingEnabled()).thenReturn(true);

		try {
			calculationService.resetAllValues(entry);
		} catch (CalculationException e) {
			e.printStackTrace();
		}

		verify(sapS4OrderManagementConfigService).isCartPricingEnabled();

	}

	@Test
	public void testResetAllValuesOrder() {

		OrderModel order = spy(OrderModel.class);
		OrderEntryModel entry = spy(OrderEntryModel.class);
		order.setEntries(Collections.singletonList(entry));

		when(sapS4OrderManagementConfigService.isCartPricingEnabled()).thenReturn(true);

		try {
			Map<TaxValue, Map<Set<TaxValue>, Double>> taxValueMap = calculationService.resetAllValues(order);
			assertEquals(0, taxValueMap.size());
		} catch (CalculationException e) {
			e.printStackTrace();
		}

		verify(sapS4OrderManagementConfigService).isCartPricingEnabled();

	}

	@Test
	public void testFindDiscountValues() {

		OrderModel order = spy(OrderModel.class);
		OrderEntryModel entry = spy(OrderEntryModel.class);
		entry.setOrder(order);
		order.setEntries(Collections.singletonList(entry));
		when(sapS4OrderManagementConfigService.isCartPricingEnabled()).thenReturn(true);

		try {
			List<DiscountValue> discounts = calculationService.findDiscountValues(entry);
			assertNull(discounts);
		} catch (CalculationException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void testFindBasePrice() {

		OrderModel order = spy(OrderModel.class);
		OrderEntryModel entry = spy(OrderEntryModel.class);
		ProductModel product = spy(ProductModel.class);
		entry.setOrder(order);
		entry.setProduct(product);
		PriceInformation priceInfo = new PriceInformation(new PriceValue("USD", 10, false));

		order.setEntries(Collections.singletonList(entry));
		when(sapS4OrderManagementConfigService.isCatalogPricingEnabled()).thenReturn(true);
		when(product.getSapProductTypes()).thenReturn(Collections.singleton(SAPProductType.PHYSICAL));
		when(sapS4OMPricingService.getPriceForProduct(product)).thenReturn(Collections.singletonList(priceInfo));

		try {
			PriceValue priceValue = calculationService.findBasePrice(entry);
			assertEquals(0, Double.compare(10, priceValue.getValue()));
		} catch (CalculationException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void testFindBasePriceWhenDisabled() {

		OrderEntryModel entry = spy(OrderEntryModel.class);

		when(sapS4OrderManagementConfigService.isCatalogPricingEnabled()).thenReturn(false);

		try {
			when(findPriceStrategy.findBasePrice(entry)).thenReturn(null);
			assertNull(calculationService.findBasePrice(entry));
		} catch (CalculationException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void testResetAdditionalCosts() {

		OrderModel order = spy(OrderModel.class);

		when(sapS4OrderManagementConfigService.isCartPricingEnabled()).thenReturn(true);

		calculationService.resetAdditionalCosts(order, Collections.emptyList());
		verify(sapS4OrderManagementConfigService).isCartPricingEnabled();

	}

	@Test
	public void testResetAdditionalCostsWhenDisabled() {

		OrderModel order = spy(OrderModel.class);

		when(sapS4OrderManagementConfigService.isCartPricingEnabled()).thenReturn(false);
		when(findDeliveryCostStrategy.getDeliveryCost(order)).thenReturn(null);
		when(findPaymentCostStrategy.getPaymentCost(order)).thenReturn(null);

		calculationService.resetAdditionalCosts(order, Collections.emptyList());
		verify(sapS4OrderManagementConfigService).isCartPricingEnabled();

	}

	@Test
	public void testFindDiscountValuesWhenDisabled() {

		OrderEntryModel entry = spy(OrderEntryModel.class);

		when(sapS4OrderManagementConfigService.isCartPricingEnabled()).thenReturn(false);
		calculationService.setFindDiscountsStrategies(Collections.emptyList());

		try {
			List<DiscountValue> discounts = calculationService.findDiscountValues(entry);
			assertEquals(0, discounts.size());
		} catch (CalculationException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void testCalculateOrder() {

		OrderModel order = spy(OrderModel.class);
		order.setEntries(Collections.emptyList());

		calculationService.calculateOrder(order);

		verify(sapS4SalesOrderSimulationService, times(0)).setCartDetails(order);

	}
	
	@Test
	public void testCalculateOrderWhenDisabled() {

		OrderModel order = spy(OrderModel.class);
		OrderEntryModel entry = spy(OrderEntryModel.class);
		order.setEntries(Collections.singletonList(entry));
		when(sapS4OrderManagementConfigService.isCartPricingEnabled()).thenReturn(false);
		
		calculationService.calculateOrder(order);

		verify(sapS4SalesOrderSimulationService, times(0)).setCartDetails(order);

	}

}
