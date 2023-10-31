/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.searchservices.populators;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.searchservices.search.data.SnSearchHit;
import de.hybris.platform.searchservices.search.data.SnSearchResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.hybris.backoffice.search.daos.ItemModelSearchDAO;


@RunWith(MockitoJUnitRunner.class)
public class SearchResultItemModelsPopulatorTest
{

	@Mock
	private ItemModelSearchDAO itemModelSearchDAO;

	@InjectMocks
	private SearchResultItemModelsPopulator searchResultItemModelsPopulator;

	private final SnSearchResultSourceData snSearchResultSourceData = mock(SnSearchResultSourceData.class);
	private final SnSearchResultConverterData snSearchResultConverterData = mock(SnSearchResultConverterData.class);
	private static final String TYPE_CODE = "typeCode";
	private static final String HIT_PK = "123456";

	@Test
	public void shouldSetItemModelsAsNullWhenGotNoSearchHits()
	{
		final SnSearchResult snSearchResult = mock(SnSearchResult.class);
		when(snSearchResultSourceData.getSnSearchResult()).thenReturn(snSearchResult);
		when(snSearchResult.getSearchHits()).thenReturn(new ArrayList<>());

		searchResultItemModelsPopulator.populate(snSearchResultSourceData, snSearchResultConverterData);
		verify(snSearchResultConverterData).setItemModels(Collections.emptyList());
	}

	@Test
	public void shouldSetItemModelsWhenGotSearchHits()
	{
		final SnSearchResult snSearchResult = mock(SnSearchResult.class);
		final SnSearchHit searchHit = mock(SnSearchHit.class);
		final List<SnSearchHit> searchHits = Arrays.asList(searchHit);
		final List<Long> resultPKs = Arrays.asList(Long.valueOf(HIT_PK));
		final ItemModel itemModel = mock(ItemModel.class);
		final List<ItemModel> itemModels = Arrays.asList(itemModel);

		when(searchHit.getId()).thenReturn(HIT_PK);
		when(snSearchResultSourceData.getSnSearchResult()).thenReturn(snSearchResult);
		when(snSearchResult.getSearchHits()).thenReturn(searchHits);
		when(snSearchResultSourceData.getTypeCode()).thenReturn(TYPE_CODE);
		when(itemModelSearchDAO.findAll(TYPE_CODE, resultPKs)).thenReturn(itemModels);

		searchResultItemModelsPopulator.populate(snSearchResultSourceData, snSearchResultConverterData);
		verify(snSearchResultConverterData).setItemModels(itemModels);
	}
}
