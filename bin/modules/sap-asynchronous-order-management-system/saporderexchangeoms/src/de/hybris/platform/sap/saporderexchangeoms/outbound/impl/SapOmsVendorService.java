/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saporderexchangeoms.outbound.impl;

import de.hybris.platform.sap.saporderexchangeoms.constants.SapOmsOrderExchangeConstants;
import de.hybris.platform.sap.saporderexchangeoms.outbound.SapVendorService;

/**
 * Concrete implementation to provide business logic for {@link de.hybris.platform.sap.saporderexchangeoms.outbound.SapVendorService}
 */
public class SapOmsVendorService implements
		SapVendorService {

	@Override
	public boolean isVendorExternal(String vendorCode) {
        
		return !vendorCode.contentEquals(SapOmsOrderExchangeConstants.INTERNAl_VENDOR);

	}

	@Override
	public String getVendorItemCategory() {

		return SapOmsOrderExchangeConstants.VENDOR_ITEM_CATEGORY;
		
	}
	
	
}
