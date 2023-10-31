/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.languages.impl;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.storesession.StoreSessionFacade;
import de.hybris.platform.commercefacades.storesession.data.LanguageData;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.site.BaseSiteService;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.Silent.class)
public class DefaultLanguageFacadeTest
{
	private static final String FRENCH = "FR";
	private static final String ENGLISH = "EN";
	private static final String GERMAN = "DE";
	private static final String GERMAN_SWISS = "de_CH";

	@InjectMocks
	private DefaultLanguageFacade languageFacade;

	@Mock
	private StoreSessionFacade storeSessionFacade;
	@Mock
	private BaseSiteService baseSiteService;

	private final LanguageData languageEN = createLanguageData(ENGLISH);
	private final LanguageData languageDE = createLanguageData(GERMAN);
	private final LanguageData languageDE_CH = createLanguageData(GERMAN_SWISS);
	private final LanguageData languageFR = createLanguageData(FRENCH);

	@Mock
	private BaseSiteModel baseSite;
	@Mock
	private LanguageModel languageModelDE;

	@Before
	public void setUp()
	{
		final List<LanguageData> languages = Arrays.asList(languageFR, languageEN, languageDE, languageDE_CH);

		when(languageModelDE.getIsocode()).thenReturn(GERMAN);

		when(storeSessionFacade.getAllLanguages()).thenReturn(languages);
		when(storeSessionFacade.getDefaultLanguage()).thenReturn(languageEN);
	}

	protected LanguageData createLanguageData(final String isocode)
	{
		final LanguageData data = new LanguageData();
		data.setIsocode(isocode);
		return data;
	}

	@Test
	public void getLanguagesDefaultEnglishDefinedInBaseStore()
	{
		// GIVEN default language is not defined in base site
		when(storeSessionFacade.getDefaultLanguage()).thenReturn(languageEN);
		when(baseSiteService.getCurrentBaseSite()).thenReturn(baseSite);

		// WHEN
		final List<LanguageData> languagesFound = languageFacade.getLanguages();

		// THEN default language defined in base store is returned
		assertEquals(ENGLISH, languagesFound.get(0).getIsocode());
		assertTrue(languagesFound.get(0).isRequired());
		assertEquals(FRENCH, languagesFound.get(1).getIsocode());
		assertFalse(languagesFound.get(1).isRequired());
	}

	@Test
	public void getLanguagesDefaultGermanDefinedInBaseSite()
	{
		// GIVEN default language is defined in base site
		when(baseSiteService.getCurrentBaseSite()).thenReturn(baseSite);
		when(baseSite.getDefaultLanguage()).thenReturn(languageModelDE);

		// WHEN
		final List<LanguageData> languagesFound = languageFacade.getLanguages();

		// THEN
		assertEquals(GERMAN, languagesFound.get(0).getIsocode());
		assertTrue(languagesFound.get(0).isRequired());
		assertEquals(FRENCH, languagesFound.get(1).getIsocode());
		assertFalse(languagesFound.get(1).isRequired());
	}

	@Test
	public void getLanguagesWithUnderscoreDelimiter()
	{
		// GIVEN
		when(storeSessionFacade.getDefaultLanguage()).thenReturn(languageDE_CH);

		// WHEN
		final List<LanguageData> languagesFound = languageFacade.getLanguages();

		// THEN
		assertThat(languagesFound.get(0).getIsocode(), is(GERMAN_SWISS));
	}

	@Test
	public void getLanguagesWithNoDefaultLanguage()
	{
		// GIVEN no default language defined in base site and base store
		when(baseSiteService.getCurrentBaseSite()).thenReturn(baseSite);
		when(storeSessionFacade.getDefaultLanguage()).thenReturn(null);

		// WHEN
		final List<LanguageData> languagesFound = languageFacade.getLanguages();

		// THEN
		assertThat(languagesFound.get(0).getIsocode(), is(FRENCH));
	}

}
