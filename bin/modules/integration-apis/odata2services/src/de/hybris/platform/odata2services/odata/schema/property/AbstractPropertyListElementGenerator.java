/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.odata.schema.property;

import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor;
import de.hybris.platform.integrationservices.model.TypeDescriptor;
import de.hybris.platform.odata2services.odata.schema.SchemaElementGenerator;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.apache.commons.collections4.ListUtils;
import org.apache.olingo.odata2.api.edm.provider.Property;
import org.apache.olingo.odata2.api.edm.provider.SimpleProperty;

import com.google.common.base.Preconditions;

/**
 * An abstract generator of properties for simple attributes in the EDMX schema from the {@link TypeDescriptor}.
 */
public abstract class AbstractPropertyListElementGenerator implements SchemaElementGenerator<List<Property>, TypeDescriptor>
{
	private final SchemaElementGenerator<SimpleProperty, TypeAttributeDescriptor> simplePropertyGenerator;

	AbstractPropertyListElementGenerator(
			@NotNull final SchemaElementGenerator<SimpleProperty, TypeAttributeDescriptor> propertyGenerator)
	{
		Preconditions.checkArgument(propertyGenerator != null, "propertyGenerator cannot be null");
		simplePropertyGenerator = propertyGenerator;
	}

	/**
	 * Generates simple properties for simple (not navigation) attributes declared in the type descriptor
	 *
	 * @param typeDescriptor describes integration object item, for which simple EDM properties need to be generated.
	 * @return a list of simple EDM properties or an empty list, if the integration object item does not have properties or has
	 * only navigation properties referring other integration object items.
	 */
	@Override
	public List<Property> generate(@NotNull final TypeDescriptor typeDescriptor)
	{
		final List<Property> simpleProperties = generateSimpleProperties(typeDescriptor);
		final List<Property> additionalProperties = generateAdditionalProperties(typeDescriptor);
		return ListUtils.union(simpleProperties, additionalProperties);
	}

	protected List<Property> generateSimpleProperties(final TypeDescriptor typeDescriptor)
	{
		return typeDescriptor.getAttributes().stream()
		                     .filter(this::isSimpleAttribute)
		                     .filter(this::isApplicable)
		                     .map(simplePropertyGenerator::generate)
		                     .map(Property.class::cast)
		                     .toList();
	}

	/**
	 * Allows subclass to have a hook to add additional properties(i.e. key or language properties)
	 *
	 * @param typeDescriptor describes integration object item, for which simple EDM properties need to be generated.
	 * @return a list of additional properties if available
	 */
	protected abstract List<Property> generateAdditionalProperties(TypeDescriptor typeDescriptor);

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
}
