/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.modeling.services.impl;

import de.hybris.platform.integrationbackoffice.widgets.modeling.data.IntegrationObjectDefinition;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.TreeNodeData;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.AbstractListItemDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.IntegrationMapKeyDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.services.IntegrationObjectDefinitionTrimmer;
import de.hybris.platform.integrationbackoffice.widgets.modeling.services.ReadService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.zkoss.zul.Tree;
import org.zkoss.zul.Treeitem;

import com.google.common.base.Preconditions;

/**
 * Default implementation of {@link IntegrationObjectDefinitionTrimmer}.
 */
public class DefaultIntegrationObjectDefinitionTrimmer implements IntegrationObjectDefinitionTrimmer
{
	private ReadService readService;

	/**
	 * The constructor of DefaultIntegrationObjectDefinitionTrimmer.
	 * @param readService This implementation requires a readService.
	 */
	public DefaultIntegrationObjectDefinitionTrimmer(@NotNull final ReadService readService)
	{
		Preconditions.checkArgument(readService != null, "ReadService must be provided");
		this.readService = readService;
	}

	@Override
	public IntegrationObjectDefinition trimMap(final Tree composedTypeTree, final IntegrationObjectDefinition fullMap)
	{
		final IntegrationObjectDefinition trimmedMap = new IntegrationObjectDefinition();
		trim(composedTypeTree.getItems().iterator().next(), fullMap, trimmedMap);
		return trimmedMap;
	}

	private void trim(final Treeitem treeItem, final IntegrationObjectDefinition fullMap,
	                  final IntegrationObjectDefinition trimmedMap)
	{
		final IntegrationMapKeyDTO key = ((TreeNodeData) treeItem.getValue()).getMapKeyDTO();
		final List<AbstractListItemDTO> dtoList = fullMap.getAttributesByKey(key);
		final List<AbstractListItemDTO> trimmedList = new ArrayList<>();

		for (final AbstractListItemDTO dto : dtoList)
		{
			if (dto.isSelected())
			{
				trimmedList.add(dto);
			}
		}

		if (treeItem.getTreechildren() != null)
		{
			final Collection<Treeitem> children = treeItem.getTreechildren().getChildren();
			for (final AbstractListItemDTO dto : trimmedList)
			{
				trimChild(children, dto, fullMap, trimmedMap);
			}
		}

		trimmedMap.setAttributesByKey(key, trimmedList);
	}

	private void trimChild(final Collection<Treeitem> children, final AbstractListItemDTO dto,
	                       final IntegrationObjectDefinition fullMap, final IntegrationObjectDefinition trimmedMap)
	{
		final boolean isStructuredType;
		final boolean isComplexType;
		final String qualifier;

		isStructuredType = dto.isStructureType();
		isComplexType = dto.isComplexType(readService);
		qualifier = dto.getQualifier();


		if (isComplexType || isStructuredType)
		{
			for (final Treeitem child : children)
			{
				final TreeNodeData treeNodeData = child.getValue();
				if (treeNodeData.getQualifier().equals(qualifier))
				{
					trim(child, fullMap, trimmedMap);
					break;
				}
			}
		}
	}
}
