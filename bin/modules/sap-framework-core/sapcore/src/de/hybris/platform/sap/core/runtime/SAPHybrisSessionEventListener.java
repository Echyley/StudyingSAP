/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.runtime;



/**
 * Event listener interface for SAP hybris session.
 */
public interface SAPHybrisSessionEventListener
{

	/**
	 * Event called after the SAP hybris session has been destroyed.
	 * 
	 * @param sapHybrisSession
	 *           SAP hybris session
	 */
	public void onAfterDestroy(SAPHybrisSession sapHybrisSession);

}
