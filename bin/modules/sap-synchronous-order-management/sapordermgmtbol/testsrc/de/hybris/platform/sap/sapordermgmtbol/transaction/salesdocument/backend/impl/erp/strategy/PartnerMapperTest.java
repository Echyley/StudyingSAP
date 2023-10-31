/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp.strategy;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.platform.sap.sapcommonbol.businesspartner.businessobject.impl.AddressImpl;
import de.hybris.platform.sap.sapcommonbol.businesspartner.businessobject.interf.Address;
import de.hybris.platform.sap.sapordermgmtbol.constants.SapordermgmtbolConstants;
import de.hybris.platform.sap.sapordermgmtbol.unittests.base.SapordermanagmentBolSpringJunitTest;

import org.junit.Test;


@SuppressWarnings("javadoc")
public class PartnerMapperTest extends SapordermanagmentBolSpringJunitTest
{

	public PartnerMapper cut = new PartnerMapper();

	@Test
	public void testBeanInitialization()
	{
		final PartnerMapper cut = (PartnerMapper) genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BEAN_PARTNER_MAPPER);
		assertNotNull(cut);
	}


	@Test
	public void testAddressChanged()
	{
		final Address address = new AddressImpl();
		assertFalse(cut.isAddressChanged(address));
		address.setTel1Numbr("1");
		assertTrue(cut.isAddressChanged(address));
	}

	@Test
	public void testAddressChangedNullAddress()
	{
		assertFalse(cut.isAddressChanged(null));
	}

}
