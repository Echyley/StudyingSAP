/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapproductavailability.constants;

/**
 * Global class for all Sapproductavailability constants. You can add global constants for your extension into this
 * class.
 */
public final class SapproductavailabilityConstants
{
	public static final String EXTENSIONNAME = "sapproductavailability";

	public static final String SAP_PRODUCT_AVAILABILITY_BO = "sapProductAvailabilityBO";

	public static final String BAPI_MATERIAL_AVAILABILITY = "BAPI_MATERIAL_AVAILABILITY";

	public static final String SALES_ORG = "sapcommon_salesOrganization";
	public static final String DIS_CHANNEL = "sapcommon_distributionChannel";
	public static final String DIVISION = "sapcommon_division";

	public static final String ATPACTIVE = "sapproductavailability_atpActive";

	// plant customer + material
	public static final String BEAN_ID_CACHE_PLANT = "sapAtpCheckPlantCacheRegion";
	public static final Object CACHEKEY_SAP_ATP = "SAP_ATP";

	// plant meterial
	public static final String BEAN_ID_CACHE_PLANT_MATERIAL = "sapAtpCheckPlantMaterialCacheRegion";

	// plant customer
	public static final String BEAN_ID_CACHE_PLANT_CUSTOMER = "sapAtpCheckPlantCustomerCacheRegion";

	// availability
	public static final String BEAN_ID_CACHE_AVAILABILITY = "sapAtpCheckAvailabilityCacheRegion";







	private SapproductavailabilityConstants()
	{
		//empty to avoid instantiating this constant class
	}

	// implement here constants used by this extension
	
	public static class Attributes
	{
	        private Attributes(){
	                //private constructor to hide public constructor
	        }
		public static class SAPConfiguration
		{
		        private SAPConfiguration(){
                            //private constructor to hide public constructor
                        }
			public static final String SAPPRODUCTAVAILABILITY_ATPACTIVE = ATPACTIVE.intern();
		}
	}
}
