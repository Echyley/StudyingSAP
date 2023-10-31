/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtbol.transaction.basket.backend.impl.erp;

import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.sapordermgmtbol.constants.SapordermgmtbolConstants;
import de.hybris.platform.sap.sapordermgmtbol.unittests.base.SapordermanagmentBolSpringJunitTest;

import org.junit.Test;


@UnitTest
@SuppressWarnings("javadoc")
public class BasketERPTest extends SapordermanagmentBolSpringJunitTest
{

	private BasketERP classUnderTest;

	@Test
	public void testBeanInstantiation()
	{

		classUnderTest = genericFactory.getBean(SapordermgmtbolConstants.BEAN_ID_BE_CART_ERP);

		assertNotNull(classUnderTest.getAdditionalPricing());

	}


}
