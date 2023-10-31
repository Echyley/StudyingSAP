/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.odata.schema.property;

import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;
import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor;
import de.hybris.platform.odata2services.odata.schema.SchemaElementGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.olingo.odata2.api.edm.provider.Property;
import org.springframework.beans.factory.annotation.Required;

/**
 * @deprecated use {@link PrimitivePropertyListElementGenerator} instead.
 */
@Deprecated(since = "2211.FP1", forRemoval = true)
public class PrimitivePropertyListGenerator extends AbstractPropertyListGenerator
{
	private SchemaElementGenerator<Optional<Property>, IntegrationObjectItemModel> integrationKeyPropertyGenerator;

	@Override
	public List<Property> generate(final IntegrationObjectItemModel itemModel)
	{
		final List<Property> properties = new ArrayList<>(super.generate(itemModel));
		integrationKeyPropertyGenerator.generate(itemModel)
		                               .ifPresent(properties::add);
		return properties;
	}

	@Override
	protected boolean isApplicable(final TypeAttributeDescriptor descriptor)
	{
		return true;
	}

	@Required
	public void setIntegrationKeyPropertyGenerator(final SchemaElementGenerator<Optional<Property>, IntegrationObjectItemModel> integrationKeyPropertyGenerator)
	{
		this.integrationKeyPropertyGenerator = integrationKeyPropertyGenerator;
	}
}
