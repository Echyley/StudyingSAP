/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.oaa.commerce.services.common.util;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import com.sap.retail.oaa.commerce.services.common.util.impl.DefaultCommonUtils;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@UnitTest
public class DefaultCommonUtilsTest {

	@Mock
	private DefaultCommonUtils defaultCommonUtils;

	@Mock
	private AbstractOrderModel abstractOrderModel;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

	}

	@Test
	public void testCAREnabled() {
		given(defaultCommonUtils.isCAREnabled()).willReturn(Boolean.TRUE);
		Assert.assertTrue(defaultCommonUtils.isCAREnabled());

	}

	@Test
	public void testCOSEnabled() {
		given(defaultCommonUtils.isCOSEnabled()).willReturn(Boolean.TRUE);
		Assert.assertTrue(defaultCommonUtils.isCOSEnabled());

	}

	@Test
	public void testCAREnabledAbstractOrderModel() {
		given(defaultCommonUtils.isCAREnabled(abstractOrderModel)).willReturn(Boolean.TRUE);
		Assert.assertTrue(defaultCommonUtils.isCAREnabled(abstractOrderModel));

	}

	@Test
	public void testCOSEnabledAbstractOrderModel() {
		given(defaultCommonUtils.isCOSEnabled(abstractOrderModel)).willReturn(Boolean.TRUE);
		Assert.assertTrue(defaultCommonUtils.isCOSEnabled(abstractOrderModel));

	}

}