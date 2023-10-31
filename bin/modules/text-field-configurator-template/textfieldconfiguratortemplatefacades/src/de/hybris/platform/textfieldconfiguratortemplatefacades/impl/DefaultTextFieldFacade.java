/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.textfieldconfiguratortemplatefacades.impl;

import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.textfieldconfiguratortemplatefacades.TextFieldFacade;

import java.util.List;
import java.util.Objects;
import java.util.Optional;


/**
 * Default implementation of {@link TextFieldFacade}
 */
public class DefaultTextFieldFacade implements TextFieldFacade
{
	@Override
	public OrderEntryData getAbstractOrderEntry(final int entryNumber, final AbstractOrderData abstractOrder)
			throws CommerceCartModificationException
	{
		final List<OrderEntryData> entries = abstractOrder.getEntries();
		if (entries == null)
		{
			throw new CommerceCartModificationException("Cart is empty");
		}

		final Optional<OrderEntryData> entryOptional = entries.stream().filter(Objects::nonNull)
				.filter(e -> e.getEntryNumber() == entryNumber).findAny();
		if (entryOptional.isPresent())
		{
			return entryOptional.get();
		}
		else
		{
			throw new CommerceCartModificationException("Cart entry #" + entryNumber + " does not exist");
		}
	}
}
