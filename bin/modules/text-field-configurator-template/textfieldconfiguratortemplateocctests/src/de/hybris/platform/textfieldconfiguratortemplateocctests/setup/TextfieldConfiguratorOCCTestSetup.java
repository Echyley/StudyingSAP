/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.textfieldconfiguratortemplateocctests.setup;

import de.hybris.platform.commercewebservicestests.setup.CommercewebservicesTestSetup;



public class TextfieldConfiguratorOCCTestSetup extends CommercewebservicesTestSetup
{
	public void loadData()
	{
		getSetupImpexService().importImpexFile(
				"/textfieldconfiguratortemplateocctests/import/sampledata/productCatalogs/wsTestProductCatalog/standaloneTestData.impex",
				false);
		getSetupSolrIndexerService().executeSolrIndexerCronJob(String.format("%sIndex", WS_TEST), true);
	}
}
