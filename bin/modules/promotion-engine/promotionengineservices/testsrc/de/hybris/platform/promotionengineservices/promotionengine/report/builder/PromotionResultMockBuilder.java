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
package de.hybris.platform.promotionengineservices.promotionengine.report.builder;

import com.google.common.collect.Lists;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.promotionengineservices.constants.PromotionEngineServicesConstants.PromotionCertainty;
import de.hybris.platform.promotionengineservices.model.RuleBasedPromotionModel;
import de.hybris.platform.promotions.PromotionResultService;
import de.hybris.platform.promotions.model.PromotionOrderEntryConsumedModel;
import de.hybris.platform.promotions.model.PromotionResultModel;
import de.hybris.platform.ruleengine.model.AbstractRuleEngineRuleModel;
import de.hybris.platform.ruleengine.model.DroolsRuleModel;
import de.hybris.platform.ruleengineservices.model.AbstractRuleModel;
import de.hybris.platform.ruleengineservices.model.SourceRuleModel;

import org.mockito.Mockito;

import static org.mockito.Mockito.mock;


public class PromotionResultMockBuilder
{
	public static final String CODE = "code";
	public static final String NAME = "name";
	public static final String DESCRIPTION = "description";

	public PromotionResultModel createSamplePromotionResult(final PromotionResultService promotionResultServiceMock)
	{

		final PromotionResultModel promotionResultModel = mock(PromotionResultModel.class);
		final RuleBasedPromotionModel promotion = createRuleBasedPromotion(CODE, NAME);
		Mockito.lenient().when(promotionResultModel.getPromotion()).thenReturn(promotion);
		Mockito.lenient().when(promotionResultServiceMock.getDescription(promotionResultModel)).thenReturn(DESCRIPTION);
		return promotionResultModel;
	}

	public PromotionResultModel createSamplePotentialPromotionResult(final PromotionResultService promotionResultServiceMock)
	{
		final PromotionResultModel promotionResultModel =createSamplePromotionResult(promotionResultServiceMock);
		Mockito.lenient().when(promotionResultModel.getCertainty()).thenReturn(PromotionCertainty.POTENTIAL.value());
		return promotionResultModel;
	}

	public PromotionResultModel createSampleFiredPromotionResult(final PromotionResultService promotionResultServiceMock)
	{

		final PromotionResultModel promotionResultModel =createSamplePromotionResult(promotionResultServiceMock);
		Mockito.lenient().when(promotionResultModel.getCertainty()).thenReturn(PromotionCertainty.FIRED.value());
		return promotionResultModel;
	}

	public PromotionResultModel createSamplePotentialPromotionResultForOrderEntry(AbstractOrderEntryModel orderEntry)
	{
		final PromotionResultModel promotionResultModel = createSamplePromotionResultForOrderEntry(orderEntry);
		Mockito.lenient().when(promotionResultModel.getCertainty()).thenReturn(PromotionCertainty.POTENTIAL.value());
		return promotionResultModel;
	}

	public PromotionResultModel createSamplePromotionResultForOrderEntry(AbstractOrderEntryModel orderEntry)
	{
		final PromotionResultModel promotionResultModel = mock(PromotionResultModel.class);
		final RuleBasedPromotionModel promotion = createRuleBasedPromotion(CODE, NAME);
		Mockito.lenient().when(promotionResultModel.getPromotion()).thenReturn(promotion);
		associateToOrderEntry(promotionResultModel, orderEntry);
		return promotionResultModel;
	}

	public PromotionResultModel createSamplePromotionResultForOrder()
	{
		final PromotionResultModel promotionResultModel = mock(PromotionResultModel.class);
		final RuleBasedPromotionModel promotion = createRuleBasedPromotion(CODE, NAME);
		Mockito.lenient().when(promotionResultModel.getPromotion()).thenReturn(promotion);
		associateToOrderEntry(promotionResultModel, null);
		return promotionResultModel;
	}

	protected void associateToOrderEntry(final PromotionResultModel promotionResultModel, final AbstractOrderEntryModel orderEntry)
	{
		final PromotionOrderEntryConsumedModel consumedEntry = mock(PromotionOrderEntryConsumedModel.class);
		Mockito.lenient().when(promotionResultModel.getConsumedEntries()).thenReturn(Lists.newArrayList(consumedEntry));
		Mockito.lenient().when(consumedEntry.getOrderEntry()).thenReturn(orderEntry);
	}

	public RuleBasedPromotionModel createRuleBasedPromotion(final String code, final String name)
	{
		final RuleBasedPromotionModel promotion = mock(RuleBasedPromotionModel.class);
		final AbstractRuleEngineRuleModel rule = mock(DroolsRuleModel.class);
		Mockito.lenient().when(promotion.getRule()).thenReturn(rule);
		Mockito.lenient().when(promotion.getCode()).thenReturn(code);
		Mockito.lenient().when(promotion.getName()).thenReturn(name);
		final AbstractRuleModel sourceRule = createSourceRule(code, name);
		Mockito.lenient().when(rule.getSourceRule()).thenReturn(sourceRule);
		return promotion;
	}

	public AbstractRuleModel createSourceRule(final String code, final String name)
	{
		final AbstractRuleModel sourceRule = mock(SourceRuleModel.class);
		Mockito.lenient().when(sourceRule.getCode()).thenReturn(code);
		Mockito.lenient().when(sourceRule.getName()).thenReturn(name);
		return sourceRule;
	}
}
