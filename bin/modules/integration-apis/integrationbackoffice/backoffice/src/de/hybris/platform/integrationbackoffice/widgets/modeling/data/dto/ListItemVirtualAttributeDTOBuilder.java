/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto;

import de.hybris.platform.integrationservices.model.IntegrationObjectVirtualAttributeDescriptorModel;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Preconditions;

/**
 * Builds a {@link ListItemVirtualAttributeDTO}.
 */
public class ListItemVirtualAttributeDTOBuilder
{
	private boolean selected = false;
	private boolean customUnique = false;
	private boolean autocreate = false;
	private String attributeName = StringUtils.EMPTY;
	private final IntegrationObjectVirtualAttributeDescriptorModel retrievalDescriptor;

	/**
	 * Instantiates the builder.
	 *
	 * @param retrievalDescriptor the object containing the type information of the virtual attribute the list item represents.
	 */
	ListItemVirtualAttributeDTOBuilder(@NotNull final IntegrationObjectVirtualAttributeDescriptorModel retrievalDescriptor)
	{
		Preconditions.checkArgument(retrievalDescriptor != null, "Retrieval descriptor can't be null.");
		this.retrievalDescriptor = retrievalDescriptor;
	}

	/**
	 * Sets the value of the selected field for the list item to build.
	 *
	 * @param selected if the list item is selected.
	 * @return a builder with the field set.
	 */
	public ListItemVirtualAttributeDTOBuilder withSelected(final boolean selected)
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
	public ListItemVirtualAttributeDTOBuilder withCustomUnique(final boolean customUnique)
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
	public ListItemVirtualAttributeDTOBuilder withAutocreate(final boolean autocreate)
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
	public ListItemVirtualAttributeDTOBuilder withAttributeName(final String attributeName)
	{
		this.attributeName = attributeName;
		return this;
	}

	/**
	 * Instantiates a {@link ListItemVirtualAttributeDTO} with values provided or otherwise default values.
	 *
	 * @return a {@link ListItemVirtualAttributeDTO} with the values provided.
	 */
	public ListItemVirtualAttributeDTO build()
	{
		return new ListItemVirtualAttributeDTO(selected, customUnique, autocreate, retrievalDescriptor, attributeName);
	}
}
