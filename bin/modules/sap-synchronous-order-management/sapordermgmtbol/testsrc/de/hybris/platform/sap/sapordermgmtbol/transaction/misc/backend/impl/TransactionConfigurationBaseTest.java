/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtbol.transaction.misc.backend.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.core.jco.connection.JCoConnection;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.sapordermgmtbol.transaction.misc.backend.impl.erp.TransactionConfigurationERP;

import java.util.Map;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;


@UnitTest
@SuppressWarnings("javadoc")
public class TransactionConfigurationBaseTest
{
	TransactionConfigurationBase classUnderTest = new TransactionConfigurationERP();
	JCoConnection connection = null;

	@Before
	public void init() throws BackendException
	{
		connection = EasyMock.createMock(JCoConnection.class);
		EasyMock.expect(connection.isFunctionAvailable(TransactionConfigurationBase.RFC_NAME_LANGUAGE_SWITCH_DELIV_CUSTOMIZING))
				.andReturn(true);
		EasyMock.replay(connection);
	}

	@Test
	public void testLanguageSwitchDelivCustomizingAvailable()
	{
		assertTrue(classUnderTest.isLanguageSwitchDelivCustomizingAvailable(connection));
	}


	public void testDeliveryTypes()
	{
		final Map<String, String> deliveryTypes = classUnderTest.getDeliveryTypes();
		assertNotNull(deliveryTypes);
	}
}
