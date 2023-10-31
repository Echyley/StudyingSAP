/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.addonsupport.setup.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.setup.SetupImpexService;
import de.hybris.platform.commerceservices.util.ResponsiveUtils;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.util.JspContext;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultAddonSampleDataImportServiceUnitTest
{

	private static final String TEST_IMPEX_FILE = "testImpexFile";
	private static final String STORES_URL = "/stores/";
	private static final String IMPORT_ROOT = "importRoot";
	private static final String TEST_STORE = "testStore";
	private static final String PRODUCT_CATALOG = "productCatalog";
	private static final boolean SOLR_REINDEX = true;

	@Mock
	private SetupImpexService setupImpexService;

	@InjectMocks
	private DefaultAddonSampleDataImportService importService = new DefaultAddonSampleDataImportService()
	{
		@Override
		protected boolean synchronizeProductCatalog(final SystemSetupContext context, final String catalogName, final boolean sync)
		{
			return true;
		}

		@Override
		protected void synchronizeContent(final SystemSetupContext context, final String productCatalog,
				final List<String> contentCatalogs, final boolean productSyncSuccess)
		{
			//mock empty method
		}

		@Override
		protected void processStoreNames(final SystemSetupContext context, final String importRoot, final List<String> storeNames,
				final String productCatalog, final boolean solrReindex)
		{
			//mock empty method
		}
	};

	@Mock
	private SystemSetupContext context;


	@Before
	public void setup()
	{
		final JspContext jspContext = mock(JspContext.class);
		when(context.getJspContext()).thenReturn(jspContext);
		doNothing().when(jspContext).println(any());

		doNothing().when(setupImpexService).importImpexFile(anyString(), anyBoolean());

		importService.setSetupImpexService(setupImpexService);
	}

	@Test
	public void testImportContentCatalog()
	{
		final MockedStatic<ResponsiveUtils> transactionMock = mockStatic(ResponsiveUtils.class);
		transactionMock.when(() -> ResponsiveUtils.isResponsive()).thenReturn(true);
		// when
		importService.importContentCatalog(context, IMPORT_ROOT, PRODUCT_CATALOG);

		//then
		verify(setupImpexService).importImpexFile("importRoot/contentCatalogs/productCatalogContentCatalog/cms-content.impex",
				false);
	}

	@Test
	public void testExecuteImportForOneStoreImpexFile()
	{
		// when
		importService.importImpexFile(context, TEST_IMPEX_FILE, false);

		//then
		verify(setupImpexService).importImpexFile(TEST_IMPEX_FILE, false);
	}

	@Test
	public void testExecuteImportForOneStoreImpexFileAndMissingError()
	{
		// when
		importService.importImpexFile(context, TEST_IMPEX_FILE, true);

		// then
		verify(setupImpexService).importImpexFile(TEST_IMPEX_FILE, true);
	}

	@Test
	public void testExecuteImportImpexFileForOneStoreImpexFileWhenCallImportStoreInitialData()
	{
		//given
		final List<String> storeNames = new ArrayList<>();
		storeNames.add(TEST_STORE);
		final List<String> contentCatalogs = new ArrayList<>();
		contentCatalogs.add(PRODUCT_CATALOG);

		// when
		importService.importStoreInitialData(context, IMPORT_ROOT, storeNames, PRODUCT_CATALOG, contentCatalogs, SOLR_REINDEX);

		// then
		verify(setupImpexService).importImpexFile(IMPORT_ROOT + STORES_URL + TEST_STORE + "/searchservices.impex", false);

	}

	@Test
	public void shouldImportSolrSearchSampleDataWhenImportStoreInitialData()
	{
		//given
		final List<String> storeNames = new ArrayList<>();
		storeNames.add(TEST_STORE);
		final List<String> contentCatalogs = new ArrayList<>();
		contentCatalogs.add(PRODUCT_CATALOG);

		// when
		importService.importStoreInitialData(context, IMPORT_ROOT, storeNames, PRODUCT_CATALOG, contentCatalogs, SOLR_REINDEX);

		// then
		verify(setupImpexService).importImpexFile(IMPORT_ROOT + STORES_URL + TEST_STORE + "/solr.impex", false);
	}

	@Test
	public void shouldImportSearchServicesFacetSampleDataWhenImportStoreInitialData()
	{
		//given
		final List<String> storeNames = new ArrayList<>();
		storeNames.add(TEST_STORE);
		final List<String> contentCatalogs = new ArrayList<>();
		contentCatalogs.add(PRODUCT_CATALOG);

		// when
		importService.importStoreInitialData(context, IMPORT_ROOT, storeNames, PRODUCT_CATALOG, contentCatalogs, SOLR_REINDEX);

		// then
		verify(setupImpexService).importImpexFile(IMPORT_ROOT + STORES_URL + TEST_STORE + "/searchservices-facet.impex", false);
	}

}
