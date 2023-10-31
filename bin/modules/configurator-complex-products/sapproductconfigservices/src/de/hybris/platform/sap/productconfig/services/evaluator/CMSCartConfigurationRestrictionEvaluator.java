/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.services.evaluator;

import de.hybris.platform.cms2.servicelayer.data.RestrictionData;
import de.hybris.platform.cms2.servicelayer.services.evaluator.CMSRestrictionEvaluator;
import de.hybris.platform.sap.productconfig.services.model.CMSCartConfigurationRestrictionModel;


/**
 * This evaluator is attached to the configuration specific cart component. In our CPQ context, it always returns
 * 'true'. Dependent extensions can register other evaluators and register them instead, to offer a more dynamic
 * behaviour.
 */
public class CMSCartConfigurationRestrictionEvaluator implements CMSRestrictionEvaluator<CMSCartConfigurationRestrictionModel>
{

	@Override
	public boolean evaluate(final CMSCartConfigurationRestrictionModel arg0, final RestrictionData arg1)
	{

		return false;
	}

}
