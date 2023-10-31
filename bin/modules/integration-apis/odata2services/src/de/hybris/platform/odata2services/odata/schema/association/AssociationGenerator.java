/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.odata.schema.association;

import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor;
import de.hybris.platform.odata2services.odata.schema.SchemaElementGenerator;

import org.apache.olingo.odata2.api.edm.provider.Association;

public interface AssociationGenerator extends SchemaElementGenerator<Association, TypeAttributeDescriptor>
{
	/**
	 * Generates an association for the referenced TypeAttributeDescriptor
	 *
	 * @param descriptor the integration object attribute descriptor
	 * @return the association
	 */
	Association generate(TypeAttributeDescriptor descriptor);

	/**
	 * Determines if this association generator is applicable for the given attribute descriptor
	 *
	 * @param attributeDescriptor the attribute descriptor we are verifying
	 * @return true if applicable, otherwise false
	 */
	boolean isApplicable(TypeAttributeDescriptor attributeDescriptor);
}
