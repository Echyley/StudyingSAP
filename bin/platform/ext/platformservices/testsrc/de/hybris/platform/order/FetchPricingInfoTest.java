/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.DemoTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.price.DiscountModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.order.CartEntry;
import de.hybris.platform.jalo.order.price.PriceFactory;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.order.impl.DefaultCalculationService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.util.DiscountValue;
import de.hybris.platform.util.PriceValue;
import de.hybris.platform.util.TaxValue;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoSession;


/**
 * This test focuses on checking correct usage of {@link DefaultCalculationService}'s strategies that fetch tax,
 * discount and price data.
 */
@DemoTest
public class FetchPricingInfoTest extends ServicelayerTransactionalTest
{

	private static final Logger LOG = Logger.getLogger(FetchPricingInfoTest.class);

	@Resource
	private CalculationService calculationService;

	@Resource
	private ModelService modelService;

	@Resource
	private SessionService sessionService;

	MockitoSession mockito;

	@Mock
	private PriceFactory mockPriceFactory;

	@Resource
	private CartService cartService;
	
	@Resource
	private ProductService productService;

	private PriceFactory originalPriceFactory;

	private CartModel cart;
	private CartEntryModel cartEntry0;
	private CartEntryModel cartEntry1;

	private TaxValue entryTax0;
	private TaxValue entryTax1;

	private DiscountValue entryDiscount0;
	private DiscountValue entryDiscount1;

	private DiscountModel globalDiscount;


	@Before
	public void setUp() throws Exception
	{
		mockito = Mockito.mockitoSession().initMocks(this).startMocking();

		ServicelayerTest.createCoreData();
		ServicelayerTest.createDefaultCatalog();


		originalPriceFactory = sessionService.getAttribute(SessionContext.PRICEFACTORY);
		LOG.info(" -- Current price factory : " + originalPriceFactory + " -- ");

		cart = cartService.getSessionCart();
		cart.setNet(Boolean.TRUE);

		final ProductModel product0 = productService.getProductForCode("testProduct0");
		final ProductModel product1 = productService.getProductForCode("testProduct1");

		cartEntry0 = cartService.addNewEntry(cart, product0, 1, null);
		cartEntry1 = cartService.addNewEntry(cart, product1, 2, null);

		modelService.saveAll(cart, cartEntry0, cartEntry1);
		modelService.refresh(cart);
		modelService.refresh(cartEntry0);
		modelService.refresh(cartEntry1);

		final CartEntry cartEntryItem0 = modelService.getSource(cartEntry0);
		final CartEntry cartEntryItem1 = modelService.getSource(cartEntry1);

		entryTax0 = new TaxValue("10% tax", 10, false, null);
		entryTax1 = new TaxValue("5 tax", 5, true, "EUR");


		entryDiscount0 = new DiscountValue("-20 absolute", 20, true, "EUR");
		entryDiscount1 = new DiscountValue("-10% ", 10, false, null);

		final PriceValue entryPrice0 = new PriceValue("EUR", 100, true);
		final PriceValue entryPrice1 = new PriceValue("EUR", 50, true);

		//global discount:
		globalDiscount = modelService.create(DiscountModel.class);
		globalDiscount.setCode("test global discount");
		globalDiscount.setValue(30.0);
		globalDiscount.setGlobal(Boolean.TRUE);
		globalDiscount.setPriority(1);
		modelService.save(globalDiscount);

		when(mockPriceFactory.getTaxValues(cartEntryItem0)).thenReturn(Collections.singletonList(entryTax0));
		when(mockPriceFactory.getTaxValues(cartEntryItem1)).thenReturn(Collections.singletonList(entryTax1));
		when(mockPriceFactory.getBasePrice(cartEntryItem0)).thenReturn(entryPrice0);
		when(mockPriceFactory.getBasePrice(cartEntryItem1)).thenReturn(entryPrice1);
		when(mockPriceFactory.getDiscountValues(cartEntryItem0)).thenReturn(Collections.singletonList(entryDiscount0));
		when(mockPriceFactory.getDiscountValues(cartEntryItem1)).thenReturn(Collections.singletonList(entryDiscount1));

		sessionService.setAttribute(SessionContext.PRICEFACTORY, mockPriceFactory);
	}


	@After
	public void tearDown()
	{
		mockito.finishMocking();
		sessionService.setAttribute(SessionContext.PRICEFACTORY, originalPriceFactory);
	}


	@Test
	public void testCalculateAbstractOrderModel() throws CalculationException
	{
		assertThat(cartEntry0.getDiscountValues()).isNullOrEmpty();
		assertThat(cartEntry1.getDiscountValues()).isNullOrEmpty();
		assertThat(cart.getGlobalDiscountValues()).isNullOrEmpty();

		assertThat(cartEntry0.getTaxValues()).isNullOrEmpty();
		assertThat(cartEntry1.getTaxValues()).isNullOrEmpty();
		assertThat(cart.getTotalTaxValues()).isNullOrEmpty();

		assertOrderCalculated(cart, false);

		calculationService.calculate(cart);
		assertOrderEntryContainsAppliedDiscountVersion(cartEntry0, entryDiscount0);
		assertOrderEntryContainsAppliedTaxVersion(cartEntry0, entryTax0);
		assertEquals(100d, cartEntry0.getBasePrice(), 0.001);
		assertEquals(80d, cartEntry0.getTotalPrice(), 0.001);
		assertTrue(cartEntry0.getCalculated());

		calculationService.calculate(cart);
		assertOrderEntryContainsAppliedDiscountVersion(cartEntry1, entryDiscount1);
		assertOrderEntryContainsAppliedTaxVersion(cartEntry1, entryTax1);
		assertEquals(50d, cartEntry1.getBasePrice(), 0.001);
		assertEquals(90d, cartEntry1.getTotalPrice(), 0.001);
		assertTrue(cartEntry1.getCalculated());

		cart.setDiscounts(Collections.singletonList(globalDiscount));
		//save it so the calculated flag is changed
		cart = cartService.saveOrder(cart);
		calculationService.calculate(cart);

		assertOrderCalculated(cart, true);
		//price calculation
		//170-(30% * 170) = 170 - 51 = 119
		assertEquals(119, cart.getTotalPrice(), 0.001);
		assertEquals(51, cart.getTotalDiscounts(), 0.001);
	}

	private void assertOrderCalculated(final AbstractOrderModel order, final boolean result)
	{
		Assert.assertEquals("order should " + (result ? "" : " not ") + "be calculated", result,
				order.getCalculated());
		final List<AbstractOrderEntryModel> entries = order.getEntries();
		if (entries != null)
		{
			for (final AbstractOrderEntryModel entry : entries)
			{
				Assert.assertEquals(
						"order entry (" + entry.getEntryNumber() + ") should " + (result ? "" : " not ") + "be calculated",
						result, entry.getCalculated());
			}
		}
	}

	private void assertOrderEntryContainsAppliedDiscountVersion(final AbstractOrderEntryModel entry,
	                                                            final DiscountValue discountValue)
	{
		final List<DiscountValue> discountValues = entry.getDiscountValues();
		assertThat(discountValues).isNotEmpty();
		for (final DiscountValue appliedDiscountValue : discountValues)
		{
			if (appliedDiscountValue.getCode().equals(discountValue.getCode())
					&& appliedDiscountValue.getValue() == discountValue.getValue())
			{
				return;
			}
		}
		Assert.fail("order entry " + entry + " doesn't have the discount value " + discountValue);
	}


	private void assertOrderEntryContainsAppliedTaxVersion(final AbstractOrderEntryModel entry, final TaxValue taxValue)
	{
		final Collection<TaxValue> taxValues = entry.getTaxValues();
		assertThat(taxValues).isNotEmpty();
		for (final TaxValue appliedTaxValue : taxValues)
		{
			if (appliedTaxValue.getCode().equals(taxValue.getCode()) && appliedTaxValue.getValue() == taxValue.getValue())
			{
				return;
			}
		}
		Assert.fail("order entry " + entry + " doesn't not have the tax value " + taxValue);
	}

}
