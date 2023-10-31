/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp.strategy;

import static org.junit.Assert.assertFalse;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;

import org.junit.Test;



@UnitTest
@SuppressWarnings("javadoc")
public class ERPLO_APICustomerExitsImplTest
{
	ERPLO_APICustomerExitsImpl classUnderTest = new ERPLO_APICustomerExitsImpl();

	@Test
	public void testItemChanged()
	{
		final Item currentItem = null;
		final Item existingItem = null;
		assertFalse(classUnderTest.customerExitIsItemChanged(currentItem, existingItem));
	}
}
