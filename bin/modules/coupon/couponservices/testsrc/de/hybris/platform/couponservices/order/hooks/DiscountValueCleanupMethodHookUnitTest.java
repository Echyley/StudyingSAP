/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.couponservices.order.hooks;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CalculationService;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.promotionengineservices.util.ActionUtils;
import de.hybris.platform.promotions.model.AbstractPromotionActionModel;
import de.hybris.platform.promotions.model.PromotionResultModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.util.DiscountValue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.*;


/**
 * JUnit test suite for implementation {@link DiscountValueCleanupMethodHook}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DiscountValueCleanupMethodHookUnitTest {
    private final AbstractPromotionActionModel action = new AbstractPromotionActionModel();
    private final Set<AbstractPromotionActionModel> allPromotionActions = new HashSet<>();
    private final PromotionResultModel promotionResult = new PromotionResultModel();
    private final Set<PromotionResultModel> allPromotionResults = new HashSet<>();

    private DiscountValue validPromotionDiscountValue;
    private DiscountValue nonPromotionDiscountValue;
    private DiscountValue fakePromotionDiscountValue;

    @InjectMocks
    private DiscountValueCleanupMethodHook discountValueCleanupMethodHook;

    @InjectMocks
    private final ActionUtils actionUtils = new ActionUtils();

    @Mock
    private CommerceCheckoutParameter parameter;

    @Mock
    private CartModel cartModel;

    @Mock
    private AbstractOrderEntryModel entry1;

    @Mock
    private AbstractOrderEntryModel entry2;

    @Mock
    private ModelService modelService;

    @Mock
    private CalculationService calculationService;

    @Before
    public void setup() {
        discountValueCleanupMethodHook = new DiscountValueCleanupMethodHook(modelService, actionUtils, calculationService);

        final String actionUUID = actionUtils.createActionUUID();
        action.setGuid(actionUUID);
        allPromotionActions.add(action);
        promotionResult.setAllPromotionActions(allPromotionActions);
        allPromotionResults.add(promotionResult);

        validPromotionDiscountValue = new DiscountValue(actionUUID, 10.0d, false, 0.0d, null, false);
        nonPromotionDiscountValue = new DiscountValue("non-promotion-discount-value", 20.0d, false, 0.0d, null, false);
        fakePromotionDiscountValue = new DiscountValue(actionUtils.createActionUUID(), 10.0d, false, 0.0d, null, false);

        Mockito.when(parameter.getCart()).thenReturn(cartModel);
        Mockito.when(cartModel.getAllPromotionResults()).thenReturn(allPromotionResults);
        Mockito.when(cartModel.getEntries()).thenReturn(Arrays.asList(entry1, entry2));
    }

    @Test
    public void shouldPassBeforePlaceOrderCheckAndCleanInvalidDiscountValues() throws Exception {
        Mockito.when(cartModel.getGlobalDiscountValues()).thenReturn(Arrays.asList(validPromotionDiscountValue));
        Mockito.when(entry1.getDiscountValues()).thenReturn(Arrays.asList(validPromotionDiscountValue));
        Mockito.when(entry2.getDiscountValues()).thenReturn(Arrays.asList(validPromotionDiscountValue));

        discountValueCleanupMethodHook.beforePlaceOrder(parameter);

        verify(modelService, never()).save(any(CartModel.class));
        verify(modelService, never()).save(any(AbstractOrderEntryModel.class));
        verify(calculationService, never()).calculateTotals(any(CartModel.class), eq(true));
    }

    @Test(expected = InvalidCartException.class)
    public void shouldBeforePlaceOrderThrowExceptionForInvalidDiscountValuesOnCart() throws Exception {
        Mockito.when(cartModel.getGlobalDiscountValues())
                .thenReturn(Arrays.asList(validPromotionDiscountValue, nonPromotionDiscountValue, fakePromotionDiscountValue));
        Mockito.when(entry1.getDiscountValues()).thenReturn(Arrays.asList(validPromotionDiscountValue));
        Mockito.when(entry2.getDiscountValues()).thenReturn(Arrays.asList(validPromotionDiscountValue));

        discountValueCleanupMethodHook.beforePlaceOrder(parameter);
    }

    @Test(expected = InvalidCartException.class)
    public void shouldBeforePlaceOrderThrowExceptionForInvalidDiscountValuesOnEntry() throws Exception {
        Mockito.when(cartModel.getGlobalDiscountValues()).thenReturn(Arrays.asList(validPromotionDiscountValue));
        Mockito.when(entry1.getDiscountValues()).thenReturn(Arrays.asList(validPromotionDiscountValue, nonPromotionDiscountValue));
        Mockito.when(entry2.getDiscountValues()).thenReturn(Arrays.asList(nonPromotionDiscountValue, fakePromotionDiscountValue));

        discountValueCleanupMethodHook.beforePlaceOrder(parameter);
    }
}
