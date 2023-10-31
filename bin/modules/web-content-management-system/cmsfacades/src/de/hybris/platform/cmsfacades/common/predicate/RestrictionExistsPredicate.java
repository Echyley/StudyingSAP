/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.common.predicate;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminRestrictionService;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Required;


/**
 * Predicate to test if a given restriction uid maps to an existing restriction.
 * <p>
 * Returns <tt>TRUE</tt> if the restriction exists; <tt>FALSE</tt> otherwise.
 * </p>
 */
public class RestrictionExistsPredicate implements Predicate<String>
{
	private CMSAdminRestrictionService adminRestrictionService;

	@Override
	public boolean test(final String target)
	{
		boolean result = true;
		try
		{
			getAdminRestrictionService().getRestriction(target);
		}
		catch (UnknownIdentifierException | AmbiguousIdentifierException | CMSItemNotFoundException e)
		{
			result = false;
		}
		return result;
	}

	protected CMSAdminRestrictionService getAdminRestrictionService()
	{
		return adminRestrictionService;
	}

	@Required
	public void setAdminRestrictionService(final CMSAdminRestrictionService adminRestrictionService)
	{
		this.adminRestrictionService = adminRestrictionService;
	}
}
