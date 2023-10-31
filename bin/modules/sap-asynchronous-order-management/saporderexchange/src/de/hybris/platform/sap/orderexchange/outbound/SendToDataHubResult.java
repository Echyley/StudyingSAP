/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.orderexchange.outbound;

/**
 * Result container for sending raw items to DataHub
 */
public interface SendToDataHubResult
{
	
	int SENDING_FAILED_CODE = -1;
	
	int MESSAGE_HANDLING_ERROR = -2;

	/**
	 * @return true if sending was successful
	 */
	boolean isSuccess();

	
	int getErrorCode();

	
	String getErrorText();

}
