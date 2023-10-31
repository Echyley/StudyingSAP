/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.facades.analytics;

import java.util.List;


/**
 * Facade for integration of analytical capbalilities with product configuration. A typical use case is show the percent
 * of customers that selected a certain option with the configuartion.
 */
public interface ConfigurationAnalyticsFacade
{

	/**
	 * Reads the analytical data for the given list of cstics.
	 *
	 * @param csticUiKeys
	 *           list of cstic ui keys for which analytical data should be obtained
	 * @param configId
	 *           the config id for which analytical data should be obtained
	 * @return analytical data
	 */
	List<AnalyticCsticData> getAnalyticData(List<String> csticUiKeys, String configId);
}
