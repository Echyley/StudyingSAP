/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto;

import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.TypeModel;

import javax.validation.constraints.NotNull;

import com.google.common.base.Preconditions;

/**
 * Builder for {@link AttributeTypeDTO}.
 */
public class AttributeTypeDTOBuilder
{
	private final AttributeDescriptorModel attributeDescriptor;
	private ListItemStructureType structureType = ListItemStructureType.NONE;
	private TypeModel type = null;

	/**
	 * Instantiates the builder.
	 *
	 * @param attributeDescriptor the mandatory object containing the type information of the attribute.
	 */
	AttributeTypeDTOBuilder(@NotNull final AttributeDescriptorModel attributeDescriptor)
	{
		Preconditions.checkArgument(attributeDescriptor != null, "Attribute descriptor can't be null.");
		this.attributeDescriptor = attributeDescriptor;
	}

	/**
	 * Sets the value of the structureType field for the DTO to build.
	 *
	 * @param structureType the structure type of the attribute.
	 * @return a builder with the field set.
	 */
	public AttributeTypeDTOBuilder withStructureType(final ListItemStructureType structureType)
	{
		this.structureType = structureType;
		return this;
	}

	/**
	 * Sets the value of the type field for the DTO to build.
	 *
	 * @param type the current {@link TypeModel} of the attribute. When the attribute be subtyped, it will be different
	 *             from its {@link AttributeDescriptorModel#getAttributeType()}.
	 * @return a builder with the field set.
	 */
	public AttributeTypeDTOBuilder withType(final TypeModel type)
	{
		this.type = type;
		return this;
	}

	/**
	 * Instantiates a {@link AttributeTypeDTO} with values provided or otherwise default values.
	 *
	 * @return a {@link AttributeTypeDTO} with the values provided.
	 */
	public AttributeTypeDTO build()
	{
		return new AttributeTypeDTO(attributeDescriptor, structureType, type);
	}
}
