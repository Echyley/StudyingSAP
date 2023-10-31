/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.search.dataaccess.facades;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.Mock;
import org.mockito.InjectMocks;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collection;

import com.hybris.cockpitng.dataaccess.context.impl.DefaultContext;
import com.hybris.cockpitng.dataaccess.context.Context;
import com.hybris.cockpitng.search.data.SearchQueryData;
import com.hybris.cockpitng.search.data.pageable.Pageable;
import com.hybris.cockpitng.search.data.AutosuggestionQueryData;
import com.hybris.backoffice.search.services.BackofficeFacetSearchConfigService;
import com.hybris.cockpitng.dataaccess.facades.search.impl.FieldSearchFacadeStrategyRegistry;
import com.hybris.cockpitng.dataaccess.facades.search.FieldSearchFacadeStrategy;
import com.hybris.backoffice.widgets.advancedsearch.engine.AdvancedSearchQueryData;
import com.hybris.backoffice.widgets.advancedsearch.AdvancedSearchMode;
import com.hybris.backoffice.widgets.advancedsearch.engine.PageableWithFullTextDataCallback;
import com.hybris.cockpitng.widgets.collectionbrowser.CollectionBrowserController;

@RunWith(MockitoJUnitRunner.class)
public class DefaultFieldSearchFacadeStrategyTest
{
	static class DefaultFieldSearchFacadeStrategyImpl extends DefaultFieldSearchFacadeStrategy {
		public String getStrategyName() {
			return "flexible";
		}
		public Pageable<Collection> search(final SearchQueryData queryData) {
			return (Pageable) new ArrayList<Collection>();
		}

		public Map<String, Collection<String>> getAutosuggestionsForQuery(final AutosuggestionQueryData queryData, final Context context) {
			return new HashMap<>();
		}
	}

	@Mock
	private BackofficeFacetSearchConfigService facetSearchConfigService;

	@InjectMocks
	private DefaultFieldSearchFacadeStrategyImpl defaultFieldSearchFacadeStrategyImpl;

	 @Test
	 public void shouldReturnFalseWhenStrategyNotMatched()
	 {
	 	final String typeCode = "123";
	 	final Context context = new DefaultContext();
	 	context.addAttribute(FieldSearchFacadeStrategyRegistry.CONTEXT_ATTR_PREFERRED_STRATEGY_NAME, "solr");
	 	assertFalse(defaultFieldSearchFacadeStrategyImpl.canHandle(typeCode, context));
	 }

	 @Test
	 public void shouldInvokeIsValidSearchConfiguredForType()
	 {
	 	final String typeCode = "123";
	 	final Context context = new DefaultContext();
	 	context.addAttribute(FieldSearchFacadeStrategyRegistry.CONTEXT_ATTR_PREFERRED_STRATEGY_NAME, "flexible");
	 	assertFalse(defaultFieldSearchFacadeStrategyImpl.canHandle(typeCode, context));
	 	verify(facetSearchConfigService).isValidSearchConfiguredForType(typeCode);
	 }

	@Test
	public void shouldReturnFalseWhenStrategyNotSimple()
	{
		final String typeCode = "123";
		final Context context = new DefaultContext();
		final AdvancedSearchQueryData query = mock(AdvancedSearchQueryData.class);
		doReturn(AdvancedSearchMode.ADVANCED).when(query).getAdvancedSearchMode();
		context.addAttribute(FieldSearchFacadeStrategyRegistry.CONTEXT_ATTR_PREFERRED_STRATEGY_NAME, "flexible");
		context.addAttribute(FieldSearchFacadeStrategy.CONTEXT_ORIGINAL_QUERY, query);
		assertFalse(defaultFieldSearchFacadeStrategyImpl.canHandle(typeCode, context));
	}

	@Test
	public void shouldReturnTrueWhenPageable()
	{
		final String typeCode = "123";
		final Context context = new DefaultContext();
		final AdvancedSearchQueryData query = mock(AdvancedSearchQueryData.class);
		final PageableWithFullTextDataCallback modelPageable = mock(PageableWithFullTextDataCallback.class);
		final AbstractBackofficeSearchPageable abstractBackofficeSearchPageable = mock(AbstractBackofficeSearchPageable.class);
		doReturn(AdvancedSearchMode.SIMPLE).when(query).getAdvancedSearchMode();
		doReturn(abstractBackofficeSearchPageable).when(modelPageable).getPageable();
		doReturn(true).when(facetSearchConfigService).isValidSearchConfiguredForType(typeCode);
		context.addAttribute(FieldSearchFacadeStrategyRegistry.CONTEXT_ATTR_PREFERRED_STRATEGY_NAME, "flexible");
		context.addAttribute(FieldSearchFacadeStrategy.CONTEXT_ORIGINAL_QUERY, query);
		context.addAttribute(CollectionBrowserController.MODEL_PAGEABLE, modelPageable);
		assertTrue(defaultFieldSearchFacadeStrategyImpl.canHandle(typeCode, context));
	}

}
