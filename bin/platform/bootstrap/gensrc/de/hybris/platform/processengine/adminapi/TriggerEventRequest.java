/*
 * ----------------------------------------------------------------
 * --- WARNING: THIS FILE IS GENERATED AND WILL BE OVERWRITTEN!
 * --- Generated at 31 de out. de 2023 09:42:37
 * ----------------------------------------------------------------
 *
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.processengine.adminapi;

import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.Objects;
/**
 * Request to trigger business process event
 */
@Schema(name="TriggerEventRequest", description="Request to trigger business process event")
public  class TriggerEventRequest  implements Serializable 

{

	/** Default serialVersionUID value. */
 
	private static final long serialVersionUID = 1L;

	/** Event name<br/><br/><i>Generated property</i> for <code>TriggerEventRequest.event</code> property defined at extension <code>processing</code>. */
@Schema(name="event", description="Event name") 	
	private String event;

	/** Choice of triggered event<br/><br/><i>Generated property</i> for <code>TriggerEventRequest.choice</code> property defined at extension <code>processing</code>. */
@Schema(name="choice", description="Choice of triggered event") 	
	private String choice;
	
	public TriggerEventRequest()
	{
		// default constructor
	}
	
	public void setEvent(final String event)
	{
		this.event = event;
	}

	public String getEvent() 
	{
		return event;
	}
	
	public void setChoice(final String choice)
	{
		this.choice = choice;
	}

	public String getChoice() 
	{
		return choice;
	}
	

}