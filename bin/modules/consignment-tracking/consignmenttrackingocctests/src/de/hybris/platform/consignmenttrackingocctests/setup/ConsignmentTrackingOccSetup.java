/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.consignmenttrackingocctests.setup;
import de.hybris.platform.commercewebservicestests.setup.CommercewebservicesTestSetup;
import de.hybris.platform.validation.services.ValidationService;

import javax.annotation.Resource;


public class ConsignmentTrackingOccSetup extends CommercewebservicesTestSetup
{
	@Resource
	private ValidationService validationService;

	public void loadData()
	{
		getSetupImpexService().importImpexFile("/consignmenttrackingocctests/import/coredata/common/essential-data.impex", false);
		getSetupSolrIndexerService().executeSolrIndexerCronJob(String.format("%sIndex", WS_TEST), true);
		validationService.reloadValidationEngine();
	}
}
