/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.modeling.services.impl;

import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.SubtypeData;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.TreeNodeData;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.AbstractListItemDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.IntegrationMapKeyDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.ListItemAttributeDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.ListItemClassificationAttributeDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.services.AttributeDescriptorSubtypeService;
import de.hybris.platform.integrationbackoffice.widgets.modeling.services.ReadService;
import de.hybris.platform.integrationbackoffice.widgets.modeling.services.TreeNodeDataSetGenerator;

import javax.validation.constraints.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

/**
 * Default implementation of {@link TreeNodeDataSetGenerator}.
 */
public class DefaultTreeNodeDataSetGenerator implements TreeNodeDataSetGenerator
{
	private final ReadService readService;
	private final AttributeDescriptorSubtypeService attributeDescriptorSubtypeService;

	/**
	 * Default constructor of {@link DefaultTreeNodeDataSetGenerator}.
	 *
	 * @param readService                       the service used to perform database queries.
	 * @param attributeDescriptorSubtypeService the service to find the subtype of an {@link AttributeDescriptorModel}.
	 */
	public DefaultTreeNodeDataSetGenerator(@NotNull final ReadService readService,
	                                       @NotNull final AttributeDescriptorSubtypeService attributeDescriptorSubtypeService)
	{
		Preconditions.checkNotNull(readService, "ReadService can't be null.");
		Preconditions.checkNotNull(attributeDescriptorSubtypeService, "AttributeDescriptorSubtypeService can't be null.");
		this.readService = readService;
		this.attributeDescriptorSubtypeService = attributeDescriptorSubtypeService;
	}


	@Override
	public Set<TreeNodeData> generate(final Set<AttributeDescriptorModel> attributes,
	                                  final List<AbstractListItemDTO> existingAttributes,
	                                  final Set<SubtypeData> subtypeDataSet,
	                                  final IntegrationMapKeyDTO parentKey)
	{
		final Set<TreeNodeData> set = new HashSet<>();
		set.addAll(generateAttributesTreeNodeData(attributes, existingAttributes, subtypeDataSet, parentKey));
		set.addAll(generateClassificationAttributesTreeNodeData(existingAttributes));
		return set;
	}

	@Override
	public Set<TreeNodeData> generateInSelectedMode(final List<AbstractListItemDTO> attributes,
	                                                final Set<SubtypeData> subtypeDataSet,
	                                                final IntegrationMapKeyDTO parentKey)
	{
		final Set<TreeNodeData> set = new HashSet<>();
		set.addAll(generateAttributesTreeNodeDataInSelectedMode(attributes, subtypeDataSet, parentKey));
		set.addAll(generateClassificationAttributesTreeNodeData(attributes));
		return set;
	}

	private boolean isComplexType(final AttributeDescriptorModel attributeDescriptor)
	{
		return readService.getComplexTypeForAttributeDescriptor(attributeDescriptor) != null;
	}

	private Set<TreeNodeData> generateAttributesTreeNodeData(final Set<AttributeDescriptorModel> attributes,
	                                                         final List<AbstractListItemDTO> existingAttributes,
	                                                         final Set<SubtypeData> subtypeDataSet,
	                                                         final IntegrationMapKeyDTO parentKey)
	{
		return attributes.stream()
		                 .filter(this::isComplexType)
		                 .map(attribute -> mapAttributeToTreeNodeData(attribute, existingAttributes, subtypeDataSet, parentKey))
		                 .collect(Collectors.toSet());
	}

	/**
	 * Creates {@link TreeNodeData} based on if the {@link AttributeDescriptorModel} already exists as part of a {@link AbstractListItemDTO}.
	 * If the attribute already exists, then it may have an alias and type alias.
	 */
	private TreeNodeData mapAttributeToTreeNodeData(final AttributeDescriptorModel attribute,
	                                                final List<AbstractListItemDTO> existingAttributes,
	                                                final Set<SubtypeData> subtypeDataSet,
	                                                final IntegrationMapKeyDTO parentKey)
	{

		final String attributeDescriptorQualifier = attribute.getQualifier();
		final ComposedTypeModel type = attributeDescriptorSubtypeService.findSubtype(attribute, subtypeDataSet, parentKey);

		for (final AbstractListItemDTO dto : existingAttributes)
		{
			if (dto instanceof ListItemAttributeDTO && ((ListItemAttributeDTO) dto).getAttributeDescriptor().equals(attribute))
			{
				final String typeCode = dto.getTypeCode();
				final var mapKeyDTO = new IntegrationMapKeyDTO(type, typeCode);
				return new TreeNodeData(attributeDescriptorQualifier, dto.getAlias(), mapKeyDTO);
			}
		}

		final var mapKeyDTO = new IntegrationMapKeyDTO(type, type.getCode());
		return new TreeNodeData(attributeDescriptorQualifier, null, mapKeyDTO);
	}

	private Set<TreeNodeData> generateClassificationAttributesTreeNodeData(final List<AbstractListItemDTO> attributes)
	{
		return attributes.stream()
		                 .filter(ListItemClassificationAttributeDTO.class::isInstance)
		                 .map(ListItemClassificationAttributeDTO.class::cast)
		                 .filter(dto -> dto.getClassAttributeAssignmentModel().getReferenceType() != null)
		                 .map(this::mapClassificationAttributeToTreeNodeData)
		                 .collect(Collectors.toSet());
	}

	private TreeNodeData mapClassificationAttributeToTreeNodeData(final ListItemClassificationAttributeDTO dto)
	{
		final var composedTypeModel = dto.getClassAttributeAssignmentModel().getReferenceType();
		final var mapKeyDTO = new IntegrationMapKeyDTO(composedTypeModel, composedTypeModel.getCode());
		return new TreeNodeData(dto.getClassificationAttributeCode(), dto.getAlias(), mapKeyDTO);
	}

	private Set<TreeNodeData> generateAttributesTreeNodeDataInSelectedMode(final List<AbstractListItemDTO> attributes,
	                                                                       final Set<SubtypeData> subtypeDataSet,
	                                                                       final IntegrationMapKeyDTO parentKey)
	{
		return attributes.stream()
		                 .filter(ListItemAttributeDTO.class::isInstance)
		                 .map(ListItemAttributeDTO.class::cast)
		                 .filter(dto -> isComplexType(dto.getAttributeDescriptor()))
		                 .map(dto -> mapAttributeToTreeNodeDataInSelectedMode(dto, subtypeDataSet, parentKey))
		                 .collect(Collectors.toSet());
	}

	private TreeNodeData mapAttributeToTreeNodeDataInSelectedMode(final ListItemAttributeDTO dto,
	                                                              final Set<SubtypeData> subtypeDataSet,
	                                                              final IntegrationMapKeyDTO parentKey)
	{
		final AttributeDescriptorModel descriptor = dto.getAttributeDescriptor();
		final ComposedTypeModel type = attributeDescriptorSubtypeService.findSubtype(descriptor, subtypeDataSet, parentKey);
		final var mapKeyDTO = new IntegrationMapKeyDTO(type, dto.getTypeCode());
		return new TreeNodeData(descriptor.getQualifier(), dto.getAlias(), mapKeyDTO);
	}
}
