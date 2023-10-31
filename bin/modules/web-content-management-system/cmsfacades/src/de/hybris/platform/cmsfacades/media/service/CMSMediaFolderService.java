/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.media.service;

import de.hybris.platform.cms2.data.PageableData;
import de.hybris.platform.core.model.media.MediaFolderModel;
import de.hybris.platform.servicelayer.search.SearchResult;


/**
 * service to find cms media folder
 */
public interface CMSMediaFolderService
{
	/**
	 * Finds media folders using a free-text. It also supports pagination.
	 *
	 * @param text
	 * 		The free-text string to be used on the media folder search
	 * @param pageableData
	 * 		the pagination object
	 * @return the search result object.
	 */
	SearchResult<MediaFolderModel> findMediaFolders(String text, PageableData pageableData);
}
