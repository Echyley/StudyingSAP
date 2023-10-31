/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.odata.schema.property;

import de.hybris.platform.integrationservices.model.DescriptorFactory;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;
import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor;
import de.hybris.platform.integrationservices.model.TypeDescriptor;
import de.hybris.platform.odata2services.odata.schema.SchemaElementGenerator;

import java.util.List;

import org.apache.olingo.odata2.api.edm.provider.Property;
import org.apache.olingo.odata2.api.edm.provider.SimpleProperty;
import org.springframework.beans.factory.annotation.Required;

/**
 * @deprecated use {@link AbstractPropertyListElementGenerator} instead.
 */
@Deprecated(since = "2211.FP1", forRemoval = true)
public abstract class AbstractPropertyListGenerator implements SchemaElementGenerator<List<Property>, IntegrationObjectItemModel>
{
	private SchemaElementGenerator<SimpleProperty, TypeAttributeDescriptor> simplePropertyGenerator;
	private DescriptorFactory descriptorFactory;

	/**
	 * {@inheritDoc}
	 * <p>Converts {@code itemMode} to a {@link TypeDescriptor} and then delegates to {@link #generate(TypeDescriptor)}</p>
	 *
	 * @param itemModel integration object item to generate simple EDM properties for.
	 * @return a list of simple EDM properties or an empty list, if the integration object item does not have properties or has
	 * only navigation properties referring other integration object items.
	 * @see #generate(TypeDescriptor)
	 */
	@Override
	public List<Property> generate(final IntegrationObjectItemModel itemModel)
	{
		final var typeDescriptor = getDescriptorFactory().createItemTypeDescriptor(itemModel);
		return generate(typeDescriptor);
	}

	/**
	 * Generates simple properties for simple (not navigation) attributes declared in the type descriptor
	 *
	 * @param typeDescriptor describes integration object item, for which simple EDM properties need to be generated.
	 * @return a list of simple EDM properties or an empty list, if the integration object item does not have properties or has
	 * only navigation properties referring other integration object items.
	 */
	public List<Property> generate(final TypeDescriptor typeDescriptor)
	{
		return typeDescriptor.getAttributes().stream()
		                     .filter(this::isSimpleAttribute)
		                     .filter(this::isApplicable)
		                     .map(getSimplePropertyGenerator()::generate)
		                     .map(Property.class::cast)
		                     .toList();
	}

	private boolean isSimpleAttribute(final TypeAttributeDescriptor descriptor)
	{
		return descriptor.isPrimitive() && !descriptor.isCollection();
	}

	/**
	 * Allows subclasses to have a hook into the decision of whether an attribute should be presented as a simple property. This
	 * class already checks whether the attribute is simple, i.e. it's a primitive and not a collection, before
	 * checking this method.
	 *
	 * @param descriptor an attribute descriptor to decided about whether it should be presented as EDM property or not.
	 * @return {@code true}, if the attribute should be converted to a property; {@code false}, otherwise. This default
	 * implementation always returns {@code true}.
	 */
	protected abstract boolean isApplicable(TypeAttributeDescriptor descriptor);

	protected DescriptorFactory getDescriptorFactory()
	{
		return descriptorFactory;
	}

	@Required
	public void setDescriptorFactory(final DescriptorFactory factory)
	{
		descriptorFactory = factory;
	}

	protected SchemaElementGenerator<SimpleProperty, TypeAttributeDescriptor> getSimplePropertyGenerator()
	{
		return simplePropertyGenerator;
	}

	@Required
	public void setSimplePropertyGenerator(final SchemaElementGenerator<SimpleProperty, TypeAttributeDescriptor> generator)
	{
		simplePropertyGenerator = generator;
	}
}
