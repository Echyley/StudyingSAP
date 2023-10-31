/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.modeling.services;

import de.hybris.platform.integrationbackoffice.widgets.modeling.data.IntegrationObjectDefinition;

import org.zkoss.zul.Tree;

/**
 * Trims the definition map of the modeling view.
 */
public interface IntegrationObjectDefinitionTrimmer
{
	/**
	 * Given a map that represents all the tree nodes with each tree node's complete listBox and a Tree, this method will iterate
	 * through the tree from its first child (normally the only child) to all of its descendants. If a tree node has any item that
	 * is selected, a new entry will be created and added to another instance of {@link IntegrationObjectDefinition} which is
	 * returned at the end.
	 *
	 * @param composedTypeTree the tree that contains a root type and its displayable descendants. Normally composedType type and
	 *                         element's type of structured type will be displayed.
	 * @return a {@link IntegrationObjectDefinition} containing the trimmed definition map of the full definition map.
	 */
	IntegrationObjectDefinition trimMap(Tree composedTypeTree,
	                                    IntegrationObjectDefinition fullMap);
}
