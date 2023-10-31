/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.sapppspricing.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.sap.retail.sapppspricing.SapPPSPricingRuntimeException;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.promotions.model.OrderThresholdDiscountPromotionModel;
import de.hybris.platform.promotions.model.PromotionOrderEntryConsumedModel;
import de.hybris.platform.promotions.model.PromotionResultModel;
import de.hybris.platform.servicelayer.internal.model.impl.DefaultModelService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.util.DiscountValue;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.sap.retail.sapppspricing.swagger.businessobject.dto.DiscountBase;
import com.sap.retail.sapppspricing.swagger.businessobject.dto.LineItemDomainSpecific;
import com.sap.retail.sapppspricing.swagger.businessobject.dto.PriceCalculateBase;
import com.sap.retail.sapppspricing.swagger.businessobject.dto.PriceCalculateResponse;
import com.sap.retail.sapppspricing.swagger.businessobject.dto.PriceDerivationRuleBase;
import com.sap.retail.sapppspricing.swagger.businessobject.dto.QuantityCommonData;
import com.sap.retail.sapppspricing.swagger.businessobject.dto.RetailPriceModifierDomainSpecific;
import com.sap.retail.sapppspricing.swagger.businessobject.dto.Amount;
import com.sap.retail.sapppspricing.swagger.businessobject.dto.SaleBase;
import com.sap.retail.sapppspricing.swagger.businessobject.dto.ShoppingBasketBase;
import com.sap.retail.sapppspricing.swagger.businessobject.dto.UnitPriceCommonData;
import com.sap.retail.sapppspricing.swagger.businessobject.dto.PriceDerivationRuleBase.TransactionControlBreakCodeEnum;
import com.sap.retail.sapppspricing.impl.RequestHelperImpl;
import com.sap.retail.sapppspricing.RequestHelper;
import com.sap.retail.sapppspricing.impl.DefaultPriceCalculateToOrderMapper;
import com.sap.retail.sapppspricing.impl.PPSAccessorHelper;

@UnitTest
public class DefaultPriceCalculateToOrderMapperTest {

	public class PPSAccessorHelperForTest extends PPSAccessorHelper {

		@Override
		public RequestHelper getHelper() {
			return new RequestHelperImpl();
		}
	}

	private DefaultPriceCalculateToOrderMapper cut;
	private static final String DEFAULT_PREFIX = "DEF";
	private List<LineItemDomainSpecific> lineItems;
	private LineItemDomainSpecific lineItem;
	private DiscountBase discount;
	private Amount amount;
	private PriceDerivationRuleBase priceDerivRule;
	private CurrencyModel currency;
	private RetailPriceModifierDomainSpecific priceModifier;

	@Before
	public void setUp() {
		priceModifier = new RetailPriceModifierDomainSpecific();
		lineItems = new ArrayList<>();
		lineItem = new LineItemDomainSpecific();
		discount = new DiscountBase();
		amount = new Amount();
		priceDerivRule = new PriceDerivationRuleBase();
		currency = new CurrencyModel();
		cut = new DefaultPriceCalculateToOrderMapper();
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testIsItemDiscountFalse1() {
		assertFalse(cut.isItemDiscount(priceModifier));
	}

	@Test
	public void testIsItemDiscountFalse2() {
		priceModifier.addItemLinkItem(1);
		assertFalse(cut.isItemDiscount(priceModifier));
	}

	@Test
	public void testIsItemDiscountFalse3() {
		priceModifier.addItemLinkItem(1);
		amount.setValue(BigDecimal.ZERO);
		priceModifier.setAmount(amount);
		assertFalse(cut.isItemDiscount(priceModifier));
	}

	@Test
	public void testIsItemDiscountFalse4() {
		priceModifier.addItemLinkItem(1);
		amount.setValue(BigDecimal.TEN);
		priceModifier.setAmount(amount);
		assertFalse(cut.isItemDiscount(priceModifier));
	}

	@Test
	public void testIsItemDiscountFalse5() {
		amount.setValue(BigDecimal.ZERO);
		priceModifier.setAmount(amount);
		assertFalse(cut.isItemDiscount(priceModifier));
	}

	@Test
	public void testIsItemDiscountFalse6() {
		priceModifier.addItemLinkItem(1);
		amount.setValue(BigDecimal.ONE);
		priceModifier.setAmount(amount);
		assertFalse(cut.isItemDiscount(priceModifier));
	}

	@Test
	public void testIsDistibutedItemDiscountFalse() {
		priceModifier.addItemLinkItem(1);
		amount.setValue(BigDecimal.ZERO);
		priceModifier.setAmount(amount);
		assertFalse(cut.isDistributedItemDiscount(priceModifier));
	}

	@Test
	public void testIsDistibutedItemDiscountFalse2() {
		priceModifier.addItemLinkItem(1);
		assertFalse(cut.isDistributedItemDiscount(priceModifier));
	}

	@Test
	public void testIsDistibutedItemDiscountFalse3() {
		priceModifier.addItemLinkItem(1);
		priceModifier.setAmount(amount);
		assertFalse(cut.isDistributedItemDiscount(priceModifier));
	}

	@Test
	public void testIsDistibutedItemDiscountFalse4() {
		amount.setValue(BigDecimal.ONE);
		priceModifier.setAmount(amount);
		assertFalse(cut.isDistributedItemDiscount(priceModifier));
	}

	@Test
	public void testIsDistibutedItemDiscount() {
		priceModifier.addItemLinkItem(1);
		amount.setValue(BigDecimal.ONE);
		priceModifier.setAmount(amount);
		assertTrue(cut.isDistributedItemDiscount(priceModifier));
	}

	@Test
	public void testGetOrder() {
		assertEquals(0, cut.getOrder());
	}

	@Test
	public void testCodeForDiscountOneItemDiscount() {
		amount.setValue(BigDecimal.ONE);
		fillPriceDerivationRule(TransactionControlBreakCodeEnum.PO);
		fillRetailPriceModifier();
		assertEquals("ABC", cut.codeForDiscount(null, priceModifier, DEFAULT_PREFIX));

	}

	@Test
	public void testCodeForDiscountOneItemDiscountNoTypeCode() {
		priceDerivRule.setTransactionControlBreakCode(TransactionControlBreakCodeEnum.PO);
		priceDerivRule.setPromotionPriceDerivationRuleTypeCode("");
		priceModifier.getPriceDerivationRule().add(priceDerivRule);
		assertEquals("DEF" + "_" + null, cut.codeForDiscount(null, priceModifier, DEFAULT_PREFIX));
	}

	@Test
	public void testCodeForDiscountPriceRuleIsEmpty() {
		assertEquals("DEF" + "_" + null, cut.codeForDiscount(null, priceModifier, DEFAULT_PREFIX));
	}

	@Test
	public void testConvertToEntryDiscountsNoItemDiscount() {
		final AbstractOrderModel order = new AbstractOrderModel();
		order.setCurrency(currency);
		final SaleBase item = new SaleBase();
		item.addRetailPriceModifierItem(priceModifier);
		final List<DiscountValue> discountValues = cut.convertToEntryDiscounts(null, item, order);
		assertTrue(discountValues.isEmpty());
	}

	@Test
	public void testConvertToEntryDiscountsOneItemDiscount() {
		final AbstractOrderModel order = new AbstractOrderModel();
		final SaleBase item = new SaleBase();
		amount.setValue(BigDecimal.ONE);
		fillPriceDerivationRule(TransactionControlBreakCodeEnum.PO);
		fillRetailPriceModifier();
		item.addRetailPriceModifierItem(priceModifier);
		final QuantityCommonData quantity = new QuantityCommonData();
		item.addQuantityItem(quantity);
		item.getQuantity().get(0).setValue(BigDecimal.ONE);
		setOrderCurrency(order);
		final List<DiscountValue> discountValues = cut.convertToEntryDiscounts(null, item, order);
		assertEquals(1, discountValues.size());
		assertEquals("ABC", discountValues.get(0).getCode());
		assertEquals("USD", discountValues.get(0).getCurrencyIsoCode());
		final Object doubleValue = 1.0;
		assertEquals(doubleValue, discountValues.get(0).getValue());

	}

	@Test
	public void testConvertToEntryDiscountsOneItemDiscountAndOneHeaderDiscount() {
		final List<LineItemDomainSpecific> lineItemList = new LinkedList<LineItemDomainSpecific>();
		final LineItemDomainSpecific lineItemDiscount = new LineItemDomainSpecific();
		final DiscountBase discountItem = new DiscountBase();
		discountItem.setSequenceNumber(1);
		final PriceDerivationRuleBase priceDerivRuleDiscount = new PriceDerivationRuleBase();
		priceDerivRuleDiscount.setPromotionPriceDerivationRuleTypeCode("HeaderTypeCode");
		discountItem.getPriceDerivationRule().add(priceDerivRuleDiscount);
		lineItemDiscount.setDiscount(discountItem);
		lineItemList.add(lineItemDiscount);
		final LineItemDomainSpecific lineItemSale = new LineItemDomainSpecific();
		final SaleBase saleItem = new SaleBase();
		amount.setValue(BigDecimal.ONE);
		final RetailPriceModifierDomainSpecific priceModifierItemDiscount = new RetailPriceModifierDomainSpecific();
		fillPriceDerivationRule(TransactionControlBreakCodeEnum.PO);
		priceModifierItemDiscount.getPriceDerivationRule().add(priceDerivRule);
		priceModifierItemDiscount.setAmount(amount);
		saleItem.addRetailPriceModifierItem(priceModifierItemDiscount);
		final RetailPriceModifierDomainSpecific priceModifierDistributedItemDiscount = new RetailPriceModifierDomainSpecific();
		priceModifierDistributedItemDiscount.setAmount(amount);
		priceModifierDistributedItemDiscount.addItemLinkItem(1);
		saleItem.addRetailPriceModifierItem(priceModifierDistributedItemDiscount);
		final QuantityCommonData quantity = new QuantityCommonData();
		saleItem.addQuantityItem(quantity);
		saleItem.getQuantity().get(0).setValue(BigDecimal.ONE);
		lineItemSale.setSale(saleItem);
		lineItemList.add(lineItemSale);
		final AbstractOrderModel order = new AbstractOrderModel();
		setOrderCurrency(order);
		final List<DiscountValue> discountValues = cut.convertToEntryDiscounts(lineItemList, saleItem, order);
		final Object doubleValue = 1.0;
		assertEquals(2, discountValues.size());
		assertEquals("ABC", discountValues.get(0).getCode());
		assertEquals("USD", discountValues.get(0).getCurrencyIsoCode());
		assertEquals(doubleValue, discountValues.get(0).getValue());
		assertEquals("HeaderTypeCode", discountValues.get(1).getCode());
		assertEquals("USD", discountValues.get(1).getCurrencyIsoCode());
		assertEquals(doubleValue, discountValues.get(1).getValue());

	}

	@Test
	public void testConvertToEntryDiscountsOneItemDiscountAndTwoHeaderDiscount() {
		final List<LineItemDomainSpecific> lineItemList = new LinkedList<LineItemDomainSpecific>();
		final PriceDerivationRuleBase priceDerivRuleDiscount1 = new PriceDerivationRuleBase();
		priceDerivRuleDiscount1.setPromotionPriceDerivationRuleTypeCode("HeaderTypeCode1");
		final PriceDerivationRuleBase priceDerivRuleDiscount2 = new PriceDerivationRuleBase();
		priceDerivRuleDiscount2.setPromotionPriceDerivationRuleTypeCode("HeaderTypeCode2");
		final LineItemDomainSpecific lineItemDiscount1 = new LineItemDomainSpecific();
		final DiscountBase discountItem1 = new DiscountBase();
		discountItem1.setSequenceNumber(1);
		discountItem1.getPriceDerivationRule().add(priceDerivRuleDiscount1);
		lineItemDiscount1.setDiscount(discountItem1);
		final LineItemDomainSpecific lineItemDiscount2 = new LineItemDomainSpecific();
		final DiscountBase discountItem2 = new DiscountBase();
		discountItem2.setSequenceNumber(10);
		discountItem2.getPriceDerivationRule().add(priceDerivRuleDiscount2);
		lineItemDiscount2.setDiscount(discountItem2);
		lineItemList.add(lineItemDiscount1);
		lineItemList.add(lineItemDiscount2);
		final LineItemDomainSpecific lineItemSale = new LineItemDomainSpecific();
		final SaleBase saleItem = new SaleBase();
		amount.setValue(BigDecimal.ONE);
		final QuantityCommonData quantity = new QuantityCommonData();
		saleItem.addQuantityItem(quantity);
		saleItem.getQuantity().get(0).setValue(BigDecimal.ONE);
		final RetailPriceModifierDomainSpecific priceModifierItemDiscount = new RetailPriceModifierDomainSpecific();
		fillPriceDerivationRule(TransactionControlBreakCodeEnum.PO);
		priceModifierItemDiscount.getPriceDerivationRule().add(priceDerivRule);
		priceModifierItemDiscount.setAmount(amount);
		saleItem.addRetailPriceModifierItem(priceModifierItemDiscount);
		final RetailPriceModifierDomainSpecific priceModifierDistributedItemDiscount1 = new RetailPriceModifierDomainSpecific();
		priceModifierDistributedItemDiscount1.setAmount(amount);
		priceModifierDistributedItemDiscount1.addItemLinkItem(10);
		saleItem.addRetailPriceModifierItem(priceModifierDistributedItemDiscount1);
		final RetailPriceModifierDomainSpecific priceModifierDistributedItemDiscount2 = new RetailPriceModifierDomainSpecific();
		priceModifierDistributedItemDiscount2.setAmount(amount);
		priceModifierDistributedItemDiscount2.addItemLinkItem(1);
		saleItem.addRetailPriceModifierItem(priceModifierDistributedItemDiscount2);
		lineItemSale.setSale(saleItem);
		lineItemList.add(lineItemSale);
		final AbstractOrderModel order = new AbstractOrderModel();
		setOrderCurrency(order);
		final List<DiscountValue> discountValues = cut.convertToEntryDiscounts(lineItemList, saleItem, order);
		final Object doubleValue = 1.0;
		assertEquals(3, discountValues.size());
		assertEquals("ABC", discountValues.get(0).getCode());
		assertEquals("USD", discountValues.get(0).getCurrencyIsoCode());
		assertEquals(doubleValue, discountValues.get(0).getValue());
		assertEquals("HeaderTypeCode2", discountValues.get(1).getCode());
		assertEquals("USD", discountValues.get(1).getCurrencyIsoCode());
		assertEquals(doubleValue, discountValues.get(1).getValue());
		assertEquals("HeaderTypeCode1", discountValues.get(2).getCode());
		assertEquals("USD", discountValues.get(2).getCurrencyIsoCode());
		assertEquals(doubleValue, discountValues.get(2).getValue());
	}

	private void setOrderCurrency(final AbstractOrderModel order) {
		currency.setIsocode("USD");
		order.setCurrency(currency);
	}

	@Test(expected = RuntimeException.class)
	public void testMapResponseToCartEntriesNoSaleItemsInResponse() {
		final AbstractOrderModel order = new AbstractOrderModel();
		final AbstractOrderEntryModel orderEntry = new AbstractOrderEntryModel();
		final List<AbstractOrderEntryModel> orderEntries = new ArrayList<>();
		ArrayList<LineItemDomainSpecific> lineStubList = new ArrayList<LineItemDomainSpecific>();
		LineItemDomainSpecific lineStub = new LineItemDomainSpecific();
		orderEntries.add(orderEntry);
		order.setEntries(orderEntries);
		fillPriceDerivationRule(TransactionControlBreakCodeEnum.SU);
		amount.setValue(BigDecimal.ONE);
		discount.setAmount(amount);
		discount.getPriceDerivationRule().add(priceDerivRule);
		lineStub.setDiscount(discount);
		lineStubList.add(lineStub);
		cut.mapResponseToCartEntries(lineStubList, order);
	}

	@Test
	public void testMapResponseToCartEntries() {
		final AbstractOrderEntryModel orderEntry = Mockito.spy(new OrderEntryModel());
		ModelService model = Mockito.mock(ModelService.class);
		Mockito.when(model.create(PromotionResultModel.class)).thenReturn(new PromotionResultModel());
		Mockito.when(model.create(OrderThresholdDiscountPromotionModel.class))
				.thenReturn(new OrderThresholdDiscountPromotionModel());
		Mockito.when(model.create(PromotionOrderEntryConsumedModel.class))
		.thenReturn(new PromotionOrderEntryConsumedModel());
		final ArgRecorderAnswer<Void> answer = new ArgRecorderAnswer<Void>();
		Mockito.doAnswer(answer).when(orderEntry).setDiscountValues(Mockito.anyListOf(DiscountValue.class));
		ArrayList<LineItemDomainSpecific> lineStubList = new ArrayList<LineItemDomainSpecific>();
		LineItemDomainSpecific lineStub = new LineItemDomainSpecific();
		final AbstractOrderModel order = new AbstractOrderModel();
		final List<AbstractOrderEntryModel> orderEntries = new ArrayList<>();
		final ProductModel productModel = new ProductModel();
		amount.setValue(BigDecimal.ONE);
		cut.setModelService(model);
		orderEntry.setProduct(productModel);
		orderEntries.add(orderEntry);
		order.setEntries(orderEntries);
		setOrderCurrency(order);
		fillPriceDerivationRule(TransactionControlBreakCodeEnum.PO);
		fillRetailPriceModifier();
		final SaleBase sale = new SaleBase();
		final UnitPriceCommonData price = new UnitPriceCommonData();
		price.setValue(BigDecimal.TEN);
		sale.setRegularSalesUnitPrice(price);
		final QuantityCommonData quantity = new QuantityCommonData();
		quantity.setValue(BigDecimal.TEN);
		sale.addQuantityItem(quantity);
		sale.addRetailPriceModifierItem(priceModifier);
		lineStub.setSale(sale);
		lineStubList.add(lineStub);
		cut.setAccessorHelper(new PPSAccessorHelperForTest());
		cut.mapResponseToCartEntries(lineStubList, order);
		try {
			cut.mapResponseToCartEntries(lineItems, order);
			final List<DiscountValue> arg = answer.getArg(0);
			assertEquals(1, arg.size());
			final Object doubleValue = 0.1;
			assertEquals(doubleValue, arg.get(0).getValue());
			assertEquals("ABC", arg.get(0).getCode());
		} catch (SapPPSPricingRuntimeException e) {
			// TODO: handle exception
			assertTrue("Exception expected", e != null);
		}
	}

	@Test
	public void testMapCallsSupMethods() {
		final PriceCalculateResponse response = new PriceCalculateResponse();
		final PriceCalculateBase priceCalculateBase = new PriceCalculateBase();
		final ShoppingBasketBase shoppingBasketBase = new ShoppingBasketBase();
		final List<LineItemDomainSpecific> lineItems = shoppingBasketBase.getLineItem();
		lineItems.add(lineItem);
		priceCalculateBase.setShoppingBasket(shoppingBasketBase);
		response.addPriceCalculateBodyItem(priceCalculateBase);
		final AbstractOrderModel order = null;
		final DefaultPriceCalculateToOrderMapper cutSpy = Mockito.spy(cut);
		Mockito.doNothing().when(cutSpy).mapResponseToCartEntries(lineItems, order);
		cutSpy.map(response, order);
		Mockito.verify(cutSpy).mapResponseToCartEntries(lineItems, order);
	}

	private void fillRetailPriceModifier() {
		priceModifier.getPriceDerivationRule().add(priceDerivRule);
		priceModifier.setAmount(amount);
	}

	private void fillPriceDerivationRule(final TransactionControlBreakCodeEnum ruleControlCode) {
		priceDerivRule.setTransactionControlBreakCode(ruleControlCode);
		priceDerivRule.setPromotionPriceDerivationRuleTypeCode("ABC");
	}
}
