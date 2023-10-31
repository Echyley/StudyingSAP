/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.bol.businessobject.test.be;

import de.hybris.platform.sap.core.bol.backend.BackendBusinessObjectBase;
import de.hybris.platform.sap.core.bol.backend.BackendType;


/**
 * Test BackendBusinedssObjectBase implementation - for backend type determination test.
 */
@BackendType("CRM")
public class TestBackendBusinessObjectBaseBENotUniqueDeterminationCRMImpl extends BackendBusinessObjectBase implements
		TestBackendInterfaceBENotUniqueDetermination
{
	// only for testing
}
