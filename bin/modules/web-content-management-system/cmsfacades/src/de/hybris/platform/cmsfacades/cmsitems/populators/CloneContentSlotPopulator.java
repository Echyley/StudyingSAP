/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.cmsitems.populators;

import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_CLONE_ACTION;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_CONTENT_SLOT_UUID;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_IS_SLOT_CUSTOM;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_PAGE_UUID;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import de.hybris.platform.cms2.cloning.strategy.impl.ContentSlotCloningStrategy;
import de.hybris.platform.cms2.constants.Cms2Constants;
import de.hybris.platform.cms2.enums.CloneAction;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.servicelayer.services.RelationBetweenComponentsService;
import de.hybris.platform.cmsfacades.uniqueidentifier.UniqueItemIdentifierService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Required;


/**
 * CloneContentSlot populator for cmsfacades used to clone a ContentSlot
 */
public class CloneContentSlotPopulator implements Populator<Map<String, Object>, ItemModel>
{
	private ContentSlotCloningStrategy contentSlotCloningStrategy;
	private UniqueItemIdentifierService uniqueItemIdentifierService;
	private RelationBetweenComponentsService relationBetweenComponentsService;

	@Override
	public void populate(final Map<String, Object> source, final ItemModel itemModel) throws ConversionException
	{
		if (itemModel == null)
		{
			throw new ConversionException("Item Model used in the populator should not be null.");
		}
		if (source == null)
		{
			throw new ConversionException("Source map used in the populator should not be null.");
		}

		final String sourcePageUUID = (String) source.get(FIELD_PAGE_UUID);
		final String sourceContentSlotUUID = (String) source.get(FIELD_CONTENT_SLOT_UUID);
		final CloneAction cloneAction = getCloneAction(source);
		final Object isSlotCustom = source.get(FIELD_IS_SLOT_CUSTOM);

		if (isNotBlank(sourcePageUUID) && isNotBlank(sourceContentSlotUUID) && Objects.nonNull(isSlotCustom) && Objects.nonNull(cloneAction))
		{
			try
			{
				final AbstractPageModel sourcePage = getUniqueItemIdentifierService()
						.getItemModel(sourcePageUUID, AbstractPageModel.class)
						.orElseThrow(() -> new ConversionException("Unique identifier not present [" + sourcePageUUID + "]."));

				final ContentSlotModel sourceContentSlot = getUniqueItemIdentifierService()
						.getItemModel(sourceContentSlotUUID, ContentSlotModel.class)
						.orElseThrow(() -> new ConversionException("Unique identifier not present [" + sourceContentSlotUUID + "]."));

				final Map<String, Object> context = new HashMap<>();
				context.put(Cms2Constants.PAGE_CONTEXT_KEY, sourcePage);
				context.put(Cms2Constants.CLONE_ACTION, cloneAction);
				context.put(Cms2Constants.IS_SLOT_CUSTOM, isSlotCustom);

				getContentSlotCloningStrategy()
						.clone(sourceContentSlot, Optional.of((ContentSlotModel) itemModel), Optional.of(context));

				if (cloneAction.equals(CloneAction.CLONE))
				{
					getRelationBetweenComponentsService().maintainRelationBetweenComponentsOnSlot((ContentSlotModel) itemModel);
				}
			}
			catch (final CMSItemNotFoundException e)
			{
				throw new ConversionException("CMS Item not found", e);
			}
		}
		else if (Stream.of(isNotBlank(sourcePageUUID), isNotBlank(sourceContentSlotUUID), Objects.nonNull(cloneAction)).anyMatch(b -> b))
		{
			throw new ConversionException(
					"Incomplete arguments. You must provide a value to one of the following parameter: pageUuid, contentSlotUuid, cloneAction, isCustom.");
		}
	}

	/**
	 * Retrieves the CloneAction object from the source map.
	 * @param source the source map of the new slot
	 * @return the CloneAction that defined what to do with components inside the slot, null if the value can not be retrieved.
	 */
	protected CloneAction getCloneAction(final Map<String, Object> source)
	{
		final Object rawValue = source.get(FIELD_CLONE_ACTION);
		try
		{
			return CloneAction.valueOf(rawValue.toString().toUpperCase());
		}
		catch (final Exception ex)
		{
			return null;
		}
	}

	protected ContentSlotCloningStrategy getContentSlotCloningStrategy()
	{
		return contentSlotCloningStrategy;
	}

	@Required
	public void setContentSlotCloningStrategy(final ContentSlotCloningStrategy contentSlotCloningStrategy)
	{
		this.contentSlotCloningStrategy = contentSlotCloningStrategy;
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

	public RelationBetweenComponentsService getRelationBetweenComponentsService()
	{
		return relationBetweenComponentsService;
	}

	public void setRelationBetweenComponentsService(
			final RelationBetweenComponentsService relationBetweenComponentsService)
	{
		this.relationBetweenComponentsService = relationBetweenComponentsService;
	}
}
