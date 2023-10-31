/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.solrsearch.dataaccess;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.hybris.backoffice.search.services.BackofficeFacetSearchConfigService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.config.exceptions.FacetConfigServiceException;
import de.hybris.platform.solrfacetsearch.model.config.SolrFacetSearchConfigModel;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SolrSearchStrategyTest
{

	@Mock
	private BackofficeFacetSearchConfigService backofficeFacetSearchConfigService;

	@InjectMocks
	private SolrSearchStrategy solrSearchStrategy;

	final String testTypeCode = "typeCode";
	final String testFieldName = "testFieldName";
	final String testType = "java.lang.String";


	@Before
	public void setUp()
	{
		final Map<String, String> typeMappings = new HashMap<>();
		typeMappings.put("text", "java.lang.String");
		typeMappings.put("sortabletext", "java.lang.String");
		typeMappings.put("string", "java.lang.String");

		solrSearchStrategy.setTypeMappings(typeMappings);
	}

	@Test
	public void checkGetFieldType() throws FacetConfigServiceException
	{
		final FacetSearchConfig facetSearchConfig = createFacetSearchConfigData();

		Mockito.when(backofficeFacetSearchConfigService.getFacetSearchConfig(testTypeCode)).thenReturn(facetSearchConfig);

		assertThat(solrSearchStrategy.getFieldType(testTypeCode, testFieldName)).isEqualTo(testType);
		assertThat(solrSearchStrategy.isLocalized(testTypeCode, testFieldName)).isEqualTo(true);
	}

	@Test
	public void shouldGetOnlyIndexedLanguages() throws FacetConfigServiceException
	{
		final LanguageModel languageEnglish = new LanguageModel(Locale.ENGLISH.getLanguage());
		final LanguageModel languageGerman = new LanguageModel(Locale.GERMAN.getLanguage());
		final SolrFacetSearchConfigModel solrFacetSearchConfigModel = mock(SolrFacetSearchConfigModel.class);

		when(backofficeFacetSearchConfigService.getFacetSearchConfigModel(testTypeCode)).thenReturn(solrFacetSearchConfigModel);
		when(solrFacetSearchConfigModel.getLanguages()).thenReturn(List.of(languageEnglish, languageGerman));

		final Collection<String> availableLanguages = solrSearchStrategy.getAvailableLanguages(testTypeCode);

		assertThat(availableLanguages).containsOnly(Locale.ENGLISH.getLanguage(), Locale.GERMAN.getLanguage());
	}

	private FacetSearchConfig createFacetSearchConfigData()
	{
		final FacetSearchConfig facetConfig = new FacetSearchConfig();

		final IndexConfig indexConfig = mock(IndexConfig.class);
		final IndexedType indexedType = Mockito.mock(IndexedType.class);
		final ComposedTypeModel composedTypeModel = mock(ComposedTypeModel.class);

		when(indexedType.getComposedType()).thenReturn(composedTypeModel);
		when(indexedType.getComposedType().getCode()).thenReturn(testTypeCode);

		final Map<String, IndexedProperty> indexedPropertyMap = mock(Map.class);
		when(indexedPropertyMap.get(anyString())).thenAnswer(invocationOnMock -> {
			final String name = (String) invocationOnMock.getArguments()[0];
			final IndexedProperty indexedProperty = new IndexedProperty();
			indexedProperty.setName(name);
			indexedProperty.setBackofficeDisplayName(name);
			indexedProperty.setLocalized(true);
			indexedProperty.setType("string"); //key in the map "typeMappings"
			return indexedProperty;
		});
		when(indexedType.getIndexedProperties()).thenReturn(indexedPropertyMap);

		final Map<String, IndexedType> indexedTypeMap = new HashMap<>();
		indexedTypeMap.put(testTypeCode, indexedType);
		when(indexConfig.getIndexedTypes()).thenReturn(indexedTypeMap);

		facetConfig.setName("facetConfigName");
		facetConfig.setIndexConfig(indexConfig);

		return facetConfig;
	}

}
