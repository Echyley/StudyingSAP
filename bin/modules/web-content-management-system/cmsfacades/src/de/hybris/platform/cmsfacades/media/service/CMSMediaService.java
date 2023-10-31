/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.media.service;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.data.PageableData;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.search.SearchResult;


/**
 * service to find cms medias
 */
public interface CMSMediaService {
    /**
     * Finds medias using a free-text form in a given catalog version. It also supports pagination.
     *
     * @param mask
     *           The free-text string to be used on the media search
     * @param mimeType
     *           The mime type to be used on the media search
     * @param catalogVersion
     *           The catalog version that is active in the session
     * @param pageableData
     *           the pagination object
     * @return the search result object.
     */
    SearchResult<MediaModel> findMediasForCatalogVersion(String mask, String mimeType, CatalogVersionModel catalogVersion,
                                                         PageableData pageableData);
}
