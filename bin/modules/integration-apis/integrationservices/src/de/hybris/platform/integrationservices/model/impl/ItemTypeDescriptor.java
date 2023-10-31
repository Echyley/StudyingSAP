/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationservices.model.impl;

import static de.hybris.platform.integrationservices.model.IntegrationObjectItemAttributeModelUtils.falseIfNull;

import de.hybris.platform.core.HybrisEnumValue;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.enumeration.EnumerationMetaTypeModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.integrationservices.model.AbstractIntegrationObjectItemAttributeModel;
import de.hybris.platform.integrationservices.model.DescriptorFactory;
import de.hybris.platform.integrationservices.model.IntegrationObjectDescriptor;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;
import de.hybris.platform.integrationservices.model.KeyDescriptor;
import de.hybris.platform.integrationservices.model.ReferencePath;
import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor;
import de.hybris.platform.integrationservices.model.TypeDescriptor;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.constraints.NotNull;

import com.google.common.base.Preconditions;

/**
 * {@inheritDoc}
 * <p>This implementation is effectively immutable and therefore is thread safe</p>
 * <p>Reuse this implementation through composition not inheritance</p>
 */
public class ItemTypeDescriptor extends AbstractDescriptor implements TypeDescriptor
{
	private static final ReferencePathFinder DEFAULT_PATH_FINDER = new AttributePathFinder();

	private final IntegrationObjectItemModel itemTypeModel;
	private final Map<String, TypeAttributeDescriptor> attributeDescriptors;
	private IntegrationObjectDescriptor integrationObjectDescriptor;
	private ReferencePathFinder referencePathFinder;

	/**
	 * Instantiates this descriptor for an item in an integration object. This constructor does not specify a factory, and for
	 * that reason it should be used only as convenience in the unit tests. Production code should use only constructor with the
	 * factory passed as a parameter.
	 *
	 * @param model an item model to create a descriptor for.
	 */
	ItemTypeDescriptor(@NotNull final IntegrationObjectItemModel model)
	{
		this(model, null);
	}


	/**
	 * Instantiates this descriptor for an item in an integration object.
	 * <p>WARNING: the constructors do not create nested descriptors for the attributes in the integration object. The descriptor
	 * is instantiated without attributes being present. To populate this descriptor with attribute descriptors call the
	 * {@code initialize()} method.</p>
	 *
	 * @param model an item model to create a descriptor for.
	 * @param factory a descriptor factory implementation to be used by this descriptor for creating nested infrastructure, e.g.
	 *                attribute descriptors for attributes in the integration object item, value accessors, etc.
	 * @see #initialize()
	 */
	ItemTypeDescriptor(@NotNull final IntegrationObjectItemModel model, final DescriptorFactory factory)
	{
		super(factory);
		Preconditions.checkArgument(model != null, "Non-null integration object item model must be provided");
		itemTypeModel = model;
		attributeDescriptors = new HashMap<>();
		referencePathFinder = DEFAULT_PATH_FINDER;
	}

	/**
	 * Uses the injected into the constructor factory to create descriptors for all attributes present for the context item in the
	 * integration object. Before this method is called any attribute related methods are not going to give correct results.
	 * Therefore, this method should be always called sooner rather than later after an instance of this descriptor is created.
	 * The separation is done, to minimize the chances of crashing in the constructors and to make constructors fast and simple.
	 */
	void initialize() {
		attributeDescriptors.putAll(initializeAttributeDescriptors(itemTypeModel));
	}

	private Map<String, TypeAttributeDescriptor> initializeAttributeDescriptors(final IntegrationObjectItemModel model)
	{
		return Stream.of(model.getAttributes(), model.getClassificationAttributes(), model.getVirtualAttributes())
		             .filter(Objects::nonNull)
		             .flatMap(Collection::stream)
		             .map(this::createAttributeDescriptor)
		             .collect(Collectors.toMap(TypeAttributeDescriptor::getAttributeName, Function.identity()));
	}

	@Override
	public String getIntegrationObjectCode()
	{
		return getIntegrationObjectDescriptor().getCode();
	}

	@Override
	public String getItemCode()
	{
		return itemTypeModel.getCode();
	}

	@Override
	public String getTypeCode()
	{
		return itemTypeModel.getType().getCode();
	}

	@Override
	public Optional<TypeAttributeDescriptor> getAttribute(final String attrName)
	{
		return Optional.ofNullable(attributeDescriptors.get(attrName));
	}

	@Override
	public Collection<TypeAttributeDescriptor> getAttributes()
	{
		return new HashSet<>(attributeDescriptors.values());
	}

	@Override
	public boolean isPrimitive()
	{
		return false;
	}

	@Override
	public boolean isEnumeration()
	{
		return itemTypeModel.getType() instanceof EnumerationMetaTypeModel;
	}

	@Override
	public boolean isAbstract()
	{
		return falseIfNull(itemTypeModel.getType().getAbstract());
	}

	@Override
	public boolean isInstance(final Object obj)
	{
		if (obj instanceof ItemModel)
		{
			final Collection<String> typeHierarchy = getTypeHierarchy();
			if (typeHierarchy.contains(((ItemModel) obj).getItemtype()))
			{
				return true;
			}
			if (obj instanceof ComposedTypeModel)
			{
				return typeHierarchy.contains(((ComposedTypeModel) obj).getCode());
			}
			return false;
		}
		else if (obj instanceof HybrisEnumValue)
		{
			return ((HybrisEnumValue) obj).getType().equals(itemTypeModel.getType().getCode());
		}
		return false;
	}

	private Collection<String> getTypeHierarchy()
	{
		final Collection<String> typeHierarchy = itemTypeModel.getType().getAllSubTypes().stream()
		                                                      .map(ComposedTypeModel::getCode)
		                                                      .collect(Collectors.toSet());
		typeHierarchy.add(itemTypeModel.getType().getCode());
		return typeHierarchy;
	}

	@Override
	public boolean isRoot()
	{
		return Boolean.TRUE.equals(itemTypeModel.getRoot());
	}

	@Override
	public KeyDescriptor getKeyDescriptor()
	{
		return ItemKeyDescriptor.create(this);
	}

	@Override
	public List<ReferencePath> getPathsToRoot()
	{
		return getIntegrationObjectDescriptor().getRootType()
		                                       .map(rootType -> referencePathFinder.findAllPaths(this, rootType))
		                                       .orElse(Collections.emptyList());
	}

	@Override
	public boolean hasPathToRoot()
	{
		return !getPathsToRoot().isEmpty();
	}

	@Override
	public List<ReferencePath> pathFrom(final TypeDescriptor itemType)
	{
		return referencePathFinder.findAllPaths(itemType, this);
	}

	private TypeAttributeDescriptor createAttributeDescriptor(final AbstractIntegrationObjectItemAttributeModel model)
	{
		return getFactory().createTypeAttributeDescriptor(model);
	}

	private IntegrationObjectDescriptor getIntegrationObjectDescriptor()
	{
		if (integrationObjectDescriptor == null)
		{
			integrationObjectDescriptor = getFactory().createIntegrationObjectDescriptor(itemTypeModel.getIntegrationObject());
		}
		return integrationObjectDescriptor;
	}

	/**
	 * Injects {@code ReferencePathFinder} implementation to use.
	 * @param finder a finder implementation to use for methods returning {@link ReferencePath}s. {@code null} value resets the
	 *               finder implementation to default, which is {@link AttributePathFinder}
	 */
	public void setReferencePathFinder(final ReferencePathFinder finder)
	{
		referencePathFinder = finder != null ? finder : DEFAULT_PATH_FINDER;
	}

	@Override
	public boolean equals(final Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (o != null && getClass() == o.getClass())
		{
			final ItemTypeDescriptor that = (ItemTypeDescriptor) o;
			return Objects.equals(integrationObjectName(), that.integrationObjectName())
					&& Objects.equals(getItemCode(), that.getItemCode());
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(integrationObjectName(), getItemCode());
	}

	@Override
	public String toString()
	{
		return "ItemTypeDescriptor{" +
				"integrationObject='" + integrationObjectName() +
				"', typeCode='" + getItemCode() +
				"'}";
	}

	private String integrationObjectName()
	{
		return itemTypeModel.getIntegrationObject() != null
				? getIntegrationObjectCode()
				: "";
	}

	IntegrationObjectItemModel getItemTypeModel()
	{
		return itemTypeModel;
	}
}
