/*
 * [y] hybris Platform
 *
 * Copyright (c) 2021 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.couponservices.cart.hooks;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.couponservices.service.data.CouponResponse;
import de.hybris.platform.couponservices.services.CouponService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;


/**
 * JUnit test suite for implementation {@link MergeCouponCodeHook}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class MergeCouponCodeHookUnitTest
{

    @InjectMocks
    private MergeCouponCodeHook mergeCouponCodeHook;
    @Mock
    private CouponService couponService;

    private final CartModel fromCart = new CartModel();
    private final CartModel toCart = new CartModel();
    private final CouponResponse response = new CouponResponse();

    @Test
    public void testAfterCartMergeWithEmptyFromCart()
    {
        mergeCouponCodeHook.afterCartMerge(fromCart, toCart);
        verify(couponService, never()).redeemCoupon(Matchers.anyString(), Matchers.any(CartModel.class));
    }
    @Test
    public void testAfterCartMergeWithNotEmptyFromCart()
    {
        final List couponCodes = new ArrayList();
        couponCodes.add("testCode");
        fromCart.setAppliedCouponCodes(couponCodes);
        response.setSuccess(true);
        when(couponService.redeemCoupon(Matchers.anyString(), Matchers.any(CartModel.class))).thenReturn(response);
        mergeCouponCodeHook.afterCartMerge(fromCart, toCart);
        verify(couponService, only()).redeemCoupon(Matchers.anyString(), Matchers.any(CartModel.class));
    }
}
