/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.rules.service;

import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;



/**
 * Translates Characteristic Values from format used in the service layer into the format that the rule service requires
 * to match any rule reliable. For example a unified format has to be used for numeric values to avoid a rule not
 * matching to different formats.
 */
public interface ProductConfigRuleFormatTranslator
{
	/**
	 * format the cstic value so that the rule engine can compare the value to it's rules maintained in backoffice
	 *
	 * @param cstic
	 *           the characteristic the value belongs to and which provides the context for translation
	 * @param value
	 *           the actual value to translate
	 * @return translated value
	 */
	String formatForRules(final CsticModel cstic, String value);

	/**
	 * format the rules value so that the configuration engine can apply the value to the runtime configuration
	 *
	 * @param cstic
	 *           the characteristic the value belongs to and which provides the context for translation
	 * @param value
	 *           the actual value to translate
	 * @return translated value
	 */
	String formatForService(final CsticModel cstic, String value);


	/**
	 * checks whether a rules value can be formatted so that the configuration engine can apply the value to the runtime
	 * configuration
	 *
	 * @param cstic
	 *           the characteristic the value belongs to and which provides the context for translation
	 * @param value
	 *           the actual value to translate
	 * @return <code>true</code> only if the given value can be formatted
	 */
	boolean canBeFormattedForService(final CsticModel cstic, String value);


	/**
	 *
	 * @return the string indicating that no value is assigned to a ctsic at all
	 */
	String getNoValueIndicator();
}
