/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.modeling.data;

import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.IntegrationMapKeyDTO;

/**
 * Contains data for a tree node.
 */
public class TreeNodeData
{

	private final String qualifier;
	private String alias;
	private IntegrationMapKeyDTO mapKeyDTO;

	/**
	 * Contains data for a tree node.
	 *
	 * @param qualifier Used for the label of the tree node if alias is {@code null}
	 * @param alias     Used for the label of the tree node if one is present
	 * @param mapKeyDTO Used for the value of the tree node
	 */
	public TreeNodeData(final String qualifier, final String alias, final IntegrationMapKeyDTO mapKeyDTO)
	{
		this.qualifier = qualifier;
		this.alias = (alias == null) ? qualifier : alias;
		this.mapKeyDTO = mapKeyDTO;
	}

	/**
	 * Gets the qualifier of the attribute represented by the tree node.
	 *
	 * @return the qualifier of the attribute represented by the tree node.
	 */
	public String getQualifier()
	{
		return qualifier;
	}

	/**
	 * Gets the {@link IntegrationMapKeyDTO} of the attribute represented by the tree node.
	 *
	 * @return the {@link IntegrationMapKeyDTO} of the attribute represented by the tree node.
	 */
	public IntegrationMapKeyDTO getMapKeyDTO()
	{
		return mapKeyDTO;
	}

	/**
	 * Sets the {@link IntegrationMapKeyDTO} of the attribute represented by the tree node.
	 *
	 * @param mapKeyDTO the {@link IntegrationMapKeyDTO} of the attribute represented by the tree node.
	 */
	public void setMapKeyDTO(final IntegrationMapKeyDTO mapKeyDTO)
	{
		this.mapKeyDTO = mapKeyDTO;
	}

	/**
	 * Gets the alias of the attribute represented by the tree node.
	 *
	 * @return the alias of the attribute represented by the tree node.
	 */

	public String getAlias()
	{
		return alias;
	}

	/**
	 * Sets the alias of the attribute represented by the tree node.
	 *
	 * @param alias the alias of the attribute represented by the tree node.
	 */
	public void setAlias(final String alias)
	{
		this.alias = alias;
	}

	/**
	 * Creates a TreeNodeData for the root item of the tree. This case is special as the root tree node does not have its
	 * label generated in the same way as other nodes.
	 *
	 * @param mapKeyDTO The value data of the tree node
	 * @return a TreeNodeData
	 */
	public static TreeNodeData createRootTreeNodeData(final IntegrationMapKeyDTO mapKeyDTO)
	{
		return new TreeNodeData(null, null, mapKeyDTO);
	}

}
