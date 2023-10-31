package de.hybris.platform.ruleengineservices.rule.evaluation.actions.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ruleengine.constants.RuleEngineConstants;
import de.hybris.platform.ruleengineservices.enums.FixedDiscountDistributeStrategy;
import de.hybris.platform.ruleengineservices.rao.AbstractRuleActionRAO;
import de.hybris.platform.ruleengineservices.rao.DiscountRAO;
import de.hybris.platform.ruleengineservices.rao.OrderEntryConsumedRAO;
import de.hybris.platform.ruleengineservices.rao.OrderEntryRAO;
import de.hybris.platform.ruleengineservices.rao.RuleEngineResultRAO;
import de.hybris.platform.ruleengineservices.rule.evaluation.actions.AbstractRuleSubsetProductActionUnitTest;
import de.hybris.platform.ruleengineservices.util.RAOConstants;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.fest.assertions.Assertions.assertThat;


@UnitTest
public class RuleSubsetOrderEntryPercentageDiscountRAOActionUnitTest extends AbstractRuleSubsetProductActionUnitTest
{
	private RuleSubsetOrderEntryPercentageDiscountRAOAction ruleSubsetOrderEntryPercentageDiscountRAOAction;
	private Map<String, BigDecimal> qualifyingContainers;
	private List<String> targetContainers;
	private final BigDecimal percentageDiscount = new BigDecimal(0.3) ;

	@Before
	public void setUp()
	{
		ruleSubsetOrderEntryPercentageDiscountRAOAction = getRuleSubsetOrderEntryPercentageDiscountRAOAction();
		qualifyingContainers = createQualifyingContainers("qualifyingContainer_X",threshold);
		targetContainers = createTargetProductsContainers("targetContainer_A");
		Mockito.lenient().when(ruleActionContext.getParameter(RAOConstants.VALUE_PARAM)).thenReturn(percentageDiscount);
		Mockito.lenient().when(ruleActionContext.getParameter(ruleSubsetOrderEntryPercentageDiscountRAOAction.SELECT_CURRENCY_PARAM)).thenReturn(USD);
		Mockito.lenient().when(ruleActionContext.getParameter(ruleSubsetOrderEntryPercentageDiscountRAOAction.QUALIFYING_CONTAINERS_PARAM)).thenReturn(qualifyingContainers);
		Mockito.lenient().when(ruleActionContext.getParameter(ruleSubsetOrderEntryPercentageDiscountRAOAction.TARGET_CONTAINERS_PARAM)).thenReturn(targetContainers);
	}
	/**
	 * Selected currency is not the same as the CurrencyIsoCode of the Cart
	 */
	@Test
	public void testPerformActionInternalWhenCurrencyDiff() //throws Exception
	{
		//Given
		cart.setCurrencyIsoCode("EUR");
		//When
		final boolean isPerformed=ruleSubsetOrderEntryPercentageDiscountRAOAction.performActionInternal(ruleActionContext);
		//Then
		assertThat(isPerformed).isFalse();
	}
	/**
	 * When the container's total value has not reached the input discount threshold and Consumption is not enabled
	 */
	@Test
	public void testPerformActionInternalWhenThresholdNotReachedAndConsumptionDisabled()
	{
		//Given

		Set<OrderEntryRAO> qualifyingOrderEntries = new HashSet<>();
		OrderEntryRAO qualifyingOrderEntry = new OrderEntryRAO();
		qualifyingOrderEntry.setBasePrice(new BigDecimal(189));
		qualifyingOrderEntry.setQuantity(1);
		qualifyingOrderEntries.add(qualifyingOrderEntry);
		Mockito.lenient().when(ruleActionContext.getValues(OrderEntryRAO.class,"qualifyingContainer_X")).thenReturn(qualifyingOrderEntries);
		Mockito.lenient().when(consumptionSupport.isConsumptionEnabled()).thenReturn(false);
		//When
		final boolean isPerformed=ruleSubsetOrderEntryPercentageDiscountRAOAction.performActionInternal(ruleActionContext);
		//Then
		assertThat(isPerformed).isFalse();
	}

	/**
	 * When the container's total value has reached the input discount threshold and Consumption is enabled
	 */
	@Test
	public void testPerformActionInternalWhenThresholdReachedAndConsumptionEnabled()
	{
		//Given
		//qualifyingOrderEntries ->1 qualifyingOrderEntry(111111)->4 quantity->(3 consumedquantity qualifyingOrderEntryConsumedRAO   1 uncomsumedquantity qualifyingOrderEntryConsumedRAONew)
		//targetOrderEntries -> 1 targetOrderEntries(222222) ->2 unconsumedquantity targetOrderEntryConsumedRAO
		Set<OrderEntryRAO> qualifyingOrderEntries = new HashSet<>();
		OrderEntryRAO qualifyingOrderEntry = new OrderEntryRAO();
		qualifyingOrderEntry.setBasePrice(new BigDecimal(200));
		qualifyingOrderEntry.setAvailableQuantity(1);
		qualifyingOrderEntries.add(qualifyingOrderEntry);


		Set<OrderEntryRAO> targetOrderEntries = new HashSet<>();
		OrderEntryRAO targetOrderEntry = new OrderEntryRAO();
		targetOrderEntry.setEntryNumber(222222);
		targetOrderEntries.add(targetOrderEntry);

		Set<DiscountRAO> discountRAOSet = new HashSet<>();
		DiscountRAO discountRAO = new DiscountRAO();
		discountRAO.setAppliedToObject(targetOrderEntry);
		discountRAOSet.add(discountRAO);

		LinkedHashSet<AbstractRuleActionRAO> actions = new LinkedHashSet<>();
		targetOrderEntry.setActions(actions);
		RuleEngineResultRAO result = new RuleEngineResultRAO();
		result.setActions(new LinkedHashSet<>());

		Map<String,Object> ruleMetaData = new HashMap<>();
		ruleMetaData.put(RuleEngineConstants.RULEMETADATA_RULECODE,"rule_code");   //rule_code
		ruleMetaData.put(RuleEngineConstants.RULEMETADATA_MODULENAME,"moduleName");   //moduleName

		Set<OrderEntryConsumedRAO> targetOrderEntryConsumedRAOs = new HashSet<>();
		OrderEntryConsumedRAO targetOrderEntryConsumedRAO = new OrderEntryConsumedRAO();
		targetOrderEntryConsumedRAOs.add(targetOrderEntryConsumedRAO);
		discountRAO.setConsumedEntries(targetOrderEntryConsumedRAOs);

		OrderEntryConsumedRAO qualifyingOrderEntryConsumedRAONew = new OrderEntryConsumedRAO();
		qualifyingOrderEntryConsumedRAONew.setQuantity(1);

		List<DiscountRAO> discounts = discountRAOSet.stream().collect(Collectors.toList());

		Mockito.lenient().when(ruleActionContext.getValues(OrderEntryRAO.class,"qualifyingContainer_X")).thenReturn(qualifyingOrderEntries);
		Mockito.lenient().when(consumptionSupport.isConsumptionEnabled()).thenReturn(true);

		Mockito.lenient().when(ruleActionContext.getValues(OrderEntryRAO.class,"targetContainer_A")).thenReturn(targetOrderEntries);
		Mockito.lenient().when(ruleActionContext.getRuleEngineResultRao()).thenReturn(result);
		Mockito.lenient().when(consumptionSupport.getConsumableQuantity(targetOrderEntry)).thenReturn(2);
		Mockito.lenient().when(ruleEngineCalculationService.addOrderEntryLevelDiscount(targetOrderEntry,false,percentageDiscount,false)).thenReturn(discountRAOSet);
		Mockito.lenient().when(consumptionSupport.consumeOrderEntry(targetOrderEntry,2,discountRAO)).thenReturn(targetOrderEntryConsumedRAO);
		Mockito.lenient().when(ruleActionContext.getRuleMetadata()).thenReturn(ruleMetaData);
		Mockito.lenient().doNothing().when(ruleActionContext).insertFacts(discountRAO);
		Mockito.lenient().doNothing().when(ruleActionContext).insertFacts(discountRAO.getConsumedEntries());
		Mockito.lenient().doNothing().when(ruleActionContext).scheduleForUpdate(discountRAO.getAppliedToObject());
		Mockito.lenient().when(consumptionSupport.consumeOrderEntry(qualifyingOrderEntry,discounts.isEmpty() ?null:discounts.get(0))).thenReturn(qualifyingOrderEntryConsumedRAONew);
		Mockito.lenient().doNothing().when(ruleActionContext).updateFacts(qualifyingOrderEntries);

		//When
		final boolean isPerformed=ruleSubsetOrderEntryPercentageDiscountRAOAction.performActionInternal(ruleActionContext);
		//Then
		assertThat(isPerformed).isTrue();
	}

	/**
	 * When the getEntriesDiscountDistributeStrategy method is called by the subset percentage discount class
	 */
	@Test
	public void testgetEntriesDiscountDistributeStrategyWhenPercentage()
	{
		//Expect
		assertThatThrownBy(() -> ruleSubsetOrderEntryPercentageDiscountRAOAction.getEntriesDiscountDistributeStrategy(
				FixedDiscountDistributeStrategy.UNIFORM)).isInstanceOf(UnsupportedOperationException.class);
	}

}
