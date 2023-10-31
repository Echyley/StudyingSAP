/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp.strategy;

import static org.junit.Assert.assertNotNull;

import de.hybris.platform.sap.sapordermgmtbol.constants.SapordermgmtbolConstants;
import de.hybris.platform.sap.sapordermgmtbol.unittests.base.SapordermanagmentBolSpringJunitTest;

import org.junit.Test;


@SuppressWarnings("javadoc")
public class HeaderTextMapperTest extends SapordermanagmentBolSpringJunitTest
{

	@Test
	public void testBeanInitializtion()
	{
		final HeadTextMapper cut = (HeadTextMapper) genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BEAN_HEADER_TEXT_MAPPER);
		assertNotNull(cut);
	}


}
