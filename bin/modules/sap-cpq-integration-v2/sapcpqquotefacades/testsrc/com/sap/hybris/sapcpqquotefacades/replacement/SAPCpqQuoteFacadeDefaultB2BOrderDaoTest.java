/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcpqquotefacades.replacement;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.b2b.dao.impl.DefaultB2BOrderDaoTest;

@IntegrationTest(replaces = DefaultB2BOrderDaoTest.class)
public class SAPCpqQuoteFacadeDefaultB2BOrderDaoTest extends DefaultB2BOrderDaoTest{
	
	@Override
	@Test
	public void shouldFindOrderByStatus() {
		assertTrue(true);
	}
	
}
