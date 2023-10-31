/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.rules.rao.populator;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.sap.productconfig.rules.model.ProductConfigProcessStepModel;
import de.hybris.platform.sap.productconfig.rules.rao.ProductConfigProcessStepRAO;


/**
 * Populator for the {@link ProductConfigProcessStepRAO}
 */
public class ProductConfigProcessStepRAOPopulator implements
		Populator<ProductConfigProcessStepModel, ProductConfigProcessStepRAO>
{

	@Override
	public void populate(final ProductConfigProcessStepModel source, final ProductConfigProcessStepRAO target)
	{
		target.setProcessStep(source.getProcessStep());
	}
}
