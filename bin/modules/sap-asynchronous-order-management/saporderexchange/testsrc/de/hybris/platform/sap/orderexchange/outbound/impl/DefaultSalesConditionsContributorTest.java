/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.orderexchange.outbound.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.sap.orderexchange.constants.OrderCsvColumns;
import de.hybris.platform.sap.orderexchange.constants.SalesConditionCsvColumns;
import de.hybris.platform.sap.orderexchange.constants.SaporderexchangeConstants;
import de.hybris.platform.sap.sapmodel.model.SAPPricingConditionModel;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.util.DiscountValue;
import de.hybris.platform.util.TaxValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@SuppressWarnings("javadoc")
@UnitTest
public class DefaultSalesConditionsContributorTest
{
	private static final Double PRICE_QUANTITY = new Double(2.5);
	private static final String EUR = "EUR";
	private static final Double PAYMENT_COST = new Double(4.95);
	private static final Double DELIVERY_COST = new Double(8.95);
	private static final Double BASE_PRICE = new Double(89.95);
	private static final Double DISCOUNT_VALUE = new Double(10.00);
	private static final String PAYMENT_COST_CODE = "paymentCostCode";
	private static final String DELIVERY_COST_CODE = "deliveryCostCode";
	private static final String GROSS_PRICE_CODE = "grossPriceCode";
	private static final String TAX_CODE = "taxCode";
	private static final String DISCOUNT_CODE = "discountCode";
	private static final String CODE = "Code";
	private static final int ENTRY_NUMBER_100_INT = 100;
	private static final long ENTRY_QUANTITY = 2;
	private static final String UNIT_CODE = "unitCode";
	private static final Double TAX_VALUE = new Double(19.0);

	private DefaultSalesConditionsContributor cut;
	private OrderModel order;

	@Mock
	private OrderEntryModel entry; // Use mock to entry since otherwise collections cannot be set!

	@Mock
	private TaxValue tax;

	@Mock
	private DiscountValue discount;

	@Before
	public void setUp()
	{
		cut = new DefaultSalesConditionsContributor(){
			@Override
			public Map<String, String> getBatchIdAttributes() {
				Map<String, String> batchIdAttributes = new HashMap<String, String>();
				batchIdAttributes.put("dh_batchId", "000001");
				batchIdAttributes.put("dh_sourceId", "HYBRIS");
				batchIdAttributes.put("dh_type", "SALESORDER_CREATE");

				return batchIdAttributes;
			}

		};
		constructOrder();
	}

	private void constructOrder()
	{
		MockitoAnnotations.initMocks(this);
		Mockito.when(tax.getValue()).thenReturn(TAX_VALUE);
		Mockito.when(discount.getCode()).thenReturn(DISCOUNT_CODE);
		Mockito.when(discount.getValue()).thenReturn(DISCOUNT_VALUE);
		Mockito.when(discount.isAbsolute()).thenReturn(true);

		order = new OrderModel();
		order.setCode(CODE);
		final List<AbstractOrderEntryModel> entries = new ArrayList<AbstractOrderEntryModel>();
		order.setEntries(entries);

		final UnitModel unit = new UnitModel();
		unit.setCode(UNIT_CODE);

		final ProductModel product = new ProductModel();
		product.setPriceQuantity(PRICE_QUANTITY);

		final Collection<TaxValue> taxValues = new ArrayList<TaxValue>();
		taxValues.add(tax);

		final List<DiscountValue> discountValues = new ArrayList<DiscountValue>();
		discountValues.add(discount);

		Mockito.when(entry.getEntryNumber()).thenReturn(ENTRY_NUMBER_100_INT);
		Mockito.when(entry.getBasePrice()).thenReturn(BASE_PRICE);
		Mockito.when(entry.getUnit()).thenReturn(unit);
		Mockito.when(entry.getTaxValues()).thenReturn(taxValues);
		Mockito.when(entry.getDiscountValues()).thenReturn(discountValues);
		Mockito.when(entry.getProduct()).thenReturn(product);
		Mockito.when(entry.getQuantity()).thenReturn(ENTRY_QUANTITY);

		entries.add(entry);

		final BaseStoreModel store = new BaseStoreModel();
		order.setStore(store);

		final CurrencyModel currency = new CurrencyModel();
		currency.setIsocode(EUR);
		order.setCurrency(currency);
		order.setPaymentCost(PAYMENT_COST);
		order.setDeliveryCost(DELIVERY_COST);
		cut.setPaymentCosts(PAYMENT_COST_CODE);
		cut.setDeliveryCosts(DELIVERY_COST_CODE);
		cut.setGrossPrice(GROSS_PRICE_CODE);
		cut.setTax1(TAX_CODE);
	}

	@Test
	public void testGetColumns()
	{
		final Set<String> columns = cut.getColumns();

		assertTrue(columns.contains(OrderCsvColumns.ORDER_ID));
		assertTrue(columns.contains(SalesConditionCsvColumns.CONDITION_ENTRY_NUMBER));
		assertTrue(columns.contains(SalesConditionCsvColumns.CONDITION_CODE));
		assertTrue(columns.contains(SalesConditionCsvColumns.CONDITION_CURRENCY_ISO_CODE));
		assertTrue(columns.contains(SalesConditionCsvColumns.CONDITION_VALUE));
		assertTrue(columns.contains(SalesConditionCsvColumns.ABSOLUTE));
		assertTrue(columns.contains(SalesConditionCsvColumns.CONDITION_UNIT_CODE));
		assertTrue(columns.contains(SalesConditionCsvColumns.CONDITION_PRICE_QUANTITY));
		assertTrue(columns.contains(SalesConditionCsvColumns.CONDITION_COUNTER));
	}

	@Test
	public void testCreateRows()
	{
		final List<Map<String, Object>> rows = cut.createRows(order);

		Map<String, Object> row = rows.get(0);
		assertEquals(GROSS_PRICE_CODE, row.get(SalesConditionCsvColumns.CONDITION_CODE));

		row = rows.get(1);
		assertEquals(TAX_CODE, row.get(SalesConditionCsvColumns.CONDITION_CODE));

		row = rows.get(2);
		assertEquals(DISCOUNT_CODE, row.get(SalesConditionCsvColumns.CONDITION_CODE));

		row = rows.get(3);
		assertEquals(DELIVERY_COST_CODE, row.get(SalesConditionCsvColumns.CONDITION_CODE));

		row = rows.get(4);
		assertEquals(PAYMENT_COST_CODE, row.get(SalesConditionCsvColumns.CONDITION_CODE));
	}

	@Test
	public void testCreateRowsSyncPricing()
	{
		final Set<SAPPricingConditionModel> sapPricingConditionModelSet = new HashSet<SAPPricingConditionModel>();
		final SAPPricingConditionModel sapPricingConditionModel1 = new SAPPricingConditionModel();
		sapPricingConditionModel1.setConditionCounter("01");
		sapPricingConditionModel1.setConditionType(GROSS_PRICE_CODE);
		sapPricingConditionModel1.setCurrencyKey(EUR);
		sapPricingConditionModel1.setConditionPricingUnit("1");
		sapPricingConditionModel1.setConditionUnit("PCE");
		sapPricingConditionModel1.setConditionRate("1.50");
		sapPricingConditionModel1.setConditionValue("41.12");
		sapPricingConditionModelSet.add(sapPricingConditionModel1);

		Mockito.when(entry.getSapPricingConditions()).thenReturn(sapPricingConditionModelSet);

		final List<Map<String, Object>> rows = cut.createRows(order);
		assertEquals(1, rows.size());

		final Map<String, Object> row = rows.get(0);
		assertEquals(GROSS_PRICE_CODE, row.get(SalesConditionCsvColumns.CONDITION_CODE));
		assertEquals(CODE, row.get(OrderCsvColumns.ORDER_ID));
		assertEquals(EUR, row.get(SalesConditionCsvColumns.CONDITION_CURRENCY_ISO_CODE));
		assertEquals("01", row.get(SalesConditionCsvColumns.CONDITION_COUNTER));

	}

	@Test
	public void testCreatePaymentCostRow()
	{
		final List<Map<String, Object>> result = new ArrayList<>();

		cut.createPaymentCostRow(order, result);

		final Map<String, Object> row = result.get(0);
		assertEquals(CODE, row.get(OrderCsvColumns.ORDER_ID));
		assertEquals(SaporderexchangeConstants.HEADER_ENTRY, row.get(SalesConditionCsvColumns.CONDITION_ENTRY_NUMBER));
		assertEquals(PAYMENT_COST_CODE, row.get(SalesConditionCsvColumns.CONDITION_CODE));
		assertEquals(Double.valueOf(PAYMENT_COST), row.get(SalesConditionCsvColumns.CONDITION_VALUE));
		assertEquals(EUR, row.get(SalesConditionCsvColumns.CONDITION_CURRENCY_ISO_CODE));
		assertEquals(2, row.get(SalesConditionCsvColumns.CONDITION_COUNTER));
		assertEquals(Boolean.TRUE, row.get(SalesConditionCsvColumns.ABSOLUTE));
	}

	@Test
	public void testCreateDeliveryCostRow()
	{
		final List<Map<String, Object>> result = new ArrayList<>();

		cut.createDeliveryCostRow(order, result);

		final Map<String, Object> row = result.get(0);
		assertEquals(CODE, row.get(OrderCsvColumns.ORDER_ID));
		assertEquals(SaporderexchangeConstants.HEADER_ENTRY, row.get(SalesConditionCsvColumns.CONDITION_ENTRY_NUMBER));
		assertEquals(DELIVERY_COST_CODE, row.get(SalesConditionCsvColumns.CONDITION_CODE));
		assertEquals(8.95, row.get(SalesConditionCsvColumns.CONDITION_VALUE));
		assertEquals(EUR, row.get(SalesConditionCsvColumns.CONDITION_CURRENCY_ISO_CODE));
		assertEquals(1, row.get(SalesConditionCsvColumns.CONDITION_COUNTER));
		assertEquals(Boolean.TRUE, row.get(SalesConditionCsvColumns.ABSOLUTE));
	}

	@Test
	public void testCreateDiscountRows()
	{
		final List<Map<String, Object>> result = new ArrayList<>();

		cut.createProductDiscountRows(order, result, entry);

		final Map<String, Object> row = result.get(0);
		assertEquals(CODE, row.get(OrderCsvColumns.ORDER_ID));
		assertEquals(ENTRY_NUMBER_100_INT, row.get(SalesConditionCsvColumns.CONDITION_ENTRY_NUMBER));
		assertEquals(EUR, row.get(SalesConditionCsvColumns.CONDITION_CURRENCY_ISO_CODE));
		assertEquals(UNIT_CODE, row.get(SalesConditionCsvColumns.CONDITION_UNIT_CODE));
		assertEquals(ENTRY_QUANTITY, row.get(SalesConditionCsvColumns.CONDITION_PRICE_QUANTITY));
		assertEquals(Boolean.TRUE, row.get(SalesConditionCsvColumns.ABSOLUTE));
		assertEquals(DISCOUNT_CODE, row.get(SalesConditionCsvColumns.CONDITION_CODE));
		assertEquals(Double.valueOf(DISCOUNT_VALUE * ENTRY_QUANTITY * -1), row.get(SalesConditionCsvColumns.CONDITION_VALUE));
	}

	@Test
	public void testCreateTaxRows()
	{
		final List<Map<String, Object>> result = new ArrayList<>();

		cut.createTaxRows(order, result, entry);

		final Map<String, Object> row = result.get(0);
		assertEquals(CODE, row.get(OrderCsvColumns.ORDER_ID));
		assertEquals(Integer.valueOf(ENTRY_NUMBER_100_INT), row.get(SalesConditionCsvColumns.CONDITION_ENTRY_NUMBER));
		assertEquals(TAX_CODE, row.get(SalesConditionCsvColumns.CONDITION_CODE));
		assertEquals(TAX_VALUE, row.get(SalesConditionCsvColumns.CONDITION_VALUE));
		assertEquals(Boolean.FALSE, row.get(SalesConditionCsvColumns.ABSOLUTE));
	}

	@Test
	public void testCreateGrossPriceRow()
	{
		final List<Map<String, Object>> result = new ArrayList<>();

		cut.createGrossPriceRow(order, result, entry);

		final Map<String, Object> row = result.get(0);
		assertEquals(CODE, row.get(OrderCsvColumns.ORDER_ID));
		assertEquals(Integer.valueOf(ENTRY_NUMBER_100_INT), row.get(SalesConditionCsvColumns.CONDITION_ENTRY_NUMBER));
		assertEquals(GROSS_PRICE_CODE, row.get(SalesConditionCsvColumns.CONDITION_CODE));
		assertEquals(BASE_PRICE, row.get(SalesConditionCsvColumns.CONDITION_VALUE));
		assertEquals(UNIT_CODE, row.get(SalesConditionCsvColumns.CONDITION_UNIT_CODE));
		assertEquals(PRICE_QUANTITY, row.get(SalesConditionCsvColumns.CONDITION_PRICE_QUANTITY));
		assertEquals(EUR, row.get(SalesConditionCsvColumns.CONDITION_CURRENCY_ISO_CODE));
		assertEquals(Boolean.TRUE, row.get(SalesConditionCsvColumns.ABSOLUTE));
	}

	@Test
	public void testCreateRows_withOrderDiscountAndMultipleOrderEntries()
	{
		// OrderEntry setup
		final OrderEntryModel orderEntry = mock(OrderEntryModel.class);

		final UnitModel unit = new UnitModel();
		unit.setCode(UNIT_CODE);

		final ProductModel product = new ProductModel();
		product.setPriceQuantity(PRICE_QUANTITY);

		Mockito.when(orderEntry.getEntryNumber()).thenReturn(ENTRY_NUMBER_100_INT + 1);
		Mockito.when(orderEntry.getBasePrice()).thenReturn(BASE_PRICE);
		Mockito.when(orderEntry.getUnit()).thenReturn(unit);
		Mockito.when(orderEntry.getTaxValues()).thenReturn(Collections.singletonList(tax));
		Mockito.when(orderEntry.getDiscountValues()).thenReturn(Collections.emptyList());
		Mockito.when(orderEntry.getProduct()).thenReturn(product);
		Mockito.when(orderEntry.getQuantity()).thenReturn(ENTRY_QUANTITY);

		// Order discount setup
		final String orderDiscountCode = "KA01";
		final double orderDiscountValuePercentage = 15.0;
		final DiscountValue orderDiscount = new DiscountValue(orderDiscountCode, orderDiscountValuePercentage, false, EUR);

		// Order setup
		final OrderModel mockOrder = mock(OrderModel.class);

		final CurrencyModel currency = new CurrencyModel();
		currency.setIsocode(EUR);

		Mockito.when(mockOrder.getCode()).thenReturn(CODE);
		Mockito.when(mockOrder.getCurrency()).thenReturn(currency);
		Mockito.when(mockOrder.getPaymentCost()).thenReturn(PAYMENT_COST);
		Mockito.when(mockOrder.getDeliveryCost()).thenReturn(DELIVERY_COST);
		Mockito.when(mockOrder.getGlobalDiscountValues()).thenReturn(Collections.singletonList(orderDiscount));
		Mockito.when(mockOrder.getEntries()).thenReturn(Arrays.asList(entry, orderEntry));
		Mockito.when(mockOrder.getStore()).thenReturn(new BaseStoreModel());

		// Build expected row for order discount
		final Map<String, Object> orderDiscountRow = new HashMap<>();
		orderDiscountRow.put(OrderCsvColumns.ORDER_ID, mockOrder.getCode());
		orderDiscountRow.put(SalesConditionCsvColumns.CONDITION_ENTRY_NUMBER, SaporderexchangeConstants.HEADER_ENTRY);
		orderDiscountRow.put(SalesConditionCsvColumns.CONDITION_VALUE, orderDiscountValuePercentage * -1);
		orderDiscountRow.put(SalesConditionCsvColumns.CONDITION_COUNTER, cut.getConditionCounterStartOrderDiscount());
		orderDiscountRow.put(SalesConditionCsvColumns.ABSOLUTE, Boolean.FALSE);
		orderDiscountRow.put(SalesConditionCsvColumns.CONDITION_CODE, orderDiscountCode);
		cut.getBatchIdAttributes().forEach(orderDiscountRow::putIfAbsent);
		orderDiscountRow.put("dh_batchId", order.getCode());

		// Assert that the number of order discount rows is exactly one
		final List<Map<String, Object>> actualRows = cut.createRows(mockOrder);

		int numOfOrderDiscounts = 0;
		for (final Map<String, Object> actualRow : actualRows)
		{
			if (actualRow.get("conditionCode").equals(orderDiscountCode))
			{
				if (actualRow.equals(orderDiscountRow)) {
					numOfOrderDiscounts++;
				}
			}
		}

		assertEquals(1, numOfOrderDiscounts);
	}
}
