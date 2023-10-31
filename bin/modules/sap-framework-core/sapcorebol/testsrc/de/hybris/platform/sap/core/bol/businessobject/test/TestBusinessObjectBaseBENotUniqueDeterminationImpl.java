/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.bol.businessobject.test;

import de.hybris.platform.sap.core.bol.businessobject.BackendInterface;
import de.hybris.platform.sap.core.bol.businessobject.BusinessObjectBase;
import de.hybris.platform.sap.core.bol.businessobject.test.be.TestBackendInterfaceBENotUniqueDetermination;


/**
 * Test BusinessObjectBase implementation - for backend type determination test where the determination is not unique.
 */
@BackendInterface(TestBackendInterfaceBENotUniqueDetermination.class)
public class TestBusinessObjectBaseBENotUniqueDeterminationImpl extends BusinessObjectBase
{
	// only for testing
}
