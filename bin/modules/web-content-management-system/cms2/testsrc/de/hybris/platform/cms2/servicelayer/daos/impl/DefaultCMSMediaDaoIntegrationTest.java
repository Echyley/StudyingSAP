/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cms2.servicelayer.daos.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.data.PageableData;
import de.hybris.platform.cms2.servicelayer.daos.CMSMediaDao;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.SearchResult;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@IntegrationTest
public class DefaultCMSMediaDaoIntegrationTest  extends ServicelayerTransactionalTest {
    @Resource
    private ModelService modelService;
    @Resource
    private CatalogVersionService catalogVersionService;
    @Resource
    private CMSMediaDao cmsMediaDao;

    private CatalogVersionModel catalogVersion;
    private MediaModel defaultMedia;

    @Before
    public void setUp() throws Exception
    {
        super.createCoreData();
        super.createDefaultCatalog();
        catalogVersion = catalogVersionService.getCatalogVersion("testCatalog", "Online");

        defaultMedia = createMediaModel("valid-id");
    }

    @Test
    public void shouldGetMediaForCode()
    {
        final PageableData pageableData = new PageableData();
        pageableData.setCurrentPage(0);
        pageableData.setPageSize(10);
        final SearchResult<MediaModel> result = cmsMediaDao.findMediasForCatalogVersion("valid-id", null, catalogVersion, pageableData);

        assertThat(result.getResult().get(0), equalTo(defaultMedia));
        assertThat(result.getCount(), equalTo(1));
    }

    protected MediaModel createMediaModel(final String code)
    {
        final MediaModel media = modelService.create(MediaModel.class);
        media.setCode(code);
        media.setCatalogVersion(catalogVersion);
        modelService.save(media);
        return media;
    }
}
