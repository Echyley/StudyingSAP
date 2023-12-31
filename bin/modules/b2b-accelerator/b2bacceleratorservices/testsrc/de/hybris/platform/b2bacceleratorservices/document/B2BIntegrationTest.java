/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2bacceleratorservices.document;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Ignore;


@Ignore
public class B2BIntegrationTest extends ServicelayerTransactionalTest
{

	@Resource
	protected UserService userService;
	@Resource
	protected BaseSiteService baseSiteService;
	@Resource
	protected B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService;
	@Resource
	public SessionService sessionService;

	/**
	 * Sets the user in the session and updates the branch in session context.
	 *
	 * @param userId
	 * @return A {@link de.hybris.platform.core.model.user.UserModel}
	 */
	public B2BCustomerModel login(final String userId)
	{
		final B2BCustomerModel user = userService.getUserForUID(userId, B2BCustomerModel.class);
		Assert.assertNotNull(userId + " user is null", user);
		login(user);
		return user;
	}

	public void login(final UserModel user)
	{
		baseSiteService.setCurrentBaseSite(baseSiteService.getBaseSiteForUID("storetemplate"), false);
		userService.setCurrentUser(user);
		b2bUnitService.updateBranchInSession(sessionService.getCurrentSession(), user);
	}

	protected PageableData createPageableData(final int pageNumber, final int pageSize, final String sortCode)
	{
		final PageableData pageableData = new PageableData();
		pageableData.setCurrentPage(pageNumber);
		pageableData.setSort(sortCode);
		pageableData.setPageSize(pageSize);
		return pageableData;
	}
}
