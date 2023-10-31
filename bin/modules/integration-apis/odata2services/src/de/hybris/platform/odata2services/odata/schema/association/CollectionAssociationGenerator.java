/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.odata.schema.association;

import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor;

public class CollectionAssociationGenerator extends AbstractAssociationGenerator
{
	@Override
	public boolean isApplicable(final TypeAttributeDescriptor attrDescriptor)
	{
		return falseIfNull(attrDescriptor) && (attrDescriptor.isCollection() || (attrDescriptor.isMap() && !attrDescriptor.isLocalized()));
	}
}
