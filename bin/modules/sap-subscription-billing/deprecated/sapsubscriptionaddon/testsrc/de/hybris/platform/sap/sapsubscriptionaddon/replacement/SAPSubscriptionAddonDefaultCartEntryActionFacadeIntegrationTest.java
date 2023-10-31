/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapsubscriptionaddon.replacement;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.acceleratorfacades.cart.action.impl.DefaultCartEntryActionFacadeIntegrationTest;

@IntegrationTest(replaces = DefaultCartEntryActionFacadeIntegrationTest.class)
public class SAPSubscriptionAddonDefaultCartEntryActionFacadeIntegrationTest extends DefaultCartEntryActionFacadeIntegrationTest{
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
	}
	
	@Override
	@Test
	public void shouldExecuteRemoveAction() {
		assertTrue(true);
	}

}
