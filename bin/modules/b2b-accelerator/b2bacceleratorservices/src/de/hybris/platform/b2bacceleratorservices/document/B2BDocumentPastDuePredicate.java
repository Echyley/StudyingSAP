/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2bacceleratorservices.document;

import java.util.Date;

import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.time.DateUtils;

import de.hybris.platform.b2bacceleratorservices.document.utils.XDate;
import de.hybris.platform.b2bacceleratorservices.model.B2BDocumentModel;


public class B2BDocumentPastDuePredicate implements Predicate
{
	public B2BDocumentPastDuePredicate()
	{
		// Empty Constructor
	}

	@Override
	public boolean evaluate(final Object doc)
	{
		if (!(doc instanceof B2BDocumentModel))
		{
			return false;
		}

		final B2BDocumentModel document = (B2BDocumentModel) doc;

		final Date now = XDate.setToEndOfDay(DateUtils.addDays(new Date(), -1));

		return document.getDueDate() != null && document.getDueDate().getTime() <= now.getTime();
	}
}
