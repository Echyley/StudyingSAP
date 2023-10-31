/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtservices.impl;

import static org.junit.Assert.assertFalse;

import de.hybris.platform.sap.sapordermgmtservices.bolfacade.BolCartFacade;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;


@SuppressWarnings(
{ "boxing", "javadoc" })
public class DefaultBackendAvailabilityServiceTest
{
	DefaultBackendAvailabilityService classUnderTest = new DefaultBackendAvailabilityService();
	BolCartFacade bolCartFacade;


	@Before
	public void init()
	{
		bolCartFacade = EasyMock.createMock(BolCartFacade.class);

		EasyMock.expect(Boolean.valueOf(bolCartFacade.isBackendDown())).andReturn(Boolean.valueOf(false));
		EasyMock.replay(bolCartFacade);
		classUnderTest.setBolCartFacade(bolCartFacade);
	}

	@Test
	public void testBackendDown()
	{
		assertFalse(classUnderTest.isBackendDown());
	}
}
