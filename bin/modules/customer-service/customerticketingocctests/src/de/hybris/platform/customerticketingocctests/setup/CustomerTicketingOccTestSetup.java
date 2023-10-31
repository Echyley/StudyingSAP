/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.customerticketingocctests.setup;

import de.hybris.platform.commercewebservicestests.setup.CommercewebservicesTestSetup;
import de.hybris.platform.validation.services.ValidationService;

import javax.annotation.Resource;


public class CustomerTicketingOccTestSetup extends CommercewebservicesTestSetup
{
	@Resource
	private ValidationService validationService;

	public void loadData()
	{
		getSetupImpexService().importImpexFile("/customerticketingocctests/import/sampledata/wsCommerceOrg/test-data.impex", false);
		getSetupImpexService()
				.importImpexFile("/customerticketingocctests/import/sampledata/wsCommerceOrg/warehouses.impex", false);
		getSetupImpexService()
				.importImpexFile("/customerticketingocctests/import/sampledata/wsCommerceOrg/products.impex", false);
		getSetupImpexService()
				.importImpexFile("/customerticketingocctests/import/sampledata/wsCommerceOrg/products-stocklevels.impex", false);
		getSetupImpexService()
				.importImpexFile("/customerticketingocctests/import/sampledata/wsCommerceOrg/ticket-event-attachment-testData.impex", false);
		getSetupImpexService()
				.importImpexFile("/impex/essentialdata-ticket-constraints.impex", false);

		getSetupSolrIndexerService().executeSolrIndexerCronJob(String.format("%sIndex", WS_TEST), true);
		validationService.reloadValidationEngine();
	}
}
