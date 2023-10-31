/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.cps;

import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;

import java.util.Date;


/**
 * Wraps the service calls for KB determination. Implementation details (usage of charon) should be hidden from the
 * configuration provider
 */
public interface CharonKbDeterminationFacade
{

	/**
	 * Check for knowledge bases on a given date
	 *
	 * @param productcode
	 *           Product code
	 * @param kbdate
	 *           Validity date
	 * @return Does at least one knowledge base exist?
	 */
	boolean hasKbForDate(String productcode, Date kbdate);

	/**
	 * Reads KB ID for a given product and date. It's not required that the KB for the product/date combination is
	 * unique, if multiple knowledge bases exist, the ID for the first found KB is returned
	 *
	 * @param productcode
	 *           product code
	 * @param kbdate
	 *           Validity date of KB
	 * @return KB ID for first matching KB
	 */
	Integer readKbIdForDate(String productcode, Date kbdate);

	/**
	 * Reads the current KB ID for a given product.
	 *
	 * @param productcode
	 *           product code
	 * @return KB ID for matching KB
	 */
	default Integer getCurrentKbIdForProduct(final String productcode)
	{
		return readKbIdForDate(productcode, new Date());
	}

	/**
	 * parses a KBKey from the external configuration
	 *
	 * @param productCode
	 *           productCode
	 * @param externalcfg
	 *           extConfig
	 * @return KBKey with all fields filled
	 */
	KBKey parseKBKeyFromExtConfig(String productCode, String externalcfg);

	/**
	 * checks the given key for existence
	 *
	 * @param kbKey
	 *           key to check
	 * @return <code>true<code>, only if the given KBKey is known by the configuration engine
	 */
	boolean hasKBForKey(KBKey kbKey);

	/**
	 * checks the given key existence <b>and validity</b>
	 *
	 * @param kbKey
	 *           key to check
	 * @return <code>true<code>, only if the given KBKey is known by the configuration engine <b> and valid as of
	 *         today</b>
	 */
	boolean hasValidKBForKey(KBKey kbKey);

}
