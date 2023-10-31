/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapmodel.jalo.interceptor;

import de.hybris.platform.sap.sapmodel.model.SAPPlantLogSysOrgModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;
import de.hybris.platform.util.localization.Localization;

public class SapPlantLogSysOrgValidationInterceptor implements
		ValidateInterceptor<SAPPlantLogSysOrgModel> {

	@Override
	public void onValidate(SAPPlantLogSysOrgModel sapPlantLogSysOrg, InterceptorContext ctx)
			throws InterceptorException {

		if (sapPlantLogSysOrg.getLogSys().getSapGlobalConfiguration() == null) {

			throw new InterceptorException(
					Localization
							.getLocalizedString("validation.Saplogicalsystem.GlobalConfigurationMissing",
									new Object[] { sapPlantLogSysOrg.getLogSys().getSapLogicalSystemName() }));

		}

	}

}
