/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.customerticketingc4cintegration.converters.populators;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.customerticketingc4cintegration.SitePropsHolder;
import de.hybris.platform.customerticketingc4cintegration.constants.Customerticketingc4cintegrationConstants;
import de.hybris.platform.customerticketingc4cintegration.data.RelatedTransaction;
import de.hybris.platform.customerticketingc4cintegration.data.ServiceRequestData;
import de.hybris.platform.customerticketingfacades.data.StatusData;
import de.hybris.platform.customerticketingfacades.data.TicketData;

import java.util.Collections;
import java.util.Map;

import org.apache.commons.lang.StringUtils;


/**
 * TicketData -> ServiceRequestData populator
 *
 * @param <S> source
 * @param <T> target
 */
public class Y2C4CTicketPopulator<S extends TicketData, T extends ServiceRequestData>
		implements Populator<S, T>
{
	private SitePropsHolder sitePropsHolder;
	private Map<String, StatusData> statusMapping;

	@Override
	public void populate(final S source, final T target)
	{
		if (source.getStatus() == null || source.getStatus().getId() == null) // creating
		{
			target.setName(source.getSubject());
			target.setDataOriginTypeCode(Customerticketingc4cintegrationConstants.DATA_ORIGIN_TYPECODE);

			if (getSitePropsHolder().isB2C())
			{
				target.setBuyerPartyID(source.getCustomerId());
			}

			if (StringUtils.isNotEmpty(source.getCartId()))
			{
				final RelatedTransaction rt = new RelatedTransaction();
				rt.setID(source.getCartId());
				rt.setBusinessSystemID(getSitePropsHolder().getSiteId());
				rt.setTypeCode(Customerticketingc4cintegrationConstants.RELATED_TRANSACTION_TYPECODE);
				rt.setRoleCode(Customerticketingc4cintegrationConstants.ROLE_CODE);
				target.setRelatedTransactions(Collections.singletonList(rt));
			}

		}
		else
		// updating
		{
			target.setObjectID(source.getId());
			for (final String id : getStatusMapping().keySet())
			{
				if (getStatusMapping().get(id).getId().equalsIgnoreCase(source.getStatus().getId()))
				{
					target.setStatusCode(id);
				}
			}
		}
	}

	protected SitePropsHolder getSitePropsHolder()
	{
		return sitePropsHolder;
	}

	public void setSitePropsHolder(final SitePropsHolder sitePropsHolder)
	{
		this.sitePropsHolder = sitePropsHolder;
	}

	protected Map<String, StatusData> getStatusMapping()
	{
		return statusMapping;
	}

	public void setStatusMapping(final Map<String, StatusData> statusMapping)
	{
		this.statusMapping = statusMapping;
	}
}
