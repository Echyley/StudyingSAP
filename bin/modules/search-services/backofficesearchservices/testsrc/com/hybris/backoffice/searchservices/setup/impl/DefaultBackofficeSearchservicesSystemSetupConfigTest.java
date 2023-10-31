/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.searchservices.setup.impl;

import com.hybris.backoffice.search.setup.impl.ConfigStringResolver;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class DefaultBackofficeSearchservicesSystemSetupConfigTest
{
	private static final String BACKOFFICE_SEARCH_SERVICES_NON_LOCALIZED_ROOTS_KEY = "backoffice.search.services.nonlocalized.files";
	private static final String BACKOFFICE_SEARCH_SERVICES_LOCALIZED_ROOTS_KEY = "backoffice.search.services.localized.roots";
	private static final String BACKOFFICE_SEARCH_SERVICES_FACET_NON_LOCALIZED_ROOTS_KEY = "backoffice.search.services.facet.nonlocalized.files";
	private static final String BACKOFFICE_SEARCH_SERVICES_FACET_LOCALIZED_ROOTS_KEY = "backoffice.search.services.facet.localized.roots";
	private static final String BACKOFFICE_SEARCH_SERVICES_ROOTS_SEPARATOR_KEY = "backoffice.search.services.roots.separator";
	private static final String BACKOFFICE_SEARCH_SERVICES_ROOTS_FILE_ENCODING_KEY = "backoffice.search.services.roots.file.encoding";
	private static final String BACKOFFICE_SEARCH_SERVICES_ROOTS_LANGUAGE_SEPARATOR_KEY = "backoffice.search.services.roots.language.separator";
	private static final String CUSTOMIZED_FILE_LIST_SEPARATOR = ";";
	private static final String CUSTOMIZED_FILE_ENCODING = "GBK";
	private static final String CUSTOMIZED_LANGUAGE_SEPARATOR = "-";
	private static final String SEARCH_SERVICE_CONFIGURED_FILE_LIST = "file1;file2";
	private static final String SEARCH_SERVICE_FACET_CONFIGURED_FILE_LIST = "file3;file4";
	private static final String FILE_1 = "file1";
	private static final String FILE_2 = "file2";
	private static final String FILE_3 = "file3";
	private static final String FILE_4 = "file4";
	private static final String SEARCH_SERVICE_MODULE = "searchservices";
	private static final String ADAPTIVE_SEARCH_MODULE = "adaptivesearch";

	private final ConfigStringResolver configStringResolver = mock(ConfigStringResolver.class);
	private final DefaultBackofficeSearchservicesSystemSetupConfig config = new DefaultBackofficeSearchservicesSystemSetupConfig(
			configStringResolver);

	@Before
	public void setUp()
	{
		mockConfigResolverWithNonEmptyKeys();
		if (config.isExtensionLoaded(SEARCH_SERVICE_MODULE))
		{
			mockRoots(BACKOFFICE_SEARCH_SERVICES_NON_LOCALIZED_ROOTS_KEY, SEARCH_SERVICE_CONFIGURED_FILE_LIST);
			mockRoots(BACKOFFICE_SEARCH_SERVICES_LOCALIZED_ROOTS_KEY, SEARCH_SERVICE_CONFIGURED_FILE_LIST);
		}
		if (config.isExtensionLoaded(ADAPTIVE_SEARCH_MODULE))
		{
			mockRoots(BACKOFFICE_SEARCH_SERVICES_FACET_NON_LOCALIZED_ROOTS_KEY, SEARCH_SERVICE_FACET_CONFIGURED_FILE_LIST);
			mockRoots(BACKOFFICE_SEARCH_SERVICES_FACET_LOCALIZED_ROOTS_KEY, SEARCH_SERVICE_FACET_CONFIGURED_FILE_LIST);
		}
	}

	@Test
	public void shouldReturnCustomizedFileListSeparatorWhenCustomized()
	{
		assertThat(config.getListSeparator()).isEqualTo(CUSTOMIZED_FILE_LIST_SEPARATOR);
	}

	@Test
	public void shouldReturnCustomizedFileEncodingWhenCustomized()
	{
		assertThat(config.getFileEncoding()).isEqualTo(CUSTOMIZED_FILE_ENCODING);
	}

	@Test
	public void shouldReturnCustomizedLanguageSeparatorWhenCustomized()
	{
		assertThat(config.getRootNameLanguageSeparator()).isEqualTo(CUSTOMIZED_LANGUAGE_SEPARATOR);
	}

	@Test
	public void shouldReturnCollectionOfLocalizedImpexFilesRootsAccordingToLoadedExtensions()
	{
		final Collection<String> roots = config.getLocalizedRootNames();

		if (config.isExtensionLoaded(SEARCH_SERVICE_MODULE))
		{
			assertThat(roots).contains(FILE_1, FILE_2);
		}
		if (config.isExtensionLoaded(ADAPTIVE_SEARCH_MODULE))
		{
			assertThat(roots).contains(FILE_3, FILE_4);
		}
		if (!config.isExtensionLoaded(ADAPTIVE_SEARCH_MODULE) && !config.isExtensionLoaded(SEARCH_SERVICE_MODULE))
		{
			assertThat(roots).isEmpty();
		}
	}

	@Test
	public void shouldReturnCollectionOfNonLocalizedImpexFilesRootsAccordingToLoadedExtensions()
	{
		final Collection<String> roots = config.getNonLocalizedRootNames();

		if (config.isExtensionLoaded(SEARCH_SERVICE_MODULE))
		{
			assertThat(roots).contains(FILE_1, FILE_2);
		}
		if (config.isExtensionLoaded(ADAPTIVE_SEARCH_MODULE))
		{
			assertThat(roots).contains(FILE_3, FILE_4);
		}
		if (!config.isExtensionLoaded(ADAPTIVE_SEARCH_MODULE) && !config.isExtensionLoaded(SEARCH_SERVICE_MODULE))
		{
			assertThat(roots).isEmpty();
		}
	}

	private void mockConfigResolverWithNonEmptyKeys()
	{
		when(configStringResolver.resolveConfigStringParameter(BACKOFFICE_SEARCH_SERVICES_ROOTS_SEPARATOR_KEY))
				.thenReturn(CUSTOMIZED_FILE_LIST_SEPARATOR);
		when(configStringResolver.resolveConfigStringParameter(BACKOFFICE_SEARCH_SERVICES_ROOTS_FILE_ENCODING_KEY))
				.thenReturn(CUSTOMIZED_FILE_ENCODING);
		when(configStringResolver.resolveConfigStringParameter(BACKOFFICE_SEARCH_SERVICES_ROOTS_LANGUAGE_SEPARATOR_KEY))
				.thenReturn(CUSTOMIZED_LANGUAGE_SEPARATOR);
	}

	private void mockRoots(final String rootsKey, final String files)
	{
		when(configStringResolver.resolveConfigStringParameter(rootsKey)).thenReturn(files);
	}
}
