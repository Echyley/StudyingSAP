/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmssmarteditwebservices.catalogs.populator;

import com.google.common.collect.Lists;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.contents.ContentCatalogModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cmsfacades.common.populator.impl.DefaultLocalizedPopulator;
import de.hybris.platform.cmsfacades.data.CatalogHierarchyData;
import de.hybris.platform.cmsfacades.languages.LanguageFacade;
import de.hybris.platform.cmsfacades.users.services.CMSUserService;
import de.hybris.platform.cmssmarteditwebservices.catalogs.ContentCatalogService;
import de.hybris.platform.commercefacades.storesession.data.LanguageData;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.HashSet;

import static java.util.Locale.ENGLISH;
import static java.util.Locale.FRENCH;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.Silent.class)
public class CatalogHierarchyModelToDataPopulatorTest
{

    private static final String CATALOG_NAME_EN = "Test Catalog - EN";
    private static final String CATALOG_NAME_FR = "Test Catalog - FR";
    private static final String CATALOG_ID = "test-catalog-id";
    private static final String SITE_1_NAME_EN = "Test Site 1 - EN";
    private static final String SITE_1_NAME_FR = "Test Site 1 - FR";
    private static final String SITE_1_ID = "site-1-id";
    private static final String SITE_2_NAME_EN = "Test Site 2 - EN";
    private static final String SITE_2_NAME_FR = "Test Site 2 - FR";
    private static final String SITE_2_ID = "site-2-id";
    private static final String FR = "fr";
    private static final String EN = "en";

    @InjectMocks
    CatalogHierarchyModelToDataPopulator populator;

    @InjectMocks
    private final DefaultLocalizedPopulator localizedPopulator = new DefaultLocalizedPopulator();

    @Mock
    private CMSUserService cmsUserService;
    @Mock
    private LanguageFacade languageFacade;
    @Mock
    private CommonI18NService commonI18NService;
    @Mock
    private ContentCatalogModel catalogModel;
    @Mock
    private ContentCatalogService contentCatalogService;

    private CatalogHierarchyData catalogHierarchyData;

    @Before
    public void setUp()
    {

        catalogHierarchyData = new CatalogHierarchyData();
        populator.setLocalizedPopulator(localizedPopulator);

        when(catalogModel.getId()).thenReturn(CATALOG_ID);
        when(catalogModel.getName(ENGLISH)).thenReturn(CATALOG_NAME_EN);
        when(catalogModel.getName(FRENCH)).thenReturn(CATALOG_NAME_FR);

        final LanguageData languageEN = new LanguageData();
        languageEN.setIsocode(EN);
        final LanguageData languageFR = new LanguageData();
        languageFR.setIsocode(FR);
        when(languageFacade.getLanguages()).thenReturn(Lists.newArrayList(languageEN, languageFR));
        when(commonI18NService.getLocaleForIsoCode(EN)).thenReturn(ENGLISH);
        when(commonI18NService.getLocaleForIsoCode(FR)).thenReturn(FRENCH);

        when(contentCatalogService.getSites(catalogModel)).thenReturn(Arrays.asList());

        // Language Permissions
        when(cmsUserService.getReadableLanguagesForCurrentUser()).thenReturn(new HashSet<>(Arrays.asList(EN, FR)));
    }

    @Test
    public void shouldPopulateAllFields()
    {
        populator.populate(catalogModel, catalogHierarchyData);

        assertThat(catalogHierarchyData.getCatalogId(), equalTo(CATALOG_ID));
        assertThat(catalogHierarchyData.getCatalogName().get(EN), equalTo(CATALOG_NAME_EN));
        assertThat(catalogHierarchyData.getCatalogName().get(FR), equalTo(CATALOG_NAME_FR));

    }

}
