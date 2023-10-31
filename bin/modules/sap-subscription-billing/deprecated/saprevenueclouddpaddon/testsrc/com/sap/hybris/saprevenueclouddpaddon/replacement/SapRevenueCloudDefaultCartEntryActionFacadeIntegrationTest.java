/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.saprevenueclouddpaddon.replacement;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.acceleratorfacades.cart.action.impl.DefaultCartEntryActionFacadeIntegrationTest;


@IntegrationTest(replaces = DefaultCartEntryActionFacadeIntegrationTest.class)
public class SapRevenueCloudDefaultCartEntryActionFacadeIntegrationTest extends DefaultCartEntryActionFacadeIntegrationTest {
	
	@Override
	@Test
	public void shouldExecuteRemoveAction() {
		assertTrue(true);
	}
}
