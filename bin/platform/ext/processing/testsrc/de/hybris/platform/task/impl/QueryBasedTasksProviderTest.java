/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.task.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.AbstractTenant;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.Registry;
import de.hybris.platform.jalo.flexiblesearch.FlexibleSearch;
import de.hybris.platform.jalo.flexiblesearch.FlexibleSearchDataSourceTest;
import de.hybris.platform.jalo.flexiblesearch.internal.FlexibleSearchExecutor;
import de.hybris.platform.jalo.flexiblesearch.internal.ReadOnlyConditionsHelper;
import de.hybris.platform.jdbcwrapper.HybrisDataSource;
import de.hybris.platform.persistence.flexiblesearch.TranslatedQuery;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.impl.DefaultFlexibleSearchService;
import de.hybris.platform.servicelayer.search.internal.preprocessor.QueryPreprocessorRegistry;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.testframework.PropertyConfigSwitcher;

import java.util.List;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.verification.VerificationMode;

import com.codahale.metrics.MetricRegistry;

@IntegrationTest
public class QueryBasedTasksProviderTest extends ServicelayerBaseTest
{
	private static final String READ_ONLY_DATASOURCE_ID = "f";
	private static final int MAX_ITEMS_TO_SCHEDULE = 3;
	private static final int NUMBER_OF_INVOCATION = 5;
	private final PropertyConfigSwitcher flexibleSearchReadOnlyDataSource = new PropertyConfigSwitcher(
			ReadOnlyConditionsHelper.PARAM_FS_READ_ONLY_DATASOURCE);
	private final PropertyConfigSwitcher readReplicaEnabledOnTaskSwitcher =
			new PropertyConfigSwitcher(QueryBasedTasksProvider.PROPERTY_USE_READ_REPLICA);
	protected AbstractTenant tenant;

	@Resource
	private MetricRegistry metricRegistry;

	@Resource
	private TypeService typeService;

	@Resource
	private SessionService sessionService;

	@Resource
	private ModelService modelService;

	@Resource
	private QueryPreprocessorRegistry queryPreprocessorRegistry;

	private FlexibleSearchExecutor flexibleSearchExecutor;
	private RuntimeConfigHolder runtimeConfigHolder;
	private TaskEngineParameters taskEngineParameters;
	private QueryBasedTasksProvider queryBasedTasksProvider;
	private HybrisDataSource masterDataSource;
	private HybrisDataSource readonlyDataSource;

	@Before
	public void setUp() throws Exception
	{
		this.tenant = Registry.getCurrentTenant();

		this.flexibleSearchReadOnlyDataSource.switchToValue(READ_ONLY_DATASOURCE_ID);

		this.flexibleSearchExecutor = spy(new FlexibleSearchExecutor(this.tenant));
		final FlexibleSearch flexibleSearch = new FlexibleSearchDataSourceTest.TestFlexibleSearch(this.flexibleSearchExecutor);
		final FlexibleSearchService flexibleSearchService = new TestFlexibleSearchService(flexibleSearch, this.sessionService,
				this.modelService,
				this.queryPreprocessorRegistry);

		this.runtimeConfigHolder = new RuntimeConfigHolder();

		this.taskEngineParameters = new TaskEngineParameters.ParametersBuilder().withTenant(this.tenant)
		                                                                        .withClusterGroupsForThisNode(List.of())
		                                                                        .withMaxThreads(1)
		                                                                        .build();

		this.queryBasedTasksProvider = new QueryBasedTasksProvider(this.metricRegistry, flexibleSearchService, this.typeService);

		this.masterDataSource = this.tenant.getDataSource();
		this.readonlyDataSource = this.tenant.getAllSlaveDataSources()
		                                     .stream()
		                                     .filter(ds -> READ_ONLY_DATASOURCE_ID.equals(ds.getID()))
		                                     .findFirst()
		                                     .orElseThrow();

	}

	@Test
	public void getTasksToScheduleWhenReadReplicaDisabled()
	{
		// given
		this.readReplicaEnabledOnTaskSwitcher.switchToValue("false");

		// when
		this.queryBasedTasksProvider.getTasksToSchedule(this.runtimeConfigHolder, this.taskEngineParameters, MAX_ITEMS_TO_SCHEDULE);

		// then
		this.verifyDataSourceWasUsedOnExecute(this.masterDataSource);
		this.verifyDataSourcesWereNotUsedOnExecute(this.readonlyDataSource);
	}

	@Test
	public void getTasksToScheduleWhenReadReplicaEnabled()
	{
		// given
		this.readReplicaEnabledOnTaskSwitcher.switchToValue("true");

		// when
		this.queryBasedTasksProvider.getTasksToSchedule(this.runtimeConfigHolder, this.taskEngineParameters, MAX_ITEMS_TO_SCHEDULE);

		// then
		this.verifyDataSourceWasUsedOnExecute(this.readonlyDataSource);
		this.verifyDataSourcesWereNotUsedOnExecute(this.masterDataSource);
	}

	@Test
	public void getTasksToScheduleWhenNoValueReadReplica()
	{
		// given
		this.readReplicaEnabledOnTaskSwitcher.switchToValue("");

		// when
		this.queryBasedTasksProvider.getTasksToSchedule(this.runtimeConfigHolder, this.taskEngineParameters, MAX_ITEMS_TO_SCHEDULE);

		// then
		this.verifyDataSourceWasUsedOnExecute(this.masterDataSource);
		this.verifyDataSourcesWereNotUsedOnExecute(this.readonlyDataSource);
	}


	@After
	public void tearDown()
	{
		this.readReplicaEnabledOnTaskSwitcher.switchBackToDefault();
		this.flexibleSearchReadOnlyDataSource.switchBackToDefault();
	}

	private void verifyDataSourceWasUsedOnExecute(final HybrisDataSource expectedDS)
	{
		this.verifyDataSourceWasUsedOnExecute(expectedDS, times(QueryBasedTasksProviderTest.NUMBER_OF_INVOCATION));
	}

	private void verifyDataSourceWasUsedOnExecute(
			final HybrisDataSource expectedDS, final VerificationMode verificationMode)
	{
		verify(this.flexibleSearchExecutor, verificationMode)
				.execute(anyInt(), anyInt(), anyBoolean(), any(TranslatedQuery.class), anyList(), anyMap(),
						any(PK.class), anyInt(), anySet(), anyList(), eq(expectedDS));
	}

	private void verifyDataSourcesWereNotUsedOnExecute(final HybrisDataSource... notExpectedDS)
	{

		final ArgumentCaptor<DataSource> dataSourceArgumentCaptor = ArgumentCaptor.forClass(DataSource.class);

		verify(this.flexibleSearchExecutor, atLeastOnce())
				.execute(anyInt(), anyInt(), anyBoolean(), any(TranslatedQuery.class), anyList(), anyMap(),
						any(PK.class), anyInt(), anySet(), anyList(), dataSourceArgumentCaptor.capture());

		assertThat(dataSourceArgumentCaptor.getAllValues()).doesNotContain(notExpectedDS);
	}

	public static class TestFlexibleSearchService extends DefaultFlexibleSearchService
	{
		private final FlexibleSearch flexibleSearch;
		private final SessionService sessionService;
		private final ModelService modelService;
		private final QueryPreprocessorRegistry queryPreprocessorRegistry;

		public TestFlexibleSearchService(final FlexibleSearch flexibleSearch,
		                                 final SessionService sessionService,
		                                 final ModelService modelService,
		                                 final QueryPreprocessorRegistry queryPreprocessorRegistry)
		{
			this.flexibleSearch = flexibleSearch;
			this.sessionService = sessionService;
			this.modelService = modelService;
			this.queryPreprocessorRegistry = queryPreprocessorRegistry;
		}


		@Override
		protected FlexibleSearch getFlexibleSearchInstance()
		{
			return this.flexibleSearch;
		}

		@Override
		protected SessionService getSessionService()
		{
			return this.sessionService;
		}

		@Override
		protected ModelService getModelService()
		{
			return this.modelService;
		}

		@Override
		public QueryPreprocessorRegistry lookupQueryPreprocessorRegistry()
		{
			return this.queryPreprocessorRegistry;
		}
	}
}
