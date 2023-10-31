/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcpqquoteintegrationocctests.setup;

import de.hybris.platform.commercewebservicestests.setup.CommercewebservicesTestSetup;


public class SapCpqQuoteIntegrationOCCTestSetup extends CommercewebservicesTestSetup
{
	public void loadData()
	{
		getSetupImpexService().importImpexFile(
				"/sapcpqquoteintegrationocctests/import/coredata/common/cpq-discounts.impex", false);


		getSetupSolrIndexerService().executeSolrIndexerCronJob(String.format("%sIndex", WS_TEST), true);
	}
}
