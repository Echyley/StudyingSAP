/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationservices.util.lifecycle.impl;

import de.hybris.platform.core.Registry;
import de.hybris.platform.core.Tenant;
import de.hybris.platform.core.TenantListener;
import de.hybris.platform.integrationservices.util.lifecycle.TenantLifecycle;
import de.hybris.platform.integrationservices.util.lifecycle.TenantLifecycleFactory;

import java.util.List;

import javax.validation.constraints.NotNull;

public class DefaultIntegrationTenantLifecycleFactory implements TenantLifecycleFactory
{
	@Override
	public TenantLifecycle create(@NotNull final Tenant tenant)
	{
		final List<TenantListener> tenantList = Registry.getTenantListeners();
		for (TenantListener each : tenantList)
		{
			if (each instanceof DefaultIntegrationTenantLifecycle && ((DefaultIntegrationTenantLifecycle) each).hasTenant(tenant))
			{
				return (DefaultIntegrationTenantLifecycle) each;
			}
		}
		final var lifecycle = new DefaultIntegrationTenantLifecycle(tenant);
		Registry.registerTenantListener(lifecycle);
		return lifecycle;
	}
}
