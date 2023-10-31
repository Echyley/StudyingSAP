package de.hybris.platform.ruleengineservices.calculation.impl;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ruleengineservices.enums.FixedDiscountDistributeStrategy;
import de.hybris.platform.ruleengineservices.rao.EntriesDiscountDistributeStrategyRPD;
import de.hybris.platform.ruleengineservices.rao.OrderEntryConsumedRAO;
import de.hybris.platform.ruleengineservices.rao.OrderEntryRAO;
import de.hybris.platform.ruleengineservices.rule.evaluation.actions.RAOConsumptionSupport;
import de.hybris.platform.ruleengineservices.util.CurrencyUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class UniformFixedDiscountDistributeStrategyUnitTest
{
	private UniformFixedDiscountDistributeStrategy uniformFixedDiscountDistributeStrategy;
	private EntriesDiscountDistributeStrategyRPD entriesDiscountDistributeStrategyRPD;
	@Mock
	protected RAOConsumptionSupport consumptionSupport;
	@Mock
	private CurrencyUtils currencyUtils;
	private static final String USD = "USD";

	@Before
	public void setUp()
	{
		uniformFixedDiscountDistributeStrategy = new UniformFixedDiscountDistributeStrategy();
		uniformFixedDiscountDistributeStrategy.setConsumptionSupport(consumptionSupport);
		uniformFixedDiscountDistributeStrategy.setCurrencyUtils(currencyUtils);

	}

	/**
	 * test with strategy is not fixed count
	 */
	@Test
	public void testDistributeDiscountWithNotFixedCountStrategy()
	{
		//Given
		entriesDiscountDistributeStrategyRPD = new EntriesDiscountDistributeStrategyRPD();
		entriesDiscountDistributeStrategyRPD.setFixDiscount(false);

		//Expect
		assertThatThrownBy(() -> uniformFixedDiscountDistributeStrategy.distributeDiscount(entriesDiscountDistributeStrategyRPD))
				.isInstanceOf(IllegalArgumentException.class);
	}

	/**
	 * test with strategy is fixed count
	 */
	@Test
	public void testDistributeDiscountWithFixedCountStrategy()
	{
		//Given
		// fixedDiscount 30 USD
		// targetOrderEntries  targetOrderEntry1 333333   quantity 2  totalPrice 95  targetOrderEntry1 ConsumedRAOs quantity 1 adjustedUnitPrice 15  unCounumedRAOs price 80
		//	                    targetOrderEntry2 444444   quantity 1  totalPrice 40
		final Set<OrderEntryRAO> targetOrderEntries = new HashSet<>();
		final OrderEntryRAO targetOrderEntry1 = new OrderEntryRAO();
		targetOrderEntry1.setEntryNumber(333333);
		targetOrderEntry1.setTotalPrice(new BigDecimal(95));
		targetOrderEntries.add(targetOrderEntry1);
		final OrderEntryRAO targetOrderEntry2 = new OrderEntryRAO();
		targetOrderEntry2.setEntryNumber(444444);
		targetOrderEntry2.setTotalPrice(new BigDecimal(40));
		targetOrderEntries.add(targetOrderEntry2);
		final BigDecimal fixedDiscount = new BigDecimal(30);


		final Set<OrderEntryConsumedRAO> targetOrderEntry1ConsumedRAOs = new HashSet<>();
		final OrderEntryConsumedRAO targetOrderEntry1ConsumedRAO = new OrderEntryConsumedRAO();
		targetOrderEntry1ConsumedRAO.setAdjustedUnitPrice(new BigDecimal(15));
		targetOrderEntry1ConsumedRAO.setQuantity(1);
		targetOrderEntry1ConsumedRAOs.add(targetOrderEntry1ConsumedRAO);
		final Set<OrderEntryConsumedRAO> targetOrderEntry2ConsumedRAOs = new HashSet<>();
		entriesDiscountDistributeStrategyRPD = createDiscountDistributeStrategyRPD(
				targetOrderEntries.stream().collect(Collectors.toUnmodifiableList()), fixedDiscount, USD, true,
				FixedDiscountDistributeStrategy.UNIFORM);

		Mockito.lenient().when(consumptionSupport.isConsumptionEnabled()).thenReturn(true);
		Mockito.lenient().when(consumptionSupport.getConsumedOrderEntryInfoForOrderEntry(targetOrderEntry1))
				.thenReturn(targetOrderEntry1ConsumedRAOs);
		Mockito.lenient().when(consumptionSupport.getConsumedOrderEntryInfoForOrderEntry(targetOrderEntry2))
				.thenReturn(targetOrderEntry2ConsumedRAOs);
		Mockito.lenient()
				.when(currencyUtils.applyRounding(
						new BigDecimal(80).divide(new BigDecimal(120), 10, RoundingMode.DOWN).multiply(new BigDecimal(30)), USD))
				.thenReturn(new BigDecimal(20.00));
		Mockito.lenient()
				.when(currencyUtils.applyRounding(
						new BigDecimal(40).divide(new BigDecimal(120), 10, RoundingMode.DOWN).multiply(new BigDecimal(30)), USD))
				.thenReturn(new BigDecimal(10.00));
		//Mockito.lenient().when(currencyUtils.applyRounding(new BigDecimal(19.9999999980), USD)).thenReturn(new BigDecimal(20.00));
		//Mockito.lenient().when(currencyUtils.applyRounding(new BigDecimal(9.9999999990), USD)).thenReturn(new BigDecimal(10.00));
		Mockito.lenient().when(currencyUtils.getDigitsOfCurrency(USD)).thenReturn(Optional.of(2));


		//When
		final Map<Integer, BigDecimal> returnedDiscountMap = uniformFixedDiscountDistributeStrategy
				.distributeDiscount(entriesDiscountDistributeStrategyRPD);

		//Then
		final BigDecimal totalDiscount = returnedDiscountMap.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
		assertEquals(totalDiscount, fixedDiscount);

	}

	private EntriesDiscountDistributeStrategyRPD createDiscountDistributeStrategyRPD(final List<OrderEntryRAO> orderEntries,
			final BigDecimal totalDiscount, final String currencyISOCode, final boolean fixDiscount,
			final FixedDiscountDistributeStrategy fixedDiscountDistributeStrategy)
	{
		final EntriesDiscountDistributeStrategyRPD entriesDiscountDistributeStrategyRPD = new EntriesDiscountDistributeStrategyRPD();
		entriesDiscountDistributeStrategyRPD.setOrderEntries(orderEntries);
		entriesDiscountDistributeStrategyRPD.setTotalDiscount(totalDiscount);
		entriesDiscountDistributeStrategyRPD.setCurrencyIsoCode(currencyISOCode);
		entriesDiscountDistributeStrategyRPD.setFixDiscount(fixDiscount);
		entriesDiscountDistributeStrategyRPD.setFixedDiscountDistributeStrategy(fixedDiscountDistributeStrategy);
		return entriesDiscountDistributeStrategyRPD;
	}

}
