/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commercefacades.product.converters.populator;

import de.hybris.platform.commercefacades.product.ImageFormatMapping;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.commercefacades.product.data.ImageDataType;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaFormatModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.media.MediaContainerService;
import de.hybris.platform.servicelayer.media.MediaService;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;

/**
 * Populate the product data with the product's gallery images
 */
public abstract class AbstractProductImagePopulator<SOURCE extends ProductModel, TARGET extends ProductData> extends AbstractProductPopulator<SOURCE, TARGET>
{
	private MediaService mediaService;
	private MediaContainerService mediaContainerService;
	private ImageFormatMapping imageFormatMapping;
	private List<String> imageFormats;
	private Converter<MediaModel, ImageData> imageConverter;

	protected MediaService getMediaService()
	{
		return mediaService;
	}

	@Required
	public void setMediaService(final MediaService mediaService)
	{
		this.mediaService = mediaService;
	}

	protected MediaContainerService getMediaContainerService()
	{
		return mediaContainerService;
	}

	@Required
	public void setMediaContainerService(final MediaContainerService mediaContainerService)
	{
		this.mediaContainerService = mediaContainerService;
	}

	protected ImageFormatMapping getImageFormatMapping()
	{
		return imageFormatMapping;
	}

	@Required
	public void setImageFormatMapping(final ImageFormatMapping imageFormatMapping)
	{
		this.imageFormatMapping = imageFormatMapping;
	}

	protected List<String> getImageFormats()
	{
		return imageFormats;
	}

	@Required
	public void setImageFormats(final List<String> imageFormats)
	{
		this.imageFormats = imageFormats;
	}

	protected Converter<MediaModel, ImageData> getImageConverter()
	{
		return imageConverter;
	}

	@Required
	public void setImageConverter(final Converter<MediaModel, ImageData> imageConverter)
	{
		this.imageConverter = imageConverter;
	}

	protected void addImagesInFormats(final MediaContainerModel mediaContainer, final ImageDataType imageType, final int galleryIndex, final List<ImageData> list)
	{
		for (final String imageFormat : getImageFormats())
		{
			try
			{
				final String mediaFormatQualifier = getImageFormatMapping().getMediaFormatQualifierForImageFormat(imageFormat);
				if (mediaFormatQualifier != null)
				{
					final MediaFormatModel mediaFormat = getMediaService().getFormat(mediaFormatQualifier);
					if (mediaFormat != null)
					{
						final MediaModel media = getMediaContainerService().getMediaForFormat(mediaContainer, mediaFormat);
						if (media != null)
						{
							final var imageData = getImageConverter().convert(media);
							if (imageData != null)
							{
								imageData.setFormat(imageFormat);
								imageData.setImageType(imageType);
								if (ImageDataType.GALLERY == imageType)
								{
									imageData.setGalleryIndex(Integer.valueOf(galleryIndex));
								}
								list.add(imageData);
							}
						}
					}
				}
			}
			catch (final ModelNotFoundException ignore)
			{
				// Ignore
			}
		}
	}
}
