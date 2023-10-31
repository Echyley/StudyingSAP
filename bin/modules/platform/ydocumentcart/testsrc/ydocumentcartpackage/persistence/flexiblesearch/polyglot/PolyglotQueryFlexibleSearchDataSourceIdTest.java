/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package ydocumentcartpackage.persistence.flexiblesearch.polyglot;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.servicelayer.ServicelayerTransactionalBaseTest;
import de.hybris.platform.servicelayer.constants.ServicelayerConstants;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@IntegrationTest
public class PolyglotQueryFlexibleSearchDataSourceIdTest extends ServicelayerTransactionalBaseTest
{
    @Resource
    private FlexibleSearchService flexibleSearchService;

    @Test
    public void testPolyglotDataSourceId()
    {
        // given
        final String polyglotQuery = "GET {Cart} WHERE {code}=?code";
        final Map<String, Object> params = new HashMap<>();
        params.put("code", "1");

        // when
        final SearchResult<Object> polyglotSearchResult = flexibleSearchService.search(polyglotQuery, params);

        // then
        assertEquals(ServicelayerConstants.UNDEFINED_DATASOURCE, polyglotSearchResult.getDataSourceId());
    }

    @Test
    public void testFlexibleSearchDataSourceId()
    {
        // given
        final String flexibleSearchQuery = "SELECT {PK} FROM {Title} WHERE {code}=?code";
        final Map<String, Object> params = new HashMap<>();
        params.put("code", "1");

        // when
        final SearchResult<Object> flexibleSearchResult = flexibleSearchService.search(flexibleSearchQuery, params);

        // then
        assertEquals("master", flexibleSearchResult.getDataSourceId());
    }
}
