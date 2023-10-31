/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapbillinginvoiceocctests.setup;

import de.hybris.platform.commercewebservicestests.setup.CommercewebservicesTestSetup;


/**
 * Utility class to be used in test suites to manage tests (e.g. start server, load data).
 */
public class SapBillingInvoiceOCCTestSetup extends CommercewebservicesTestSetup
{
	public void loadData()
	{
		getSetupImpexService().importImpexFile("/sapbillinginvoiceocctests/import/sampledata/user-sap-orders.impex", false);
	}
}
