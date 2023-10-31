/*
 * [y] hybris Platform
 *
 * Copyright (c) 2017 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.configurablebundlebackoffice.widgets.searchadapters;

import static com.google.common.collect.Sets.newHashSet;
import static org.mockito.BDDMockito.given;

import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.configurablebundlebackoffice.factory.ExplorerTreeSimpleNodeFactory;
import de.hybris.platform.core.PK;

import org.junit.Before;
import org.mockito.Mock;
import org.mockito.Mockito;


public abstract class BundleTemplateConditionAdapterTestBase
{
	public static final PK STAGED_PK = PK.fromLong(1l);
	private final ExplorerTreeSimpleNodeFactory nodeFactory = new ExplorerTreeSimpleNodeFactory();
	@Mock
	protected CatalogModel catalog;
	@Mock
	protected CatalogVersionModel staged;

	@Before
	public void setUp()
	{
		Mockito.lenient().when(catalog.getCatalogVersions()).thenReturn(newHashSet(staged));
		Mockito.lenient().when(staged.getPk()).thenReturn(STAGED_PK);
	}

	protected ExplorerTreeSimpleNodeFactory getNodeFactory()
	{
		return nodeFactory;
	}
}
