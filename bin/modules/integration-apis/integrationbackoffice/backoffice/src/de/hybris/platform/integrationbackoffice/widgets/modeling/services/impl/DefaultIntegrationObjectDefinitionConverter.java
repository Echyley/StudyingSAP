/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.modeling.services.impl;

import static de.hybris.platform.integrationbackoffice.widgets.modeling.utility.EditorUtils.getListItemStructureType;

import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.core.model.type.TypeModel;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.IntegrationObjectDefinition;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.AbstractListItemDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.AttributeTypeDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.IntegrationMapKeyDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.ListItemAttributeDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.ListItemClassificationAttributeDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.ListItemStructureType;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.ListItemVirtualAttributeDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.services.IntegrationObjectDefinitionConverter;
import de.hybris.platform.integrationbackoffice.widgets.modeling.services.ReadService;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemAttributeModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemClassificationAttributeModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemVirtualAttributeModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectVirtualAttributeDescriptorModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

/**
 * Default implementation of {@link IntegrationObjectDefinitionConverter}.
 */
public class DefaultIntegrationObjectDefinitionConverter implements IntegrationObjectDefinitionConverter
{
	private final ReadService readService;

	/**
	 * Default constructor of {@link DefaultIntegrationObjectDefinitionConverter}.
	 *
	 * @param readService the service used to perform database queries.
	 */
	public DefaultIntegrationObjectDefinitionConverter(final ReadService readService)
	{
		this.readService = readService;
	}

	/**
	 * Converts an {@link IntegrationObjectModel} to a {@link IntegrationObjectDefinition}.
	 *
	 * @param integrationObject the {@link IntegrationObjectModel} to convert.
	 * @return an {@link IntegrationObjectDefinition} representation of the integration object.
	 */
	@Override
	public IntegrationObjectDefinition toDefinitionMap(final IntegrationObjectModel integrationObject)
	{
		final Map<IntegrationMapKeyDTO, List<AbstractListItemDTO>> definitionMap =
				integrationObject.getItems()
				                 .stream()
				                 .collect(Collectors.toMap(this::toIntegrationMapKeyDTO, this::toAbstractListItemDTOs));
		return new IntegrationObjectDefinition(definitionMap);
	}

	private IntegrationMapKeyDTO toIntegrationMapKeyDTO(final IntegrationObjectItemModel item)
	{
		final ComposedTypeModel composedTypeModel = item.getType();
		return new IntegrationMapKeyDTO(composedTypeModel, item.getCode());
	}

	private List<AbstractListItemDTO> toAbstractListItemDTOs(final IntegrationObjectItemModel item)
	{
		final List<ListItemAttributeDTO> attributes = toListItemAttributeDTOs(item);
		final List<ListItemClassificationAttributeDTO> classificationAttributes = toListItemClassificationDTOs(item);
		final List<ListItemVirtualAttributeDTO> virtualAttributes = toListItemVirtualAttributeDTOs(item);
		final List<AbstractListItemDTO> combinedAttributes = new ArrayList<>();
		combinedAttributes.addAll(attributes);
		combinedAttributes.addAll(classificationAttributes);
		combinedAttributes.addAll(virtualAttributes);
		return combinedAttributes;
	}

	private List<ListItemAttributeDTO> toListItemAttributeDTOs(final IntegrationObjectItemModel item)
	{
		return item.getAttributes()
		           .stream()
		           .map(this::toListItemAttributeDTO)
		           .collect(Collectors.toList());
	}

	private ListItemAttributeDTO toListItemAttributeDTO(final IntegrationObjectItemAttributeModel attribute)
	{
		final boolean isSelected = true;
		final AttributeDescriptorModel attributeDescriptor = attribute.getAttributeDescriptor();
		final boolean isCustomUnique = BooleanUtils.isTrue(attribute.getUnique()) &&
				BooleanUtils.isNotTrue(attributeDescriptor.getUnique());
		final boolean isAutocreate = BooleanUtils.isTrue(attribute.getAutoCreate());
		final String attributeName = attribute.getAttributeName();
		final boolean hasTypeAlias = attributeHasTypeAlias(attribute);
		final String itemCode = hasTypeAlias ? attribute.getReturnIntegrationObjectItem().getCode() : StringUtils.EMPTY;
		final ListItemStructureType structureType = getListItemStructureType(readService, attributeDescriptor);
		final TypeModel type = attribute.getReturnIntegrationObjectItem() != null
				? attribute.getReturnIntegrationObjectItem().getType()
				: attributeDescriptor.getAttributeType();
		final AttributeTypeDTO attributeTypeDTO = AttributeTypeDTO.builder(attributeDescriptor)
		                                                          .withStructureType(structureType)
		                                                          .withType(type)
		                                                          .build();
		return ListItemAttributeDTO.builder(attributeTypeDTO)
		                           .withSelected(isSelected)
		                           .withCustomUnique(isCustomUnique)
		                           .withAutocreate(isAutocreate)
		                           .withAttributeName(attributeName)
		                           .withTypeAlias(itemCode)
		                           .build();
	}

	private List<ListItemClassificationAttributeDTO> toListItemClassificationDTOs(final IntegrationObjectItemModel item)
	{
		return item.getClassificationAttributes()
		           .stream()
		           .map(this::toListItemClassificationDTO)
		           .collect(Collectors.toList());
	}

	private ListItemClassificationAttributeDTO toListItemClassificationDTO(
			final IntegrationObjectItemClassificationAttributeModel classificationAttribute)
	{
		final boolean isSelected = true;
		final boolean isCustomUnique = true;
		final boolean isAutocreate = BooleanUtils.isTrue(classificationAttribute.getAutoCreate());
		final String alias = classificationAttribute.getAttributeName();
		final ClassAttributeAssignmentModel classAttributeAssignment = classificationAttribute.getClassAttributeAssignment();
		return ListItemClassificationAttributeDTO.builder(classAttributeAssignment)
		                                         .withSelected(isSelected)
		                                         .withCustomUnique(isCustomUnique)
		                                         .withAutocreate(isAutocreate)
		                                         .withAttributeName(alias)
		                                         .build();
	}

	private List<ListItemVirtualAttributeDTO> toListItemVirtualAttributeDTOs(final IntegrationObjectItemModel item)
	{
		return item.getVirtualAttributes()
		           .stream()
		           .map(this::toListItemVirtualAttributeDTO)
		           .collect(Collectors.toList());
	}

	private ListItemVirtualAttributeDTO toListItemVirtualAttributeDTO(
			final IntegrationObjectItemVirtualAttributeModel virtualAttribute)
	{
		final boolean isSelected = true;
		final boolean isCustomUnique = false;
		final boolean isAutocreate = false;
		final IntegrationObjectVirtualAttributeDescriptorModel retrievalDescriptor = virtualAttribute.getRetrievalDescriptor();
		final String alias = virtualAttribute.getAttributeName();
		return ListItemVirtualAttributeDTO.builder(retrievalDescriptor)
		                                  .withSelected(isSelected)
		                                  .withCustomUnique(isCustomUnique)
		                                  .withAutocreate(isAutocreate)
		                                  .withAttributeName(alias)
		                                  .build();
	}

	private boolean attributeHasTypeAlias(final IntegrationObjectItemAttributeModel attribute)
	{
		if (attribute.getReturnIntegrationObjectItem() == null)
		{
			return false;
		}

		return !attribute.getReturnIntegrationObjectItem()
		                 .getType()
		                 .getCode()
		                 .equals(attribute.getReturnIntegrationObjectItem().getCode());
	}
}
