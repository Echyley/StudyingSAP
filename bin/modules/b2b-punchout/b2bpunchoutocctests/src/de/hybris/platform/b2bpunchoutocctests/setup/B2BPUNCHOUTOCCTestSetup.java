/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2bpunchoutocctests.setup;

import de.hybris.platform.commercewebservicestests.setup.CommercewebservicesTestSetup;


public class B2BPUNCHOUTOCCTestSetup extends CommercewebservicesTestSetup
{
	public void loadData()
	{
		getSetupImpexService().importImpexFile("/b2bpunchoutocctests/import/sampledata/wsCommerceOrg/essential-data.impex", false);
		getSetupImpexService().importImpexFile("/b2bpunchoutocctests/import/sampledata/wsCommerceOrg/warehouses.impex", false);
		getSetupImpexService().importImpexFile("/b2bpunchoutocctests/import/sampledata/wsCommerceOrg/products-stocklevels.impex", false);
		getSetupImpexService().importImpexFile("/b2bpunchoutocctests/import/sampledata/wsCommerceOrg/process.impex", false);
		getSetupImpexService().importImpexFile("/b2bpunchoutocctests/import/sampledata/wsCommerceOrg/carts.impex", false);
		getSetupSolrIndexerService().executeSolrIndexerCronJob(String.format("%sIndex", WS_TEST), true);
	}
}
