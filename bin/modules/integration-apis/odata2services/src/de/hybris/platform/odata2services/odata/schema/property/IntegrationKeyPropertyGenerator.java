/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.odata.schema.property;

import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;
import de.hybris.platform.odata2services.odata.schema.SchemaElementGenerator;
import de.hybris.platform.odata2services.odata.schema.attribute.AliasAnnotationGenerator;
import de.hybris.platform.odata2services.odata.schema.attribute.ImmutableAnnotationAttribute;

import java.util.Optional;

import org.apache.olingo.odata2.api.edm.EdmSimpleTypeKind;
import org.apache.olingo.odata2.api.edm.provider.AnnotationAttribute;
import org.apache.olingo.odata2.api.edm.provider.Property;
import org.apache.olingo.odata2.api.edm.provider.SimpleProperty;
import org.springframework.beans.factory.annotation.Required;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

/**
 * A generator for generating an {@link Optional<Property>} schema element from a {@link IntegrationObjectItemModel}.
 *
 * @deprecated use {@link IntegrationKeyPropertyElementGenerator} instead.
 */
@Deprecated(since = "2211.FP1", forRemoval = true)
public class IntegrationKeyPropertyGenerator implements SchemaElementGenerator<Optional<Property>, IntegrationObjectItemModel>
{
	private static final AnnotationAttribute NULLABLE_ATTRIBUTE = new ImmutableAnnotationAttribute().setName("Nullable")
	                                                                                                .setText("false");

	private AliasAnnotationGenerator aliasGenerator;

	@Override
	public Optional<Property> generate(final IntegrationObjectItemModel integrationObjectItemModel)
	{
		Preconditions.checkArgument(integrationObjectItemModel != null,
				"An Integration Key Property cannot be generated from a null IntegrationObjectItemModel");

		final AnnotationAttribute aliasAttribute = aliasGenerator.generate(integrationObjectItemModel);
		return aliasAttribute != null ? Optional.of(createProperty(aliasAttribute)) : Optional.empty();
	}

	private static SimpleProperty createProperty(final AnnotationAttribute aliasAttribute)
	{
		return new SimpleProperty()
				.setName("integrationKey")
				.setType(EdmSimpleTypeKind.String)
				.setAnnotationAttributes(Lists.newArrayList(NULLABLE_ATTRIBUTE, aliasAttribute));
	}

	@Required
	public void setAliasGenerator(final AliasAnnotationGenerator aliasGenerator)
	{
		this.aliasGenerator = aliasGenerator;
	}
}
