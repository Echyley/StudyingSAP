/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.common.predicate;

import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminPageService;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Required;


/**
 * Predicate to test if a given page uid maps to an active existing page.
 * <p>
 * Returns <tt>TRUE</tt> if the page exists; <tt>FALSE</tt> otherwise.
 * </p>
 */
public class PageExistsPredicate implements Predicate<String>
{

	private CMSAdminPageService adminPageService;

	@Override
	public boolean test(final String target)
	{
		boolean result = true;
		try
		{
			getAdminPageService().getPageForIdFromActiveCatalogVersion(target);
		}
		catch (final UnknownIdentifierException e)
		{
			result = false;
		}
		catch (final AmbiguousIdentifierException e)
		{
			//sonar fix, useless assignment
		}
		return result;
	}

	protected CMSAdminPageService getAdminPageService()
	{
		return adminPageService;
	}

	@Required
	public void setAdminPageService(final CMSAdminPageService adminPageService)
	{
		this.adminPageService = adminPageService;
	}

}
