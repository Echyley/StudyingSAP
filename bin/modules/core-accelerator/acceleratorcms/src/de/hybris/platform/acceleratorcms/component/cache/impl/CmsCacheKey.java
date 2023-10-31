/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorcms.component.cache.impl;

import de.hybris.platform.regioncache.key.CacheKey;
import de.hybris.platform.regioncache.key.CacheUnitValueType;


public class CmsCacheKey implements CacheKey
{
	private static final String CMS_CACHE_UNIT_CODE = "__CMS_CACHE__";
	private final String key;
	private final String tenantId;

	public CmsCacheKey(final String key, final String tenantId)
	{
		this.key = key;
		this.tenantId = tenantId;
	}

	@Override
	public Object getTypeCode()
	{
		return CMS_CACHE_UNIT_CODE;
	}

	@Override
	public String getTenantId()
	{
		return tenantId;
	}

	@Override
	public CacheUnitValueType getCacheValueType()
	{
		return CacheUnitValueType.SERIALIZABLE;
	}

	@Override
	public String toString()
	{
		return "CmsCacheKey [key=" + key + ", tenantId=" + tenantId + "]";
	}

	@Override
	public int hashCode()
	{
		int result = 1;
		result = 31 * result + (key == null ? 0 : key.hashCode());
		result = 31 * result + (tenantId == null ? 0 : tenantId.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (super.getClass() != obj.getClass())
		{
			return false;
		}
		final CmsCacheKey other = (CmsCacheKey) obj;
		if (tenantId == null)
		{
			if (other.tenantId != null)
			{
				return false;
			}
		}
		else if (!tenantId.equals(other.tenantId))
		{
			return false;
		}
		if (key == null)
		{
			if (other.key != null)
			{
				return false;
			}
		}
		else if (!key.equals(other.key))
		{
			return false;
		}
		return true;
	}
}
