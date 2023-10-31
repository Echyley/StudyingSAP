package de.hybris.platform.ruleengineservices.rule.evaluation.actions;

import de.hybris.platform.ruleengineservices.calculation.RuleEngineCalculationService;
import de.hybris.platform.ruleengineservices.enums.FixedDiscountDistributeStrategy;
import de.hybris.platform.ruleengineservices.rao.CartRAO;
import de.hybris.platform.ruleengineservices.rao.EntriesDiscountDistributeStrategyRPD;
import de.hybris.platform.ruleengineservices.rao.OrderEntryRAO;
import de.hybris.platform.ruleengineservices.rule.evaluation.RuleActionContext;
import de.hybris.platform.ruleengineservices.rule.evaluation.actions.impl.DefaultRAOConsumptionSupport;
import de.hybris.platform.ruleengineservices.rule.evaluation.actions.impl.RuleSubsetOrderEntryFixedDiscountRAOAction;
import de.hybris.platform.ruleengineservices.rule.evaluation.actions.impl.RuleSubsetOrderEntryPercentageDiscountRAOAction;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public abstract class AbstractRuleSubsetProductActionUnitTest
{
	private RuleSubsetOrderEntryPercentageDiscountRAOAction ruleSubsetOrderEntryPercentageDiscountRAOAction;
	private RuleSubsetOrderEntryFixedDiscountRAOAction ruleSubsetOrderEntryFixedDiscountRAOAction;
	@Mock
	protected RuleActionContext ruleActionContext;
	protected CartRAO cart;
	@Mock
	protected DefaultRAOConsumptionSupport consumptionSupport;
	@Mock
	protected RuleEngineCalculationService ruleEngineCalculationService;

	protected static final BigDecimal threshold = new BigDecimal(199);
	protected static final String USD = "USD";
	protected static final String EUR = "EUR";

	@Before
	public void setUpAbstractRuleSubsetProductActionUnitTest()
	{
		createCart(USD);
		Mockito.lenient().when(ruleActionContext.getValue(CartRAO.class)).thenReturn(cart);

	}

	protected CartRAO createCart(final String currencyISO)
	{
		cart = new CartRAO();
		cart.setCurrencyIsoCode(currencyISO);
		return cart;
	}

	protected Map<String, BigDecimal> createQualifyingContainers(final String containerName, final BigDecimal threshold)
	{
		final Map<String, BigDecimal> qualifyingContainers = new HashMap<>();
		qualifyingContainers.put(containerName, threshold);
		return qualifyingContainers;
	}

	protected List<String> createTargetProductsContainers(final String containerName)
	{
		final ArrayList<String> targetContainers = new ArrayList<>();
		targetContainers.add(containerName);
		return targetContainers;
	}

	protected RuleSubsetOrderEntryPercentageDiscountRAOAction getRuleSubsetOrderEntryPercentageDiscountRAOAction()
	{
		ruleSubsetOrderEntryPercentageDiscountRAOAction = new RuleSubsetOrderEntryPercentageDiscountRAOAction();
		ruleSubsetOrderEntryPercentageDiscountRAOAction.setConsumptionSupport(consumptionSupport);
		ruleSubsetOrderEntryPercentageDiscountRAOAction.setRuleEngineCalculationService(ruleEngineCalculationService);
		return ruleSubsetOrderEntryPercentageDiscountRAOAction;
	}

	protected RuleSubsetOrderEntryFixedDiscountRAOAction getRuleSubsetOrderEntryFixedDiscountRAOAction()
	{
		ruleSubsetOrderEntryFixedDiscountRAOAction = new RuleSubsetOrderEntryFixedDiscountRAOAction();
		ruleSubsetOrderEntryFixedDiscountRAOAction.setConsumptionSupport(consumptionSupport);
		ruleSubsetOrderEntryFixedDiscountRAOAction.setRuleEngineCalculationService(ruleEngineCalculationService);
		return ruleSubsetOrderEntryFixedDiscountRAOAction;
	}

	protected EntriesDiscountDistributeStrategyRPD createDistributeStrategyRPD(final Set<OrderEntryRAO> targetOrderEntries,
			final BigDecimal totalDiscount)
	{
		return ruleSubsetOrderEntryFixedDiscountRAOAction.createDiscountDistributeStrategyRPD(
				targetOrderEntries.stream().collect(Collectors.toUnmodifiableList()), totalDiscount, USD, true,
				FixedDiscountDistributeStrategy.UNIFORM);
	}
}
