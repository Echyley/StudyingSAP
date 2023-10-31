package de.hybris.platform.hac.facade;

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
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.AbstractTenant;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.Registry;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.flexiblesearch.FlexibleSearch;
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
import de.hybris.platform.testframework.PropertyConfigSwitcher;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

@IntegrationTest
public class HacMaintenanceFacadeTest extends ServicelayerBaseTest {

    private static final String READ_ONLY_DATASOURCE = "f";

    private HacMaintenanceFacade facade;

    @Resource
    private ModelService modelService;

    @Resource
    private SessionService sessionService;

    @Resource
    private QueryPreprocessorRegistry queryPreprocessorRegistry;

    private FlexibleSearchExecutor flexibleSearchExecutor;
    private final PropertyConfigSwitcher flexibleSearchReadOnlyDataSource = new PropertyConfigSwitcher(
            ReadOnlyConditionsHelper.PARAM_FS_READ_ONLY_DATASOURCE);

    private HybrisDataSource mainDatasource;
    private HybrisDataSource readOnlyDatasource;


    @Before
    public void setUp()
    {
        this.flexibleSearchExecutor = spy(new FlexibleSearchExecutor(Registry.getCurrentTenant()));
        final TestFlexibleSearch flexibleSearch = new TestFlexibleSearch(this.flexibleSearchExecutor);
        final FlexibleSearchService flexibleSearchService = new TestFlexibleSearchService(flexibleSearch,
                this.sessionService,
                this.modelService,
                this.queryPreprocessorRegistry);

	    this.facade = new HacMaintenanceFacade();
	    this.facade.setFlexibleSearchService(flexibleSearchService);

        final AbstractTenant tenant = Registry.getCurrentTenant();
        this.mainDatasource = tenant.getDataSource();
        this.readOnlyDatasource = tenant.getAllSlaveDataSources()
                .stream()
                .filter(ds -> READ_ONLY_DATASOURCE.equals(ds.getID()))
                .findFirst()
                .orElseThrow();
    }

    @Test
    public void shouldUseOnlyReadOnlyDatasource()
    {
	    // Given
        this.flexibleSearchReadOnlyDataSource.switchToValue(READ_ONLY_DATASOURCE);

	    // When
        this.facade.getMediasForPk("1111");

        // Then
        this.verifyDataSourcesWereNotUsedOnExecute(this.mainDatasource);
        this.verifyDataSourceWasUsedOnExecute(this.readOnlyDatasource);
    }

    @Test
    public void shouldUseOnlyMainDatasource()
    {
        // Given
        this.flexibleSearchReadOnlyDataSource.switchToValue("");

        // When
        this.facade.getMediasForPk("1111");

        // Then
        this.verifyDataSourcesWereNotUsedOnExecute(this.readOnlyDatasource);
        this.verifyDataSourceWasUsedOnExecute(this.mainDatasource);
    }

    @After
    public void tearDown()
    {
        this.flexibleSearchReadOnlyDataSource.switchBackToDefault();
    }

    private void verifyDataSourceWasUsedOnExecute(final HybrisDataSource expectedDS)
    {
        verify(this.flexibleSearchExecutor, atLeastOnce())
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


    private static class TestFlexibleSearch extends FlexibleSearch
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

    private static class TestFlexibleSearchService extends DefaultFlexibleSearchService
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

        @Override
        protected FlexibleSearch getFlexibleSearchInstance()
        {
            return this.flexibleSearch;
        }
    }
}
