/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.odata.schema.property;

import static de.hybris.platform.odata2services.constants.Odata2servicesConstants.LANGUAGE_KEY_PROPERTY_NAME;

import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor;
import de.hybris.platform.integrationservices.model.TypeDescriptor;
import de.hybris.platform.odata2services.odata.schema.SchemaElementGenerator;
import de.hybris.platform.odata2services.odata.schema.attribute.ImmutableAnnotationAttribute;

import java.util.Collections;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.apache.olingo.odata2.api.edm.EdmSimpleTypeKind;
import org.apache.olingo.odata2.api.edm.provider.AnnotationAttribute;
import org.apache.olingo.odata2.api.edm.provider.Property;
import org.apache.olingo.odata2.api.edm.provider.SimpleProperty;

/**
 * Generates properties for Localized___Attributes type in the EDMX schema. That is a special type to capture values of all localized
 * attributes in an integration object item for a given locale. For example, if an integration object item has {@code name} and
 * {@code description} localized attributes, then the Localized___Attributes type will have these properties: {@code language},
 * {@code name} and {@code description}
 */
public class LocalizedPropertyListElementGenerator extends AbstractPropertyListElementGenerator
{
	private static final AnnotationAttribute NULLABLE_ATTRIBUTE = new ImmutableAnnotationAttribute().setName("Nullable")
	                                                                                                .setText("false");

	/**
	 * To instantiate the {@link LocalizedPropertyListElementGenerator}.
	 *
	 * @param propertyGenerator a non-null simple property generator
	 */
	public LocalizedPropertyListElementGenerator(
			@NotNull final SchemaElementGenerator<SimpleProperty, TypeAttributeDescriptor> propertyGenerator)
	{
		super(propertyGenerator);
	}

	@Override
	public List<Property> generateAdditionalProperties(final TypeDescriptor descriptor)
	{
		return List.of(languageProperty());
	}

	@Override
	protected boolean isApplicable(final TypeAttributeDescriptor descriptor)
	{
		return descriptor.isLocalized();
	}

	private Property languageProperty()
	{
		return new SimpleProperty()
				.setName(LANGUAGE_KEY_PROPERTY_NAME)
				.setType(EdmSimpleTypeKind.String)
				.setAnnotationAttributes(Collections.singletonList(NULLABLE_ATTRIBUTE));
	}
}
