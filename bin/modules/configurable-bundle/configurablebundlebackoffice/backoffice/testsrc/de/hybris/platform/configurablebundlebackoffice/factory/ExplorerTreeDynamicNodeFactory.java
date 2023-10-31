/*
 * [y] hybris Platform
 *
 * Copyright (c) 2017 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.configurablebundlebackoffice.factory;

import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;

import com.hybris.backoffice.navigation.NavigationNode;
import com.hybris.backoffice.navigation.impl.SimpleNode;
import com.hybris.backoffice.tree.model.CatalogTreeModelPopulator;
import com.hybris.cockpitng.tree.node.DynamicNode;
import com.hybris.cockpitng.tree.node.DynamicNodePopulator;


public class ExplorerTreeDynamicNodeFactory
{
	private DynamicNodePopulator populator;

	public ExplorerTreeDynamicNodeFactory(DynamicNodePopulator populator)
	{
		this.populator = populator;
	}

	public NavigationNode createAllCatalogsNode()
	{
		return new DynamicNode(CatalogTreeModelPopulator.ALL_CATALOGS_NODE_ID, populator);
	}

	public NavigationNode createCatalogNode(final CatalogModel catalog)
	{
		final SimpleNode catalogNode = new DynamicNode(catalog.getId(), populator);
		catalogNode.setData(catalog);
		return catalogNode;
	}

	public NavigationNode createCatalogVersionNode(final CatalogVersionModel catalogVersionModel)
	{
		final SimpleNode catalogNode = new DynamicNode(catalogVersionModel.getVersion(), populator);
		catalogNode.setData(catalogVersionModel);
		return catalogNode;
	}

}
