/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.items.impl;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.permissions.PermissionCachedCRUDService;
import de.hybris.platform.cms2.version.service.CMSVersionSessionContextProvider;
import de.hybris.platform.cmsfacades.data.AbstractCMSComponentData;
import de.hybris.platform.cmsfacades.items.ComponentItemFacade;
import de.hybris.platform.cmsfacades.rendering.ComponentRenderingService;
import de.hybris.platform.core.servicelayer.data.SearchPageData;

import java.util.Collection;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link ComponentItemFacade}.
 */
public class DefaultComponentItemFacade implements ComponentItemFacade
{
	private ComponentRenderingService componentRenderingService;
	private PermissionCachedCRUDService permissionCachedCRUDService;
	private CMSVersionSessionContextProvider cmsVersionSessionContextProvider;

	@Override
	public AbstractCMSComponentData getComponentById(final String componentId, final String categoryCode, final String productCode,
			final String catalogCode) throws CMSItemNotFoundException
	{
		getPermissionCachedCRUDService().initCache();
		getCmsVersionSessionContextProvider().initCache();
		return getComponentRenderingService().getComponentById(componentId, categoryCode, productCode, catalogCode);
	}

	@Override
	public SearchPageData<AbstractCMSComponentData> getComponentsByIds(final Collection<String> componentIds,
			final String categoryCode, final String productCode, final String catalogCode, final SearchPageData searchPageData)
	{
		getPermissionCachedCRUDService().initCache();
		getCmsVersionSessionContextProvider().initCache();
		if (CollectionUtils.isEmpty(componentIds))
		{
			return getComponentRenderingService().getAllComponents(categoryCode, productCode, catalogCode, searchPageData);
		}
		else
		{
			return getComponentRenderingService().getComponentsByIds(componentIds, categoryCode, productCode, catalogCode,
					searchPageData);
		}
	}

	protected ComponentRenderingService getComponentRenderingService()
	{
		return componentRenderingService;
	}

	@Required
	public void setComponentRenderingService(final ComponentRenderingService componentRenderingService)
	{
		this.componentRenderingService = componentRenderingService;
	}

	public PermissionCachedCRUDService getPermissionCachedCRUDService()
	{
		return permissionCachedCRUDService;
	}

	@Required
	public void setPermissionCachedCRUDService(final PermissionCachedCRUDService permissionCachedCRUDService)
	{
		this.permissionCachedCRUDService = permissionCachedCRUDService;
	}

	public CMSVersionSessionContextProvider getCmsVersionSessionContextProvider()
	{
		return cmsVersionSessionContextProvider;
	}

	@Required
	public void setCmsVersionSessionContextProvider(
			CMSVersionSessionContextProvider cmsVersionSessionContextProvider)
	{
		this.cmsVersionSessionContextProvider = cmsVersionSessionContextProvider;
	}
}
