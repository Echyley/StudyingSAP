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
package de.hybris.platform.promotionengineservices.promotionengine.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.promotionengineservices.model.AbstractRuleBasedPromotionActionModel;
import de.hybris.platform.promotionengineservices.model.RuleBasedPromotionModel;
import de.hybris.platform.promotions.PromotionResultService;
import de.hybris.platform.promotions.model.AbstractPromotionActionModel;
import de.hybris.platform.promotions.model.PromotionResultModel;
import de.hybris.platform.ruleengine.dao.EngineRuleDao;
import de.hybris.platform.ruleengine.model.AbstractRuleEngineRuleModel;
import de.hybris.platform.ruleengine.versioning.ModuleVersioningService;
import de.hybris.platform.ruleengineservices.order.dao.ExtendedOrderDao;
import de.hybris.platform.ruleengineservices.rao.AbstractOrderRAO;
import de.hybris.platform.ruleengineservices.rao.AbstractRuleActionRAO;
import de.hybris.platform.ruleengineservices.rao.CartRAO;
import de.hybris.platform.ruleengineservices.rao.DiscountRAO;
import de.hybris.platform.ruleengineservices.rao.OrderEntryRAO;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static java.lang.Long.valueOf;
import static java.util.Optional.of;
import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;



@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultPromotionActionServiceTest
{

	private static final String MODULE_NAME = "MODULE_NAME";
	private static final String CART_CODE = "cartCode";
	private static final String RULE_CODE = "ruleCode";
	private static final String PROMOTION_FIRED_MESSAGE = "PROMOTION_FIRED_MESSAGE";

	@InjectMocks
	private DefaultPromotionActionService defaultPromotionActionService;

	@Mock
	private ExtendedOrderDao extendedOrderDao;
	@Mock
	private ModelService modelService;
	@Mock
	private EngineRuleDao engineRuleDao;
	@Mock
	private AbstractOrderModel cart;
	@Mock
	private CartRAO cartRAO;
	@Mock
	private AbstractRuleEngineRuleModel rule;
	@Mock
	private RuleBasedPromotionModel promotion;
	@Mock
	private DiscountRAO discountRAO;
	@Mock
	private AbstractRuleBasedPromotionActionModel action1;
	@Mock
	private AbstractRuleActionRAO actionAppliedToCart;
	@Mock
	private OrderEntryRAO orderEntryRaoWithInvalidOrder;
	@Mock
	private OrderEntryRAO orderEntryRaoWithInvalidOrder2;
	@Mock
	private AbstractOrderRAO order;
	@Mock
	private AbstractOrderRAO invalidOrder;
	//@Mock
	//private ProductRAO product;
	@Mock
	private PromotionResultModel promotionResult;
	@Mock
	private AbstractOrderEntryModel orderEntry;
	@Mock
	private ModuleVersioningService moduleVersioningService;
	@Mock
	private PromotionResultService promotionResultService;


	@Before
	public void setUp()
	{
		Mockito.lenient().when(extendedOrderDao.findOrderByCode(CART_CODE)).thenReturn(cart);
		Mockito.lenient().when(engineRuleDao.getActiveRuleByCodeAndMaxVersion(RULE_CODE, MODULE_NAME, 1)).thenReturn(rule);
		Mockito.lenient().when(discountRAO.getAppliedToObject()).thenReturn(cartRAO);
		Mockito.lenient().when(discountRAO.getFiredRuleCode()).thenReturn(RULE_CODE);
		Mockito.lenient().when(discountRAO.getModuleName()).thenReturn(MODULE_NAME);
		Mockito.lenient().when(cartRAO.getCode()).thenReturn(CART_CODE);
		Mockito.lenient().when(rule.getPromotion()).thenReturn(promotion);

		Mockito.lenient().when(modelService.create(PromotionResultModel.class)).thenReturn(promotionResult);
		Mockito.lenient().when(actionAppliedToCart.getAppliedToObject()).thenReturn(new CartRAO());

		Mockito.lenient().when(invalidOrder.getCode()).thenReturn("unknownCartCode");
		//when(product.getCode()).thenReturn("productCode");
		Mockito.lenient().when(orderEntryRaoWithInvalidOrder.getOrder()).thenReturn(invalidOrder);
		Mockito.lenient().when(orderEntryRaoWithInvalidOrder.getProductCode()).thenReturn("productCode");

		Mockito.lenient().when(order.getCode()).thenReturn(CART_CODE);
		//when(product.getCode()).thenReturn("productCode");
		Mockito.lenient().when(orderEntryRaoWithInvalidOrder2.getOrder()).thenReturn(order);
		Mockito.lenient().when(orderEntryRaoWithInvalidOrder2.getEntryNumber()).thenReturn(Integer.valueOf(1));
		Mockito.lenient().when(orderEntryRaoWithInvalidOrder2.getProductCode()).thenReturn("productCode");

		Mockito.lenient().when(moduleVersioningService.getDeployedModuleVersionForRule(RULE_CODE, MODULE_NAME)).thenReturn(Optional.of(valueOf(1)));
		Mockito.lenient().when(moduleVersioningService.getModuleVersion(rule)).thenReturn(Optional.of(valueOf(0)));
	}

	/**
	 * tests that a promotionResultModel gets created with the default values
	 */
	@Test
	public void testCreatePromotionResult()
	{
		Mockito.lenient().when(cart.getAllPromotionResults()).thenReturn(Collections.emptySet());

		final PromotionResultModel newPromotionResult = defaultPromotionActionService.createPromotionResult(discountRAO);

		verify(newPromotionResult).setCertainty(Float.valueOf(1.0F));
		verify(newPromotionResult).setPromotion(promotion);
		verify(newPromotionResult).setOrder(cart);
	}

	/**
	 * tests that if there is an existing promotionResultModel created by the same rule, the existing one is returned
	 */
	@Test
	public void testCreatePromotionResultForExistingPromotionResult()
	{
		final PK rulePk = PK.createUUIDPK(10);
		final Collection<AbstractPromotionActionModel> actions = Collections.singleton(action1);
		Mockito.lenient().when(rule.getPk()).thenReturn(rulePk);
		promotionResult.setActions(actions);
		final Set<PromotionResultModel> allPromotionResults = Collections.singleton(promotionResult);
		Mockito.lenient().when(cart.getAllPromotionResults()).thenReturn(allPromotionResults);
		Mockito.lenient().when(action1.getRule()).thenReturn(rule);

		final PromotionResultModel newPromotionResult = defaultPromotionActionService.createPromotionResult(discountRAO);

		verify(newPromotionResult).setCertainty(Float.valueOf(1.0F));
		verify(newPromotionResult).setPromotion(promotion);
		verify(newPromotionResult).setOrder(cart);
		assertEquals(newPromotionResult, promotionResult);
	}

	@Test
	public void testTryToGetOrderEntryFromImproperAction()
	{
		assertNull(defaultPromotionActionService.getOrderEntry(actionAppliedToCart));
		assertNull(defaultPromotionActionService.getOrderEntry(orderEntryRaoWithInvalidOrder));
		assertNull(defaultPromotionActionService.getOrderEntry(orderEntryRaoWithInvalidOrder2));
	}

	@Test
	public void testTryToGetOrderFromAction()
	{
		assertNotNull(defaultPromotionActionService.getOrder(discountRAO));
		assertNull(defaultPromotionActionService.getOrder(actionAppliedToCart));
	}

	@Test
	public void testCreatePromotionResultSetVersion()
	{
		final PromotionResultModel newPromotionResult = defaultPromotionActionService.createPromotionResult(discountRAO);
		assertThat(newPromotionResult.getRuleVersion()).isNotNull().isEqualTo(0);
	}

	@Test
	public void testCreatePromotionResultSetModuleVersion()
	{
		Mockito.lenient().when(moduleVersioningService.getModuleVersion(rule)).thenReturn(of(valueOf(1)));

		final PromotionResultModel newPromotionResult = defaultPromotionActionService.createPromotionResult(discountRAO);
		verify(newPromotionResult).setRuleVersion(valueOf(0l));
		verify(newPromotionResult).setModuleVersion(valueOf(1l));
	}

	@Test
	public void testCreatePromotionResultSetRulesModuleName()
	{
		Mockito.lenient().when(moduleVersioningService.getModuleVersion(rule)).thenReturn(of(valueOf(1)));

		final PromotionResultModel newPromotionResult = defaultPromotionActionService.createPromotionResult(discountRAO);

		verify(newPromotionResult).setRulesModuleName(MODULE_NAME);
	}

	@Test
	public void testCreatePromotionResultSetFiredMessage()
	{
		Mockito.lenient().when(moduleVersioningService.getModuleVersion(rule)).thenReturn(of(valueOf(1)));
		Mockito.lenient().when(promotionResultService.getDescription(any(PromotionResultModel.class))).thenReturn(PROMOTION_FIRED_MESSAGE);

		final PromotionResultModel newPromotionResult = defaultPromotionActionService.createPromotionResult(discountRAO);

		verify(newPromotionResult).setMessageFired(PROMOTION_FIRED_MESSAGE);
	}

	@Test
	public void shouldUpdateAndPersistPotentialPromotionFiredMessage() throws Exception
	{
		//given
		Mockito.lenient().when(promotionResultService.getDescription(promotionResult)).thenReturn(PROMOTION_FIRED_MESSAGE);
		//when
		defaultPromotionActionService.recalculateFiredPromotionMessage(promotionResult);
		//then
		verify(promotionResult).setMessageFired(PROMOTION_FIRED_MESSAGE);
		verify(modelService).save(promotionResult);

	}
}
