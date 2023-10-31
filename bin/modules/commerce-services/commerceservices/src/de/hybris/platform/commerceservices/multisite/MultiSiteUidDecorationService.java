/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.commerceservices.multisite;

import java.io.Serializable;


/**
 * Provide methods to convert customerUid to match UID namespace syntax
 */
public interface MultiSiteUidDecorationService extends Serializable
{
	/**
	 * Checks whether the input customer UID matches the Customer.uid Namespace Syntax
	 * <p>
	 * &lt Original Customer.uid &gt | &lt BaseSite.uid &gt
	 * </p>
	 * <!-- <Original Customer.uid>|<BaseSite.uid> -->
	 *
	 * @param customerUid to check whether this input argument matches the Namespace Syntax
	 * @param siteUid     check the Customer.uid based on this BaseSite.uid
	 * @return return true if customerUid matches the Namespace Syntax, otherwise return false
	 */
	boolean isDecorated(final String customerUid, final String siteUid);

	/**
	 * Decorates original UID and base site UID based on Namespace Syntax:
	 * <p>
	 * &lt Original Customer.uid &gt | &lt BaseSite.uid &gt
	 * <p>
	 * <!-- <Original Customer.uid>|<BaseSite.uid> -->
	 * @param plainCustomerUid original UID
	 * @param siteUid     base site UID
	 * @return combine original UID and base site UID directly
	 */
	String decorate(final String plainCustomerUid, final String siteUid);

	/**
	 * un-decorate the input customer Uid based on the Namespace Syntax:
	 * <p>
	 * &lt Original Customer.uid &gt | &lt BaseSite.uid &gt
	 * <p>
	 * <!-- <Original Customer.uid>|<BaseSite.uid> -->
	 *
	 * @param customerUid Customer.uid no matter whether it match the Namespace Syntax
	 * @param siteUid BaseSite.uid
	 * @return Return 1 element if provide customerUid is not decorated, and return 2 elements
	 * if provided customerUid is decorated. First element as plainCustomerUid and second element as siteUid.
	 */
	String[] undecorate(final String customerUid, final String siteUid);
}
