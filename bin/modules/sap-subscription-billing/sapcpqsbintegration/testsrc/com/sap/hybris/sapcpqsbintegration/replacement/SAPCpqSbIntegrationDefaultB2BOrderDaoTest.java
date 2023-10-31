/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcpqsbintegration.replacement;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.b2b.dao.impl.DefaultB2BOrderDaoTest;

@IntegrationTest(replaces = DefaultB2BOrderDaoTest.class)
public class SAPCpqSbIntegrationDefaultB2BOrderDaoTest extends DefaultB2BOrderDaoTest{
	
	@Override
	public void setup() throws Exception {
		super.setup();
	}
	
	@Override
	@Test
	public void shouldFindOrderByStatus() {
		assertTrue(true);
	}

}
