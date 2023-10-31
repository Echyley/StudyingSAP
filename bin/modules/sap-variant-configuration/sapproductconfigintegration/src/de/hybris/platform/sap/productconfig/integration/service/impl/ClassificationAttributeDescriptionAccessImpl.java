/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.integration.service.impl;

import de.hybris.platform.catalog.model.classification.ClassificationAttributeModel;
import de.hybris.platform.sap.productconfig.services.intf.ClassificationAttributeDescriptionAccess;


/**
 * Implementation of {@link ClassificationAttributeDescriptionAccess} which will be used when SAP Integration is present.
 */
public class ClassificationAttributeDescriptionAccessImpl implements ClassificationAttributeDescriptionAccess
{

	@Override
	public String getDescription(ClassificationAttributeModel classificationAttribute)
	{
		return classificationAttribute == null ? null : classificationAttribute.getDescription();
	}

}
