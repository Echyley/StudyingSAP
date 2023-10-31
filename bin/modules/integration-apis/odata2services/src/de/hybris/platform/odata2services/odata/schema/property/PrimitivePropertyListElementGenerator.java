/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.odata.schema.property;

import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor;
import de.hybris.platform.integrationservices.model.TypeDescriptor;
import de.hybris.platform.odata2services.odata.schema.SchemaElementGenerator;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.validation.constraints.NotNull;

import org.apache.olingo.odata2.api.edm.provider.Property;
import org.apache.olingo.odata2.api.edm.provider.SimpleProperty;

import com.google.common.base.Preconditions;

/**
 * Generates properties for simple primitive attributes type along with the integration key property in the EDMX schema.
 */
public class PrimitivePropertyListElementGenerator extends AbstractPropertyListElementGenerator
{
	private final IntegrationKeyPropertyElementGenerator keyGenerator;

	/**
	 * To instantiate the {@link PrimitivePropertyListElementGenerator}.
	 * @param propertyGenerator a non-null simple property generator
	 * @param keyGenerator a non-null integration key property generator
	 */
	public PrimitivePropertyListElementGenerator(
			@NotNull final SchemaElementGenerator<SimpleProperty, TypeAttributeDescriptor> propertyGenerator,
			@NotNull final IntegrationKeyPropertyElementGenerator keyGenerator)
	{
		super(propertyGenerator);

		Preconditions.checkArgument(keyGenerator != null, "keyGenerator cannot be null");
		this.keyGenerator = keyGenerator;
	}

	@Override
	public List<Property> generateAdditionalProperties(final TypeDescriptor typeDescriptor)
	{
		return Optional.of(keyGenerator)
		               .flatMap(g -> g.generate(typeDescriptor))
		               .map(List::of)
		               .orElseGet(Collections::emptyList);
	}

	@Override
	protected boolean isApplicable(final TypeAttributeDescriptor descriptor)
	{
		return true;
	}
}
