/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.facades.integrationtests;

import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.cpq.productconfig.facades.CartIntegrationFacade;
import de.hybris.platform.cpq.productconfig.services.integrationtests.BaseIntegrationServiceLayerTest;
import de.hybris.platform.servicelayer.security.auth.InvalidCredentialsException;

import javax.annotation.Resource;


public abstract class BaseIntegrationTest extends BaseIntegrationServiceLayerTest
{

	@Resource(name = "customerFacade")
	protected CustomerFacade customerFacade;

	@Resource(name = "cpqProductConfigCartIntegrationFacade")
	protected CartIntegrationFacade cartIntegrationFacade;

	@Resource(name = "cartFacade")
	protected CartFacade cartFacade;


	@Override
	public void login(final String userName, final String password) throws InvalidCredentialsException
	{
		authenticationService.login(userName, password);
		customerFacade.loginSuccess();
	}

}
