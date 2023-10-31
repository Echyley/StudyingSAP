/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.platform.uicommon.implementation;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.GenericQuery;
import de.hybris.platform.genericsearch.GenericSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.ArrayList;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.hybris.backoffice.model.ThemeModel;

import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultThemeDaoTest {
	@InjectMocks
	private DefaultThemeDao themeService;

	@Mock
	private GenericSearchService genericSearchService;
	@Mock
	private SearchResult searchResult ;

	@Test
	public void shouldReturnThemeWhenFindTheme()
	{
		final var resultList = new ArrayList<ThemeModel>();
		resultList.add(new ThemeModel());
		when(genericSearchService.search(Mockito.any(GenericQuery.class))).thenReturn(searchResult);
		when(searchResult.getResult()).thenReturn(resultList);

		final var actual = themeService.findByCode("test");
		assertThat(actual).isPresent();
	}

	@Test
	public void shouldReturnEmptyWhenNoThemeFound()
	{
		final var resultList = new ArrayList<ThemeModel>();
		when(genericSearchService.search(Mockito.any(GenericQuery.class))).thenReturn(searchResult);
		when(searchResult.getResult()).thenReturn(resultList);

		final var actual = themeService.findByCode("test");
		assertThat(actual).isNotPresent();
	}
}
