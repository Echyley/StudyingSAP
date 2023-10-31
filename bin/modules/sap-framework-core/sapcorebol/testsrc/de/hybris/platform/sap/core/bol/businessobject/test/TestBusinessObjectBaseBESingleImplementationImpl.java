/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.bol.businessobject.test;

import de.hybris.platform.sap.core.bol.businessobject.BackendInterface;
import de.hybris.platform.sap.core.bol.businessobject.BusinessObjectBase;
import de.hybris.platform.sap.core.bol.businessobject.test.be.TestBackendInterfaceBESingleImplementation;


/**
 * Test BusinessObjectBase implementation - for single backend implementation test..
 */
@BackendInterface(TestBackendInterfaceBESingleImplementation.class)
public class TestBusinessObjectBaseBESingleImplementationImpl extends BusinessObjectBase
{
	// only for testing
}
