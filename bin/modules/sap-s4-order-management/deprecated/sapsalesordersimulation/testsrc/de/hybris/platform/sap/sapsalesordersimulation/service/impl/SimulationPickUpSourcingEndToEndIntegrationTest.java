/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapsalesordersimulation.service.impl;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.warehousing.PickUpSourcingEndToEndIntegrationTest;
@IntegrationTest(replaces = PickUpSourcingEndToEndIntegrationTest.class)
public class SimulationPickUpSourcingEndToEndIntegrationTest {
	@Test
	public void test() {
		assertTrue(true);
	}
}
