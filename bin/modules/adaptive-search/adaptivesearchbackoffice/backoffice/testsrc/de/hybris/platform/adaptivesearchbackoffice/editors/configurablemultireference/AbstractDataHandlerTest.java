/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.adaptivesearchbackoffice.editors.configurablemultireference;

import de.hybris.platform.adaptivesearch.data.AsConfigurationHolder;
import de.hybris.platform.adaptivesearch.data.AsSearchProfileResult;
import de.hybris.platform.adaptivesearch.data.AsSearchResultData;
import de.hybris.platform.adaptivesearch.services.AsConfigurationService;
import de.hybris.platform.adaptivesearchbackoffice.data.SearchContextData;
import de.hybris.platform.adaptivesearchbackoffice.data.SearchResultData;

import java.util.ArrayList;
import java.util.Locale;

import org.junit.Before;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


public abstract class AbstractDataHandlerTest
{
	protected static final String LANGUAGE = "en";
	protected static final Locale LOCALE = new Locale("en");

	protected static final String PRIORITY_ATTRIBUTE = "priority";

	protected static final String UID_1 = "uid1";
	protected static final String CODE_1 = "code1";
	protected static final String NAME_1 = "name1";
	protected static final Integer PRIORITY_1 = Integer.valueOf(1);

	protected static final String UID_2 = "uid2";
	protected static final String CODE_2 = "code2";
	protected static final String NAME_2 = "name2";
	protected static final Integer PRIORITY_2 = Integer.valueOf(2);

	@Mock
	private AsConfigurationService asConfigurationService;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		Mockito.when(asConfigurationService.isValid(Mockito.any())).thenReturn(true);
	}

	protected <T, R> AsConfigurationHolder<T, R> createConfigurationHolder(final T configuration)
	{
		final AsConfigurationHolder<T, R> configurationHolder = new AsConfigurationHolder<>();
		configurationHolder.setConfiguration(configuration);

		return configurationHolder;
	}

	protected SearchResultData createSearchResult()
	{

		final AsSearchProfileResult searchProfileResult = new AsSearchProfileResult();
		searchProfileResult.setBoostRules(new ArrayList<>());

		final AsSearchResultData asSearchResult = new AsSearchResultData();
		asSearchResult.setSearchProfileResult(searchProfileResult);

		final SearchResultData searchResult = new SearchResultData();
		searchResult.setAsSearchResult(asSearchResult);

		final SearchContextData searchContextData = new SearchContextData();
		searchContextData.setLanguage(LANGUAGE);
		searchResult.setSearchContext(searchContextData);

		return searchResult;
	}
}
