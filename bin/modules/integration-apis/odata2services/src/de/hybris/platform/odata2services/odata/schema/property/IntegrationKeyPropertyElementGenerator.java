/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.odata.schema.property;

import de.hybris.platform.integrationservices.model.TypeDescriptor;
import de.hybris.platform.odata2services.odata.schema.SchemaElementGenerator;
import de.hybris.platform.odata2services.odata.schema.attribute.AliasAnnotationGenerator;
import de.hybris.platform.odata2services.odata.schema.attribute.ImmutableAnnotationAttribute;

import java.util.Optional;

import javax.annotation.Nullable;

import org.apache.olingo.odata2.api.edm.EdmSimpleTypeKind;
import org.apache.olingo.odata2.api.edm.provider.AnnotationAttribute;
import org.apache.olingo.odata2.api.edm.provider.Property;
import org.apache.olingo.odata2.api.edm.provider.SimpleProperty;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

/**
 * A generator for generating an {@link Optional<Property>} schema element from a {@link TypeDescriptor}.
 */
public class IntegrationKeyPropertyElementGenerator implements SchemaElementGenerator<Optional<Property>, TypeDescriptor>
{
	private static final String EXCEPTION_MESSAGE = "An Integration Key Property cannot be generated from a null TypeDescriptor";
	private static final String PROPERTY_NAME = "integrationKey";
	private static final AnnotationAttribute NULLABLE_ATTRIBUTE =
			new ImmutableAnnotationAttribute().setName("Nullable").setText("false");

	private AliasAnnotationGenerator aliasGenerator;

	/**
	 * Generates the schema element for the provided {@link TypeDescriptor} using assigned {@link AliasAnnotationGenerator}.
	 *
	 * @param descriptor the type descriptor for which schema element needs to be generated.
	 * @return Optional property generated from the descriptor using the given alias generator. Returns {@code Optional.empty()},
	 * if alias generator or descriptor is {@code null}, or the given alias generator generates a {@code null} alias.
	 */
	@Override
	public Optional<Property> generate(final TypeDescriptor descriptor)
	{
		Preconditions.checkArgument(descriptor != null, EXCEPTION_MESSAGE);

		if (aliasGenerator != null)
		{
			final AnnotationAttribute aliasAttribute = aliasGenerator.generate(descriptor);
			return aliasAttribute != null ? Optional.of(createProperty(aliasAttribute)) : Optional.empty();
		}
		return Optional.empty();
	}

	private static SimpleProperty createProperty(final AnnotationAttribute aliasAttribute)
	{
		return new SimpleProperty()
				.setName(PROPERTY_NAME)
				.setType(EdmSimpleTypeKind.String)
				.setAnnotationAttributes(Lists.newArrayList(NULLABLE_ATTRIBUTE, aliasAttribute));
	}

	/**
	 * Sets the nullable {@link AliasAnnotationGenerator}. If the alias generator is not injected or is {@code null}, the
	 * {@link #generate(TypeDescriptor)} method will return {@code Optional.empty()}.
	 *
	 * @param aliasGenerator alias generator used by the generate method.
	 */
	public void setAliasGenerator(@Nullable final AliasAnnotationGenerator aliasGenerator)
	{
		this.aliasGenerator = aliasGenerator;
	}
}
