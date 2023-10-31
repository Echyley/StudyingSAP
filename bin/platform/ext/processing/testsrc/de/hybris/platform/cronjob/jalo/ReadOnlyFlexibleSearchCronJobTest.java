/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.cronjob.jalo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anySetOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.AbstractTenant;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.Registry;
import de.hybris.platform.cronjob.enums.JobLogLevel;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.cronjob.model.JobModel;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.flexiblesearch.FlexibleSearch;
import de.hybris.platform.jalo.flexiblesearch.QueryOptions;
import de.hybris.platform.jalo.flexiblesearch.hints.Hint;
import de.hybris.platform.jalo.flexiblesearch.internal.FlexibleSearchExecutor;
import de.hybris.platform.jalo.flexiblesearch.internal.ReadOnlyConditionsHelper;
import de.hybris.platform.jdbcwrapper.HybrisDataSource;
import de.hybris.platform.persistence.flexiblesearch.TranslatedQuery;
import de.hybris.platform.scripting.model.ScriptModel;
import de.hybris.platform.scripting.util.TestScriptLogicHolder;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.cronjob.CronJobService;
import de.hybris.platform.servicelayer.internal.model.ScriptingJobModel;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.testframework.PropertyConfigSwitcher;
import de.hybris.platform.testframework.TestUtils;
import de.hybris.platform.util.Config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.zip.ZipInputStream;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.apache.commons.io.IOUtils;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;


@IntegrationTest
public class ReadOnlyFlexibleSearchCronJobTest extends ServicelayerBaseTest
{
	private final PropertyConfigSwitcher flexibleSearchReadOnlyDataSource = new PropertyConfigSwitcher(
			ReadOnlyConditionsHelper.PARAM_FS_READ_ONLY_DATASOURCE);

	@Resource
	private ModelService modelService;
	@Resource
	private CronJobService cronJobService;
	@Resource
	private MediaService mediaService;

	private AbstractTenant tenant;
	private TestScriptLogicHolder testScripts;
	private FlexibleSearchExecutor flexibleSearchExecutor;
	private TestFlexibleSearch flexibleSearch;

	@Before
	public void setUp() throws Exception
	{
		flexibleSearchReadOnlyDataSource.switchToValue("f");
		testScripts = new TestScriptLogicHolder();
		tenant = Registry.getCurrentTenant();
		flexibleSearchExecutor = spy(new FlexibleSearchExecutor(tenant));
		flexibleSearch = new TestFlexibleSearch(flexibleSearchExecutor);
		TestUtils.disableFileAnalyzer("testing cronjobs during db outage - there will be a lot of errors");
	}

	@After
	public void tearDown() throws Exception
	{
		TestUtils.enableFileAnalyzer();
		testScripts.clear();
		flexibleSearchReadOnlyDataSource.switchBackToDefault();
	}

	@Test
	public void shouldUseReadOnlyDataSourceWhenCronJobDefinedAsUseReadOnlyDatasource()
	{
		// given
		final ScriptModel scriptModel = createScriptModel();
		final ScriptingJobModel scriptingJobModel = prepareScriptingJob(scriptModel.getCode(), false, false);
		final CronJobModel cronJobModel = prepareCronJob("testCronJob-" + UUID.randomUUID(), scriptingJobModel, true, false);

		// when
		cronJobService.performCronJob(cronJobModel, true);

		// then
		final HybrisDataSource readOnlyDS = getConfiguredReadOnlyDataSource();
		verifyDataSourceWasUsedOnExecute(readOnlyDS);
	}

	@Test
	public void shouldNotUseReadOnlyDataSourceWhenCronJobAndJobWereNotDefinedAsUseReadOnlyDatasource()
	{
		// given
		final ScriptModel scriptModel = createScriptModel();
		final ScriptingJobModel scriptingJobModel = prepareScriptingJob(scriptModel.getCode(), false, false);
		final CronJobModel cronJobModel = prepareCronJob("testCronJob-" + UUID.randomUUID().toString(), scriptingJobModel, false,
				false);

		// when
		cronJobService.performCronJob(cronJobModel, true);

		// then
		final HybrisDataSource readOnlyDS = getConfiguredReadOnlyDataSource();
		verifyDataSourceWasNotUsedOnExecute(readOnlyDS);
	}

	@Test
	public void shouldNotUseReadOnlyDataSourceWhenCronJobAndJobHaveNullUseReadOnlyDatasource()
	{
		// given
		final ScriptModel scriptModel = createScriptModel();
		final ScriptingJobModel scriptingJobModel = prepareScriptingJob(scriptModel.getCode(), null, false);
		final CronJobModel cronJobModel = prepareCronJob("testCronJob-" + UUID.randomUUID().toString(), scriptingJobModel, null,
				false);

		// when
		cronJobService.performCronJob(cronJobModel, true);

		// then
		final HybrisDataSource readOnlyDS = getConfiguredReadOnlyDataSource();
		verifyDataSourceWasNotUsedOnExecute(readOnlyDS);
	}

	@Test
	public void shouldNotUseReadOnlyDataSourceWhenCronJobAndJobHaveDefaultSettings()
	{
		// given
		final ScriptModel scriptModel = createScriptModel();
		final ScriptingJobModel scriptingJobModel = prepareScriptingJob(scriptModel.getCode(), false, true);
		final CronJobModel cronJobModel = prepareCronJob("testCronJob-" + UUID.randomUUID(), scriptingJobModel, true, true);

		// when
		cronJobService.performCronJob(cronJobModel, true);

		// then
		final HybrisDataSource readOnlyDS = getConfiguredReadOnlyDataSource();
		verifyDataSourceWasNotUsedOnExecute(readOnlyDS);
	}


	@Test
	public void shouldNotLogWarningForNestedCronjob()
	{
		// given
		final ScriptModel nestedModelParent = createScriptModelWithNestedCronJob();
		final ScriptingJobModel scriptingJobModelParent = prepareScriptingJob(nestedModelParent.getCode(), true, true);
		final CronJobModel cronJobModelParent = prepareCronJob("parentCronJob-" + UUID.randomUUID(), scriptingJobModelParent,
				true, true);

		cronJobModelParent.setLogLevelDatabase(JobLogLevel.WARNING);

		// when
		cronJobService.performCronJob(cronJobModelParent, true);

		cronJobModelParent.getLogFiles().forEach(
				lf -> {
					final InputStream input = mediaService.getStreamFromMedia(lf);

					try (final ZipInputStream zipInput = new ZipInputStream(input);)
					{
						zipInput.getNextEntry();
						final String logMessage = IOUtils.toString(zipInput, StandardCharsets.UTF_8);

						//then
						Assertions.assertThat(logMessage).doesNotContain("There are some active local session contexts left");
					}
					catch (final IOException e)
					{
						throw new RuntimeException(e);
					}
				});
	}

	private ScriptModel createScriptModelWithNestedCronJob()
	{
		final ScriptModel childModel = createScriptModel();
		final ScriptingJobModel scriptingJobModelChild = prepareScriptingJob(childModel.getCode(), true, true);
		final CronJobModel cronJobModelChild = prepareCronJob("childCronJob-" + UUID.randomUUID(), scriptingJobModelChild, true,
				true);

		return testScripts.scriptWithLogic(modelService, () -> {
					cronJobService.performCronJob(cronJobModelChild, true);
				}
		);
	}

	private ScriptModel createScriptModel()
	{
		return testScripts.scriptWithLogic(modelService, () -> {
			final QueryOptions q = QueryOptions.newBuilder().withQuery("select {PK} from {Product}").build();
			flexibleSearch.search(q);
		});
	}

	private ScriptingJobModel prepareScriptingJob(final String scriptCode, final Boolean useReadOnlyDatasource,
	                                              final boolean useDefaultReadOnlySetting)
	{
		final ScriptingJobModel scriptingJob = modelService.create(ScriptingJobModel.class);
		scriptingJob.setCode("myGroovyJob-" + UUID.randomUUID().toString());
		scriptingJob.setScriptURI("model://" + scriptCode);
		if (!useDefaultReadOnlySetting)
		{
			scriptingJob.setUseReadOnlyDatasource(useReadOnlyDatasource);
		}

		return scriptingJob;
	}

	private CronJobModel prepareCronJob(final String code, final JobModel job, final Boolean useReadOnlyDatasource,
	                                    final boolean useDefaultReadOnlySetting)
	{
		final CronJobModel cronjob = modelService.create(CronJobModel.class);
		cronjob.setCode(code);
		cronjob.setSingleExecutable(Boolean.TRUE);
		cronjob.setJob(job);
		cronjob.setLogToDatabase(Boolean.TRUE);
		cronjob.setLogLevelDatabase(JobLogLevel.INFO);
		cronjob.setNodeID(Registry.getClusterID());

		if (!useDefaultReadOnlySetting)
		{
			cronjob.setUseReadOnlyDatasource(useReadOnlyDatasource);
		}

		modelService.save(cronjob);
		return cronjob;
	}

	private HybrisDataSource getConfiguredReadOnlyDataSource()
	{
		final String configureReadOnlyDataSourceId = Config.getString(ReadOnlyConditionsHelper.PARAM_FS_READ_ONLY_DATASOURCE, "")
		                                                   .toLowerCase(Locale.ROOT);
		assertThat(configureReadOnlyDataSourceId).isNotEmpty();

		final Optional<HybrisDataSource> readOnlyDataSource = tenant.getAllSlaveDataSources()
		                                                            .stream()
		                                                            .filter(ds -> configureReadOnlyDataSourceId.equals(
				                                                            ds.getID()))
		                                                            .findFirst();

		assertThat(readOnlyDataSource).isNotEmpty();
		return readOnlyDataSource.get();
	}

	private void verifyDataSourceWasUsedOnExecute(final HybrisDataSource expectedDS)
	{
		verify(flexibleSearchExecutor, times(1))
				.execute(anyInt(), anyInt(), anyBoolean(), any(TranslatedQuery.class), anyList(), anyMap(),
						any(PK.class), anyInt(), anySetOf(PK.class), anyListOf(Hint.class), eq(expectedDS));
	}

	private void verifyDataSourceWasNotUsedOnExecute(final HybrisDataSource notExpectedDS)
	{

		final ArgumentCaptor<DataSource> dataSourceArgumentCaptor = ArgumentCaptor.forClass(DataSource.class);

		verify(flexibleSearchExecutor, atLeastOnce())
				.execute(anyInt(), anyInt(), anyBoolean(), any(TranslatedQuery.class), anyList(), anyMap(),
						any(PK.class), anyInt(), anySetOf(PK.class), anyListOf(Hint.class), dataSourceArgumentCaptor.capture());

		assertThat(dataSourceArgumentCaptor.getAllValues()).doesNotContain(notExpectedDS);
	}

	public static class TestFlexibleSearch extends FlexibleSearch
	{
		public TestFlexibleSearch(final FlexibleSearchExecutor executor)
		{
			super(executor);
		}

		@Override
		protected boolean isCachingDisabled(final SessionContext localCtx)
		{
			return true;
		}
	}

}
