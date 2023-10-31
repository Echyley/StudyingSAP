/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.types.service.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cmsfacades.data.ComponentTypeData;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.util.collections.Sets;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static java.util.Map.entry;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@UnitTest
@RunWith(MockitoJUnitRunner.Silent.class)
public class DefaultComponentTypeMatchingServiceTest {
    private final static Map<String, String> NAME_WITH_LOCALE = Map.ofEntries(
            entry("en", "test1"),
            entry("zh", "test2"),
            entry("ja", "test3")
    );

    @InjectMocks
    private DefaultComponentTypeMatchingService service;

    @Mock
    private CommonI18NService commonI18NService;

    @Mock
    private LanguageModel esLanguageModel;

    @Mock
    private ComponentTypeData componentType1;

    @Before
    public void setUp() {
        when(componentType1.getNameWithLocale()).thenReturn(NAME_WITH_LOCALE);
    }

    @Test
    public void shouldMapNameWithLangIsoCodeName() throws Exception
    {
        // WHEN
        service.mapNameForComponentType(componentType1, "en");

        // THEN
        verify(componentType1).setName("test1");
    }

    @Test
    public void shouldMapNameWithDefaultLangNameWhenLangIsoCodeEmpty() throws Exception
    {
        // WHEN
        service.mapNameForComponentType(componentType1, "");

        // THEN
        verify(componentType1).setName("test1");
    }

    @Test
    public void shouldMapNameByLangIsoCodeWithFallBack1() throws Exception
    {
        // GIVEN
        when(commonI18NService.getLanguage("es")).thenReturn(esLanguageModel);
        List<LanguageModel> fallbackLanguages = new LinkedList<>();
        LanguageModel jaLanguage = new LanguageModel("ja");
        fallbackLanguages.add(jaLanguage);
        when(esLanguageModel.getFallbackLanguages()).thenReturn(fallbackLanguages);

        // WHEN
        service.mapNameForComponentType(componentType1, "es");

        // WHEN
        verify(componentType1).setName("test3");
    }

    @Test
    public void shouldMapNameByLangIsoCodeWithFallBack2() throws Exception
    {
        // GIVEN
        when(commonI18NService.getLanguage("es")).thenReturn(esLanguageModel);
        List<LanguageModel> fallbackLanguages = new LinkedList<>();
        LanguageModel deLanguage = new LanguageModel("de");
        LanguageModel frLanguage = new LanguageModel("fr");
        fallbackLanguages.add(deLanguage);
        fallbackLanguages.add(frLanguage);

        when(esLanguageModel.getFallbackLanguages()).thenReturn(fallbackLanguages);

        // WHEN
        service.mapNameForComponentType(componentType1, "es");

        // THEN
        verify(componentType1).setName("test1");
    }

    @Test
    public void shouldMapNameByLangIsoCodeWithFallBack3() throws Exception
    {
        // GIVEN
        when(commonI18NService.getLanguage("en_US")).thenThrow(new UnknownIdentifierException("LanguageModel en_US not Found."));

        // WHEN
        service.mapNameForComponentType(componentType1, "en_US");

        // THEN
        verify(componentType1).setName("test1");
    }

    @Test
    public void testTypeRestrictionMatchingCriteria() throws Exception
    {
        // GIVEN
        Set<String> typeRestrictionsForPage = Sets.newSet("SomeValidCode", "OtherValidCode");
        when(componentType1.getCode()).thenReturn("SomeValidCode");

        // WHEN
        Boolean isTypeMatchingCriteria = service.isTypeMatchingCriteria(componentType1, typeRestrictionsForPage,"");

        // THEN
        assertTrue(isTypeMatchingCriteria);

        // GIVEN
        when(componentType1.getCode()).thenReturn("SomeInValidCode");

        // WHEN
        isTypeMatchingCriteria = service.isTypeMatchingCriteria(componentType1, typeRestrictionsForPage,"");

        // THEN
        assertFalse(isTypeMatchingCriteria);
    }

    @Test
    public void shouldReturnTrueNameCodeMatchingCriteria() throws Exception
    {
        // GIVEN
        Set<String> typeRestrictionsForPage = Sets.newSet("SomeValidCode", "OtherValidCode");
        when(componentType1.getCode()).thenReturn("SomeValidCode");
        when(componentType1.getName()).thenReturn("SimpleBannerTest");

        // WHEN
        Boolean isTypeMatchingCriteria = service.isTypeMatchingCriteria(componentType1, typeRestrictionsForPage,"simple");

        // THEN
        assertTrue(isTypeMatchingCriteria);

        when(componentType1.getCode()).thenReturn("SomeValidCode");

        // WHEN
        isTypeMatchingCriteria = service.isTypeMatchingCriteria(componentType1, typeRestrictionsForPage,"valid");

        // THEN
        assertTrue(isTypeMatchingCriteria);
    }

    @Test
    public void shouldReturnFalseNameCodeNotMatchingCriteria() throws Exception
    {
        // GIVEN
        Set<String> typeRestrictionsForPage = Sets.newSet("SomeValidCode", "OtherValidCode");
        when(componentType1.getCode()).thenReturn("SomeValidCode");
        when(componentType1.getName()).thenReturn("SimpleBannerTest");

        // WHEN
        Boolean isTypeMatchingCriteria = service.isTypeMatchingCriteria(componentType1, typeRestrictionsForPage,"map");

        // THEN
        assertFalse(isTypeMatchingCriteria);
    }
}
