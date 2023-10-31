/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.odata.schema.association;

import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor;

import org.apache.olingo.odata2.api.edm.EdmMultiplicity;

public class OneToOneAssociationGenerator extends AbstractAssociationGenerator
{
	private static final EdmMultiplicity TARGET_MULTIPLICITY = EdmMultiplicity.ZERO_TO_ONE;

	@Override
	public boolean isApplicable(final TypeAttributeDescriptor attribute)
	{
		return falseIfNull(attribute) && !attribute.isPrimitive() && hasOneElement(attribute);
	}

	private boolean hasOneElement(final TypeAttributeDescriptor attribute)
	{
		return !attribute.isCollection() && (!attribute.isMap() || attribute.isLocalized());
	}

	@Override
	protected EdmMultiplicity getTargetCardinality(final TypeAttributeDescriptor descriptor)
	{
		return TARGET_MULTIPLICITY;
	}
}
