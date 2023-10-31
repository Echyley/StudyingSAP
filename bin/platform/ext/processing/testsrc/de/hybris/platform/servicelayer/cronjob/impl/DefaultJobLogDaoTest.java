/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.servicelayer.cronjob.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.cronjob.model.JobLogModel;
import de.hybris.platform.servicelayer.cronjob.JobLogDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import junit.framework.Assert;

@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class DefaultJobLogDaoTest
{

	@Mock
	private FlexibleSearchService flexibleSearchService;

	@Mock
	private SearchResult searchResult;

	private JobLogDao dao;

	private final String expectedQueryAsc = "GET {" + JobLogModel._TYPECODE + "} WHERE {"
			+ JobLogModel.CRONJOB + "}=?" + JobLogModel.CRONJOB + " ORDER BY {" + JobLogModel.CREATIONTIME + "} asc";

	private final String expectedQueryDsc = "GET {" + JobLogModel._TYPECODE + "} WHERE {"
			+ JobLogModel.CRONJOB + "}=?" + JobLogModel.CRONJOB + " ORDER BY {" + JobLogModel.CREATIONTIME + "} desc";

	@Before
	public void prepare()
	{
		dao = Mockito.spy(new TestDefaultJobLogDao(flexibleSearchService));
	}

	@Test
	public void testQueryBuildCustomCountAsc()
	{

		final CronJobModel model = Mockito.mock(CronJobModel.class);

		Mockito.when(flexibleSearchService.search(Mockito.any(FlexibleSearchQuery.class))).thenReturn(searchResult);

		dao.findJobLogs(model, 100, true);//count 100, asc

		final ArgumentMatcher<FlexibleSearchQuery> matcher = new ArgumentMatcher<FlexibleSearchQuery>()
		{
			@Override
			public boolean matches(final FlexibleSearchQuery query)
			{
				Assert.assertEquals(100, query.getCount());
				Assert.assertEquals(false, query.isNeedTotal());
				Assert.assertEquals(expectedQueryAsc, query.getQuery());
				return true;
			}
		};
		Mockito.verify(flexibleSearchService).search(Mockito.argThat(matcher));

	}

	@Test
	public void testQueryBuildNoCountDesc()
	{

		final CronJobModel model = Mockito.mock(CronJobModel.class);

		Mockito.when(flexibleSearchService.search(Mockito.any(FlexibleSearchQuery.class))).thenReturn(searchResult);
		dao.findJobLogs(model, -1, false);//count -1, desc

		final ArgumentMatcher<FlexibleSearchQuery> matcher = new ArgumentMatcher<FlexibleSearchQuery>()
		{
			@Override
			public boolean matches(final FlexibleSearchQuery query)
			{
				Assert.assertEquals(-1, query.getCount());
				Assert.assertEquals(true, query.isNeedTotal());
				Assert.assertEquals(expectedQueryDsc, query.getQuery());

				return true;
			}
		};
		Mockito.verify(flexibleSearchService).search(Mockito.argThat(matcher));

	}


	public static class TestDefaultJobLogDao extends DefaultJobLogDao
	{
		private final FlexibleSearchService mock;

		public TestDefaultJobLogDao(final FlexibleSearchService mock)
		{
			super();
			this.mock = mock;
		}

		@Override
		protected FlexibleSearchService getFlexibleSearchService()
		{
			return mock;
		}
	}
}
