package de.hybris.platform.ruleengineservices.rule.evaluation.actions.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ruleengine.constants.RuleEngineConstants;
import de.hybris.platform.ruleengineservices.calculation.EntriesDiscountDistributeStrategy;
import de.hybris.platform.ruleengineservices.enums.FixedDiscountDistributeStrategy;
import de.hybris.platform.ruleengineservices.rao.AbstractOrderRAO;
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
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.fest.assertions.Assertions.assertThat;


@UnitTest
public class RuleSubsetOrderEntryFixedDiscountRAOActionUnitTest extends AbstractRuleSubsetProductActionUnitTest
{
	private RuleSubsetOrderEntryFixedDiscountRAOAction ruleSubsetOrderEntryFixedDiscountRAOAction;
	private Map<String, BigDecimal> qualifyingContainers;
	private List<String> targetContainers;
	private final BigDecimal fixedDiscount = new BigDecimal(30) ;
	//private FixedDiscountDistributeStrategy fixedDiscountDistributeStrategy;
	@Mock
	protected EntriesDiscountDistributeStrategy entriesDiscountDistributeStrategy;

	@Before
	public void setUp()
	{
		ruleSubsetOrderEntryFixedDiscountRAOAction = getRuleSubsetOrderEntryFixedDiscountRAOAction();
		qualifyingContainers = createQualifyingContainers("qualifyingContainer_X",threshold);
		targetContainers = createTargetProductsContainers("targetContainer_A");
		Mockito.lenient().when(ruleActionContext.getParameter(RAOConstants.VALUE_PARAM)).thenReturn(fixedDiscount);
		Mockito.lenient().when(ruleActionContext.getParameter(ruleSubsetOrderEntryFixedDiscountRAOAction.SELECT_CURRENCY_PARAM)).thenReturn(USD);
		Mockito.lenient().when(ruleActionContext.getParameter(ruleSubsetOrderEntryFixedDiscountRAOAction.QUALIFYING_CONTAINERS_PARAM)).thenReturn(qualifyingContainers);
		Mockito.lenient().when(ruleActionContext.getParameter(ruleSubsetOrderEntryFixedDiscountRAOAction.TARGET_CONTAINERS_PARAM)).thenReturn(targetContainers);
		Mockito.lenient().when(ruleActionContext.getParameter(ruleSubsetOrderEntryFixedDiscountRAOAction.DISTRIBUTE_STRATEGY)).thenReturn(FixedDiscountDistributeStrategy.UNIFORM);
	}
	/**
	 * When the container's total value has reached the input discount threshold and Consumption is enabled
	 */
	@Test
	public void testPerformActionInternalWhenThresholdReachedAndConsumptionEnabled()
	{
		//Given
		// order 123456  qualifyingOrderEntry111111 targetOrderEntry222222
		//qualifyingOrderEntries ->1 qualifyingOrderEntry(111111)->4 quantity->(3 consumedquantity qualifyingOrderEntryConsumedRAO   1 uncomsumedquantity qualifyingOrderEntryConsumedRAONew)
		//targetOrderEntries -> 1 targetOrderEntries(222222) ->2 unconsumedquantity targetOrderEntryConsumedRAO
		Set<OrderEntryRAO> qualifyingOrderEntries = new HashSet<>();
		OrderEntryRAO qualifyingOrderEntry = new OrderEntryRAO();
		qualifyingOrderEntry.setAvailableQuantity(1);
		qualifyingOrderEntry.setBasePrice(new BigDecimal(200));
		qualifyingOrderEntries.add(qualifyingOrderEntry);

		Set<OrderEntryConsumedRAO> qualifyingOrderEntryConsumedRAOs = new HashSet<>();
		OrderEntryConsumedRAO qualifyingOrderEntryConsumedRAO = new OrderEntryConsumedRAO();
		qualifyingOrderEntryConsumedRAO.setAdjustedUnitPrice(new BigDecimal(3.3));
		qualifyingOrderEntryConsumedRAO.setQuantity(3);
		qualifyingOrderEntryConsumedRAO.setOrderEntry(qualifyingOrderEntry);
		qualifyingOrderEntryConsumedRAOs.add(qualifyingOrderEntryConsumedRAO);

		Set<OrderEntryRAO> targetOrderEntries = new HashSet<>();
		OrderEntryRAO targetOrderEntry = new OrderEntryRAO();
		targetOrderEntry.setEntryNumber(222222);
		targetOrderEntries.add(targetOrderEntry);

		//set the relationship of order and orderEntries
		AbstractOrderRAO order = new AbstractOrderRAO();
		Set<OrderEntryRAO> allEntries = new HashSet<>();
		allEntries.add(qualifyingOrderEntry);
		allEntries.add(targetOrderEntry);
		order.setEntries(allEntries);
		qualifyingOrderEntry.setOrder(order);

		//set the relationship of qualifyingOrderEntry ruleAction and orderEntryConsumedRAOs
		LinkedHashSet<AbstractRuleActionRAO> actionsOfQualifyingOrderEntry = new LinkedHashSet<>();
		DiscountRAO actionOfQualifyingOrderEntry = new DiscountRAO();
		actionOfQualifyingOrderEntry.setConsumedEntries(qualifyingOrderEntryConsumedRAOs);
		actionsOfQualifyingOrderEntry.add(actionOfQualifyingOrderEntry);
		qualifyingOrderEntry.setActions(actionsOfQualifyingOrderEntry);

		Set<DiscountRAO> discountRAOSet = new HashSet<>();
		DiscountRAO discountRAO = new DiscountRAO();
		discountRAO.setAppliedToObject(targetOrderEntry);
		discountRAOSet.add(discountRAO);

		LinkedHashSet<AbstractRuleActionRAO> actions = new LinkedHashSet<>();
		targetOrderEntry.setActions(actions);
		RuleEngineResultRAO result = new RuleEngineResultRAO();
		result.setActions(new LinkedHashSet<>());

		//fixed discount strategy related
		Map<FixedDiscountDistributeStrategy, EntriesDiscountDistributeStrategy> targetEntriesDiscountDistributeStrategy = new HashMap<>();
		targetEntriesDiscountDistributeStrategy.put(FixedDiscountDistributeStrategy.UNIFORM,entriesDiscountDistributeStrategy);
		ruleSubsetOrderEntryFixedDiscountRAOAction.setTargetEntriesDiscountDistributeStrategy(targetEntriesDiscountDistributeStrategy);
		Map<Integer,BigDecimal> distributeDiscountOnOrderEntries= new HashMap<>();
		distributeDiscountOnOrderEntries.put(222222,new BigDecimal(30));

		Map<String,Object> ruleMetaData = new HashMap<>();
		ruleMetaData.put(RuleEngineConstants.RULEMETADATA_RULECODE,"rule_code");   //rule_code
		ruleMetaData.put(RuleEngineConstants.RULEMETADATA_MODULENAME,"moduleName");   //moduleName

		Set<OrderEntryConsumedRAO> targetOrderEntryConsumedRAOs = new HashSet<>();
		OrderEntryConsumedRAO targetOrderEntryConsumedRAO = new OrderEntryConsumedRAO();
		targetOrderEntryConsumedRAOs.add(targetOrderEntryConsumedRAO);
		discountRAO.setConsumedEntries(targetOrderEntryConsumedRAOs);
		discountRAO.setFiredRuleCode("firedRuleCode");

		OrderEntryConsumedRAO qualifyingOrderEntryConsumedRAONew = new OrderEntryConsumedRAO();
		qualifyingOrderEntryConsumedRAONew.setQuantity(1);

		List<DiscountRAO> discounts = discountRAOSet.stream().collect(Collectors.toList());

		Mockito.lenient().when(ruleActionContext.getValues(OrderEntryRAO.class,"qualifyingContainer_X")).thenReturn(qualifyingOrderEntries);
		Mockito.lenient().when(consumptionSupport.isConsumptionEnabled()).thenReturn(true);


		Mockito.lenient().when(consumptionSupport.getConsumedOrderEntryInfoForOrderEntry(qualifyingOrderEntry)).thenCallRealMethod();
		Mockito.lenient().when(consumptionSupport.getConsumedOrderEntryInfoForOrderEntry(qualifyingOrderEntry,actionsOfQualifyingOrderEntry)).thenCallRealMethod();
		//Mockito.lenient().when(consumptionSupport.getConsumedOrderEntryInfoForOrderEntry(qualifyingOrderEntry)).thenReturn(qualifyingOrderEntryConsumedRAOs);
		Mockito.lenient().when(ruleActionContext.getValues(OrderEntryRAO.class,"targetContainer_A")).thenReturn(targetOrderEntries);
		Mockito.lenient().when(ruleActionContext.getRuleEngineResultRao()).thenReturn(result);
		Mockito.lenient().when(consumptionSupport.getConsumableQuantity(targetOrderEntry)).thenReturn(2);
		Mockito.lenient().when(entriesDiscountDistributeStrategy.distributeDiscount(createDistributeStrategyRPD(targetOrderEntries,fixedDiscount))).thenReturn(distributeDiscountOnOrderEntries);
		Mockito.lenient().when(ruleEngineCalculationService.addOrderEntryLevelDiscount(targetOrderEntry,true,fixedDiscount,false)).thenReturn(discountRAOSet);
		Mockito.lenient().when(consumptionSupport.consumeOrderEntry(targetOrderEntry,2,discountRAO)).thenReturn(targetOrderEntryConsumedRAO);
		Mockito.lenient().when(ruleActionContext.getRuleMetadata()).thenReturn(ruleMetaData);
		Mockito.lenient().doNothing().when(ruleActionContext).insertFacts(discountRAO);
		Mockito.lenient().doNothing().when(ruleActionContext).insertFacts(discountRAO.getConsumedEntries());
		Mockito.lenient().doNothing().when(ruleActionContext).scheduleForUpdate(discountRAO.getAppliedToObject());
		Mockito.lenient().when(consumptionSupport.consumeOrderEntry(qualifyingOrderEntry,discounts.isEmpty() ?null:discounts.get(0))).thenReturn(qualifyingOrderEntryConsumedRAONew);
		Mockito.lenient().doNothing().when(ruleActionContext).updateFacts(qualifyingOrderEntry);

		//When
		final boolean isPerformed=ruleSubsetOrderEntryFixedDiscountRAOAction.performActionInternal(ruleActionContext);
		//Then
		assertThat(isPerformed).isTrue();
	}


}
