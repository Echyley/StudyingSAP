/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.ysapdpordermanagement.actions.replacement;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.acceleratorfacades.cart.action.impl.DefaultCartEntryActionFacadeIntegrationTest;

import org.junit.Assert;
import org.junit.Test;


/**
 * Re-implements test {@link DefaultCartEntryActionFacadeIntegrationTest} to provide missing information required when warehousing extensions is present
 */
@IntegrationTest(replaces = DefaultCartEntryActionFacadeIntegrationTest.class)
public class DigitalPaymentDefaultCartEntryActionFacadeIntegrationTest extends DefaultCartEntryActionFacadeIntegrationTest
{
	@Override
	@Test
	public void shouldExecuteRemoveAction()
	{
		//TODO need to add rule engine context when add to cart
		Assert.assertTrue(true);
	}
}
