/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorfacades.cmsitems.attributeconverters;

import static de.hybris.platform.acceleratorfacades.constants.AcceleratorFacadesConstants.MEDIA_CONTAINER_UUID_FIELD;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.SESSION_CLONE_COMPONENT_LOCALE;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.SESSION_CLONE_COMPONENT_SOURCE_ATTRIBUTE;
import static java.lang.String.format;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import de.hybris.platform.acceleratorcms.model.components.AbstractMediaContainerComponentModel;
import de.hybris.platform.cms2.cloning.strategy.impl.ComponentCloningStrategy;
import de.hybris.platform.cms2.common.functions.Converter;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cmsfacades.cmsitems.CloneComponentContextProvider;
import de.hybris.platform.cmsfacades.media.service.CMSMediaFormatService;
import de.hybris.platform.cmsfacades.mediacontainers.MediaContainerFacade;
import de.hybris.platform.cmsfacades.uniqueidentifier.UniqueItemIdentifierService;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaFormatModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Required;
import com.google.common.collect.Sets;

/**
 * The {@link Converter} is invoked when cloning a component which contain a {@code MediaContainer}. This converts a
 * Map<String, String> representation of a {@code MediaContainerModel} into an actual {@link MediaContainerModel}
 *
 * When cloning a component, the "source" Map contains the values as inputed by the request payload. The
 * "cloneComponentModel" is the component model created by cloning from the source component uuid specified in the
 * request payload.
 *
 * When editing a MediaContainer, if at least one media was modified in the "source" Map for any given MediaFormat, the
 * resulting "cloneComponentModel" should update the reference to the updated "source" Map value. Otherwise, a reference
 * to the cloned media model is used instead.
 */
public class CloneComponentMediaContainerDataToAttributeContentConverter
		implements Converter<Map<String, Object>, MediaContainerModel>
{
	private CMSMediaFormatService mediaFormatService;
	private UniqueItemIdentifierService uniqueItemIdentifierService;
	private MediaContainerFacade mediaContainerFacade;
	private CloneComponentContextProvider cloneComponentContextProvider;
	private ModelService modelService;
	private ComponentCloningStrategy componentCloningStrategy;

	@Override
	public MediaContainerModel convert(final Map<String, Object> source)
	{
		if (MapUtils.isEmpty(source))
		{
			return null;
		}

		final Map<String, String> sourceMedias = ((Map<String, String>) source.get(MediaContainerModel.MEDIAS));
		if (MapUtils.isEmpty(sourceMedias))
		{
			return null;
		}

		final Map<String, MediaFormatModel> mediaFormatModelMap = getMediaFormatService()
				.getMediaFormatsByComponentType(AbstractMediaContainerComponentModel.class).stream()
				.collect(toMap(MediaFormatModel::getQualifier, identity()));

		// find the map representation of the source component
		final Map<String, Object> srcAttributeMap = (Map<String, Object>) getCloneComponentContextProvider()
				.findItemForKey(SESSION_CLONE_COMPONENT_SOURCE_ATTRIBUTE);

		// find the current context language locale
		final String languageTag = (String) getCloneComponentContextProvider().findItemForKey(SESSION_CLONE_COMPONENT_LOCALE);
		final Map<String, Object> srcLocalizedAttributeMap = (Map<String, Object>) srcAttributeMap.get(languageTag);

		MediaContainerModel newMediaContainerModel = buildMediaContainer(srcLocalizedAttributeMap, source);
		final Map<String, MediaModel> mediaModelMap = buildMediaModelMapForAllMediaFormats(mediaFormatModelMap, newMediaContainerModel);
		Sets.newHashSet(mediaModelMap.keySet()).forEach(mediaFormat -> {
			final String mediaCode = sourceMedias.get(mediaFormat);
			final MediaModel cloneMediaModel = mediaModelMap.get(mediaFormat);
			/*
			 * compare the mediaCode in the "source" Map and in the "source attribute" Map to determine if it was modified
			 * for a given MediaFormat when the mediaCode is different, add the updated value to the map
			 */
			final Optional<Entry<String, Object>> mediaCodeExistsInBothMaps = findMediaCodeExistsInSourceAndAttributeMap(srcLocalizedAttributeMap, mediaFormat, mediaCode);
			if (mediaCodeExistsInBothMaps.isEmpty())
			{
				if (cloneMediaModel != null)
				{
					/*
					 * detach cloned Media model created initially. It is no longer needed because it will be replaced by the newMediaModel or set to null
					 */
					getModelService().detach(cloneMediaModel);
					mediaModelMap.remove(mediaFormat);
				}
				if (mediaCode != null)
				{
					final MediaModel newMediaModel = getUniqueItemIdentifierService().getItemModel(mediaCode, MediaModel.class)
							.orElseThrow(() -> new ConversionException(format("could not find a media for code %s in current catalog version", mediaCode)));
					final MediaFormatModel mediaFormatModel = mediaFormatModelMap.get(mediaFormat);
					newMediaModel.setMediaFormat(mediaFormatModel);
					mediaModelMap.put(mediaFormat, newMediaModel);
				}
			}
		});
		newMediaContainerModel.setMedias(mediaModelMap.values().stream().filter(Objects::nonNull).collect(Collectors.toList()));
		return newMediaContainerModel;
	}

	/**
	 * Builds a {@link Map} of all media format qualifiers defined in the platform and all {@link MediaModel} defined in
	 * the cloned component model. If a {@code MediaModel} is not found for a given media format, the value is set to
	 * <tt>NULL</tt>
	 *
	 * {@link Map} of key-value pairs of <{@link MediaFormatModel#getQualifier()} and {@link MediaModel}>
	 *
	 * @param mediaFormatModelMap
	 *           map containing key-value pairs of <{@link MediaFormatModel#getQualifier()} and {@link MediaFormatModel}>
	 * @param mediaContainerModel
	 *           the media container model defined on the cloned component model
	 * @return a map of of key-value pairs of <{@link MediaFormatModel#getQualifier()} and {@link MediaModel}>. If a
	 *         {@code MediaModel} is not found for a given media format, the entry value is <tt>NULL</tt>.
	 */
	protected Map<String, MediaModel> buildMediaModelMapForAllMediaFormats(final Map<String, MediaFormatModel> mediaFormatModelMap,
			final MediaContainerModel mediaContainerModel)
	{
		final Map<String, MediaModel> mediaModelMap = new HashMap<>();
		mediaFormatModelMap.keySet().forEach(mediaFormatKey -> mediaModelMap.put(mediaFormatKey, null));
		if (mediaContainerModel != null && mediaContainerModel.getMedias() != null)
		{
			mediaModelMap.putAll(mediaContainerModel.getMedias().stream()
					.collect(toMap((final MediaModel media) -> media.getMediaFormat().getQualifier(), identity())));
		}
		return mediaModelMap;
	}

	/**
	 * Build a new {@link MediaContainerModel} for Clone Component
	 *
	 * @param srcLocalizedAttributeMap
	 *           the Map representation of the source attribute (saved in the session)
	 * @param source
	 *           the Map representation of the clone source attribute
	 * @return  a {@link MediaContainerModel}
	 */
	protected MediaContainerModel buildMediaContainer(final Map<String, Object> srcLocalizedAttributeMap, final Map<String, Object> source)
	{
		final String sourceQualifier = Objects.nonNull(source.get(MediaContainerModel.QUALIFIER)) ? source.get(MediaContainerModel.QUALIFIER).toString() : null;
		MediaContainerModel newMediaContainerModel = this.getMediaContainerFacade().createMediaContainer(sourceQualifier);
		if (!MapUtils.isEmpty(srcLocalizedAttributeMap))
		{
			// get original clone Component Media Container source.
			final String mediaContainerUuid = (String) srcLocalizedAttributeMap.get(MEDIA_CONTAINER_UUID_FIELD);
			MediaContainerModel mediaContainerModel;
			if (Objects.nonNull(mediaContainerUuid))
			{
				mediaContainerModel = getUniqueItemIdentifierService().getItemModel(mediaContainerUuid, MediaContainerModel.class)
						.orElseThrow(() -> new UnknownIdentifierException(
								"Failed to find a MediaContainerModel for the given uuid" + mediaContainerUuid));
				// deep clone for media container only copy the data not include the PK. it will also deeply clone the medias under media container with new code.
				try
				{
					final String newMediaContainerQualifier = newMediaContainerModel.getQualifier();
					newMediaContainerModel = (MediaContainerModel) this.getComponentCloningStrategy().cloneForItemModel(mediaContainerModel);
					// remain the source qualifier or generate qualifier.
					newMediaContainerModel.setQualifier(newMediaContainerQualifier);
				} catch (CMSItemNotFoundException e)
				{
					throw new ConversionException("Failed to clone mediaContainerModel from component uuid: " + mediaContainerModel);
				}
			}
		}
		return newMediaContainerModel;
	}

	/**
	 * Compare the mediaCode in the "source" Map and in the "source attribute" Map to determine if it was modified for a
	 * given MediaFormat when the mediaCode is different, add the updated value to the map
	 *
	 * @param srcLocalizedAttributeMap
	 *           the Map representation of the source attribute (saved in the session)
	 * @param mediaFormat
	 *           the media format defined in the source Map
	 * @param mediaCode
	 *           the media code defined in the source Map
	 * @return {@link Optional} containing an {@link Entry} from the {@code srcAttributeMap} which matches the given
	 *         {@code mediaCode} and {@code mediaFormat}; otherwise {@link Optional#empty()}
	 */
	protected Optional<Entry<String, Object>> findMediaCodeExistsInSourceAndAttributeMap(final Map<String, Object> srcLocalizedAttributeMap,
			final String mediaFormat, final String mediaCode)
	{
		if (!MapUtils.isEmpty(srcLocalizedAttributeMap))
		{
			final Map<String, Object> srcLocalizedMediaMap = (Map<String, Object>) srcLocalizedAttributeMap
					.get(MediaContainerModel.MEDIAS);
			if (!MapUtils.isEmpty(srcLocalizedMediaMap))
			{
				return srcLocalizedMediaMap.entrySet().stream() //
						.filter(mapEntry -> mapEntry.getKey().equals(mediaFormat) && mapEntry.getValue().toString().equals(mediaCode))
						.findAny();
			}
		}
		return Optional.empty();
	}

	protected UniqueItemIdentifierService getUniqueItemIdentifierService()
	{
		return uniqueItemIdentifierService;
	}

	@Required
	public void setUniqueItemIdentifierService(final UniqueItemIdentifierService uniqueItemIdentifierService)
	{
		this.uniqueItemIdentifierService = uniqueItemIdentifierService;
	}

	protected CMSMediaFormatService getMediaFormatService()
	{
		return mediaFormatService;
	}

	@Required
	public void setMediaFormatService(final CMSMediaFormatService mediaFormatService)
	{
		this.mediaFormatService = mediaFormatService;
	}

	protected MediaContainerFacade getMediaContainerFacade()
	{
		return mediaContainerFacade;
	}

	@Required
	public void setMediaContainerFacade(final MediaContainerFacade mediaContainerFacade)
	{
		this.mediaContainerFacade = mediaContainerFacade;
	}

	protected CloneComponentContextProvider getCloneComponentContextProvider()
	{
		return cloneComponentContextProvider;
	}

	@Required
	public void setCloneComponentContextProvider(final CloneComponentContextProvider cloneComponentContextProvider)
	{
		this.cloneComponentContextProvider = cloneComponentContextProvider;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	protected ComponentCloningStrategy getComponentCloningStrategy()
	{
		return componentCloningStrategy;
	}

	@Required
	public void setComponentCloningStrategy(final ComponentCloningStrategy componentCloningStrategy)
	{
		this.componentCloningStrategy = componentCloningStrategy;
	}
}
