/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.services.event.util.impl;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.sap.productconfig.services.model.ProductConfigurationPersistenceCleanUpCronJobModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.search.impl.SearchResultImpl;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * Unit test for {@link ProductConfigEventListenerUtil}
 */
@UnitTest
public class ProductConfigEventListenerUtilTest
{
	private ProductConfigEventListenerUtil classUnderTest;

	@Mock
	private FlexibleSearchService flexibleSearchService;

	private BaseSiteModel testBaseSite;
	private ProductConfigurationPersistenceCleanUpCronJobModel jobModel;


	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new ProductConfigEventListenerUtil();
		classUnderTest.setFlexibleSearchService(flexibleSearchService);

		testBaseSite = new BaseSiteModel();
		jobModel = new ProductConfigurationPersistenceCleanUpCronJobModel();
		jobModel.setBaseSite(testBaseSite);
		final SearchResult<Object> jobModelResullt = new SearchResultImpl<>(Collections.singletonList(jobModel), 1, 1, 1);
		given(flexibleSearchService.search(ProductConfigEventListenerUtil.SELECT_CRON_JOB_MODEL)).willReturn(jobModelResullt);
	}


	@Test
	public void testGetBaseSiteFromCronJobNoResult()
	{
		final SearchResult<Object> jobModelResullt = new SearchResultImpl<>(Collections.emptyList(), 0, 0, 0);
		given(flexibleSearchService.search(ProductConfigEventListenerUtil.SELECT_CRON_JOB_MODEL)).willReturn(jobModelResullt);
		assertNull(classUnderTest.getBaseSiteFromCronJob());
	}

	@Test
	public void testGetBaseSiteFromCronJobNoBaseSite()
	{
		jobModel.setBaseSite(null);
		assertNull(classUnderTest.getBaseSiteFromCronJob());
	}


	@Test
	public void testGetBaseSiteFromCronJob()
	{
		assertSame(testBaseSite, classUnderTest.getBaseSiteFromCronJob());
	}
}
