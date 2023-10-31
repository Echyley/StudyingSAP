/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.modeling.services.impl;

import de.hybris.platform.core.model.enumeration.EnumerationMetaTypeModel;
import de.hybris.platform.core.model.type.CollectionTypeModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.core.model.type.MapTypeModel;
import de.hybris.platform.core.model.type.TypeModel;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.AbstractListItemDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.ListItemAttributeDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.ListItemClassificationAttributeDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.services.ListItemDTOTypeService;
import de.hybris.platform.integrationbackoffice.widgets.modeling.services.ReadService;

import javax.validation.constraints.NotNull;

import com.google.common.base.Preconditions;

/**
 * Default implementation of {@link ListItemDTOTypeService}.
 */
public class DefaultListItemDTOTypeService implements ListItemDTOTypeService
{
	private final ReadService readService;

	/**
	 * Default constructor for {@link DefaultListItemDTOTypeService}.
	 *
	 * @param readService the service to read the type system database.
	 */
	public DefaultListItemDTOTypeService(@NotNull final ReadService readService)
	{
		Preconditions.checkNotNull(readService, "ReadService can't be null.");
		this.readService = readService;
	}

	@Override
	public TypeModel getDTOType(final AbstractListItemDTO dto)
	{
		if (dto != null)
		{
			final TypeModel type = (dto instanceof ListItemAttributeDTO) ? dto.getType() :
					((ListItemClassificationAttributeDTO) dto).getClassAttributeAssignmentModel().getReferenceType();
			return determineType(type);
		}
		return null;
	}

	/**
	 * Find the true type of a {@link TypeModel}.
	 * For {@link CollectionTypeModel}, the type is {@link CollectionTypeModel#getElementType()}.
	 * For {@link MapTypeModel}, the type is {@link MapTypeModel#getReturntype()}.
	 * For a {@link ComposedTypeModel} or {@link EnumerationMetaTypeModel}, the true type is already found.
	 * For any other type, a true type doesn't exist and {@code null} will be returned.
	 *
	 * @param type a given {@link TypeModel}.
	 * @return the true type of the given {@link TypeModel}.
	 */
	TypeModel determineType(final TypeModel type)
	{
		if (type == null)
		{
			return null;
		}

		final String attributeTypeValue = type.getItemtype();

		if (readService.isCollectionType(attributeTypeValue))
		{
			return ((CollectionTypeModel) type).getElementType();
		}
		else if (readService.isMapType(attributeTypeValue))
		{
			final MapTypeModel mapTypeModel = (MapTypeModel) type;
			return determineType(mapTypeModel.getReturntype());
		}
		else if (readService.isComposedType(attributeTypeValue) || readService.isEnumerationMetaType(attributeTypeValue))
		{
			return type;
		}
		return null;
	}
}
