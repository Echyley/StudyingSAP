/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.couponservices.dao.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.couponservices.dao.CouponDao;
import de.hybris.platform.couponservices.model.CustomerCouponForPromotionSourceRuleModel;
import de.hybris.platform.promotionengineservices.model.PromotionSourceRuleModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.ruleengineservices.rule.dao.RuleDao;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;


import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * Integration test for {@link DefaultCouponDao}
 */
@IntegrationTest
public class DefaultCouponForProDaoTest extends ServicelayerTransactionalTest
{

	private static final String PROMOTION_SOURCE_RULE_CODE_1 = "rule1";
	private static final String PROMOTION_SOURCE_RULE_CODE_2 = "rule2";
	private static final String COUPON_CODE = "SUMMER69";
	private static final String MODEULE_NAME = "primary-kie-module";

	@Resource
	private RuleDao ruleDao;

	@Resource(name = "couponDao")
	private CouponDao couponDao;


	@Before
	public void prepare() throws ImpExException
	{
		importCsv("/couponservices/test/DefaultCouponForProServiceTest.impex", "UTF-8");
	}

	@Test
	public void testFindAllCusCouponForSourceRules()
	{
		final PromotionSourceRuleModel rule = ruleDao.findRuleByCode(PROMOTION_SOURCE_RULE_CODE_1);
		final List<CustomerCouponForPromotionSourceRuleModel> result = couponDao.findAllCouponForSourceRules(rule);

		Assert.assertFalse(result.isEmpty());
		Assert.assertEquals(1, result.size());
	}

	@Test
	public void testFindAllCusCouponForSourceRules2()
	{
		final PromotionSourceRuleModel rule = ruleDao.findRuleByCode(PROMOTION_SOURCE_RULE_CODE_2);
		final List<CustomerCouponForPromotionSourceRuleModel> result = couponDao.findAllCouponForSourceRules(rule,
				MODEULE_NAME);

		Assert.assertFalse(result.isEmpty());
		Assert.assertEquals(1, result.size());
	}

	@Test
	public void testFindPromotionSourceRuleByCouponCode()
	{
		final List<PromotionSourceRuleModel> promotionSourceRule = couponDao
				.findPromotionSourceRuleByCouponCode(COUPON_CODE);

		Assert.assertEquals(PROMOTION_SOURCE_RULE_CODE_1, promotionSourceRule.get(0).getCode());
	}


}
