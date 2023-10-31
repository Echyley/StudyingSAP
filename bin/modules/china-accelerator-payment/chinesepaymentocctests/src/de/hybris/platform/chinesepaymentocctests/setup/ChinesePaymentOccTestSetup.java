/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.chinesepaymentocctests.setup;

import de.hybris.platform.commercewebservicestests.setup.CommercewebservicesTestSetup;
import de.hybris.platform.validation.services.ValidationService;

import javax.annotation.Resource;


public class ChinesePaymentOccTestSetup extends CommercewebservicesTestSetup
{
	@Resource
	private ValidationService validationService;

	public void loadData()
	{
		getSetupImpexService().importImpexFile("/chinesepaymentocctests/import/sampledata/wsCommerceOrg/test-data.impex", false);
		getSetupImpexService().importImpexFile("/chinesepaymentocctests/import/sampledata/wsCommerceOrg/warehouses.impex", false);
		getSetupImpexService().importImpexFile("/chinesepaymentocctests/import/sampledata/wsCommerceOrg/products.impex", false);
		getSetupImpexService().importImpexFile("/chinesepaymentocctests/import/sampledata/wsCommerceOrg/products-stocklevels.impex", false);


		getSetupSolrIndexerService().executeSolrIndexerCronJob(String.format("%sIndex", WS_TEST), true);
		validationService.reloadValidationEngine();
	}
}
