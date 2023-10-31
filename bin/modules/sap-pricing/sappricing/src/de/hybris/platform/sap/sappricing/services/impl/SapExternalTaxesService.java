/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sappricing.services.impl;

import de.hybris.platform.commerceservices.externaltax.impl.DefaultExternalTaxesService;
import de.hybris.platform.core.model.order.AbstractOrderModel;

/**
 * calculate sap external Taxes
 */
public class SapExternalTaxesService extends DefaultExternalTaxesService {

	@Override
	public boolean calculateExternalTaxes(AbstractOrderModel abstractOrder) {
		// since the taxes are already calculated from ERP backend, no need to process taxes
		return true;
	}
}
