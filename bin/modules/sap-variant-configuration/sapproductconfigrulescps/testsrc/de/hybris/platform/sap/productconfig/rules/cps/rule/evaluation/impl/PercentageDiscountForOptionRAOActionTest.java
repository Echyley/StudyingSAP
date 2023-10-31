/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.rules.cps.rule.evaluation.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ruleengineservices.rao.AbstractRuleActionRAO;
import de.hybris.platform.ruleengineservices.rao.ProcessStep;
import de.hybris.platform.sap.productconfig.rules.cps.rao.action.PercentageDiscountForOptionWithMessageRAO;
import de.hybris.platform.sap.productconfig.rules.rao.CsticRAO;
import de.hybris.platform.sap.productconfig.rules.rao.ProductConfigProcessStepRAO;
import de.hybris.platform.sap.productconfig.rules.rule.evaluation.impl.AbstractProductConfigRAOActionTCBase;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;


@UnitTest
public class PercentageDiscountForOptionRAOActionTest extends AbstractProductConfigRAOActionTCBase
{
	protected static final String CSTIC_NAME = "cstic";
	protected static final String CSTIC_VALUE = "cstic_value";
	protected static final String MESSAGE = "message";

	protected static final BigDecimal DISCOUNT = new BigDecimal("20.00");

	private PercentageDiscountForOptionRAOAction action;
	private ProductConfigProcessStepRAO processStep;

	private final Map<String, Object> parameters = new HashMap<>();

	@Before
	public void setUp()
	{
		action = new PercentageDiscountForOptionRAOAction();
		action.setConfigurationService(getConfigurationService());
		action.setRaoLookupService(getRaoLookupService());

		final Configuration configuration = mock(Configuration.class);
		when(getContext().getRuleEngineResultRao()).thenReturn(getResult());

		processStep = new ProductConfigProcessStepRAO();
		processStep.setProcessStep(ProcessStep.RETRIEVE_CONFIGURATION);
		when(getContext().getValue(ProductConfigProcessStepRAO.class)).thenReturn(processStep);

		parameters.put(CSTIC_NAME, "CSTIC_NAME");
		parameters.put(CSTIC_VALUE, "CSTIC_VALUE");
		parameters.put(PercentageDiscountForOptionRAOAction.DISCOUNT_VALUE, DISCOUNT);
		parameters.put(MESSAGE, "Message");
		parameters.put(DisplayPromoMessageRAOAction.EXTENDED_MESSAGE, "Extended Message");

		when(getContext().getParameters()).thenReturn(parameters);
	}

	@Test
	public void testPercentageDiscountForOption()
	{
		final PercentageDiscountForOptionWithMessageRAO percentageDiscountForOptionRAO = performActionAndRetrieveDiscountRAO();

		final CsticRAO targetCstic = (CsticRAO) percentageDiscountForOptionRAO.getAppliedToObject();
		assertEquals("CSTIC_NAME", targetCstic.getCsticName());
		assertEquals("CSTIC_VALUE", percentageDiscountForOptionRAO.getValueName().getCsticValueName());
		assertEquals(0, percentageDiscountForOptionRAO.getDiscountValue().compareTo(DISCOUNT));
		assertEquals("Message", percentageDiscountForOptionRAO.getMessage());
		assertEquals("Extended Message", percentageDiscountForOptionRAO.getExtendedMessage());
	}


	@Test
	public void testPercentageDiscountForOptionAnotherProcessStep()
	{
		processStep.setProcessStep(ProcessStep.CREATE_DEFAULT_CONFIGURATION);
		action.performActionInternal(getContext());
		assertNotNull(getResult().getActions());
		final LinkedHashSet<AbstractRuleActionRAO> actionList = getResult().getActions();
		assertEquals(0, actionList.size());
	}

	@Test
	public void testPercentageDiscountForOptionInvalidProcessStep()
	{
		processStep.setProcessStep(null);
		action.performActionInternal(getContext());

		assertNotNull(getResult().getActions());
		final LinkedHashSet<AbstractRuleActionRAO> actionList = getResult().getActions();
		assertEquals(0, actionList.size());
	}

	@Test
	public void testPercentageDiscountForOptionPrepareLog()
	{
		final String logMessage = action.prepareActionLogText(getContext(), getContext().getParameters());
		assertNotNull(logMessage);
		assertTrue(logMessage.contains("CSTIC_NAME"));
		assertTrue(logMessage.contains("CSTIC_VALUE"));
		assertTrue(logMessage.contains(DISCOUNT.toString()));
		assertTrue(logMessage.contains("message"));
		assertTrue(logMessage.contains("extended message"));
	}

	protected PercentageDiscountForOptionWithMessageRAO performActionAndRetrieveDiscountRAO()
	{
		action.performActionInternal(getContext());

		assertNotNull(getResult().getActions());
		final LinkedHashSet<AbstractRuleActionRAO> actionList = getResult().getActions();
		assertEquals(1, actionList.size());

		final PercentageDiscountForOptionWithMessageRAO percentageDiscountForOptionRAO = (PercentageDiscountForOptionWithMessageRAO) actionList
				.iterator().next();

		assertNotNull(percentageDiscountForOptionRAO);
		return percentageDiscountForOptionRAO;
	}

}
