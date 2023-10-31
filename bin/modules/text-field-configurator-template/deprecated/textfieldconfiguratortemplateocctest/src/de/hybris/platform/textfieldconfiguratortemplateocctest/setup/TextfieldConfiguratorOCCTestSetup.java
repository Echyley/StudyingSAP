/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.textfieldconfiguratortemplateocctest.setup;

import de.hybris.platform.ycommercewebservicestest.setup.YCommerceWebServicesTestSetup;


public class TextfieldConfiguratorOCCTestSetup extends YCommerceWebServicesTestSetup
{
	public void loadData()
	{
		getSetupImpexService().importImpexFile(
				"/textfieldconfiguratortemplateocctest/import/sampledata/productCatalogs/wsTestProductCatalog/standaloneTestData.impex",
				false);
		getSetupSolrIndexerService().executeSolrIndexerCronJob(String.format("%sIndex", WS_TEST), true);
	}
}
