/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.facades;

import de.hybris.platform.sap.productconfig.facades.overview.ConfigurationOverviewData;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessage;


/**
 * Mapper class for mapping {@link ProductConfigMessage} to {@link ProductConfigMessageData}
 */
public interface ConfigurationMessageMapper
{
	/**
	 * maps messages from cstic model to cstic data to display on UI
	 *
	 * @param cstciData
	 * @param csticModel
	 */
	void mapMessagesFromModelToData(CsticData cstciData, CsticModel csticModel);

	/**
	 * maps messages from cstic value model to cstic value data to display on UI
	 *
	 * @param cstciValueData
	 * @param csticValueModel
	 */
	void mapMessagesFromModelToData(CsticValueData cstciValueData, CsticValueModel csticValueModel);

	/**
	 * maps product configuration messages from model to data ( on product level)
	 *
	 * @param configData
	 * @param configModel
	 */
	void mapMessagesFromModelToData(ConfigurationData configData, ConfigModel configModel);

	/**
	 * maps product configuration messages from model to overview data ( on product level)
	 *
	 * @param configOverviewData
	 * @param configModel
	 */
	void mapMessagesFromModelToData(ConfigurationOverviewData configOverviewData, ConfigModel configModel);
}
