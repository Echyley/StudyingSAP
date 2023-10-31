/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.punchout.order;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.punchout.services.PunchOutSessionService;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.order.strategies.calculation.impl.DefaultOrderRequiresCalculationStrategy;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * Test cases for {@link PunchOutOrderRequiresCalculationStrategy}.
 */
@UnitTest
@RunWith(Parameterized.class)
public class PunchOutOrderRequiresCalculationStrategyTest
{
	@Mock
	private DefaultOrderRequiresCalculationStrategy defaultStrategy;
	@Mock
	private PunchOutSessionService punchOutSessionService;
	@InjectMocks
	private final PunchOutOrderRequiresCalculationStrategy punchOutOrderRequiresCalculationStrategy = new PunchOutOrderRequiresCalculationStrategy();

	@Mock
	private AbstractOrderEntryModel orderEntryModel;
	@Mock
	private AbstractOrderModel orderModel;

	private final boolean expectedResult;

	/**
	 * The data is consisted of <br/>
	 * 1) whether the order is a punch out order <br/>
	 * 2) whether the default injected strategy returns true or false<br/>
	 * 3) the expected result
	 */
	@Parameters
	public static Collection<Object[]> data()
	{
		return Arrays.asList(new Object[][]
		{
		{ Boolean.TRUE, Boolean.TRUE, Boolean.TRUE },
		{ Boolean.FALSE, Boolean.TRUE, Boolean.FALSE },
		{ null, Boolean.TRUE, Boolean.TRUE },
		{ Boolean.TRUE, Boolean.FALSE, Boolean.FALSE },
		{ Boolean.FALSE, Boolean.FALSE, Boolean.FALSE },
		{ null, Boolean.FALSE, Boolean.FALSE } });
	}

	public PunchOutOrderRequiresCalculationStrategyTest(final Boolean cartIsValid, final boolean defaultStrategyResult,
			final boolean expectedResult)
	{
		this.expectedResult = expectedResult;

		MockitoAnnotations.initMocks(this);
		when(orderEntryModel.getOrder()).thenReturn(orderModel);
		when(defaultStrategy.requiresCalculation(orderEntryModel)).thenReturn(defaultStrategyResult);
		when(defaultStrategy.requiresCalculation(orderModel)).thenReturn(defaultStrategyResult);
		when(punchOutSessionService.isPunchOutSessionCartValid()).thenReturn(cartIsValid);
	}

	@Test
	public void testRequiresCalculationWhenNotPunchOutOrderAndNot() throws Exception
	{
		assertEquals(expectedResult, punchOutOrderRequiresCalculationStrategy.requiresCalculation(orderEntryModel));
	}

	@Test
	public void testRequiresCalculationAbstractOrderModel() throws Exception
	{

		assertEquals(expectedResult, punchOutOrderRequiresCalculationStrategy.requiresCalculation(orderModel));
	}

}
