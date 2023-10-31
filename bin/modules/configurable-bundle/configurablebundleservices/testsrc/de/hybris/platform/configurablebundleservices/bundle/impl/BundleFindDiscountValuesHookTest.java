/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.configurablebundleservices.bundle.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.configurablebundleservices.bundle.BundleRuleService;
import de.hybris.platform.configurablebundleservices.bundle.BundleTemplateService;
import de.hybris.platform.configurablebundleservices.model.ChangeProductPriceBundleRuleModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.order.EntryGroup;
import de.hybris.platform.util.DiscountValue;

import java.math.BigDecimal;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/**
 * JUnit test suite for {@link BundleFindDiscountValuesHookTest}
 */
@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class BundleFindDiscountValuesHookTest
{
	private BundleFindDiscountValuesHook bundleFindDiscountValuesHook;

	@Mock
	private BundleRuleService bundleRuleService;

	@Mock
	private BundleTemplateService bundleTemplateService;

	private CartModel childCart;
	private CartEntryModel childEntry;
	private CurrencyModel currency;
	private EntryGroup entryGroup;

	@Before
	public void setUp()
	{
		bundleFindDiscountValuesHook = new BundleFindDiscountValuesHook();
		bundleFindDiscountValuesHook.setBundleRuleService(bundleRuleService);
		bundleFindDiscountValuesHook.setBundleTemplateService(bundleTemplateService);

		childCart = mock(CartModel.class);
		childEntry = mock(CartEntryModel.class);
		currency = mock(CurrencyModel.class);
		entryGroup = mock(EntryGroup.class);
	}

	@Test
	public void testIsHookApplicableForNotBundle() {
		when(bundleTemplateService.getBundleEntryGroup(any()))
				.thenReturn(null);

		boolean actual = bundleFindDiscountValuesHook.isApplicable(childEntry);
		assertFalse(actual);
	}

	@Test
	public void testIsHookApplicableForBundle() {
		when(bundleTemplateService.getBundleEntryGroup(any()))
				.thenReturn(entryGroup);

		boolean actual = bundleFindDiscountValuesHook.isApplicable(childEntry);
		assertTrue(actual);
	}

	@Test
	public void testFindDiscountValues() {
		when(bundleTemplateService.getBundleEntryGroup(any())).thenReturn(entryGroup);
		when(childEntry.getOrder()).thenReturn(childCart);
		when(childCart.getCurrency()).thenReturn(currency);
		when(childEntry.getBasePrice()).thenReturn(10d);
		when(bundleRuleService.getChangePriceBundleRuleForOrderEntry(childEntry)).thenReturn(createChangeProductPriceBundleRuleModel());

		boolean actual = bundleFindDiscountValuesHook.isApplicable(childEntry);
		assertTrue(actual);

		List<DiscountValue> discountValues = bundleFindDiscountValuesHook.findDiscountValues(childEntry);
		assertEquals(1, discountValues.size());
	}

	private ChangeProductPriceBundleRuleModel createChangeProductPriceBundleRuleModel() {
		ChangeProductPriceBundleRuleModel ruleModel = new ChangeProductPriceBundleRuleModel();
		ruleModel.setId("test");
		ruleModel.setPrice(new BigDecimal(10));

		return ruleModel;
	}
}
