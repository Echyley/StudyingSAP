/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapmodel.services;

import de.hybris.platform.sap.sapmodel.model.SAPLogicalSystemModel;
import de.hybris.platform.sap.sapmodel.model.SAPPlantLogSysOrgModel;
import de.hybris.platform.sap.sapmodel.model.SAPSalesOrganizationModel;
import de.hybris.platform.store.BaseStoreModel;

/**
 * Interface to provide access to Sap Logical system information for a given base store and plant combination
 */
public interface SapPlantLogSysOrgService
{

	/**
	 * Get SAPLogicalSystemModel for given BaseStoreModel and plant code
	 * @param baseStoreModel a BaseStoreModel
	 * @param plantCode the plant's code
	 * @return SAP logical system for given base store and plant
	 */
	public SAPLogicalSystemModel getSapLogicalSystemForPlant(BaseStoreModel baseStoreModel, String plantCode);

	/**
	 * Get SAPSalesOrganizationModel for a given BaseStoreModel and plant code
	 * @param baseStoreModel a BaseStoreModel
	 * @param plantCode the plant's code
	 * @return SAP sales organization for given base store and plant
	 */
	public SAPSalesOrganizationModel getSapSalesOrganizationForPlant(BaseStoreModel baseStoreModel, String plantCode);

	/**
	 * Get SAPPlantLogSysOrgModel for a given BaseStoreModel and plant code
	 * @param baseStoreModel a BaseStoreModel
	 * @param plantCode the plant's code
	 * @return SAP logical system and sales organization for given base store and plant
	 */
	public SAPPlantLogSysOrgModel getSapPlantLogSysOrgForPlant(BaseStoreModel baseStoreModel, String plantCode);

}
