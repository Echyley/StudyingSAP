/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationservices.model.impl;

import de.hybris.platform.core.enums.RelationEndCardinalityEnum;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.type.AtomicTypeModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.CollectionTypeModel;
import de.hybris.platform.core.model.type.MapTypeModel;
import de.hybris.platform.core.model.type.RelationDescriptorModel;
import de.hybris.platform.core.model.type.TypeModel;
import de.hybris.platform.integrationservices.model.CollectionDescriptor;
import de.hybris.platform.integrationservices.model.DescriptorFactory;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemAttributeModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemAttributeModelUtils;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;
import de.hybris.platform.integrationservices.model.MapDescriptor;
import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor;
import de.hybris.platform.integrationservices.model.TypeDescriptor;
import de.hybris.platform.integrationservices.util.Log;

import java.util.Optional;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;

/**
 * {@inheritDoc}
 * <p>This implementation is effectively immutable and therefore is thread safe.</p>
 * <p>Reuse this class through composition, not inheritance.</p>
 */
public class DefaultTypeAttributeDescriptor extends AbstractAttributeDescriptor<IntegrationObjectItemAttributeModel>
{
	private static final Logger LOG = Log.getLogger(DefaultTypeAttributeDescriptor.class);

	DefaultTypeAttributeDescriptor(@NotNull final TypeDescriptor itemType,
	                               @NotNull final IntegrationObjectItemAttributeModel model,
	                               final DescriptorFactory descriptorFactory)
	{
		super(itemType, model, descriptorFactory);
	}

	@Override
	public String getQualifier()
	{
		return getDescriptorModel().getQualifier();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @return {@code true}, if the underlying {@link AttributeDescriptorModel} represents a collection or a one-to-many
	 * relationship or, in other words, if the corresponding accessor in the generated item has a {@link java.util.Collection}
	 * return type, e.g.
	 * <pre>
	 *     public class Parent {
	 *         ...
	 *         Collection&#60Child&#62 Parent.getChildren() {...}
	 *         ...
	 *     }
	 * </pre>
	 * From the implementation standpoint it does not matter how that collection is modeled by {@link CollectionTypeModel} or
	 * {@link RelationDescriptorModel} - as long as it's a collection or one of its subtypes returned this method returns
	 * {@code true}. Otherwise, it returns {@code false}
	 */
	@Override
	public boolean isCollection()
	{
		return isCollectionAttributeModel() || isToManyRelationAttributeModel();
	}

	@Override
	public CollectionDescriptor getCollectionDescriptor()
	{
		return DefaultCollectionDescriptor.create(getDescriptorModel());
	}

	@Override
	public Optional<MapDescriptor> getMapDescriptor()
	{
		return Optional.ofNullable(createMapDescriptor());
	}

	private MapDescriptor createMapDescriptor()
	{
		try
		{
			if (isMap() && !isLocalized())
			{
				return new DefaultMapDescriptor(getAttributeModel());
			}
		}
		catch (final IllegalArgumentException e)
		{
			LOG.warn("Failed to create a map descriptor for {}", this, e);
		}
		return null;
	}

	private boolean isToManyRelationAttributeModel()
	{
		if (isRelationModel())
		{
			final RelationDescriptorModel relationModel = (RelationDescriptorModel) getDescriptorModel();
			final RelationEndCardinalityEnum targetCardinality = Boolean.TRUE.equals(relationModel.getIsSource())
					? relationModel.getRelationType().getTargetTypeCardinality()
					: relationModel.getRelationType().getSourceTypeCardinality();
			return targetCardinality == RelationEndCardinalityEnum.MANY;
		}
		return false;
	}

	@Override
	TypeDescriptor deriveAttributeType()
	{
		final IntegrationObjectItemModel referencedItemModel = getAttributeModel().getReturnIntegrationObjectItem();
		return referencedItemModel != null
				? typeDescriptor(referencedItemModel)
				: createNonReferencedDescriptor();
	}

	private TypeDescriptor createNonReferencedDescriptor()
	{
		return isMap() && !isLocalized() ?
				MapTypeDescriptor.create(getAttributeModel()) :
				PrimitiveTypeDescriptor.create(getTypeDescriptor().getIntegrationObjectCode(), derivePrimitiveTypeModel());
	}

	private AtomicTypeModel derivePrimitiveTypeModel()
	{
		final var attrType = getDescriptorModel().getAttributeType();
		TypeModel typeModel = isCollectionAttributeModel()
				? ((CollectionTypeModel) attrType).getElementType()
				: attrType;

		if (isLocalized() && isMap(typeModel))
		{
			typeModel = ((MapTypeModel) typeModel).getReturntype();
		}
		return getAtomicType(typeModel);
	}

	private AtomicTypeModel getAtomicType(final TypeModel typeModel)
	{
		if (typeModel instanceof AtomicTypeModel)
		{
			return (AtomicTypeModel) typeModel;
		}
		throw new IllegalStateException("Modeling error: " + this + " is not a primitive attribute");
	}

	private TypeDescriptor typeDescriptor(final IntegrationObjectItemModel integrationObjectItem)
	{
		return getFactory().createItemTypeDescriptor(integrationObjectItem);
	}

	@Override
	public Optional<TypeAttributeDescriptor> reverse()
	{
		return isRelationModel()
				? deriveReverseRelationAttribute()
				: Optional.empty();
	}

	private Optional<TypeAttributeDescriptor> deriveReverseRelationAttribute()
	{
		final String attributeName = getRelationAttribute().getQualifier();
		return getAttributeType().getAttribute(attributeName);
	}

	@Override
	public boolean isNullable()
	{
		return (isOptional() || getDescriptorModel().getDefaultValue() != null);
	}

	@Override
	public boolean isPartOf()
	{
		return falseIfNull(getAttributeModel().getPartOf());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @return {@code true}, if the attribute is defined with {@code partOf == true} in the type system or the corresponding
	 * {@link IntegrationObjectItemAttributeModel} has {@code autoCreate == true}; {@code false}, otherwise.
	 */
	@Override
	public boolean isAutoCreate()
	{
		return isAttributeModelAutoCreate() || isPartOf();
	}

	private boolean isOptional()
	{
		final Boolean optional = getDescriptorModel().getOptional();
		return optional == null || optional;
	}

	private boolean isCollectionAttributeModel()
	{
		return getDescriptorModel().getAttributeType() instanceof CollectionTypeModel;
	}

	private boolean isRelationModel()
	{
		return getDescriptorModel() instanceof RelationDescriptorModel;
	}

	private RelationDescriptorModel getRelationAttribute()
	{
		final RelationDescriptorModel model = ((RelationDescriptorModel) getDescriptorModel()).getRelationType()
		                                                                                     .getTargetAttribute();
		return model == null ? ((RelationDescriptorModel) getDescriptorModel()).getRelationType().getSourceAttribute() : model;
	}

	private boolean isAttributeModelAutoCreate()
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
		return Boolean.TRUE.equals(getDescriptorModel().getLocalized());
	}

	@Override
	public boolean isPrimitive()
	{
		return getAttributeType().isPrimitive();
	}

	@Override
	public boolean isReadable()
	{
		final Boolean readable = getDescriptorModel().getReadable();
		return readable == null || readable;
	}

	@Override
	public boolean isMap()
	{
		return isMap(getDescriptorModel().getAttributeType());
	}

	private static boolean isMap(final TypeModel typeModel)
	{
		return typeModel instanceof MapTypeModel;
	}

	@Override
	public boolean isSettable(final Object item)
	{
		return item instanceof ItemModel &&
				getSettableChecker().isSettable((ItemModel) item, this);
	}

	@Override
	public boolean isKeyAttribute()
	{
		return IntegrationObjectItemAttributeModelUtils.isUnique(getAttributeModel());
	}

	private AttributeDescriptorModel getDescriptorModel()
	{
		return getAttributeModel().getAttributeDescriptor();
	}
}
