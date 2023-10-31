/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationservices.model.impl;

import static de.hybris.platform.catalog.enums.ClassificationAttributeTypeEnum.REFERENCE;

import de.hybris.platform.catalog.enums.ClassificationAttributeTypeEnum;
import de.hybris.platform.catalog.enums.ClassificationAttributeVisibilityEnum;
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.type.AtomicTypeModel;
import de.hybris.platform.integrationservices.model.CollectionDescriptor;
import de.hybris.platform.integrationservices.model.DescriptorFactory;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemClassificationAttributeModel;
import de.hybris.platform.integrationservices.model.TypeDescriptor;

import java.util.Date;

import javax.validation.constraints.NotNull;

/**
 * {@inheritDoc}
 * <p>Implementation of {@code TypeAttributeDescriptor} based on {@code IntegrationObjectItemClassificationAttributeModel}</p>
 * <p>This implementation is effectively immutable and therefore is thread safe.</p>
 * <p>Reuse this class through composition, not inheritance.</p>
 */
public class ClassificationTypeAttributeDescriptor extends AbstractAttributeDescriptor<IntegrationObjectItemClassificationAttributeModel>
{
	ClassificationTypeAttributeDescriptor(@NotNull final TypeDescriptor itemType,
	                                      @NotNull final IntegrationObjectItemClassificationAttributeModel attribute,
	                                      final DescriptorFactory descriptorFactory)
	{
		super(itemType, attribute, descriptorFactory);
	}

	@Override
	public String getQualifier()
	{
		return getClassAttributeAssignment().getClassificationAttribute().getCode();
	}

	@Override
	public boolean isCollection()
	{
		return falseIfNull(getClassAttributeAssignment().getMultiValued());
	}

	@Override
	TypeDescriptor deriveAttributeType()
	{
		return isPrimitive() ?
				PrimitiveTypeDescriptor.create(getTypeDescriptor().getIntegrationObjectCode(), derivePrimitiveTypeModel()) :
				getFactory().createItemTypeDescriptor(getAttributeModel().getReturnIntegrationObjectItem());
	}

	private AtomicTypeModel derivePrimitiveTypeModel()
	{
		final ClassificationAttributeTypeEnum attributeTypeEnum = getClassAttributeAssignment().getAttributeType();
		final var typeModel = new AtomicTypeModel();
		switch (attributeTypeEnum)
		{
			case STRING:
			case ENUM:
				typeModel.setCode(String.class.getName());
				typeModel.setJavaClass(String.class);
				break;
			case NUMBER:
				typeModel.setCode(Double.class.getName());
				typeModel.setJavaClass(Number.class);
				break;
			case BOOLEAN:
				typeModel.setCode(Boolean.class.getName());
				typeModel.setJavaClass(Boolean.class);
				break;
			case DATE:
				typeModel.setCode(Date.class.getName());
				typeModel.setJavaClass(Date.class);
				break;
			default:
				throw new IllegalArgumentException("Modeling error: [" + attributeTypeEnum.getCode() + "] is not a primitive classification attribute");
		}
		return typeModel;
	}

	@Override
	public boolean isNullable()
	{
		return !getClassAttributeAssignment().getMandatory();
	}

	@Override
	public boolean isAutoCreate()
	{
		return falseIfNull(getAttributeModel().getAutoCreate());
	}

	private static boolean falseIfNull(final Boolean value)
	{
		return Boolean.TRUE.equals(value);
	}

	@Override
	public boolean isLocalized()
	{
		return Boolean.TRUE.equals(getClassAttributeAssignment().getLocalized());
	}

	@Override
	public boolean isPrimitive()
	{
		return getClassAttributeAssignment().getAttributeType() != REFERENCE;
	}

	@Override
	public boolean isSettable(final Object item)
	{
		return item instanceof ItemModel &&
				getSettableChecker().isSettable((ItemModel) item, this);
	}

	@Override
	public boolean isReadable()
	{
		return getClassAttributeAssignment().getVisibility() != ClassificationAttributeVisibilityEnum.NOT_VISIBLE;
	}

	@Override
	public CollectionDescriptor getCollectionDescriptor()
	{
		return ClassificationCollectionDescriptor.create(getClassAttributeAssignment());
	}

	@NotNull
	ClassAttributeAssignmentModel getClassAttributeAssignment()
	{
		return getAttributeModel().getClassAttributeAssignment();
	}
}
