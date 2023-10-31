/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2bocctests.setup;

import de.hybris.platform.commercewebservicestests.setup.CommercewebservicesTestSetup;


public class B2BOCCTestSetup extends CommercewebservicesTestSetup
{
	public void loadData()
	{
		getSetupImpexService().importImpexFile("/b2bocctests/import/sampledata/wsCommerceOrg/essential-data.impex", false);
		getSetupImpexService().importImpexFile("/b2bocctests/import/sampledata/wsCommerceOrg/products.impex", false);
		getSetupImpexService().importImpexFile("/b2bocctests/import/sampledata/wsCommerceOrg/quotes.impex", false);
		getSetupImpexService().importImpexFile("/b2bocctests/import/sampledata/wsCommerceOrg/warehouses.impex", false);
		getSetupImpexService().importImpexFile("/b2bocctests/import/sampledata/wsCommerceOrg/essential-data-user-rights.impex",
				false);
		getSetupImpexService().importImpexFile(
				"/b2bocctests/import/sampledata/wsCommerceOrg/standalonePermissionManagementTestData.impex", false);
		getSetupImpexService().importImpexFile(
				"/b2bocctests/import/sampledata/wsCommerceOrg/standaloneBudgetManagementTestData.impex", false);
		getSetupImpexService().importImpexFile(
				"/b2bocctests/import/sampledata/wsCommerceOrg/standaloneUnitManagementTestData.impex", false);
		getSetupImpexService().importImpexFile(
				"/b2bocctests/import/sampledata/wsCommerceOrg/standaloneUserManagementTestData.impex", false);
		getSetupImpexService().importImpexFile(
				"/b2bocctests/import/sampledata/wsCommerceOrg/standaloneOrderApprovalsTestData.impex", false);
		getSetupImpexService().importImpexFile(
				"/b2bocctests/import/sampledata/wsCommerceOrg/standaloneUnitGroupsManagementTestData.impex", false);
		getSetupImpexService().importImpexFile(
				"/b2bocctests/import/sampledata/wsCommerceOrg/standaloneReplenishmentOrderTestData.impex", false);
		getSetupImpexService().importImpexFile(
				"/b2bocctests/import/sampledata/wsCommerceOrg/standaloneCostCentersTestData.impex", false);
		getSetupImpexService().importImpexFile("/b2bocctests/import/sampledata/wsCommerceOrg/standaloneOrdersTestData.impex",
				false);
		getSetupImpexService().importImpexFile("/b2bocctests/import/sampledata/wsCommerceOrg/products-stocklevels.impex",
				false);
		getSetupImpexService().importImpexFile("/b2bocctests/import/sampledata/wsCommerceOrg/promotionsmodule.impex", false);
		getSetupImpexService().importImpexFile("/impex/essentialdata_1_usergroups.impex", false);
		getSetupImpexService().importImpexFile("/impex/essentialdata_2_b2bcommerce.impex", false);
		getSetupSolrIndexerService().executeSolrIndexerCronJob(String.format("%sIndex", WS_TEST), true);
		getSetupImpexService()
				.importImpexFile("/b2bocctests/import/sampledata/wsCommerceOrg/userGroupsRegistrationTestData.impex", false);
		getSetupImpexService().importImpexFile("/b2bocctests/import/sampledata/wsCommerceOrg/projectdataUsers.impex", false);
		getSetupImpexService().importImpexFile("/b2bocctests/import/sampledata/wsCommerceOrg/workflowTestData.impex", false);
		getSetupImpexService()
				.importImpexFile("/impex/projectdata-b2bacceleratorservices-registrationapprovedemailprocess.impex",
						false);
		getSetupImpexService().importImpexFile(
				"/impex/projectdata-b2bacceleratorservices-registrationpendingapprovalemailprocess.impex", false);
		getSetupImpexService()
				.importImpexFile("/impex/projectdata-b2bacceleratorservices-registrationreceivedemailprocess.impex",
						false);
		getSetupImpexService()
				.importImpexFile("/impex/projectdata-b2bacceleratorservices-registrationrejectedemailprocess.impex",
						false);
	}
}
