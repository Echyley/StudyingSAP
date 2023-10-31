/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.jco.runtime;


/**
 * Initializes the SAP JCo Reference Provider globally.
 */
public class SAPJCoReferenceProviderLifecycle
{

	/**
	 * register the SAPJCoSessionReferenceProvider.
	 */
    private SAPJCoReferenceProviderLifecycle()
    {
        
    }
	public static void init()
	{
		SAPJCoSessionReferenceProvider.init();
	}

}
