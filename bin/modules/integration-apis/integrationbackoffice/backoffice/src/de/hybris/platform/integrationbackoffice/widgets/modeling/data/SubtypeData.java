/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.modeling.data;

import de.hybris.platform.core.model.type.TypeModel;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.IntegrationMapKeyDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.ListItemAttributeDTO;

/**
 * Holds information about an attribute that has been sub-typed.
 */
public class SubtypeData
{

	private final IntegrationMapKeyDTO parentNodeKey;
	private TypeModel subtype;
	private final TypeModel baseType;
	private final String attributeAlias;
	private final String attributeQualifier;

	/**
	 * Instantiates this data
	 *
	 * @param parentNodeKey key of the parent instance
	 * @param dto           an attribute DTO
	 */
	public SubtypeData(final IntegrationMapKeyDTO parentNodeKey, ListItemAttributeDTO dto)
	{
		this(parentNodeKey, dto.getType(), dto.getBaseType(), dto.getAlias(), dto.getAttributeDescriptor().getQualifier());
	}

	/**
	 * Data class holding information pertaining to an attribute that has been sub-typed.
	 *
	 * @param parentNodeKey      Key of the parent instance
	 * @param subtype            Selected subtype of the attribute
	 * @param baseType           Original base type of the attribute
	 * @param attributeAlias     Alias of the attribute
	 * @param attributeQualifier Type system qualifier of the attribute
	 */
	public SubtypeData(final IntegrationMapKeyDTO parentNodeKey, final TypeModel subtype, final TypeModel baseType,
	                   final String attributeAlias, final String attributeQualifier)
	{
		this.parentNodeKey = parentNodeKey;
		this.subtype = subtype;
		this.baseType = baseType;
		this.attributeAlias = attributeAlias;
		this.attributeQualifier = attributeQualifier;
	}

	/**
	 * Gets the parent tree node's key.
	 *
	 * @return the parent tree node's key.
	 */
	public IntegrationMapKeyDTO getParentNodeKey()
	{
		return parentNodeKey;
	}

	/**
	 * Sets the subtype for the attribute.
	 *
	 * @param subtype the subtype of the attribute.
	 */
	public void setSubtype(final TypeModel subtype)
	{
		this.subtype = subtype;
	}

	/**
	 * Gets the subtype of the attribute.
	 *
	 * @return the subtype of the attribute.
	 */
	public TypeModel getSubtype()
	{
		return subtype;
	}

	/**
	 * Gets the base type of the attribute.
	 *
	 * @return the base type of the attribute.
	 */
	public TypeModel getBaseType()
	{
		return baseType;
	}

	/**
	 * Gets the attributes alias.
	 *
	 * @return the attribute alias.
	 */
	public String getAttributeAlias()
	{
		return attributeAlias;
	}

	/**
	 * Gets the type system qualifier of the attribute.
	 *
	 * @return the type system qualifier of the attribute.
	 */
	public String getAttributeQualifier()
	{
		return attributeQualifier;
	}

}
