/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.solrfacetsearch.config.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.solrfacetsearch.model.config.SolrIndexedPropertyModel;

import org.fest.assertions.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SolrIndexedPropertyLoadInterceptorTest
{

	private SolrIndexedPropertyLoadInterceptor loadInterceptor;

	private final static String DEFAULT_PROVIDER = "myDefaultProvider";

	@Mock
	private InterceptorContext cntx;

	@Before
	public void setUp()
	{
		loadInterceptor = new SolrIndexedPropertyLoadInterceptor();
		loadInterceptor.setDefaultFacetSortProvider(DEFAULT_PROVIDER);
	}

	@Test
	public void testProviderSet() throws InterceptorException
	{
		final SolrIndexedPropertyModel indexedProprty = new SolrIndexedPropertyModel();
		indexedProprty.setCustomFacetSortProvider("specialProvider");

		loadInterceptor.onLoad(indexedProprty, cntx);
		Assertions.assertThat(indexedProprty.getCustomFacetSortProvider()).isEqualTo("specialProvider");
	}

	@Test
	public void testProviderNoSet() throws InterceptorException
	{
		final SolrIndexedPropertyModel indexedProprty = new SolrIndexedPropertyModel();

		loadInterceptor.onLoad(indexedProprty, cntx);
		Assertions.assertThat(indexedProprty.getCustomFacetSortProvider()).isEqualTo(DEFAULT_PROVIDER);
	}
}
