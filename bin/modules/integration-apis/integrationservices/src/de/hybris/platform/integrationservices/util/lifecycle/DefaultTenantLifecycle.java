/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationservices.util.lifecycle;

import de.hybris.platform.core.Tenant;
import de.hybris.platform.integrationservices.util.lifecycle.impl.DefaultIntegrationTenantLifecycle;

import javax.validation.constraints.NotNull;

/**
 * @deprecated Preparing to remove this class. DefaultIntegrationTenantLifecycle should be used instead.
 */
@Deprecated(since = "2205", forRemoval = true)
public class DefaultTenantLifecycle extends DefaultIntegrationTenantLifecycle
{
	/**
	 * Instantiates the TenantLifecycle object for the given tenant
	 *
	 * @param tenant Tenant this lifecycle object is running under
	 */
	public DefaultTenantLifecycle(@NotNull final Tenant tenant)
	{
		super(tenant);
	}
}
