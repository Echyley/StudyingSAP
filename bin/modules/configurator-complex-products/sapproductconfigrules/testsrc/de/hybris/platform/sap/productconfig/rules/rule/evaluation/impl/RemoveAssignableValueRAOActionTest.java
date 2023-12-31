/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.rules.rule.evaluation.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ruleengineservices.rao.AbstractRuleActionRAO;
import de.hybris.platform.ruleengineservices.rao.ProcessStep;
import de.hybris.platform.sap.productconfig.rules.rao.CsticRAO;
import de.hybris.platform.sap.productconfig.rules.rao.ProductConfigProcessStepRAO;
import de.hybris.platform.sap.productconfig.rules.rao.action.RemoveAssignableValueRAO;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;


@UnitTest
public class RemoveAssignableValueRAOActionTest extends AbstractProductConfigRAOActionTCBase
{
	private RemoveAssignableValueRAOAction action;
	private ProductConfigProcessStepRAO processStep;
	private Map<String, Object> parameters;

	@Before
	public void setUp()
	{
		action = new RemoveAssignableValueRAOAction();
		action.setConfigurationService(getConfigurationService());
		action.setRaoLookupService(getRaoLookupService());

		final Configuration configuration = mock(Configuration.class);

		processStep = new ProductConfigProcessStepRAO();
		processStep.setProcessStep(ProcessStep.RETRIEVE_CONFIGURATION);
		when(getContext().getValue(ProductConfigProcessStepRAO.class)).thenReturn(processStep);

		parameters = new HashMap<>();
		parameters.put(RemoveAssignableValueRAOAction.CSTIC_NAME, "CSTIC_NAME");
		parameters.put(RemoveAssignableValueRAOAction.CSTIC_VALUE, "CSTIC_VALUE");
		when(getContext().getParameters()).thenReturn(parameters);
	}

	@Test
	public void testRemoveAssignableValue()
	{
		action.performActionInternal(getContext());

		assertNotNull(getResult().getActions());
		final LinkedHashSet<AbstractRuleActionRAO> actionList = getResult().getActions();
		assertEquals(1, actionList.size());

		final RemoveAssignableValueRAO removeAssignableValueRAO = (RemoveAssignableValueRAO) actionList.iterator().next();

		assertNotNull(removeAssignableValueRAO);

		final CsticRAO targetCstic = (CsticRAO) removeAssignableValueRAO.getAppliedToObject();
		assertEquals("CSTIC_NAME", targetCstic.getCsticName());
		assertEquals("CSTIC_VALUE", removeAssignableValueRAO.getValueNameToRemoveFromAssignable().getCsticValueName());
	}

	@Test
	public void testRemoveAssignableValue_wrongProcessStep()
	{
		processStep.setProcessStep(ProcessStep.CREATE_DEFAULT_CONFIGURATION);
		action.performActionInternal(getContext());

		assertNotNull(getResult().getActions());
		final LinkedHashSet<AbstractRuleActionRAO> actionList = getResult().getActions();
		assertEquals(0, actionList.size());
	}

}
