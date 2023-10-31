/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.datahubbackoffice.dataaccess.metadata.impl;

import de.hybris.platform.datahubbackoffice.dataaccess.PredefinableTypeCache;
import de.hybris.platform.datahubbackoffice.dataaccess.metadata.DataHubTypeService;
import de.hybris.platform.datahubbackoffice.service.datahub.DataHubServer;
import de.hybris.platform.datahubbackoffice.service.datahub.DataHubServerAware;
import de.hybris.platform.dataimportcommons.ApplicationBeans;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;

import javax.validation.constraints.NotNull;

import com.hybris.cockpitng.dataaccess.facades.type.DataType;
import com.hybris.datahub.dto.item.ItemData;

/**
 * Provides cache for another <code>DataHubTypeService</code>.
 */
public class CachingTypeService implements DataHubTypeService, DataHubServerAware, Serializable
{
	@Serial
	private static final long serialVersionUID = -1309276102528705769L;

	private transient DataHubTypeService unCachedService;
	private transient PredefinableTypeCache typeCache;

	@Override
	public boolean exists(final String code)
	{
		return cache().isDefined(code);
	}

	@Override
	public Collection<String> getAllTypeCodes()
	{
		return cache().definedCodes();
	}

	@Override
	public DataType getType(final String code)
	{
		DataType type = cache().get(code);
		if (type == null)
		{
			type = getDelegate().getType(code);
			cache().add(type);
		}
		return type;
	}

	@Override
	public String deriveTypeCode(final ItemData item)
	{
		return getDelegate().deriveTypeCode(item);
	}

	private PredefinableTypeCache cache()
	{
		return typeCache != null ? typeCache : initializeCache();
	}

	private PredefinableTypeCache initializeCache()
	{
		typeCache = new PredefinableTypeCache();
		typeCache.define(getDelegate().getAllTypeCodes());
		return typeCache;
	}

	@Override
	public void setDataHubServer(final DataHubServer server)
	{
		typeCache = null; // resets any cached items on the DataHub instance change
	}

	public void setDelegate(@NotNull final DataHubTypeService s)
	{
		unCachedService = s;
	}

	public DataHubTypeService getDelegate()
	{
		if (unCachedService ==  null)
		{
			unCachedService = ApplicationBeans.getBean("dataHubTypeService", DataHubTypeService.class);
		}
		return unCachedService;
	}

}
