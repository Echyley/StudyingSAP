/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.chineseaddressocctests.setup;

import de.hybris.platform.commercewebservicestests.setup.CommercewebservicesTestSetup;
import de.hybris.platform.validation.services.ValidationService;

import javax.annotation.Resource;


public class ChineseAddressOccTestSetup extends CommercewebservicesTestSetup
{
	@Resource
	private ValidationService validationService;

	public void loadData()
	{
		getSetupImpexService().importImpexFile("/chineseaddressocctests/import/sampledata/wsCommerceOrg/test-data.impex", false);

		getSetupSolrIndexerService().executeSolrIndexerCronJob(String.format("%sIndex", WS_TEST), true);
		validationService.reloadValidationEngine();
	}
}
