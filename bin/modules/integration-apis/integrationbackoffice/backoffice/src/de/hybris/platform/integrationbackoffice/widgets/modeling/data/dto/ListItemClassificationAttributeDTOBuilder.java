/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto;

import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Preconditions;

/**
 * Builds a {@link ListItemClassificationAttributeDTO}.
 */
public class ListItemClassificationAttributeDTOBuilder
{
	private boolean selected = false;
	private boolean customUnique = false;
	private boolean autocreate = false;
	private final ClassAttributeAssignmentModel classAttributeAssignment;
	private String attributeName = StringUtils.EMPTY;
	private String typeAlias = StringUtils.EMPTY;

	/**
	 * Instantiates the builder.
	 *
	 * @param classAttributeAssignment the object containing the type information of the classification attribute the list item represents.
	 */
	ListItemClassificationAttributeDTOBuilder(@NotNull final ClassAttributeAssignmentModel classAttributeAssignment)
	{
		Preconditions.checkArgument(classAttributeAssignment != null, "Class attribute assignment can't be null.");
		this.classAttributeAssignment = classAttributeAssignment;
	}

	/**
	 * Sets the value of the selected field for the list item to build.
	 *
	 * @param selected if the list item is selected.
	 * @return a builder with the field set.
	 */
	public ListItemClassificationAttributeDTOBuilder withSelected(final boolean selected)
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
	public ListItemClassificationAttributeDTOBuilder withCustomUnique(final boolean customUnique)
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
	public ListItemClassificationAttributeDTOBuilder withAutocreate(final boolean autocreate)
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
	public ListItemClassificationAttributeDTOBuilder withAttributeName(final String attributeName)
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
	public ListItemClassificationAttributeDTOBuilder withTypeAlias(final String typeAlias)
	{
		this.typeAlias = typeAlias;
		return this;
	}

	/**
	 * Instantiates a {@link ListItemClassificationAttributeDTO} with values provided or otherwise default values.
	 *
	 * @return a {@link ListItemClassificationAttributeDTO} with the values provided.
	 */
	public ListItemClassificationAttributeDTO build()
	{
		return new ListItemClassificationAttributeDTO(selected, customUnique, autocreate, classAttributeAssignment,
				attributeName, typeAlias);
	}
}
