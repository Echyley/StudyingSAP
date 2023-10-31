/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saps4omservices.services.impl;

import de.hybris.platform.commerceservices.externaltax.impl.DefaultExternalTaxesService;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.sap.saps4omservices.services.SapS4OrderManagementConfigService;

/**
 * calculate sap external Taxes
 */
public class SapExternalTaxesService extends DefaultExternalTaxesService {
	private SapS4OrderManagementConfigService sapS4OrderManagementConfigService;
	@Override
	public boolean calculateExternalTaxes(AbstractOrderModel abstractOrder) {
		if (getSapS4OrderManagementConfigService().isCatalogPricingEnabled()) {
			// since the taxes are already calculated from ERP backend, no need to process taxes
			return true;
		} else {
			return super.calculateExternalTaxes( abstractOrder);
		}
	}
	public SapS4OrderManagementConfigService getSapS4OrderManagementConfigService() {
		return sapS4OrderManagementConfigService;
	}
	public void setSapS4OrderManagementConfigService(SapS4OrderManagementConfigService sapS4OrderManagementConfigService) {
		this.sapS4OrderManagementConfigService = sapS4OrderManagementConfigService;
	}
	


}
