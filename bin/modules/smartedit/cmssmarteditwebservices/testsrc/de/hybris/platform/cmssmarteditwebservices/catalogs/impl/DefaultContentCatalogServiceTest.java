/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmssmarteditwebservices.catalogs.impl;

import static java.util.Locale.ENGLISH;
import static java.util.Locale.FRENCH;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.contents.ContentCatalogModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cmsfacades.common.populator.impl.DefaultLocalizedPopulator;
import de.hybris.platform.cmsfacades.data.SiteData;
import de.hybris.platform.cmsfacades.languages.LanguageFacade;
import de.hybris.platform.cmsfacades.users.services.CMSUserService;
import de.hybris.platform.commercefacades.storesession.data.LanguageData;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.google.common.collect.Lists;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.Silent.class)
@UnitTest
public class DefaultContentCatalogServiceTest
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
	private DefaultContentCatalogService contentCatalogService;

	@InjectMocks
	private final DefaultLocalizedPopulator localizedPopulator = new DefaultLocalizedPopulator();

	@Mock
	private CMSUserService cmsUserService;
	@Mock
	private LanguageFacade languageFacade;
	@Mock
	private CommonI18NService commonI18NService;
	@Mock
	private CMSSiteModel site1;
	@Mock
	private CMSSiteModel site2;
	@Mock
	private ContentCatalogModel catalogModel;

	@Before
	public void setUp()
	{
		contentCatalogService.setLocalizedPopulator(localizedPopulator);

		final LanguageData languageEN = new LanguageData();
		languageEN.setIsocode(EN);
		final LanguageData languageFR = new LanguageData();
		languageFR.setIsocode(FR);
		when(languageFacade.getLanguages()).thenReturn(Lists.newArrayList(languageEN, languageFR));
		when(commonI18NService.getLocaleForIsoCode(EN)).thenReturn(ENGLISH);
		when(commonI18NService.getLocaleForIsoCode(FR)).thenReturn(FRENCH);

		when(site1.getUid()).thenReturn(SITE_1_ID);
		when(site2.getUid()).thenReturn(SITE_2_ID);
		when(site1.getName(ENGLISH)).thenReturn(SITE_1_NAME_EN);
		when(site1.getName(FRENCH)).thenReturn(SITE_1_NAME_FR);
		when(site2.getName(ENGLISH)).thenReturn(SITE_2_NAME_EN);
		when(site2.getName(FRENCH)).thenReturn(SITE_2_NAME_FR);
		when(catalogModel.getCmsSites()).thenReturn(Arrays.asList(site1, site2));

		// Language Permissions
		when(cmsUserService.getReadableLanguagesForCurrentUser()).thenReturn(new HashSet<>(Arrays.asList(EN, FR)));
	}

	@Test
	public void shouldReturnTwoCMSSites()
	{
		List<SiteData> sites =  contentCatalogService.getSites(catalogModel);

		assertThat(sites, hasSize(2));
		assertThat(sites.get(0).getUid(), equalTo(SITE_1_ID));
		assertThat(sites.get(0).getName().get(EN), equalTo(SITE_1_NAME_EN));
		assertThat(sites.get(0).getName().get(FR), equalTo(SITE_1_NAME_FR));
		assertThat(sites.get(1).getUid(), equalTo(SITE_2_ID));
		assertThat(sites.get(1).getName().get(EN), equalTo(SITE_2_NAME_EN));
		assertThat(sites.get(1).getName().get(FR), equalTo(SITE_2_NAME_FR));
	}
}
