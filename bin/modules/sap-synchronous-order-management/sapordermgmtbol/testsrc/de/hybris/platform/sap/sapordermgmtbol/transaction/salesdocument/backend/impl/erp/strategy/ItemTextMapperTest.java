/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp.strategy;

import static org.junit.Assert.assertNotNull;

import de.hybris.platform.sap.sapordermgmtbol.constants.SapordermgmtbolConstants;
import de.hybris.platform.sap.sapordermgmtbol.unittests.base.SapordermanagmentBolSpringJunitTest;

import org.junit.Test;


@SuppressWarnings("javadoc")
public class ItemTextMapperTest extends SapordermanagmentBolSpringJunitTest
{

	@Test
	public void testBeanInitializtion()
	{
		final ItemTextMapper cut = (ItemTextMapper) genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BEAN_ITEM_TEXT_MAPPER);
		assertNotNull(cut);
	}


}
