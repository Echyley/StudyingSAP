/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.common.predicate;

import de.hybris.platform.cmsfacades.uniqueidentifier.UniqueItemIdentifierService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import java.util.NoSuchElementException;
import java.util.function.BiPredicate;

import org.springframework.beans.factory.annotation.Required;


/**
 * Predicate to test if a given unique item identifier maps to an existing item model. It uses the
 * {@link UniqueItemIdentifierService} to find the item model associated to the given composite key.
 * <p>
 * Returns <tt>TRUE</tt> if the item model exists; <tt>FALSE</tt> otherwise.
 * </p>
 */
public class ItemModelExistsPredicate implements BiPredicate<String, Class<?>>
{
	private UniqueItemIdentifierService uniqueItemIdentifierService;

	@Override
	public boolean test(final String compositeKey, final Class<?> classType)
	{
		boolean result;
		try
		{
			result = getUniqueItemIdentifierService().getItemModel(compositeKey, classType).isPresent();
		}
		catch (final UnknownIdentifierException | ConversionException | NoSuchElementException e)
		{
			result = false;
		}
		return result;
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

}
