/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp.strategy;

import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.erp.strategy.GetAllStrategy;

import junit.framework.TestCase;

import org.junit.Test;


@SuppressWarnings("javadoc")
public class StrategyFactoryERP604Test extends TestCase
{

	private final StrategyFactoryERP604 classUnderTest = new StrategyFactoryERP604();

	@Test
	public void testCreateGetAllStrategy()
	{
		final GetAllStrategy factory = classUnderTest.createGetAllStrategy();

		assertNotNull(factory);
		assertEquals(GetAllStrategyERP604.class.getName(), factory.getClass().getName());
	}

}
