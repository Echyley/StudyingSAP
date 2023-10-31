/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedservicefacades.customer360.populators;

import de.hybris.platform.assistedservicefacades.util.AssistedServiceUtils;
import de.hybris.platform.assistedservicefacades.customer360.GeneralActivityData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.ticket.model.CsTicketModel;

import static de.hybris.platform.assistedservicefacades.constants.AssistedservicefacadesConstants.TICKET_TEXT;


/**
 * CustomerTicketModel -> GeenralActivityTicket populator
 *
 */
public class GeneralActivityTicketPopulator implements Populator<CsTicketModel, GeneralActivityData>
{
	private BaseSiteService baseSiteService;

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.converters.Populator#populate(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void populate(final CsTicketModel ticketModel, final GeneralActivityData ticketData)
	{
		ticketData.setType(TICKET_TEXT);
		ticketData.setId(ticketModel.getTicketID());
		ticketData.setStatus(ticketModel.getState().getCode());
		ticketData.setCreated(ticketModel.getCreationtime());
		ticketData.setUpdated(ticketModel.getModifiedtime());
		ticketData.setDescription(ticketModel.getHeadline());
		ticketData.setUrl(AssistedServiceUtils.populateTicketUrl(ticketModel, getBaseSiteService().getCurrentBaseSite()));
		ticketData.setCategory(ticketModel.getCategory() == null ? "---" : ticketModel.getCategory().toString());

	}

	protected BaseSiteService getBaseSiteService()
	{
		return baseSiteService;
	}

	public void setBaseSiteService(final BaseSiteService baseSiteService)
	{
		this.baseSiteService = baseSiteService;
	}
}
