/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.customerticketingc4cintegration.converters.populators;

import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.customerticketingc4cintegration.constants.Customerticketingc4cintegrationConstants;
import de.hybris.platform.customerticketingc4cintegration.data.ServiceRequestData;
import de.hybris.platform.customerticketingfacades.data.TicketData;
import de.hybris.platform.customerticketingfacades.data.TicketEventData;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.util.localization.Localization;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

/**
 *
 * Populator for populating TicketEventData in TicketData from MemoActivity in
 * ServiceRequestData
 *
 * @param <SOURCE> ServiceRequestData
 * @param <TARGET> TicketData
 */
public class C4C2YMemoActivityTicketEventsPopulator<SOURCE extends ServiceRequestData, TARGET extends TicketData>
	implements Populator<SOURCE, TARGET> {

    private CustomerFacade customerFacade;
    private ConfigurationService configurationService;

    public static final Comparator<TicketEventData> TICKET_EVENT_DATE_COMPARATOR = (TicketEventData o1,
	    TicketEventData o2) -> o2.getStartDateTime().compareTo(o1.getStartDateTime());

    @Override
    public void populate(SOURCE source, TARGET target) throws ConversionException {

	if (CollectionUtils.isNotEmpty(source.getMemoActivities())) {

	    final List<TicketEventData> memoActivities = source.getMemoActivities().stream().filter(
		    memo -> Customerticketingc4cintegrationConstants.MEMO_ACTIVITY_TYPE_CODE.equals(memo.getTypeCode()))
		    .map(memo -> {

			final TicketEventData ticketEventData = new TicketEventData();

			String date = memo.getCreationDateTime() != null ? memo.getCreationDateTime()
				: memo.getEntityLastChangedOn();

			ticketEventData.setStartDateTime(parseDate(date));

			String text = StringUtils.isNotBlank(memo.getText()) ? memo.getText() : memo.getSubjectName();
			ticketEventData.setText(text);

			String createdBy = null;

			if (Customerticketingc4cintegrationConstants.INITIATOR_CODE_CUSTOMER
				.equals(memo.getInitiatorCode())) {
			    createdBy = getCustomerName();
			    ticketEventData.setAddedByAgent(Boolean.FALSE);
			    ticketEventData.setAuthor(createdBy);
			} else {
			    createdBy = Localization.getLocalizedString(
				    Customerticketingc4cintegrationConstants.SUPPORT_TICKET_AGENT_NAME);
			    ticketEventData.setAddedByAgent(Boolean.TRUE);
			}

			final StringBuilder textBuilder = new StringBuilder(createdBy);
			textBuilder.append(" ")
				.append(Localization.getLocalizedString("text.account.supporttickets.updateTicket.on"))
				.append(" ").append(date).append("\n").append(text);

			ticketEventData.setDisplayText(textBuilder.toString());

			return ticketEventData;

		    }).collect(Collectors.toList());
	    
	    if (CollectionUtils.isNotEmpty(target.getTicketEvents())) {
		memoActivities.addAll(target.getTicketEvents());
	    }
	    
	    Collections.sort(memoActivities, TICKET_EVENT_DATE_COMPARATOR);

	    target.setTicketEvents(memoActivities);

	}

    }

    /**
     * Returns current customer name
     * 
     * @return customer name
     */
    protected String getCustomerName() {

	CustomerData customer = customerFacade.getCurrentCustomer();
	StringBuilder name = new StringBuilder();
	name.append(customer.getFirstName()).append(' ').append(customer.getLastName());

	return name.toString().trim();
    }

    protected Date parseDate(final String date) {
	if (StringUtils.isEmpty(date)) {
	    return null;
	}
	final Pattern p = Pattern.compile("[0-9]+");
	final Matcher m = p.matcher(date);
	long longDate = 0;
	while (m.find()) {
	    final String dateString = m.group();
	    longDate = StringUtils.isNotEmpty(dateString) ? Long.parseLong(dateString) : 0;
	}
	return (longDate != 0) ? new Date(longDate) : null;
    }

    /**
     * @return the customerFacade
     */
    public CustomerFacade getCustomerFacade() {
	return customerFacade;
    }

    /**
     * @param customerFacade the customerFacade to set
     */
    public void setCustomerFacade(CustomerFacade customerFacade) {
	this.customerFacade = customerFacade;
    }

    /**
     * @return the configurationService
     */
    public ConfigurationService getConfigurationService() {
	return configurationService;
    }

    /**
     * @param configurationService the configurationService to set
     */
    public void setConfigurationService(ConfigurationService configurationService) {
	this.configurationService = configurationService;
    }

}
