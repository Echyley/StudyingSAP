/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.uniqueidentifier.functions;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cmsfacades.data.ItemData;
import de.hybris.platform.cmsfacades.uniqueidentifier.UniqueIdentifierConverter;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation for conversion of {@link CatalogVersionModel}
 */
public class DefaultCatalogVersionModelUniqueIdentifierConverter implements UniqueIdentifierConverter<CatalogVersionModel>
{

	public static final String SEPARATOR = "/";
	public static final int PARAMETER_LENGTH = 2;

	private CatalogVersionService catalogVersionService;
	private ObjectFactory<ItemData> itemDataDataFactory;

	@Override
	public String getItemType()
	{
		return CatalogVersionModel._TYPECODE;
	}

	@Override
	public ItemData convert(CatalogVersionModel catalogVersion) throws IllegalArgumentException
	{
		if (catalogVersion == null) 
		{
			throw new IllegalArgumentException("The argument itemModel is null");
		}
		final ItemData itemData = getItemDataDataFactory().getObject();
		itemData.setItemId(catalogVersion.getCatalog().getId() + SEPARATOR + catalogVersion.getVersion());
		itemData.setItemType(catalogVersion.getItemtype());
		itemData.setName(catalogVersion.getVersion());
		
		return itemData;
	}

	@Override
	public CatalogVersionModel convert(ItemData itemData)
	{
		final String[] keys = itemData.getItemId().split(SEPARATOR);
		if (keys.length != PARAMETER_LENGTH)
		{
			throw new ConversionException("Invalid Catalog Version Unique Identifier [" + itemData.getItemId() + "].");
		}
		final CatalogVersionModel catalogVersion = getCatalogVersionService() //
				.getCatalogVersion(keys[0], keys[1]);
	
		return catalogVersion;
	}

	public CatalogVersionService getCatalogVersionService()
	{
		return catalogVersionService;
	}

	@Required
	public void setCatalogVersionService(final CatalogVersionService catalogVersionService)
	{
		this.catalogVersionService = catalogVersionService;
	}

	protected ObjectFactory<ItemData> getItemDataDataFactory()
	{
		return itemDataDataFactory;
	}

	@Required
	public void setItemDataDataFactory(final ObjectFactory<ItemData> itemDataDataFactory)
	{
		this.itemDataDataFactory = itemDataDataFactory;
	}
}
