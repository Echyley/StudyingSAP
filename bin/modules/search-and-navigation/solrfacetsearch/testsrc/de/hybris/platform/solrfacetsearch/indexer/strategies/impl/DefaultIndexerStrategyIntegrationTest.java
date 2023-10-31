package de.hybris.platform.solrfacetsearch.indexer.strategies.impl;

import javax.annotation.Resource;

import org.junit.Test;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.flexiblesearch.internal.ReadOnlyConditionsHelper;
import de.hybris.platform.solrfacetsearch.integration.AbstractIntegrationTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@IntegrationTest
public class DefaultIndexerStrategyIntegrationTest extends AbstractIntegrationTest {

    @Resource
    private DefaultIndexerStrategy defaultIndexerStrategy;

    @Test
    public void testResolvesReadOnlyDataSourceToTrueWhenEnabledInSession()
    {
        testResolvesUseReadOnlyDataSourceIfEnabledInSession(true);
    }

    @Test
    public void testResolvesReadOnlyDataSourceToFalseWhenDisabledInSession()
    {
        testResolvesUseReadOnlyDataSourceIfEnabledInSession(false);
    }

    private void testResolvesUseReadOnlyDataSourceIfEnabledInSession(final boolean enabledInSession)
    {
        // given
        final SessionContext ctx = JaloSession.getCurrentSession().getSessionContext();
        ctx.setAttribute(ReadOnlyConditionsHelper.CTX_ENABLE_FS_ON_READ_REPLICA, enabledInSession);

        // when
        final boolean useReadOnlyDataSource = defaultIndexerStrategy.resolveSessionUseReadOnlyDataSource();

        // then
        assertEquals(useReadOnlyDataSource, enabledInSession);
    }

    @Test
    public void testResolvesReadOnlyDataSourceToFalseWhenNotConfiguredInSession()
    {
        // given
        final SessionContext ctx = JaloSession.getCurrentSession().getSessionContext();
        ctx.removeAttribute(ReadOnlyConditionsHelper.CTX_ENABLE_FS_ON_READ_REPLICA);

        // when
        final boolean useReadOnlyDataSource = defaultIndexerStrategy.resolveSessionUseReadOnlyDataSource();

        // then
        assertFalse(useReadOnlyDataSource);
    }
}
