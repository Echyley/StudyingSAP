/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.common.util;

import static org.junit.Assert.assertNotSame;

import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.core.test.SapcoreSpringJUnitTest;


/**
 * Test for Default Generic Factory.
 */
@UnitTest
@ContextConfiguration(locations =
{ "DefaultGenericFactoryTest-spring.xml" })
public class DefaultGenericFactoryTest extends SapcoreSpringJUnitTest
{

	/**
	 * Remove session scoped bean test.
	 */
	@Test
	public void testRemoveSessionScopeBean()
	{
		final Object beanBefore = genericFactory.getBean("testBean");
		genericFactory.removeBean("testBean");
		final Object beanAfter = genericFactory.getBean("testBean");
		assertNotSame(beanBefore, beanAfter);
	}

}
