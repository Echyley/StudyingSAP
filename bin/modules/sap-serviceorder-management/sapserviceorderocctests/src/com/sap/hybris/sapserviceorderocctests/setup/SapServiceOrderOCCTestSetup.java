/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapserviceorderocctests.setup;

import de.hybris.platform.commercewebservicestests.setup.CommercewebservicesTestSetup;


public class SapServiceOrderOCCTestSetup extends CommercewebservicesTestSetup
{
	public void loadData()
	{
		getSetupImpexService().importImpexFile(
				"/sapserviceorderocctests/import/coredata/common/sapserviceorder-data.impex", false);
		getSetupImpexService().importImpexFile(
				"/sapserviceorderocctests/import/coredata/common/products.impex", false);
		getSetupImpexService().importImpexFile(
				"/sapserviceorderocctests/import/coredata/common/cartdata/projectdata-dynamic-business-process-order.impex", false);

		getSetupSolrIndexerService().executeSolrIndexerCronJob(String.format("%sIndex", WS_TEST), true);
	}
}
