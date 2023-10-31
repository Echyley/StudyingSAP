/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.servicelayer.impex.impl;

import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.impex.model.DistributedImportProcessModel;
import de.hybris.platform.processing.enums.DistributedProcessState;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.impex.ImpExError;
import de.hybris.platform.servicelayer.impex.ImportConfig;
import de.hybris.platform.servicelayer.impex.ImportResult;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Resource;

import org.awaitility.Awaitility;
import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class RelationImportIntegrationTest extends ServicelayerTest
{
	private static final int DISTRIBUTED_IMPEX_TIMEOUT = 30;
	private static final String COMMON_DATA_IMPORT_SCRIPT = "$catalogversion=catalogversion(catalog(id[default='photoCatalog']),version[default='Online'])[unique=true]\n"
			+ "$supercategories=supercategories(code, $catalogversion);\n"
			+ "INSERT_UPDATE Category;code[unique=true];$catalogversion;name[lang=en]\r\n" + ";cameras;photoCatalog:Online;\n"
			+ ";lenses;photoCatalog:Online;\n";
	private static final String DIRECT_RELATION_IMPORT_SCRIPT = "INSERT_UPDATE CategoryCategoryRelation;target(code, $catalogversion)[unique=true];source(code, $catalogversion)[unique=true]\n"
			+ ";lenses;cameras;";
	private static final String INDIRECT_RELATION_IMPORT_SCRIPT = "INSERT_UPDATE Category;code[unique=true];$catalogversion;$supercategories\r\n"
			+ ";lenses;;cameras";

	@Resource
	private ModelService modelService;
	@Resource
	private FlexibleSearchService flexibleSearchService;

	@Before
	public void setUp() throws Exception
	{
		ServicelayerTest.createCoreData();
		prepareTestCatalog("photoCatalog", "Online");
	}

	private CatalogVersionModel prepareTestCatalog(final String catalogId, final String versionCode)
	{
		final CatalogModel catalog = modelService.create(CatalogModel.class);
		catalog.setId(catalogId);
		catalog.setName("Test Catalog");
		catalog.setDefaultCatalog(Boolean.TRUE);
		final CatalogVersionModel ctgVer = modelService.create(CatalogVersionModel.class);
		ctgVer.setCatalog(catalog);
		ctgVer.setVersion(versionCode);
		ctgVer.setActive(Boolean.TRUE);

		modelService.saveAll(catalog, ctgVer);
		return ctgVer;
	}

	@Test
	public void directDistributedTest()
	{
		// given
		final ImportConfig config = createImportConfig(COMMON_DATA_IMPORT_SCRIPT + DIRECT_RELATION_IMPORT_SCRIPT);
		config.setDistributedImpexEnabled(true);

		// when
		final ImportResult importResult = importService.importData(config);
		waitForDistributedImpEx(importResult, DISTRIBUTED_IMPEX_TIMEOUT);
		final List<? extends ImpExError> errors = collectErrors(importResult);

		// then
		assertThat(importResult.isFinished()).isTrue();
		assertThat(errors).isEmpty();
		assertThat(findDistributedImportProcessForCronJob(importResult.getCronJob()).getState())
				.isEqualTo(DistributedProcessState.SUCCEEDED);
		checkCategories();
	}

	@Test
	public void directNotDistributedTest()
	{
		// given
		final ImportConfig config = createImportConfig(COMMON_DATA_IMPORT_SCRIPT + DIRECT_RELATION_IMPORT_SCRIPT);
		config.setDistributedImpexEnabled(false);

		// when
		final ImportResult importResult = importService.importData(config);
		final List<? extends ImpExError> errors = collectErrors(importResult);

		// then
		assertThat(importResult.isFinished()).isTrue();
		assertThat(importResult.isSuccessful()).isTrue();
		assertThat(importResult.isError()).isFalse();
		assertThat(errors).isEmpty();
		checkCategories();
	}

	@Test
	public void indirectDistributedTest()
	{
		// given
		final ImportConfig config = createImportConfig(COMMON_DATA_IMPORT_SCRIPT + INDIRECT_RELATION_IMPORT_SCRIPT);
		config.setDistributedImpexEnabled(true);

		// when
		final ImportResult importResult = importService.importData(config);
		waitForDistributedImpEx(importResult, DISTRIBUTED_IMPEX_TIMEOUT);
		final List<? extends ImpExError> errors = collectErrors(importResult);

		// then
		assertThat(importResult.isFinished()).isTrue();
		assertThat(errors).isEmpty();
		assertThat(findDistributedImportProcessForCronJob(importResult.getCronJob()).getState())
				.isEqualTo(DistributedProcessState.SUCCEEDED);
		checkCategories();
	}

	@Test
	public void indirectNotDistributedTest()
	{
		// given
		final ImportConfig config = createImportConfig(COMMON_DATA_IMPORT_SCRIPT + INDIRECT_RELATION_IMPORT_SCRIPT);
		config.setDistributedImpexEnabled(false);

		// when
		final ImportResult importResult = importService.importData(config);
		final List<? extends ImpExError> errors = collectErrors(importResult);

		// then
		assertThat(importResult.isFinished()).isTrue();
		assertThat(importResult.isSuccessful()).isTrue();
		assertThat(importResult.isError()).isFalse();
		assertThat(errors).isEmpty();
		checkCategories();
	}

	private ImportConfig createImportConfig(final String inputScript)
	{
		final ImportConfig config = new ImportConfig();
		config.setScript(inputScript);
		config.setSynchronous(true);

		return config;
	}

	private List<? extends ImpExError> collectErrors(final ImportResult importResult)
	{
		final Stream<? extends ImpExError> errors = importService.collectImportErrors(importResult);
		return errors.collect(Collectors.toList());
	}

	private void waitForDistributedImpEx(final ImportResult importResult, final long seconds)
	{
		Awaitility.await()
			.atMost(seconds, TimeUnit.SECONDS)
			.untilTrue(new AtomicBoolean(importResult.isFinished()));
	}

	private void checkCategories()
	{
		assertThat(findCategoryByCode("lenses").getSupercategories())
			.containsExactly(findCategoryByCode("cameras"));
	}

	private CategoryModel findCategoryByCode(final String code)
	{
		return findByGenericField(CategoryModel._TYPECODE, CategoryModel.CODE, code);
	}

	private DistributedImportProcessModel findDistributedImportProcessForCronJob(final CronJobModel cronJob)
	{
		return findByGenericField(DistributedImportProcessModel._TYPECODE, 
											DistributedImportProcessModel.IMPEXIMPORTCRONJOB, 
											cronJob.getPk());
	}

	private <T> T findByGenericField(final String typeCode, final String searchField, final Object searchObject)
	{
		final String query = String.format("SELECT {PK} from {%s} WHERE {%s} = ?searchObject ", typeCode, searchField);
		final SearchResult<T> searchResult = flexibleSearchService.search(query, Map.of("searchObject", searchObject));

		assertThat(searchResult).isNotNull();
		assertThat(searchResult.getCount()).isEqualTo(1);

		return searchResult.getResult().get(0);
	}
}
