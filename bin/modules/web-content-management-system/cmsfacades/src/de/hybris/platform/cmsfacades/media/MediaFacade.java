/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.media;

import de.hybris.platform.cms2.data.PageableData;
import de.hybris.platform.cmsfacades.data.MediaData;
import de.hybris.platform.cmsfacades.data.NamedQueryData;
import de.hybris.platform.cmsfacades.dto.MediaFileDto;
import de.hybris.platform.cmsfacades.exception.ValidationException;
import de.hybris.platform.media.exceptions.MediaNotFoundException;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.List;


/**
 * Facade for managing Media.
 */
public interface MediaFacade
{
	/**
	 * Get a media DTO by code.
	 *
	 * @param code
	 *           - the code to search for
	 * @return the media
	 * @throws MediaNotFoundException
	 *            when the media cannot be found
	 * @deprecated since 1905, please use {@link #getMediaByUUID(String)} instead
	 */
	@Deprecated(since = "1905", forRemoval = true)
	MediaData getMediaByCode(String code);

	/**
	 * Get a media DTO by uuid.
	 *
	 * @param uuid
	 *           - the uuid to search for
	 * @throws MediaNotFoundException
	 *            when the media cannot be found
	 * @return the media
	 */
	MediaData getMediaByUUID(String uuid);

	/**
	 * Get a list of media DTO by their universally unique identifiers (uuids).
	 *
	 * @param uuids
	 *           - the list of uuids of the media to search for
	 * @throws MediaNotFoundException
	 *            when one of the media cannot be found
	 * @return list of media
	 */
	List<MediaData> getMediaByUUIDs(List<String> uuids);

	/**
	 * Search for a single page of media using a named query.
	 *
	 * @param namedQuery
	 *           - the named query
	 * @return the list of search results or empty collection
	 * @throws ValidationException
	 *            when the input contains validation errors
	 */
	List<MediaData> getMediaByNamedQuery(NamedQueryData namedQuery);

	/**
	 * Create a media item from an {@code InputStream}.
	 *
	 * @param media
	 *           - the attributes required to create a new media item
	 * @param mediaFile
	 *           - the actual file and an {@code InputStream} and its properties
	 * @return the newly created media
	 * @throws ValidationException
	 *            when the input contains validation errors
	 * @throws ConversionException
	 *            when unable to convert the DTOs to a hybris model
	 */
	MediaData addMedia(MediaData media, MediaFileDto mediaFile);

	/**
	 * Create a media item from an {@code InputStream}.
	 *
	 * @param media
	 *           - the attributes required to create a new media item
	 * @param mediaFile
	 *           - the actual file and an {@code InputStream} and its properties
	 * @param folder
	 * 			 - the folder that media will be upload to
	 * @return the newly created media
	 * @throws ValidationException
	 *            when the input contains validation errors
	 * @throws ConversionException
	 *            when unable to convert the DTOs to a hybris model
	 */
	default MediaData addMediaToFolder(MediaData media, MediaFileDto mediaFile, String folder)
	{
		return null;
	}

	/**
	 * Finds medias using a free-text form. It also supports pagination.
	 *
	 * @param mask
	 *           The free-text string to be used on the media search
	 * @param mimeType
	 *           The mime type to be used on the media search
	 * @param pageableData
	 *           the pagination object
	 * @return the search result object.
	 */
	default SearchResult<MediaData> findMedias(String mask, String mimeType, PageableData pageableData)
	{
		return null;
	}
}
