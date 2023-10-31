/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.customerticketingc4cintegration.converters.populators;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.customerticketingc4cintegration.SitePropsHolder;
import de.hybris.platform.customerticketingc4cintegration.constants.Customerticketingc4cintegrationConstants;
import de.hybris.platform.customerticketingc4cintegration.data.ActivityParty;
import de.hybris.platform.customerticketingc4cintegration.data.ActivityText;
import de.hybris.platform.customerticketingc4cintegration.data.MemoActivity;
import de.hybris.platform.customerticketingfacades.data.TicketData;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Collections;


/**
 *
 * Populator implementation for populating MemoActivity from TicketData
 *
 * @param <S> source as TicketData
 * @param <T> target as MemoActivity
 *           
 */
public class Y2C4CMemoActivityPopulator<S extends TicketData, T extends MemoActivity> implements Populator<S, T>
{

	private SitePropsHolder sitePropsHolder;

	@Override
	public void populate(final S source, final T target) throws ConversionException
	{

	    target.setInitiatorCode(Customerticketingc4cintegrationConstants.INITIATOR_CODE_CUSTOMER);
		target.setSubjectName(source.getMessage());

		final ActivityParty party = new ActivityParty();
		party.setPartyID(source.getCustomerId());

		if (sitePropsHolder.isB2C())
		{
			party.setPartyTypeCode(Customerticketingc4cintegrationConstants.INDUVIDUAL_CUSTOMER_PARTY_TYPE_CODE);
			party.setRoleCode(Customerticketingc4cintegrationConstants.INDUVIDUAL_CUSTOMER_ROLE_CODE);
		}
		else
		{
			party.setPartyTypeCode(Customerticketingc4cintegrationConstants.CONTACT_PERSON_PARTY_TYPE_CODE);
			party.setRoleCode(Customerticketingc4cintegrationConstants.CONTACT_PERSON_ROLE_CODE);
		}

		target.setActivityParties(Collections.singletonList(party));

		final ActivityText text = new ActivityText();
		text.setText(source.getMessage());
		text.setTypeCode(Customerticketingc4cintegrationConstants.TEXT_TYPE_CODE);

		target.setActivityTexts(Collections.singletonList(text));

	}

	public SitePropsHolder getSitePropsHolder()
	{
		return sitePropsHolder;
	}

	public void setSitePropsHolder(final SitePropsHolder sitePropsHolder)
	{
		this.sitePropsHolder = sitePropsHolder;
	}

}
