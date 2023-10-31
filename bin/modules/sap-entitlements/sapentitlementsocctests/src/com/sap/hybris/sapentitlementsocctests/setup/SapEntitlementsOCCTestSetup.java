/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapentitlementsocctests.setup;

import de.hybris.platform.commercewebservicestests.setup.CommercewebservicesTestSetup;


/**
 *
 */
public class SapEntitlementsOCCTestSetup extends CommercewebservicesTestSetup
{
	public void loadData()
	{

		getSetupImpexService().importImpexFile(
				"/sapentitlementsocctests/resources/sapentitlementsocctests/import/coredata/common/essential-data.impex", false);
	}
}
