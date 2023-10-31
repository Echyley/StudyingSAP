/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.services.impl;

import de.hybris.platform.catalog.model.classification.ClassificationAttributeModel;
import de.hybris.platform.sap.productconfig.services.intf.ClassificationAttributeDescriptionAccess;


/**
 * Trivial implementation of {@link ClassificationAttributeDescriptionAccess}, active in a deployment where no SAP
 * integration is available. If SAP integration is available, this bean isn't active, another implementation of the
 * interface residing in an SAP integration extension will take over. <br>
 * <br>
 * This implementation does not access {@link ClassificationAttributeDescriptionAccess} at all.
 */
public class SimpleClassificationAttributeDescriptionAccessImpl implements ClassificationAttributeDescriptionAccess
{

	@Override
	public String getDescription(ClassificationAttributeModel classificationAttribute)
	{
		//The Description property does not exist, therefore we cannot access the value and return null
		return null;
	}

}
