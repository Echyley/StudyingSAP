/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.solrsearch.setup.impl;

import com.hybris.backoffice.search.setup.impl.ConfigStringResolver;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class DefaultBackofficeSolrSearchSystemSetupConfigTest
{
	private static final String FILE_1 = "file1";
	private static final String FILE_2 = "file2";
	private static final String CUSTOMIZED_FILE_LIST_SEPARATOR = ";";
	private static final String CUSTOMIZED_FILE_ENCODING = "GBK";
	private static final String CUSTOMIZED_LANGUAGE_SEPARATOR = "-";
	private static final String BACKOFFICE_SOLR_SEARCH_ROOTS_SEPARATOR_KEY = "backoffice.solr.search.roots.separator";
	private static final String BACKOFFICE_SOLR_SEARCH_LOCALIZED_ROOTS_KEY = "backoffice.solr.search.localized.roots";
	private static final String BACKOFFICE_SOLR_SEARCH_NON_LOCALIZED_ROOTS_KEY = "backoffice.solr.search.nonlocalized.files";
	private static final String BACKOFFICE_SOLR_SEARCH_ROOTS_FILE_ENCODING_KEY = "backoffice.solr.search.roots.file.encoding";
	private static final String BACKOFFICE_SOLR_SEARCH_ROOTS_LANGUAGE_SEPARATOR_KEY = "backoffice.solr.search.roots.language.separator";
	private static final String CONFIGURED_FILE_LIST = "file1;file2";
	private static final String SOLR_FACET_SEARCH_MODULE = "solrfacetsearch";

	private final ConfigStringResolver configStringResolver = mock(ConfigStringResolver.class);
	private final DefaultBackofficeSolrSearchSystemSetupConfig config = new DefaultBackofficeSolrSearchSystemSetupConfig(configStringResolver);

	@Before
	public void setUp()
	{
		mockConfigResolverWithNonEmptyKeys();
		if (config.isExtensionLoaded(SOLR_FACET_SEARCH_MODULE))
		{
			mockRoots(BACKOFFICE_SOLR_SEARCH_LOCALIZED_ROOTS_KEY, CONFIGURED_FILE_LIST);
			mockRoots(BACKOFFICE_SOLR_SEARCH_NON_LOCALIZED_ROOTS_KEY, CONFIGURED_FILE_LIST);
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
	public void shouldReturnConfiguredLocalizedImpexFilesRootsWhenConfigurationKeyIsSet()
	{
		//when
		final Collection<String> roots = config.getLocalizedRootNames();

		//then
		if (config.isExtensionLoaded(SOLR_FACET_SEARCH_MODULE))
		{
			assertThat(roots).contains(FILE_1, FILE_2);
		}
		else
		{
			assertThat(roots).isEmpty();
		}
	}

	@Test
	public void shouldReturnConfiguredNonLocalizedImpexFilesRootsWhenConfigurationKeyIsSet()
	{

		//when
		final Collection<String> roots = config.getNonLocalizedRootNames();

		//then
		if (config.isExtensionLoaded(SOLR_FACET_SEARCH_MODULE))
		{
			assertThat(roots).contains(FILE_1, FILE_2);
		}
		else
		{
			assertThat(roots).isEmpty();
		}
	}

	private void mockConfigResolverWithNonEmptyKeys()
	{
		when(configStringResolver.resolveConfigStringParameter(BACKOFFICE_SOLR_SEARCH_ROOTS_SEPARATOR_KEY))
				.thenReturn(CUSTOMIZED_FILE_LIST_SEPARATOR);
		when(configStringResolver.resolveConfigStringParameter(BACKOFFICE_SOLR_SEARCH_ROOTS_FILE_ENCODING_KEY))
				.thenReturn(CUSTOMIZED_FILE_ENCODING);
		when(configStringResolver.resolveConfigStringParameter(BACKOFFICE_SOLR_SEARCH_ROOTS_LANGUAGE_SEPARATOR_KEY))
				.thenReturn(CUSTOMIZED_LANGUAGE_SEPARATOR);
	}

	private void mockRoots(final String rootsKey, final String files)
	{
		when(configStringResolver.resolveConfigStringParameter(rootsKey)).thenReturn(files);
	}
}
