/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.couponservices.strategies.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.couponservices.dao.CouponDao;
import de.hybris.platform.couponservices.model.CustomerCouponForPromotionSourceRuleModel;
import de.hybris.platform.promotionengineservices.model.PromotionSourceRuleModel;
import de.hybris.platform.promotionengineservices.model.RuleBasedPromotionModel;
import de.hybris.platform.ruleengineservices.compiler.RuleCompilerContext;
import de.hybris.platform.ruleengineservices.rule.data.RuleConditionData;
import de.hybris.platform.ruleengineservices.rule.data.RuleParameterData;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


/**
 * Unit test for {@link DefaultCouponConditionResolutionStrategy}
 */
@UnitTest
public class DefaultCouponConditionResolutionStrategyTest
{

	private static final String PARAM_KEY = "coupons";
	private static final String COUPON_CODE = "test";
	private static final String MODULE_NAME = "module";

	private DefaultCouponConditionResolutionStrategy strategy;

	@Mock
	private ModelService modelService;
	@Mock
	private CouponDao couponDao;

	@Mock
	private RuleConditionData condition;
	@Mock
	private PromotionSourceRuleModel rule;
	@Mock
	private RuleBasedPromotionModel promotion;
	@Mock
	private RuleParameterData parameter;
	@Mock
	private RuleCompilerContext context;
	@Mock
	private Map<String, RuleParameterData> parameters;

	private List<String> couponCodes;
	private CustomerCouponForPromotionSourceRuleModel cusCouponForRule;
	private List<CustomerCouponForPromotionSourceRuleModel> cusCouponForRules;


	@Before
	public void prepare()
	{
		MockitoAnnotations.initMocks(this);

		strategy = new DefaultCouponConditionResolutionStrategy();
		strategy.setModelService(modelService);
		strategy.setCouponDao(couponDao);

		couponCodes = Collections.singletonList(COUPON_CODE);
		cusCouponForRule = new CustomerCouponForPromotionSourceRuleModel();
		cusCouponForRules = Collections.emptyList();

		Mockito.lenient().when(condition.getParameters()).thenReturn(parameters);
		Mockito.lenient().when(parameters.get(PARAM_KEY)).thenReturn(parameter);
		Mockito.lenient().when(parameter.getValue()).thenReturn(couponCodes);
		Mockito.lenient().when(modelService.create(CustomerCouponForPromotionSourceRuleModel.class)).thenReturn(cusCouponForRule);
		Mockito.doNothing().when(modelService).save(cusCouponForRule);

		Mockito.lenient().when(context.getRule()).thenReturn(rule);
		Mockito.lenient().when(context.getModuleName()).thenReturn(MODULE_NAME);
		Mockito.lenient().when(couponDao.findAllCouponForSourceRules(rule, MODULE_NAME)).thenReturn(cusCouponForRules);
		Mockito.doNothing().when(modelService).removeAll(cusCouponForRules);
	}

	@Test
	public void testGetAndStoreParameterValues()
	{
		strategy.getAndStoreParameterValues(condition, rule, promotion);
		Mockito.verify(modelService, Mockito.times(couponCodes.size())).save(cusCouponForRule);
	}

	@Test
	public void testCleanStoredParameterValues()
	{
		strategy.cleanStoredParameterValues(context);
		Mockito.verify(modelService, Mockito.times(1)).removeAll(cusCouponForRules);
	}

	@Test
	public void testGetAndStoreParameterValues_couponRuleParamsNull()
	{
		Mockito.lenient().when(condition.getParameters().get("coupons")).thenReturn(null);
		strategy.getAndStoreParameterValues(condition, rule, promotion);
		Mockito.verify(modelService, Mockito.times(0)).save(cusCouponForRule);
	}

	@Test
	public void testGetAndStoreParameterValues_couponCodesEmpty()
	{
		Mockito.lenient().when(parameter.getValue()).thenReturn(Collections.emptyList());
		strategy.getAndStoreParameterValues(condition, rule, promotion);
		Mockito.verify(modelService, Mockito.times(0)).save(cusCouponForRule);

	}
}
