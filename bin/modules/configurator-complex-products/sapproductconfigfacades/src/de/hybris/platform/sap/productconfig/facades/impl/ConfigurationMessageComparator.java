/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.facades.impl;

import de.hybris.platform.sap.productconfig.facades.ProductConfigMessageData;
import de.hybris.platform.sap.productconfig.facades.ProductConfigMessageUISeverity;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessagePromoType;

import java.util.Comparator;


/**
 * Sorts messages for display on Ui. The order is:<br>
 * <ol>
 * <li>Message with ProductConfigMessageSeverity.WARNING (i.e. UI-Severity "Info" marked with question mark)
 * <li>Message with ProductConfigMessageSeverity.INFO/ERROR (i.e. UI-Severity "Config"/"Error" marked with i-Sign)
 * <li>Promo-Messages
 * <li>Opportunity-Messages
 * </ol>
 *
 */
public class ConfigurationMessageComparator implements Comparator<ProductConfigMessageData>
{

	/**
	 * Indicates that no matches were found
	 */
	public static final int NO_MATCHING_CONDITION = -2;

	@Override
	public int compare(final ProductConfigMessageData m1, final ProductConfigMessageData m2)
	{
		// Handle standard messages considering severity
		int returnValue = compareStandardMessagesConsideringSeverity(m1, m2);
		if (returnValue != NO_MATCHING_CONDITION)
		{
			return returnValue;
		}

		// Handle discount messages considering promo-type
		returnValue = compareDiscountMessagesConsideringPromoType(m1, m2);
		if (returnValue != NO_MATCHING_CONDITION)
		{
			return returnValue;
		}

		// Handle standard message vs. discount message
		if (m1.getPromoType() == null && m2.getPromoType() != null)
		{
			return -1;
		}
		else
		{
			return 1;
		}
	}

	protected int compareDiscountMessagesConsideringPromoType(final ProductConfigMessageData m1, final ProductConfigMessageData m2)
	{
		if (m1.getPromoType() != null && m2.getPromoType() != null)
		{
			if (m1.getPromoType() == m2.getPromoType())
			{
				return 0;
			}
			// PROMO_APPLIED has higher prio
			if (m1.getPromoType() == ProductConfigMessagePromoType.PROMO_APPLIED)
			{
				return -1;
			}
			else
			{
				return 1;
			}
		}
		return NO_MATCHING_CONDITION;
	}

	protected int compareStandardMessagesConsideringSeverity(final ProductConfigMessageData m1, final ProductConfigMessageData m2)
	{
		if (m1.getPromoType() == null && m2.getPromoType() == null)
		{
			if (m1.getSeverity() == m2.getSeverity())
			{
				return 0;
			}
			// INFO severity has highest prio
			if (m1.getSeverity() == ProductConfigMessageUISeverity.INFO)
			{
				return -1;
			}
			else
			{
				return 1;
			}
		}
		return NO_MATCHING_CONDITION;
	}


}
