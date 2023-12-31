/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.customerticketingc4cb2bintegration.converters.populators;

import de.hybris.platform.b2bcommercefacades.company.B2BUserFacade;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.customerticketingc4cintegration.SitePropsHolder;
import de.hybris.platform.customerticketingc4cintegration.data.ServiceRequestData;
import de.hybris.platform.customerticketingfacades.data.TicketData;

import javax.annotation.Resource;


/**
 *
 * Populator for B2B external customer id
 *
 */
public class Y2C4CB2BTicketPopulator<SOURCE extends TicketData, TARGET extends ServiceRequestData> implements
		Populator<SOURCE, TARGET>
{
	private static final int BUSINESS_UNIT_ID_MAX_LEN = 10;

	@Resource(name = "b2bUserFacade")
	protected B2BUserFacade b2bUserFacade;

	@Resource(name = "customerFacade")
	private CustomerFacade customerFacade;

	@Resource
	private SitePropsHolder sitePropsHolder;

	@Override
	public void populate(final TicketData source, final ServiceRequestData target)
	{
		if (!sitePropsHolder.isB2C() && (source.getStatus() == null || source.getStatus().getId() == null)) // creating
		{
			final B2BUnitData businessUnit = b2bUserFacade.getParentUnitForCustomer(customerFacade.getCurrentCustomerUid());
			target.setBuyerMainContactPartyID(source.getCustomerId());

			//if the length of the BU id is 10 or less then just use it as the business unit it
			//else just get the first 10 digits
			String businessUnitId = businessUnit.getUid();
			if (businessUnitId.length() <= BUSINESS_UNIT_ID_MAX_LEN)
			{
				target.setBuyerPartyID(businessUnitId);
			}
			else
			{
				businessUnitId = businessUnitId.substring(0, BUSINESS_UNIT_ID_MAX_LEN);
				target.setBuyerPartyID(businessUnitId);
			}
		}
	}
}
