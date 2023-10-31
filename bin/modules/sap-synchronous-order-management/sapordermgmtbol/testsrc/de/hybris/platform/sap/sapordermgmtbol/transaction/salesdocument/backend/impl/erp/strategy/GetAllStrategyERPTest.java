/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp.strategy;

import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.sapordermgmtbol.constants.SapordermgmtbolConstants;
import de.hybris.platform.sap.sapordermgmtbol.unittests.base.SapordermanagmentBolSpringJunitTest;

import org.junit.Test;


@UnitTest
@SuppressWarnings("javadoc")
public class GetAllStrategyERPTest extends SapordermanagmentBolSpringJunitTest
{


	private GetAllStrategyERP classUnderTest;

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.sap.core.test.SapcoreSpringJUnitTest#setUp()
	 */
	@Override
	public void setUp()
	{
		super.setUp();
	}

	@Test
	public void testConstructor()
	{
		classUnderTest = (GetAllStrategyERP) genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BEAN_READ_STRATEGY);
		assertNotNull(classUnderTest);
	}

}
