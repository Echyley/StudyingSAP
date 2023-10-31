/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.customerinterestsocctests.setup;
import de.hybris.platform.commercewebservicestests.setup.CommercewebservicesTestSetup;
import de.hybris.platform.validation.services.ValidationService;

import javax.annotation.Resource;


public class CustomerInterestsOccTestsSetup extends CommercewebservicesTestSetup
{
	@Resource
	private ValidationService validationService;

	public void loadData()
	{
		getSetupImpexService().importImpexFile("/customerinterestsocctests/import/coredata/common/essential-data.impex", false);
		getSetupSolrIndexerService().executeSolrIndexerCronJob(String.format("%sIndex", WS_TEST), true);
		validationService.reloadValidationEngine();
	}
}
