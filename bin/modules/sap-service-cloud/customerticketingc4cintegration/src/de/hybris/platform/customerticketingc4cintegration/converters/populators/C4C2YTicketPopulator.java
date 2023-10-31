/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.customerticketingc4cintegration.converters.populators;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.customerticketingc4cintegration.SitePropsHolder;
import de.hybris.platform.customerticketingc4cintegration.data.ServiceRequestData;
import de.hybris.platform.customerticketingfacades.data.StatusData;
import de.hybris.platform.customerticketingfacades.data.TicketData;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;


/**
 * ServiceRequestData -> TicketData populator, used for converting c4c data object to hybris one.
 *
 * @param <SOURCE
 *           extends ServiceRequestData>
 * @param <TARGET
 *           extends TicketData>
 */
public class C4C2YTicketPopulator<SOURCE extends ServiceRequestData, TARGET extends TicketData>
		implements Populator<SOURCE, TARGET>
{
	private SitePropsHolder sitePropsHolder;
	private Map<StatusData, List<StatusData>> validTransitions;
	private Map<String, StatusData> statusMapping;
	private Converter<ServiceRequestData, TicketData> ticketEventConverter;

	@Override
	public void populate(final SOURCE source, final TARGET target)
	{
		target.setSubject(source.getName());
		target.setId(source.getID());

		target.setCreationDate(parseDate(source.getCreationDateTime()));
		target.setLastModificationDate(parseDate(source.getLastChangeDateTime()));

		if (getSitePropsHolder().isB2C())
		{
			target.setCustomerId(source.getBuyerPartyID());
		}
		else
		{
			target.setCustomerId(source.getBuyerMainContactPartyID());
		}

		if (CollectionUtils.isNotEmpty(source.getRelatedTransactions()))
		{
			target.setCartId(source.getRelatedTransactions().get(0).getID());
		}

		getTicketEventConverter().convert(source, target);

		target.setStatus(getStatusMapping().get(source.getStatusCode()));
		final List<StatusData> transitionStatuuses = getValidTransitions().get(target.getStatus());
		target.setAvailableStatusTransitions(transitionStatuuses == null ? Collections.emptyList() : transitionStatuuses);
	}

	protected Date parseDate(final String date)
	{
		if (StringUtils.isEmpty(date))
		{
			return null;
		}
		//Date info is being sent in this format Date(1234567890123) and we need to make sure it is following this format to parse it
		final Pattern datePattern = Pattern.compile("Date\\((\\d{13})\\)");
		final Matcher m = datePattern.matcher(date);
		if (m.find())
		{
			final String dateString = m.group(1);
			return new Date(Long.parseLong(dateString));
		}
		return null;
	}

	protected SitePropsHolder getSitePropsHolder()
	{
		return sitePropsHolder;
	}

	public void setSitePropsHolder(final SitePropsHolder sitePropsHolder)
	{
		this.sitePropsHolder = sitePropsHolder;
	}

	protected Map<StatusData, List<StatusData>> getValidTransitions()
	{
		return validTransitions;
	}

	public void setValidTransitions(final Map<StatusData, List<StatusData>> validTransitions)
	{
		this.validTransitions = validTransitions;
	}

	protected Map<String, StatusData> getStatusMapping()
	{
		return statusMapping;
	}

	public void setStatusMapping(final Map<String, StatusData> statusMapping)
	{
		this.statusMapping = statusMapping;
	}

	protected Converter<ServiceRequestData, TicketData> getTicketEventConverter()
	{
		return ticketEventConverter;
	}

	public void setTicketEventConverter(final Converter<ServiceRequestData, TicketData> ticketEventConverter)
	{
		this.ticketEventConverter = ticketEventConverter;
	}
}
