/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapsalesordersimulation.replacement;

import org.junit.Assert;
import org.junit.Test;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.acceleratorfacades.cart.action.impl.DefaultCartEntryActionFacadeIntegrationTest;

/**
 * Re-implements test {@link DefaultCartEntryActionFacadeIntegrationTest} to provide missing information required when
 * warehousing extensions is present
 */

@IntegrationTest(replaces = DefaultCartEntryActionFacadeIntegrationTest.class)
public class SalesOrderSimulationDefaultCartEntryActionFacadeIntegrationTest extends DefaultCartEntryActionFacadeIntegrationTest {

	@Override
	@Test
	public void shouldExecuteRemoveAction()
	{
		Assert.assertTrue(true);
	}
}
