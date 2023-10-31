/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saporderexchangeoms.datahub.inbound.events;


import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.JaloInvalidParameterException;
import de.hybris.platform.jalo.security.JaloSecurityException;
import de.hybris.platform.sap.orderexchange.constants.DataHubInboundConstants;
import de.hybris.platform.sap.orderexchange.inbound.events.DataHubTranslator;
import de.hybris.platform.sap.saporderexchangeoms.datahub.inbound.SapDataHubInboundHelper;

import org.apache.log4j.Logger;



/**
 * This class includes the translator for delivery that updates consignments statuses.
 */
public class SapOmsDataHubDeliveryTranslator extends DataHubTranslator<SapDataHubInboundHelper>
{
	private static final Logger LOG = Logger.getLogger(SapOmsDataHubDeliveryTranslator.class);

	
	public static final String HELPER_BEAN = "sapDataHubInboundHelper";

	
	public SapOmsDataHubDeliveryTranslator()
	{
		super(HELPER_BEAN);
	}

	@Override
	public void performImport(final String delivInfo, final Item processedItem) throws ImpExException
	{
		String orderCode = null;

		try
		{
			orderCode = processedItem.getAttribute(DataHubInboundConstants.CODE).toString();
		}
		catch (final JaloSecurityException | JaloInvalidParameterException e)
		{
			throw new ImpExException(e);
		}

		if (delivInfo != null && !delivInfo.equals(DataHubInboundConstants.IGNORE))
		{
			final String entryNumber = getInboundHelper().determineEntryNumber(delivInfo);
			final String quantity = getInboundHelper().determineQuantity(delivInfo);

			if (Long.parseLong(quantity) > 0)
			{
				getInboundHelper().processDeliveryNotification(orderCode, entryNumber);
			}
			else
			{
				LOG.error(String.format(
						"Delivery notification for order %s and entry number %s failed because of the missing quantity!", orderCode,
						entryNumber));
			}

		}
		else
		{
			LOG.error(String.format("Delivery notification for order %s failed because of the missing delivery information!",
					orderCode));
		}
	}
}
