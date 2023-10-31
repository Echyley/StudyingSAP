/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedservicewebservices.dto;


import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Representation of a list of tickets.
 */
@Schema(name="C360TicketList", description="Representation of a list of tickets.")
public  class C360TicketList extends Customer360DataWsDTO

{
	/** Type of the customer 360 data returned<br/><br/><i>Generated property</i> for <code>C360TicketList.type</code> property defined at extension <code>assistedservicewebservices</code>. */
	@Schema(name="type", description="Type of the returned Customer 360 data.", example = "c360TicketList")
	private String type;

	/** List of tickets the customer has<br/><br/><i>Generated property</i> for <code>C360TicketList.tickets</code> property defined at extension <code>assistedservicewebservices</code>. */
	@Schema(name="tickets", description="List of tickets the customer has.")
	private List<C360TicketWsDTO> tickets;

	public C360TicketList()
	{
		// default constructor
	}

	public void setTickets(final List<C360TicketWsDTO> tickets)
	{
		this.tickets = tickets;
	}

	public List<C360TicketWsDTO> getTickets()
	{
		return tickets;
	}
}
