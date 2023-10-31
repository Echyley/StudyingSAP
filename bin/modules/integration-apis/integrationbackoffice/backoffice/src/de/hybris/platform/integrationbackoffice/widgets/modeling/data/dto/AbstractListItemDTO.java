/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto;

import de.hybris.platform.core.model.type.TypeModel;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.IntegrationObjectDefinition;
import de.hybris.platform.integrationbackoffice.widgets.modeling.services.ReadService;

import org.apache.commons.lang.StringUtils;

/**
 * Represents an {@link de.hybris.platform.integrationservices.model.AbstractIntegrationObjectItemAttributeModel} stored as the
 * value of a {@link org.zkoss.zul.Listitem} in the modeling view.
 */
public abstract class AbstractListItemDTO
{
	protected boolean selected;
	protected boolean customUnique;
	protected boolean autocreate;
	protected String description;
	protected String alias;
	protected String typeAlias;

	/**
	 * Represents an {@link de.hybris.platform.integrationservices.model.AbstractIntegrationObjectItemAttributeModel} stored as the
	 * value of a {@link org.zkoss.zul.Listitem} in the modeling view.
	 *
	 * @param selected     Flag for whether the listitem is selected in the UI
	 * @param customUnique If the unique checkbox has been checked by the user through the UI
	 * @param autocreate   If the autocreate checkbox has been checked by the user through the UI
	 */
	protected AbstractListItemDTO(final boolean selected, final boolean customUnique, final boolean autocreate)
	{
		this(selected, customUnique, autocreate, StringUtils.EMPTY);
	}

	/**
	 * Represents an {@link de.hybris.platform.integrationservices.model.AbstractIntegrationObjectItemAttributeModel} stored as the
	 * value of a {@link org.zkoss.zul.Listitem} in the modeling view.
	 *
	 * @param selected     Flag for whether the listitem is selected in the UI
	 * @param customUnique If the unique checkbox has been checked by the user through the UI
	 * @param autocreate   If the autocreate checkbox has been checked by the user through the UI
	 * @param typeAlias    The representation of the {@link de.hybris.platform.integrationservices.model.IntegrationObjectItemModel}'s code
	 */
	protected AbstractListItemDTO(final boolean selected, final boolean customUnique, final boolean autocreate,
			final String typeAlias)
	{
		this.selected = selected;
		this.customUnique = customUnique;
		this.autocreate = autocreate;
		this.typeAlias = typeAlias;
	}

	public boolean isCustomUnique()
	{
		return customUnique;
	}

	public void setCustomUnique(final boolean customUnique)
	{
		this.customUnique = customUnique;
	}

	public boolean isSelected()
	{
		return selected;
	}

	public void setSelected(final boolean selected)
	{
		this.selected = selected;
	}

	public boolean isAutocreate()
	{
		return autocreate;
	}

	public void setAutocreate(final boolean autocreate)
	{
		this.autocreate = autocreate;
	}

	public String getAlias()
	{
		return this.alias;
	}

	public abstract void setAlias(final String alias);

	public String getDescription()
	{
		return description;
	}

	public String getTypeAlias()
	{
		return typeAlias;
	}

	public void setTypeAlias(final String typeAlias)
	{
		this.typeAlias = typeAlias;
	}

	public abstract void createDescription();

	public abstract AbstractListItemDTO findMatch(final IntegrationObjectDefinition currentAttributesMap,
	                                              final IntegrationMapKeyDTO parentKey);

	public abstract boolean isComplexType(final ReadService readService);

	public abstract String getQualifier();

	public abstract TypeModel getType();

	public abstract boolean isStructureType();

	/**
	 * Retrieves the type alias if one exists, if not the type's code will be used.
	 *
	 * @return String representing the type
	 */
	public String getTypeCode()
	{
		return StringUtils.isNotBlank(typeAlias) ? typeAlias : getType().getCode();
	}
}
