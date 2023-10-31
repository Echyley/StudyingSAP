/*
 *  Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.webhookservices.cache;

import de.hybris.platform.core.Registry;
import de.hybris.platform.core.Tenant;


/**
 * A webhook tenant utility class.
 */
public final class WebhookTenantUtil
{
	private static Tenant tenant;

	private WebhookTenantUtil()
	{
		// non-instantiable utility class
	}

	public static String getCurrentTenantId()
	{
		return getTenant().getTenantID();
	}

	static void setTenant(final Tenant tenant)
	{
		WebhookTenantUtil.tenant = tenant;
	}

	private static Tenant getTenant()
	{
		if (tenant == null)
		{
			tenant = Registry.getCurrentTenant();
		}
		return tenant;
	}

}
