/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.services.intf;

import de.hybris.platform.catalog.model.classification.ClassificationAttributeModel;


/**
 * Provide logic to access classification attribute description
 */
public interface ClassificationAttributeDescriptionAccess
{
	/**
	 * Get the description for the given Classification Attribute
	 *
	 * @param classificationAttribute
	 * @return description
	 */
	String getDescription(final ClassificationAttributeModel classificationAttribute);
}
