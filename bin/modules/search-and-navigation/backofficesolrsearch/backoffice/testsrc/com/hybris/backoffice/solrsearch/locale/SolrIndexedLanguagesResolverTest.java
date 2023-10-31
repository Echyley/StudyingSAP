/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.solrsearch.locale;

import com.hybris.backoffice.search.services.BackofficeFacetSearchConfigService;
import com.hybris.backoffice.widgets.fulltextsearch.FullTextSearchStrategy;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class SolrIndexedLanguagesResolverTest
{
    private static final String ISO_CODE = "ISO_CODE";
    private static final String TYPE_CODE = "TYPE_CODE";
    private static final String SECOND_TYPE_CODE = "TYPE_CODE";


    @InjectMocks
    private SolrIndexedLanguagesResolver testSubject;

    @Mock
    private BackofficeFacetSearchConfigService backofficeFacetSearchConfigService;
    @Mock
    private FullTextSearchStrategy fullTextSearchStrategy;

    @Test
    public void shouldInformThatLanguageIsNotIndexedIfNoConfigurationExists()
    {
        // given
        when(backofficeFacetSearchConfigService.getAllMappedTypes()).thenReturn(Collections.emptyList());

        // when
        final boolean isIndexed = testSubject.isIndexed(ISO_CODE);

        // then
        assertThat(isIndexed).isTrue();
    }

    @Test
    public void shouldInformThatLanguageIsNotIndexedIfOneConfigurationExistsAndLanguageIsNotAvailable()
    {
        // given
        final ComposedTypeModel model = mock(ComposedTypeModel.class);
        final Collection<ComposedTypeModel> models = Collections.singletonList(model);
        when(backofficeFacetSearchConfigService.getAllMappedTypes()).thenReturn(models);


        // when
        final boolean isIndexed = testSubject.isIndexed(ISO_CODE);

        // then
        assertThat(isIndexed).isFalse();
    }

    @Test
    public void shouldInformThatLanguageIsIndexedIfOneConfigurationExistsAndLanguageIsAvailable()
    {
        // given
        final ComposedTypeModel model = mock(ComposedTypeModel.class);
        when(model.getCode()).thenReturn(TYPE_CODE);
        final Collection<ComposedTypeModel> models = Collections.singletonList(model);
        when(backofficeFacetSearchConfigService.getAllMappedTypes()).thenReturn(models);

        final List<String> languages = Collections.singletonList(ISO_CODE);
        when(fullTextSearchStrategy.getAvailableLanguages(TYPE_CODE)).thenReturn(languages);

        // when
        final boolean isIndexed = testSubject.isIndexed(ISO_CODE);

        // then
        assertThat(isIndexed).isTrue();
    }

    @Test
    public void shouldInformThatLanguageIsNotIndexedIfTwoConfigurationsExistAndLanguageIsNotAvailableInOneOfThem()
    {
        // given
        final ComposedTypeModel firstModel = mock(ComposedTypeModel.class);
        when(firstModel.getCode()).thenReturn(TYPE_CODE);
        final ComposedTypeModel secondModel = mock(ComposedTypeModel.class);
        final Collection<ComposedTypeModel> models = List.of(firstModel, secondModel);
        when(backofficeFacetSearchConfigService.getAllMappedTypes()).thenReturn(models);

        final List<String> languagesForFirstTypeCode = Collections.singletonList(ISO_CODE);
        when(fullTextSearchStrategy.getAvailableLanguages(TYPE_CODE)).thenReturn(languagesForFirstTypeCode);
        final List<String> languagesForSecondTypeCode = Collections.emptyList();
        when(fullTextSearchStrategy.getAvailableLanguages(SECOND_TYPE_CODE)).thenReturn(languagesForSecondTypeCode);

        // when
        final boolean isIndexed = testSubject.isIndexed(ISO_CODE);

        // then
        assertThat(isIndexed).isFalse();
    }

    @Test
    public void shouldInformThatLanguageIsIndexedIfTwoConfigurationsExistAndLanguageIsAvailableInBothOfThem()
    {
        // given
        final ComposedTypeModel firstModel = mock(ComposedTypeModel.class);
        when(firstModel.getCode()).thenReturn(TYPE_CODE);
        final ComposedTypeModel secondModel = mock(ComposedTypeModel.class);
        when(secondModel.getCode()).thenReturn(SECOND_TYPE_CODE);
        final Collection<ComposedTypeModel> models = List.of(firstModel, secondModel);
        when(backofficeFacetSearchConfigService.getAllMappedTypes()).thenReturn(models);

        final List<String> languagesForFirstTypeCode = Collections.singletonList(ISO_CODE);
        when(fullTextSearchStrategy.getAvailableLanguages(TYPE_CODE)).thenReturn(languagesForFirstTypeCode);
        final List<String> languagesForSecondTypeCode = Collections.singletonList(ISO_CODE);
        when(fullTextSearchStrategy.getAvailableLanguages(SECOND_TYPE_CODE)).thenReturn(languagesForSecondTypeCode);

        // when
        final boolean isIndexed = testSubject.isIndexed(ISO_CODE);

        // then
        assertThat(isIndexed).isTrue();
    }
}
