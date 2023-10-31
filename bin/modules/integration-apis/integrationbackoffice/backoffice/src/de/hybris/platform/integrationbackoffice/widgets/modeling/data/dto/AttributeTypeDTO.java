/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto;

import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.TypeModel;

import java.util.Objects;

import javax.validation.constraints.NotNull;

import com.google.common.base.Preconditions;

/**
 * Contains type information for a {@link de.hybris.platform.integrationservices.model.IntegrationObjectItemAttributeModel}.
 */
public class AttributeTypeDTO
{
	private final AttributeDescriptorModel attributeDescriptor;
	private final ListItemStructureType structureType;
	private final TypeModel type;

	/**
	 * Contains type information for a {@link de.hybris.platform.integrationservices.model.IntegrationObjectItemAttributeModel}.
	 *
	 * @param attributeDescriptor the mandatory {@link AttributeDescriptorModel} of the attribute.
	 * @param structureType       the {@link ListItemStructureType} of the attribute.
	 * @param type                the current {@link TypeModel} of the attribute. When the attribute is subtyped, it will be different
	 *                            from its {@link AttributeDescriptorModel#getAttributeType()}.
	 */
	AttributeTypeDTO(@NotNull final AttributeDescriptorModel attributeDescriptor,
	                 final ListItemStructureType structureType,
	                 final TypeModel type)
	{
		Preconditions.checkArgument(attributeDescriptor != null, "Attribute descriptor can't be null.");
		this.attributeDescriptor = attributeDescriptor;
		this.structureType = structureType;
		this.type = type;
	}

	/**
	 * Instantiates a builder for this class.
	 *
	 * @param attributeDescriptor the mandatory {@link AttributeDescriptorModel} of the attribute.
	 * @return a builder for this class.
	 */
	public static AttributeTypeDTOBuilder builder(@NotNull final AttributeDescriptorModel attributeDescriptor)
	{
		Preconditions.checkArgument(attributeDescriptor != null, "Attribute descriptor can't be null.");
		return new AttributeTypeDTOBuilder(attributeDescriptor);
	}

	public AttributeDescriptorModel getAttributeDescriptor()
	{
		return attributeDescriptor;
	}

	public ListItemStructureType getStructureType()
	{
		return structureType;
	}

	public TypeModel getType()
	{
		return type;
	}

	@Override
	public boolean equals(final Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (o == null || getClass() != o.getClass())
		{
			return false;
		}
		final AttributeTypeDTO dto = (AttributeTypeDTO) o;
		return attributeDescriptor.equals(dto.getAttributeDescriptor())
				&& structureType.equals(dto.getStructureType())
				&& type.equals(dto.getType());
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(attributeDescriptor, structureType, type);
	}
}
