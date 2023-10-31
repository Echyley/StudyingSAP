/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.media.impl;

import de.hybris.platform.cms2.common.exceptions.PermissionExceptionUtils;
import de.hybris.platform.cms2.data.PageableData;
import de.hybris.platform.cmsfacades.data.MediaFolderData;
import de.hybris.platform.cmsfacades.media.CMSMediaFolderFacade;
import de.hybris.platform.cmsfacades.media.service.CMSMediaFolderService;
import de.hybris.platform.core.model.media.MediaFolderModel;
import de.hybris.platform.media.storage.MediaStorageConfigService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.search.impl.SearchResultImpl;
import de.hybris.platform.servicelayer.security.permissions.PermissionCRUDService;
import de.hybris.platform.servicelayer.security.permissions.PermissionsConstants;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


/**
 * default implementation of CMSMediaFolderFacade
 */
public class DefaultCMSMediaFolderFacade implements CMSMediaFolderFacade
{
	private final CMSMediaFolderService cmsMediaFolderService;
	private final PermissionCRUDService permissionCRUDService;
	private final MediaStorageConfigService mediaStorageConfigService;

	DefaultCMSMediaFolderFacade(final CMSMediaFolderService cmsMediaFolderService,
			final PermissionCRUDService permissionCRUDService,
			final MediaStorageConfigService mediaStorageConfigService) {
		this.cmsMediaFolderService = cmsMediaFolderService;
		this.permissionCRUDService = permissionCRUDService;
		this.mediaStorageConfigService = mediaStorageConfigService;
	}

	@Override
	public SearchResult<MediaFolderData> findMediaFolders(String text, PageableData pageableData)
	{
		// check read permission
		if (!getPermissionCRUDService().canReadType(MediaFolderModel._TYPECODE))
		{
			PermissionExceptionUtils.createTypePermissionException(PermissionsConstants.READ, MediaFolderModel._TYPECODE);
		}

		final SearchResult<MediaFolderModel> searchResults = getCmsMediaFolderService().findMediaFolders(text, pageableData);
		Collection<String> securedFolders = getMediaStorageConfigService().getSecuredFolders();

		final List<MediaFolderData> convertedResults = searchResults.getResult().stream()
				.filter(folder -> !securedFolders.stream().anyMatch(securedFolder -> securedFolder.equals(folder.getQualifier())))
				.map(this::converModelToData) //
				.collect(Collectors.toList());
		return new SearchResultImpl<>(convertedResults, searchResults.getTotalCount(), searchResults.getRequestedCount(),
				searchResults.getRequestedStart());
	}

	/**
	 * Converts the media folder model to the associated data representation
	 *
	 * @param model
	 *           {@code MediaFolderModel} to be converted
	 * @return the {@code MediaFolderData} representation of the given model
	 */
	protected MediaFolderData converModelToData(final MediaFolderModel model)
	{
		final MediaFolderData data = new MediaFolderData();
		data.setQualifier(model.getQualifier());
		return data;
	}

	protected CMSMediaFolderService getCmsMediaFolderService()
	{
		return cmsMediaFolderService;
	}

	protected PermissionCRUDService getPermissionCRUDService()
	{
		return permissionCRUDService;
	}

	protected MediaStorageConfigService getMediaStorageConfigService()
	{
		return mediaStorageConfigService;
	}

}
