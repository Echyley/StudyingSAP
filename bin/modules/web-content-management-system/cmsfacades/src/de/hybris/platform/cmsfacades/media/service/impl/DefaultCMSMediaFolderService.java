/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.media.service.impl;

import de.hybris.platform.cms2.data.PageableData;
import de.hybris.platform.cms2.servicelayer.daos.CMSMediaFolderDao;
import de.hybris.platform.cmsfacades.media.service.CMSMediaFolderService;
import de.hybris.platform.core.model.media.MediaFolderModel;
import de.hybris.platform.servicelayer.search.SearchResult;


/**
 * Default implementation of <code>CMSMediaFolderService</code> interface.
 */
public class DefaultCMSMediaFolderService implements CMSMediaFolderService
{
	private final CMSMediaFolderDao cmsMediaFolderDao;

	/**
	 * default constructor for create with CMSMediaFolderDao
	 * @param cmsMediaFolderDao
	 * 		- the cms media folder DAO
	 */
	public DefaultCMSMediaFolderService(final CMSMediaFolderDao cmsMediaFolderDao)
	{
		this.cmsMediaFolderDao = cmsMediaFolderDao;
	}

	@Override
	public SearchResult<MediaFolderModel> findMediaFolders(String text, PageableData pageableData)
	{
		return getCmsMediaFolderDao().findMediaFolders(text, pageableData);
	}

	protected CMSMediaFolderDao getCmsMediaFolderDao()
	{
		return cmsMediaFolderDao;
	}
}
