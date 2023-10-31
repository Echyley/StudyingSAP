/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.configuration.rfc.event;



/**
 * Event is triggered when ping a RFC Destination.
 */
public class SAPRFCDestinationPingEvent extends SAPRFCDestinationEvent
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3207236821397591282L;

	private int resultIndicator = 0;

	private String message;

	private boolean needRefresh;

	/**
	 * Default Constructor.
	 */
	public SAPRFCDestinationPingEvent()
	{
	}

	/**
	 * Constructor.
	 * 
	 * @param rfcDestinationName
	 *           RFC destination name
	 */
	public SAPRFCDestinationPingEvent(final String rfcDestinationName)
	{
		super(rfcDestinationName);
	}

	/**
	 * Returns the result indicator.
	 * 
	 * @return the resultIndicator
	 */
	public int getResultIndicator()
	{
		return resultIndicator;
	}

	/**
	 * Sets the result indicator.
	 * 
	 * @param resultIndicator
	 *           result indicator
	 */
	public void setResultIndicator(final int resultIndicator)
	{
		this.resultIndicator = resultIndicator;
	}

	/**
	 * Returns the result message.
	 * 
	 * @return the message
	 */
	public String getMessage()
	{
		return message;
	}

	/**
	 * Sets the result message.
	 * 
	 * @param message
	 *           the message to set
	 */
	public void setMessage(final String message)
	{
		this.message = message;
	}

	/**
	 * Returns an indicator whether a refresh is needed.
	 * 
	 * @return true, if a refresh is needed
	 */
	public boolean isNeedRefresh()
	{
		return needRefresh;
	}

	/**
	 * Set the indicator whether a refresh is needed.
	 * 
	 * @param needRefresh
	 *           refresh needed indicator
	 */
	public void setNeedRefresh(final boolean needRefresh)
	{
		this.needRefresh = needRefresh;
	}

}
