/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.media.service.impl;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.data.PageableData;
import de.hybris.platform.cms2.servicelayer.daos.CMSMediaDao;
import de.hybris.platform.cmsfacades.media.service.CMSMediaService;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.search.SearchResult;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of <code>CMSMediaService</code> interface.
 */
public class DefaultCMSMediaService implements CMSMediaService {
    private CMSMediaDao cmsMediaDao;
    @Override
    public SearchResult<MediaModel> findMediasForCatalogVersion(final String mask, final String mimeType, final CatalogVersionModel catalogVersion, final PageableData pageableData)
    {
        return getCmsMediaDao().findMediasForCatalogVersion(mask, mimeType, catalogVersion, pageableData);
    }


    protected CMSMediaDao getCmsMediaDao()
    {
        return cmsMediaDao;
    }

    @Required
    public void setCmsMediaDao(final CMSMediaDao cmsMediaDao)
    {
        this.cmsMediaDao = cmsMediaDao;
    }

}
