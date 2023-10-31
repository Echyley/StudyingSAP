/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cms2.servicelayer.daos.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.data.PageableData;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@UnitTest
@RunWith(MockitoJUnitRunner.Silent.class)
public class DefaultCMSMediaDaoTest {
    @Mock
    private FlexibleSearchService flexibleSearchService;
    @InjectMocks
    private DefaultCMSMediaDao mediaDao;

    @Mock
    private CatalogVersionModel catalogVersion;
    @Mock
    private PageableData pageableData;
    @Mock
    private SearchResult<MediaModel> searchResult;
    @Mock
    private MediaModel media;

    @Test
    public void shouldFindMedias()
    {
        doReturn(searchResult).when(flexibleSearchService).search(any(FlexibleSearchQuery.class));
        doReturn(Arrays.asList(media)).when(searchResult).getResult();

        final SearchResult<MediaModel> result = mediaDao.findMediasForCatalogVersion("valid", null, catalogVersion, pageableData);

        assertThat(result, not(nullValue()));
    }
}
