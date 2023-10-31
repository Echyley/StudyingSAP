/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

import de.hybris.platform.catalog.enums.ClassificationAttributeTypeEnum;
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeModel;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.core.model.type.AtomicTypeModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.CollectionTypeModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.core.model.type.MapTypeModel;
import de.hybris.platform.core.model.type.TypeModel;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.AttributeTypeDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.ListItemAttributeDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.ListItemClassificationAttributeDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.ListItemStructureType;
import de.hybris.platform.integrationservices.enums.ItemTypeMatchEnum;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemClassificationAttributeModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectVirtualAttributeDescriptorModel;
import de.hybris.platform.integrationservices.search.ItemTypeMatch;

/**
 * Utility class to instantiate common real or mock objects for integrationbackoffice tests.
 */
public class TypeCreatorTestUtils
{
	public static ListItemAttributeDTO createListItemAttributeDTO(final String qualifier,
	                                                              final boolean customUnique,
	                                                              final boolean unique,
	                                                              final boolean optional,
	                                                              final boolean autocreate,
	                                                              final ListItemStructureType structureType,
	                                                              final TypeModel typeModel)
	{

		final AttributeDescriptorModel attributeDescriptorModel = new AttributeDescriptorModel();
		attributeDescriptorModel.setAttributeType(typeModel);
		attributeDescriptorModel.setQualifier(qualifier);
		attributeDescriptorModel.setOptional(optional);
		attributeDescriptorModel.setUnique(unique);

		final AttributeTypeDTO attributeTypeDTO = AttributeTypeDTO.builder(attributeDescriptorModel)
		                                                          .withStructureType(structureType)
		                                                          .withType(typeModel)
		                                                          .build();
		return ListItemAttributeDTO.builder(attributeTypeDTO)
		                           .withSelected(true)
		                           .withCustomUnique(customUnique)
		                           .withAutocreate(autocreate)
		                           .build();
	}

	public static ListItemClassificationAttributeDTO createClassificationAttributeDTO(final String attributeName,
	                                                                                  final String qualifier,
	                                                                                  final ClassificationAttributeTypeEnum type,
	                                                                                  final String category)
	{
		final ClassificationAttributeModel classificationAttributeModel = new ClassificationAttributeModel();
		classificationAttributeModel.setCode(qualifier);

		final ClassificationClassModel classificationClassModel = new ClassificationClassModel();
		classificationClassModel.setCode(category);

		final ClassAttributeAssignmentModel classAttributeAssignmentModel = new ClassAttributeAssignmentModel();
		classAttributeAssignmentModel.setClassificationAttribute(classificationAttributeModel);
		classAttributeAssignmentModel.setAttributeType(type);
		classAttributeAssignmentModel.setClassificationClass(classificationClassModel);
		classAttributeAssignmentModel.setMultiValued(false);

		final IntegrationObjectItemClassificationAttributeModel integrationObjectItemClassificationAttribute = new IntegrationObjectItemClassificationAttributeModel();
		integrationObjectItemClassificationAttribute.setAttributeName(attributeName);
		integrationObjectItemClassificationAttribute.setClassAttributeAssignment(classAttributeAssignmentModel);

		return ListItemClassificationAttributeDTO.builder(classAttributeAssignmentModel)
		                                         .withSelected(true)
		                                         .withAttributeName(attributeName)
		                                         .build();
	}

	public static ClassAttributeAssignmentModel referenceClassAttributeAssignmentModel(final String qualifier,
	                                                                                   final ComposedTypeModel referenceType)
	{
		final ClassificationAttributeModel classificationAttributeModel = mock(ClassificationAttributeModel.class);
		lenient().when(classificationAttributeModel.getCode()).thenReturn(qualifier);

		final ClassificationClassModel classificationClassModel = mock(ClassificationClassModel.class);
		lenient().when(classificationClassModel.getCode()).thenReturn("ClassificationClassModel");

		final ClassAttributeAssignmentModel classAttributeAssignmentModel = mock(ClassAttributeAssignmentModel.class);
		lenient().when(classAttributeAssignmentModel.getClassificationAttribute()).thenReturn(classificationAttributeModel);
		lenient().when(classAttributeAssignmentModel.getAttributeType()).thenReturn(ClassificationAttributeTypeEnum.REFERENCE);
		lenient().when(classAttributeAssignmentModel.getClassificationClass()).thenReturn(classificationClassModel);
		lenient().when(classAttributeAssignmentModel.getReferenceType()).thenReturn(referenceType);

		return classAttributeAssignmentModel;
	}

	public static ClassAttributeAssignmentModel stringClassAttributeAssignmentModel(final String qualifier)
	{
		final ClassificationAttributeModel classificationAttributeModel = mock(ClassificationAttributeModel.class);
		lenient().when(classificationAttributeModel.getCode()).thenReturn(qualifier);

		final ClassificationClassModel classificationClassModel = mock(ClassificationClassModel.class);
		lenient().when(classificationClassModel.getCode()).thenReturn("ClassificationClassModel");

		final ClassAttributeAssignmentModel classAttributeAssignmentModel = mock(ClassAttributeAssignmentModel.class);
		lenient().when(classAttributeAssignmentModel.getClassificationAttribute()).thenReturn(classificationAttributeModel);
		lenient().when(classAttributeAssignmentModel.getAttributeType()).thenReturn(ClassificationAttributeTypeEnum.STRING);
		lenient().when(classAttributeAssignmentModel.getClassificationClass()).thenReturn(classificationClassModel);

		return classAttributeAssignmentModel;
	}

	public static TypeModel typeModel(final String code)
	{
		final TypeModel typeModel = mock(TypeModel.class);
		doReturn(code).when(typeModel).getCode();
		return typeModel;
	}

	public static AttributeDescriptorModel attributeDescriptorModel(final TypeModel typeModel)
	{
		final AttributeDescriptorModel descriptorModel = mock(AttributeDescriptorModel.class);
		lenient().doReturn(typeModel).when(descriptorModel).getAttributeType();
		lenient().doReturn("").when(descriptorModel).getQualifier();
		return descriptorModel;
	}

	public static CollectionTypeModel collectionTypeModel(final TypeModel typeModel)
	{
		final CollectionTypeModel collectionTypeModel = mock(CollectionTypeModel.class);
		lenient().doReturn(typeModel).when(collectionTypeModel).getElementType();
		return collectionTypeModel;
	}

	public static MapTypeModel mapTypeModel(final CollectionTypeModel collectionTypeModel)
	{
		final MapTypeModel mapTypeModel = mock(MapTypeModel.class);
		lenient().doReturn(collectionTypeModel).when(mapTypeModel).getReturntype();
		return mapTypeModel;
	}

	public static ComposedTypeModel composedTypeModel(final String code)
	{
		final ComposedTypeModel composedTypeModel = mock(ComposedTypeModel.class);
		lenient().when(composedTypeModel.getCode()).thenReturn(code);
		return composedTypeModel;
	}

	public static IntegrationObjectItemModel integrationObjectItemModel(final ComposedTypeModel type)
	{
		final IntegrationObjectItemModel ioi = mock(IntegrationObjectItemModel.class);
		lenient().when(ioi.getType()).thenReturn(type);
		return ioi;
	}

	public static IntegrationObjectItemModel integrationObjectItemModel(final ComposedTypeModel type,
	                                                                    final ItemTypeMatch itemTypeMatch)
	{
		final IntegrationObjectItemModel ioi = mock(IntegrationObjectItemModel.class);
		lenient().when(ioi.getType()).thenReturn(type);
		lenient().when(ioi.getItemTypeMatch()).thenReturn(ItemTypeMatchEnum.valueOf(itemTypeMatch.name()));
		return ioi;
	}

	public static IntegrationObjectVirtualAttributeDescriptorModel integrationObjectVirtualAttributeDescriptorModel(
			final String code)
	{
		final IntegrationObjectVirtualAttributeDescriptorModel virtualAttributeDescriptorModel = mock(
				IntegrationObjectVirtualAttributeDescriptorModel.class);
		lenient().when(virtualAttributeDescriptorModel.getCode()).thenReturn(code);
		final AtomicTypeModel type = new AtomicTypeModel();
		type.setCode("String");
		lenient().when(virtualAttributeDescriptorModel.getType()).thenReturn(type);

		return virtualAttributeDescriptorModel;
	}
}
