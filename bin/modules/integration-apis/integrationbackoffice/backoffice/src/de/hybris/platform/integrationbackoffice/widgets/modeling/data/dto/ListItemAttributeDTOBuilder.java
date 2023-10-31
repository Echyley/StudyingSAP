/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Preconditions;

/**
 * Builds a {@link ListItemAttributeDTO}.
 */
public class ListItemAttributeDTOBuilder
{
	private boolean selected = false;
	private boolean customUnique = false;
	private boolean autocreate = false;
	private String attributeName = StringUtils.EMPTY;
	private String typeAlias = StringUtils.EMPTY;
	private final AttributeTypeDTO attributeTypeDTO;

	/**
	 * Instantiates the builder.
	 *
	 * @param attributeTypeDTO the mandatory object containing the type information of the attribute the list item represents.
	 */
	ListItemAttributeDTOBuilder(@NotNull final AttributeTypeDTO attributeTypeDTO)
	{
		Preconditions.checkArgument(attributeTypeDTO != null, "Attribute type DTO can't be null.");
		this.attributeTypeDTO = attributeTypeDTO;
	}

	/**
	 * Sets the value of the selected field for the list item to build.
	 *
	 * @param selected if the list item is selected.
	 * @return a builder with the field set.
	 */
	public ListItemAttributeDTOBuilder withSelected(final boolean selected)
	{
		this.selected = selected;
		return this;
	}

	/**
	 * Sets the value of the customUnique field for the list item to build.
	 *
	 * @param customUnique if the list item's custom unique checkbox is checked.
	 * @return a builder with the field set.
	 */
	public ListItemAttributeDTOBuilder withCustomUnique(final boolean customUnique)
	{
		this.customUnique = customUnique;
		return this;
	}

	/**
	 * Sets the value of the autocreate field for the list item to build.
	 *
	 * @param autocreate if the list item autocreate checkbox is checked.
	 * @return a builder with the field set.
	 */
	public ListItemAttributeDTOBuilder withAutocreate(final boolean autocreate)
	{
		this.autocreate = autocreate;
		return this;
	}

	/**
	 * Sets the value of the attribute name field for the list item to build.
	 *
	 * @param attributeName the name of the attribute the list item represents.
	 * @return a builder with the field set.
	 */
	public ListItemAttributeDTOBuilder withAttributeName(final String attributeName)
	{
		this.attributeName = attributeName;
		return this;
	}

	/**
	 * Sets the value of the itemCode field for the list item to build.
	 *
	 * @param typeAlias the code of the {@link de.hybris.platform.integrationservices.model.IntegrationObjectItemModel} of the attribute
	 *                 the list item represents, if the attribute is of complex type.
	 * @return a builder with the field set.
	 */
	public ListItemAttributeDTOBuilder withTypeAlias(final String typeAlias)
	{
		this.typeAlias = typeAlias;
		return this;
	}

	/**
	 * Instantiates a {@link ListItemAttributeDTO} with values provided or otherwise default values.
	 *
	 * @return a {@link ListItemAttributeDTO} with the values provided.
	 */
	public ListItemAttributeDTO build()
	{
		return new ListItemAttributeDTO(selected, customUnique, autocreate, attributeName, typeAlias, attributeTypeDTO);
	}
}
