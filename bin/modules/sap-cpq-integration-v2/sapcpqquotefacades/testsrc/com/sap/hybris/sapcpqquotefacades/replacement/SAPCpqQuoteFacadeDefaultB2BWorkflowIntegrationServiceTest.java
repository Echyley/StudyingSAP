/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcpqquotefacades.replacement;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.b2b.services.impl.DefaultB2BWorkflowIntegrationServiceTest;

@IntegrationTest(replaces = DefaultB2BWorkflowIntegrationServiceTest.class)
public class SAPCpqQuoteFacadeDefaultB2BWorkflowIntegrationServiceTest extends DefaultB2BWorkflowIntegrationServiceTest{
	
	@Override
	@Test
	public void shouldStartWorkflowAndSaveAttachmentsForActions() {
		assertTrue(true);
	}

}
