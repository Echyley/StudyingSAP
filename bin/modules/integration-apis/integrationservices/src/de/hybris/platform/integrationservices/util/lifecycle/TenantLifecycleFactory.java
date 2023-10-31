/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationservices.util.lifecycle;

import de.hybris.platform.core.Tenant;

import javax.validation.constraints.NotNull;

/**
 * Defines methods to creating a {@link TenantLifecycle}
 */
public interface TenantLifecycleFactory
{
	/**
	 * Returns an instance of the {@code TenantLifecycle} for the specified tenant.
	 * This interface does not guarantee a new instance creation for every invocation. It's up to the
	 * implementations to create a new instance for every call or to use some cached value.
	 *
	 * @param tenant The lifecycle created is running under the given tenant.
	 * @return a non-null instance of the lifecycle for the tenant.
	 */
	TenantLifecycle create(@NotNull Tenant tenant);
}
