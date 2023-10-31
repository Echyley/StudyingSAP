/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.modeling.data;

import de.hybris.platform.core.model.type.ComposedTypeModel;

/**
 * Data needed to create the tree of {@link ComposedTypeModel} in the modeling view.
 */
public class CreateTreeData
{
	private final ComposedTypeModel root;
	private final IntegrationObjectDefinition definitionMap;

	/**
	 * Instantiates with data needed to create the tree of {@link ComposedTypeModel} for a new integration object.
	 *
	 * @param root a {@link ComposedTypeModel} which will be used to create the root node of the tree.
	 */
	public CreateTreeData(final ComposedTypeModel root)
	{
		this(root, new IntegrationObjectDefinition());
	}

	/**
	 * Instantiates with data needed to create the tree of {@link ComposedTypeModel} for an existing integration object.
	 *
	 * @param root          a {@link ComposedTypeModel} which will be used to create the root node of the tree.
	 * @param definitionMap a {@link IntegrationObjectDefinition} which contains the definition map for an existing integration object.
	 */
	public CreateTreeData(final ComposedTypeModel root, final IntegrationObjectDefinition definitionMap)
	{
		this.root = root;
		this.definitionMap = definitionMap;
	}

	public ComposedTypeModel getRoot()
	{
		return root;
	}

	public IntegrationObjectDefinition getDefinitionMap()
	{
		return definitionMap;
	}
}
