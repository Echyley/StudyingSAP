/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.modeling.builders;

import static de.hybris.platform.integrationbackoffice.widgets.modeling.utility.EditorUtils.createTreeItem;

import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.IntegrationObjectDefinition;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.SubtypeData;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.TreeNodeData;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.AbstractListItemDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.IntegrationMapKeyDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.ListItemAttributeDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.ListItemStructureType;
import de.hybris.platform.integrationbackoffice.widgets.modeling.services.TreeNodeDataSetGenerator;
import de.hybris.platform.integrationbackoffice.widgets.modeling.utility.EditorAttributesFilteringService;
import de.hybris.platform.integrationbackoffice.widgets.modeling.utility.EditorBlacklists;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import org.apache.commons.collections.CollectionUtils;
import org.zkoss.zul.Treechildren;
import org.zkoss.zul.Treeitem;

import com.google.common.base.Preconditions;

/**
 * Default implementation of {@link TreePopulator}.
 */
public class DefaultTreePopulator implements TreePopulator
{
	private static final int MAX_DEPTH = 5;

	private final EditorAttributesFilteringService editorAttrFilterService;
	private final DataStructureBuilder dataStructureBuilder;
	private final TreeNodeDataSetGenerator treeNodeDataSetGenerator;

	Deque<IntegrationMapKeyDTO> ancestors = new ArrayDeque<>();

	/**
	 * Default constructor of {@link DefaultTreePopulator}.
	 *
	 * @param editorAttrFilterService  service that filters attributes.
	 * @param dataStructureBuilder     service to perform operations on {@link IntegrationObjectDefinition}.
	 * @param treeNodeDataSetGenerator service to generate {@link TreeNodeData}.
	 */
	public DefaultTreePopulator(@NotNull final EditorAttributesFilteringService editorAttrFilterService,
	                            @NotNull final DataStructureBuilder dataStructureBuilder,
	                            @NotNull final TreeNodeDataSetGenerator treeNodeDataSetGenerator)
	{
		Preconditions.checkArgument(editorAttrFilterService != null, "EditorAttributesFilteringService must be provided");
		Preconditions.checkArgument(dataStructureBuilder != null, "DataStructureBuilder must be provided");
		Preconditions.checkArgument(treeNodeDataSetGenerator != null, "TreeNodeDataSetGenerator must be provided");
		this.editorAttrFilterService = editorAttrFilterService;
		this.dataStructureBuilder = dataStructureBuilder;
		this.treeNodeDataSetGenerator = treeNodeDataSetGenerator;
	}

	@Override
	public Deque<IntegrationMapKeyDTO> determineTreeitemAncestors(Treeitem treeitem)
	{
		final Deque<IntegrationMapKeyDTO> treeitemAncestors = new ArrayDeque<>();
		while (treeitem.getLevel() > 0)
		{
			final TreeNodeData treeNodeData = treeitem.getValue();
			treeitemAncestors.addFirst(treeNodeData.getMapKeyDTO());
			treeitem = treeitem.getParentItem();
		}
		final TreeNodeData rootTreeNodeData = treeitem.getValue();
		treeitemAncestors.addFirst(rootTreeNodeData.getMapKeyDTO());
		return treeitemAncestors;
	}

	@Override
	public Treeitem appendTreeitem(final Treeitem parent, final TreeNodeData tnd)
	{
		final Treeitem treeitem = createTreeItem(tnd, false);
		if (parent.getTreechildren() == null)
		{
			parent.appendChild(new Treechildren());
		}
		parent.getTreechildren().appendChild(treeitem);
		return treeitem;
	}


	/**
	 * {@inheritDoc}
	 * <p>
	 * The method will collect all the {@link AttributeDescriptorModel} of a {@link ComposedTypeModel}
	 * including those wrapped in collection type, generate a set of {@link TreeNodeData} from them and create a set of {@link Treeitem}
	 * under the parent Treeitem.
	 */
	@Override
	public IntegrationObjectDefinition populateTree(final Treeitem parent,
	                                                final IntegrationObjectDefinition existingDefinitions,
	                                                final IntegrationObjectDefinition currentAttributesMap,
	                                                final Set<SubtypeData> subtypeDataSet)
	{
		final IntegrationMapKeyDTO parentKey = ((TreeNodeData) parent.getValue()).getMapKeyDTO();
		final List<AbstractListItemDTO> existingAttributes = existingDefinitions.getAttributesByKey(parentKey);
		final Set<AttributeDescriptorModel> filteredAttributes = editorAttrFilterService.filterAttributesForTree(
				parentKey.getType());
		final Set<AttributeDescriptorModel> existingCollections = getStructuredAttributes(existingAttributes);
		filteredAttributes.addAll(existingCollections);
		final Set<TreeNodeData> treeNodeDataSet = treeNodeDataSetGenerator.generate(filteredAttributes, existingAttributes,
				subtypeDataSet, parentKey);

		treeNodeDataSet.stream()
		               .sorted((attribute1, attribute2) -> attribute1.getAlias().compareToIgnoreCase(attribute2.getAlias()))
		               .forEach(treeNodeData -> {
			               final IntegrationMapKeyDTO typeKey = treeNodeData.getMapKeyDTO();
			               if (!ancestorsHasType(typeKey) && !EditorBlacklists.getTypesBlackList().contains(typeKey.getCode()))
			               {
				               addAncestorAtFirst(typeKey);

				               if (!currentAttributesMap.containsKey(typeKey))
				               {
					               dataStructureBuilder.populateAttributesMap(typeKey, currentAttributesMap);
				               }

				               final Treeitem treeitem = appendTreeitem(parent, treeNodeData);
				               if (treeitem.getLevel() <= MAX_DEPTH)
				               {
					               populateTree(treeitem, existingDefinitions, currentAttributesMap, subtypeDataSet);
				               }

				               removeAncestor();
			               }
		               });
		return currentAttributesMap;
	}

	/**
	 * Gets the attribute descriptors of collection and map attributes from a list of DTOs
	 *
	 * @param dtoList a list of AbstractListItemDTO.
	 * @return a set of AttributeDescriptorModel of CollectionType or MapType attributes
	 */
	private Set<AttributeDescriptorModel> getStructuredAttributes(final Collection<AbstractListItemDTO> dtoList)
	{
		return dtoList.stream()
		              .filter(ListItemAttributeDTO.class::isInstance)
		              .map(ListItemAttributeDTO.class::cast)
		              .filter(dto -> dto.getStructureType() == ListItemStructureType.COLLECTION || dto.getStructureType() == ListItemStructureType.MAP)
		              .map(ListItemAttributeDTO::getAttributeDescriptor)
		              .collect(Collectors.toSet());
	}

	@Override
	public void populateTreeSelectedMode(final Treeitem parent,
	                                     final IntegrationObjectDefinition existingDefinitions,
	                                     final Set<SubtypeData> subtypeDataSet)
	{
		final TreeNodeData parentTreeNodeData = parent.getValue();
		final IntegrationMapKeyDTO parentKey = parentTreeNodeData.getMapKeyDTO();
		final List<AbstractListItemDTO> existingAttributes = existingDefinitions.getAttributesByKey(parentKey);
		final Set<TreeNodeData> treeNodeDataSet = treeNodeDataSetGenerator.generateInSelectedMode(existingAttributes,
				subtypeDataSet, parentKey);
		treeNodeDataSet.stream()
		               .sorted((attribute1, attribute2) -> attribute1.getAlias().compareToIgnoreCase(attribute2.getAlias()))
		               .forEach(treeNodeData -> populateTreeitemSelectedMode(parent, existingDefinitions, subtypeDataSet,
				               treeNodeData));
	}

	private void populateTreeitemSelectedMode(final Treeitem parent,
	                                          final IntegrationObjectDefinition existingDefinitions,
	                                          final Set<SubtypeData> subtypeDataSet,
	                                          final TreeNodeData treeNodeData)
	{
		final IntegrationMapKeyDTO mapKeyDTO = treeNodeData.getMapKeyDTO();
		if (!ancestorsHasType(mapKeyDTO))
		{
			addAncestorAtFirst(mapKeyDTO);
			final Treeitem treeitem = appendTreeitem(parent, treeNodeData);
			populateTreeSelectedMode(treeitem, existingDefinitions, subtypeDataSet);
			removeAncestor();
		}
	}

	/**
	 * Determines if the {@link #ancestors} already contains a given key
	 *
	 * @param integrationMapKeyDTO the key to check for in the {@link #ancestors}
	 * @return Whether or not value is present
	 */
	private boolean ancestorsHasType(final IntegrationMapKeyDTO integrationMapKeyDTO)
	{
		return ancestors.contains(integrationMapKeyDTO);
	}

	@Override
	public void setAncestors(final Deque<IntegrationMapKeyDTO> ancestors)
	{
		if (CollectionUtils.isNotEmpty(ancestors))
		{
			this.ancestors = new ArrayDeque<>(ancestors);
		}
		else
		{
			this.ancestors.clear();
		}
	}

	@Override
	public void resetState()
	{
		ancestors.clear();
	}

	@Override
	public void addAncestor(final IntegrationMapKeyDTO integrationMapKeyDTO)
	{
		ancestors.push(integrationMapKeyDTO);
	}

	private void addAncestorAtFirst(final IntegrationMapKeyDTO integrationMapKeyDTO)
	{
		ancestors.addFirst(integrationMapKeyDTO);
	}

	private void removeAncestor()
	{
		ancestors.pollFirst();
	}
}
