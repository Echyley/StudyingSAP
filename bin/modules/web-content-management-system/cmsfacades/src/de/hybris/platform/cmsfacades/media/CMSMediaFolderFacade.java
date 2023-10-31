/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.media;

import de.hybris.platform.cms2.data.PageableData;
import de.hybris.platform.cmsfacades.data.MediaFolderData;
import de.hybris.platform.servicelayer.search.SearchResult;


/**
 * facade for cms media folder
 */
public interface CMSMediaFolderFacade
{
	/**
	 * Finds media folders using a free-text form. It also supports pagination.
	 *
	 * @param text
	 *           The free-text string to be used on the media folder search
	 * @param pageableData
	 *           the pagination object
	 * @return the search result object.
	 */
	SearchResult<MediaFolderData> findMediaFolders(String text, PageableData pageableData);
}
