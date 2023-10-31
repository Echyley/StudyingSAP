/*
 *
 *  * [y] hybris Platform
 *  *
 *  * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 *  *
 *  * This software is the confidential and proprietary information of SAP
 *  * ("Confidential Information"). You shall not disclose such Confidential
 *  * Information and shall use it only in accordance with the terms of the
 *  * license agreement you entered into with SAP.
 *
 */

package com.hybris.backoffice.cockpitng.search;

import static com.hybris.backoffice.cockpitng.dataaccess.facades.search.DefaultPlatformFieldSearchFacadeStrategy.BACKOFFICE_CACHE_ON_READ_REPLICA_ENABLED;
import static com.hybris.backoffice.cockpitng.dataaccess.facades.search.DefaultPlatformFieldSearchFacadeStrategy.BACKOFFICE_SEARCH_READ_REPLICA_ENABLED;
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

import com.hybris.backoffice.cockpitng.dataaccess.facades.search.DefaultPlatformFieldSearchFacadeStrategy;
import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.AbstractTenant;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.genericsearch.GenericSearchService;
import de.hybris.platform.genericsearch.impl.DefaultGenericSearchService;
import de.hybris.platform.jalo.flexiblesearch.FlexibleSearch;
//import de.hybris.platform.jalo.flexiblesearch.FlexibleSearchCacheUnit;
import de.hybris.platform.jalo.flexiblesearch.internal.FlexibleSearchExecutor;
import de.hybris.platform.jalo.flexiblesearch.internal.ReadOnlyConditionsHelper;
import de.hybris.platform.jdbcwrapper.HybrisDataSource;
import de.hybris.platform.persistence.flexiblesearch.TranslatedQuery;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.impl.DefaultFlexibleSearchService;
import de.hybris.platform.servicelayer.search.internal.preprocessor.QueryPreprocessorRegistry;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.testframework.PropertyConfigSwitcher;
import de.hybris.platform.testframework.TestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.google.common.collect.Sets;
import com.hybris.backoffice.cockpitng.search.builder.impl.GenericConditionQueryBuilder;
import com.hybris.backoffice.cockpitng.search.builder.impl.LocalizedGenericConditionQueryBuilder;
import com.hybris.backoffice.widgets.advancedsearch.engine.AdvancedSearchQueryData;
import com.hybris.cockpitng.dataaccess.facades.search.FieldSearchFacadeStrategy;
import com.hybris.cockpitng.search.data.SearchQueryData;
import com.hybris.cockpitng.search.data.ValueComparisonOperator;

/**
 * Due to CI Pipeline env is not support multiple datasource,
 * so temporary disable current IntegrationTest annotation
 */
//@IntegrationTest
public class ReadOnlyFlexibleSearchTest extends ServicelayerBaseTest
{
    private static final String READ_ONLY_CACHE_DOMAIN = "readOnlyCacheDomain";

    private final PropertyConfigSwitcher flexibleSearchReadOnlyDataSource = new PropertyConfigSwitcher(
            ReadOnlyConditionsHelper.PARAM_FS_READ_ONLY_DATASOURCE);

    private final PropertyConfigSwitcher backofficeSearchReadReplicaEnabledSwitcher =
            new PropertyConfigSwitcher(BACKOFFICE_SEARCH_READ_REPLICA_ENABLED);

    private final PropertyConfigSwitcher backofficeCacheOnReadReplicaEnabledSwitcher =
            new PropertyConfigSwitcher(BACKOFFICE_CACHE_ON_READ_REPLICA_ENABLED);

    private static final String PRODUCT_CODE = "Product";
    private final static String READ_ONLY_DATASOURCE_ID = "readonly";
    private final static String MASTER_DATASOURCE_ID = "master";

    private final static Set<Character> queryBuilderSeparators = Sets.newHashSet(ArrayUtils.toObject(new char[]
            { ' ', ',', ';', '\t', '\n', '\r' }));

    @Resource
    private ModelService modelService;
    @Resource
    private SessionService sessionService;
    @Resource
    private ConfigurationService configurationService;
    @Resource
    private TypeService typeService;
    @Resource
    private CommonI18NService commonI18NService;
    @Resource
    private QueryPreprocessorRegistry queryPreprocessorRegistry;
    @Resource
    private I18NService i18nService;

    private FlexibleSearchExecutor flexibleSearchExecutor;
    private TestFlexibleSearch flexibleSearch;
    private AbstractTenant tenant;
    private SearchQueryData searchQueryData;

    private FieldSearchFacadeStrategy<ItemModel> fieldSearchFacadeStrategy;

    //@Before
    public void setUp()
    {
        final GenericSearchService genericSearchService = new DefaultGenericSearchService();
        this.tenant = Registry.getCurrentTenant();
        this.flexibleSearchExecutor = spy(new FlexibleSearchExecutor(this.tenant));
        this.flexibleSearch = new TestFlexibleSearch(this.flexibleSearchExecutor);

        ((DefaultGenericSearchService) genericSearchService).setSessionService(this.sessionService);
        ((DefaultGenericSearchService) genericSearchService).setTypeService(this.typeService);

        final FlexibleSearchService flexibleSearchService = new TestFlexibleSearchService(
                this.flexibleSearch,
                this.sessionService,
                this.modelService,
                this.queryPreprocessorRegistry);

        ((DefaultGenericSearchService) genericSearchService).setFlexibleSearchService(flexibleSearchService);

        this.fieldSearchFacadeStrategy = new DefaultPlatformFieldSearchFacadeStrategy<>();

        ((DefaultPlatformFieldSearchFacadeStrategy) this.fieldSearchFacadeStrategy).setGenericSearchService(genericSearchService);
        ((DefaultPlatformFieldSearchFacadeStrategy) this.fieldSearchFacadeStrategy).setConfigurationService(
                this.configurationService);
        ((DefaultPlatformFieldSearchFacadeStrategy) this.fieldSearchFacadeStrategy).setTypeService(this.typeService);

        final GenericConditionQueryBuilder genericConditionBuilder = new GenericConditionQueryBuilder();
        genericConditionBuilder.setTypeService(this.typeService);
        genericConditionBuilder.setSeparators(queryBuilderSeparators);

        final LocalizedGenericConditionQueryBuilder localizedConditionQueryBuilder = new LocalizedGenericConditionQueryBuilder();
        localizedConditionQueryBuilder.setCommonI18NService(this.commonI18NService);
        localizedConditionQueryBuilder.setI18nService(this.i18nService);
        localizedConditionQueryBuilder.setTypeService(this.typeService);
        localizedConditionQueryBuilder.setSeparators(queryBuilderSeparators);

        final AdvancedSearchQueryData.Builder searchQueryDataBuilder = new AdvancedSearchQueryData.Builder(PRODUCT_CODE);
        searchQueryDataBuilder.globalOperator(ValueComparisonOperator.OR);
        searchQueryDataBuilder.pageSize(1);

        this.searchQueryData = searchQueryDataBuilder.build();
        this.tenant.getCache().clear();
    }

    //@After
    public void tearDown()
    {
        TestUtils.enableFileAnalyzer();
        this.flexibleSearchReadOnlyDataSource.switchBackToDefault();
    }

    //@Test
    public void shouldUseReadOnlyDataSource()
    {
        // given
        final var expectedDs = this.getReadOnlyDataSource();

        final var otherDs = this.tenant.getDataSource();

        this.flexibleSearchReadOnlyDataSource.switchToValue(READ_ONLY_DATASOURCE_ID);
        this.backofficeSearchReadReplicaEnabledSwitcher.switchToValue("true");

        // when
        final var pageable = this.fieldSearchFacadeStrategy.search(this.searchQueryData);
        pageable.getAllResults();

        // then
        this.verifyDataSourceWasUsedOnExecute(expectedDs);
        this.verifyDataSourceWasNotUsedOnExecute(otherDs);
    }

    //@Test
    public void shouldNotUseReadOnlyDataSource()
    {
        //given
        final var expectedDs = this.tenant.getDataSource();
        final var otherDs = this.getReadOnlyDataSource();

        this.backofficeSearchReadReplicaEnabledSwitcher.switchToValue("false");
        this.flexibleSearchReadOnlyDataSource.switchToValue(MASTER_DATASOURCE_ID);

        // when
        final var pageable = this.fieldSearchFacadeStrategy.search(this.searchQueryData);
        pageable.getAllResults();

        // then
        this.verifyDataSourceWasUsedOnExecute(expectedDs);
        this.verifyDataSourceWasNotUsedOnExecute(otherDs);
    }

    //@Test
    public void shouldUseReadOnlyDataSourceAndCache()
    {
        // given
        final var expectedDs = this.getReadOnlyDataSource();
        final var otherDs = this.tenant.getDataSource();

        this.flexibleSearchReadOnlyDataSource.switchToValue(READ_ONLY_DATASOURCE_ID);

        this.backofficeSearchReadReplicaEnabledSwitcher.switchToValue("true");
        this.backofficeCacheOnReadReplicaEnabledSwitcher.switchToValue("true");

        // when
        final var pageable = this.fieldSearchFacadeStrategy.search(this.searchQueryData);
        pageable.getAllResults();

        // then
        this.verifyDataSourceWasUsedOnExecute(expectedDs);
        this.verifyDataSourceWasNotUsedOnExecute(otherDs);
        this.verifyCacheForReadOnlyReplicaWasUsed();
    }

    //@Test
    public void shouldUseReadOnlyDataSourceAndDontCache()
    {
        // given
        final HybrisDataSource expectedDs = this.getReadOnlyDataSource();
        final HybrisDataSource otherDs = this.tenant.getDataSource();

        this.flexibleSearchReadOnlyDataSource.switchToValue(READ_ONLY_DATASOURCE_ID);

        this.backofficeSearchReadReplicaEnabledSwitcher.switchToValue("true");
        this.backofficeCacheOnReadReplicaEnabledSwitcher.switchToValue("false");

        // when
        final var pageable = this.fieldSearchFacadeStrategy.search(this.searchQueryData);
        pageable.getAllResults();

        // then
        this.verifyDataSourceWasUsedOnExecute(expectedDs);
        this.verifyDataSourceWasNotUsedOnExecute(otherDs);
        this.verifyCacheForReadOnlyReplicaWasNotUsed();
    }

    //@Test
    public void shouldNotUseReadOnlyDataSourceAndDontCache()
    {
        // given
        final HybrisDataSource expectedDs = this.tenant.getDataSource();
        final HybrisDataSource otherDs = this.getReadOnlyDataSource();

        this.flexibleSearchReadOnlyDataSource.switchToValue(READ_ONLY_DATASOURCE_ID);

        this.backofficeSearchReadReplicaEnabledSwitcher.switchToValue("false");
        this.backofficeCacheOnReadReplicaEnabledSwitcher.switchToValue("false");

        // when
        final var pageable = this.fieldSearchFacadeStrategy.search(this.searchQueryData);
        pageable.getAllResults();

        // then
        this.verifyDataSourceWasUsedOnExecute(expectedDs);
        this.verifyDataSourceWasNotUsedOnExecute(otherDs);
        this.verifyCacheForReadOnlyReplicaWasNotUsed();
    }

    private HybrisDataSource getReadOnlyDataSource()
    {
        return this.tenant.getAllSlaveDataSources()
                .stream()
                .filter(ds -> READ_ONLY_DATASOURCE_ID.equals(ds.getID()))
                .findFirst()
                .orElseThrow();
    }

    private void verifyDataSourceWasUsedOnExecute(final HybrisDataSource expectedDS)
    {
        verify(this.flexibleSearchExecutor, atLeastOnce())
                .execute(anyInt(), anyInt(), anyBoolean(), any(TranslatedQuery.class), anyList(), anyMap(),
                        any(PK.class), anyInt(), anySet(), anyList(), eq(expectedDS));
    }

    private void verifyDataSourceWasNotUsedOnExecute(final HybrisDataSource notExpectedDS)
    {

        final ArgumentCaptor<DataSource> dataSourceArgumentCaptor = ArgumentCaptor.forClass(DataSource.class);

        verify(this.flexibleSearchExecutor, atLeastOnce())
                .execute(anyInt(), anyInt(), anyBoolean(), any(TranslatedQuery.class), anyList(), anyMap(),
                        any(PK.class), anyInt(), anySet(), anyList(), dataSourceArgumentCaptor.capture());

        assertThat(dataSourceArgumentCaptor.getAllValues()).doesNotContain(notExpectedDS);
    }

    private void verifyCacheForReadOnlyReplicaWasUsed()
    {
        assertThat(this.flexibleSearch.cacheKeysArgumentCaptor)
                .extracting("dataSourceCacheDomain").containsOnly(READ_ONLY_CACHE_DOMAIN);
    }

    private void verifyCacheForReadOnlyReplicaWasNotUsed()
    {
        assertThat(this.flexibleSearch.cacheKeysArgumentCaptor)
                .extracting("dataSourceCacheDomain").doesNotContain(READ_ONLY_CACHE_DOMAIN);
    }

    public static class TestFlexibleSearch extends FlexibleSearch
    {
        private final List<FlexibleSearchCacheKey> cacheKeysArgumentCaptor = new ArrayList<>();

        public TestFlexibleSearch(final FlexibleSearchExecutor executor)
        {
            super(executor);
        }

        /*@Override
        public FlexibleSearchCacheUnit createCacheUnit(final int prefetchSize, final Set<PK> languages, final Map _values,
                                                       final FlexibleSearchCacheKey cacheKey)
        {
            this.cacheKeysArgumentCaptor.add(cacheKey);
            return super.createCacheUnit(prefetchSize, languages, _values, cacheKey);
        }*/
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