/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.orderexchange.outbound.impl;

import de.hybris.platform.sap.orderexchange.outbound.SendToDataHubResult;

import org.springframework.http.HttpStatus;

/**
 * Container for the Result of the Datahub sending process
 */

public class DefaultSendToDataHubResult implements SendToDataHubResult
{
	public static final SendToDataHubResult OKAY = new DefaultSendToDataHubResult(HttpStatus.OK, "Sent successfully");
	public static final SendToDataHubResult SENDING_FAILED = new DefaultSendToDataHubResult(SendToDataHubResult.SENDING_FAILED_CODE, "Sending failed for unknown reason");
	public static final SendToDataHubResult MESSAGE_HANDLING_ERROR = new DefaultSendToDataHubResult(SendToDataHubResult.MESSAGE_HANDLING_ERROR, "Sending failed for unknown reason");

	private  final String errorText;
	private  final int errorCode;
	private  HttpStatus httpStatus;

	public boolean isSuccess()
	{
		final int httpOk = 200;
		return this.httpStatus != null && getErrorCode() == httpOk;
	}

	public int getErrorCode()
	{
		return this.errorCode;
	}

	public String getErrorText()
	{
		return this.errorText;
	}

	public DefaultSendToDataHubResult(final HttpStatus httpStatus, final String errorText)
	{
		this.httpStatus = httpStatus;
		this.errorText = errorText;
		this.errorCode = httpStatus.value();
	}

	public DefaultSendToDataHubResult(final int errorCode, final String errorText)
	{
		this.errorCode = errorCode;
		this.errorText = errorText;
	}

	@Override
	public String toString()
	{
		return "httpStatus=" + httpStatus + ", errorText=" + errorText;
	}

}
