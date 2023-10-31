/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cms2.servicelayer.daos.impl;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.cms2.data.PageableData;
import de.hybris.platform.cms2.servicelayer.daos.CMSMediaFolderDao;
import de.hybris.platform.core.model.media.MediaFolderModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.search.SearchResult;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class DefaultCMSMediaFolderDaoIntegrationTest extends ServicelayerTransactionalTest
{
	private static final String QUALIFIER = "qualifier";

	@Resource
	private CMSMediaFolderDao cmsMediaFolderDao;

	@Before
	public void setUp() throws ImpExException
	{
		importCsv("/test/cmsMediaFolderTestData.csv", "utf-8");
	}

	@Test
	public void shouldFindMediaFolders()
	{
		final PageableData pageableData = new PageableData();
		pageableData.setCurrentPage(0);
		pageableData.setPageSize(2);

		final SearchResult<MediaFolderModel> folders = cmsMediaFolderDao.findMediaFolders("", pageableData);


		assertEquals(2, folders.getCount());
		assertEquals(11, folders.getTotalCount());
		assertThat(folders.getResult(), hasItems( //
				allOf(hasProperty(QUALIFIER, equalTo("cronjob"))), //
				allOf(hasProperty(QUALIFIER, equalTo("documents")))
		));
	}

	@Test
	public void shouldFindMediaFoldersByQualifier()
	{
		final PageableData pageableData = new PageableData();
		pageableData.setCurrentPage(0);
		pageableData.setPageSize(2);

		final SearchResult<MediaFolderModel> folders = cmsMediaFolderDao.findMediaFolders("images", pageableData);

		assertEquals(2, folders.getCount());
		assertEquals(4, folders.getTotalCount());

		assertThat(folders.getResult(), hasItems( //
				allOf(hasProperty(QUALIFIER, equalTo("images"))), //
				allOf(hasProperty(QUALIFIER, equalTo("images2")))
		));
	}
}
