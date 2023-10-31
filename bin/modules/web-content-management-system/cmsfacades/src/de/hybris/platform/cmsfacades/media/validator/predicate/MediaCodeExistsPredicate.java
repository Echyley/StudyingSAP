/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.media.validator.predicate;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cmsfacades.data.MediaData;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.media.MediaService;

import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Required;


/**
 * Predicate to test if a given media code maps to an existing media.
 * <p>
 * Returns <tt>TRUE</tt> if the media exists; <tt>FALSE</tt> otherwise.
 * </p>
 */
public class MediaCodeExistsPredicate implements Predicate<MediaData>
{
	private MediaService mediaService;
	private CatalogVersionService catalogVersionService;

	@Override
	public boolean test(final MediaData target)
	{
		try
		{
			final CatalogVersionModel catalogVersion = getCatalogVersionService().getCatalogVersion(target.getCatalogId(),
				target.getCatalogVersion());
			getMediaService().getMedia(catalogVersion, target.getCode());
		}
		catch (final UnknownIdentifierException e)
		{
			return false;
		}
		catch (final AmbiguousIdentifierException e) { 
			return true;
		}
		return true;
	}

	protected MediaService getMediaService()
	{
		return mediaService;
	}

	@Required
	public void setMediaService(final MediaService mediaService)
	{
		this.mediaService = mediaService;
	}

	protected CatalogVersionService getCatalogVersionService() {
		return catalogVersionService;
	}

	@Required
	public void setCatalogVersionService(final CatalogVersionService catalogVersionService)
	{
		this.catalogVersionService = catalogVersionService;
	}
	

}
