/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapcommonbol.constants;


/**
 * Global class for all Sapcommonbol constants. You can add global constants for your extension into this class.
 */
public final class SapcommonbolConstants
{
	/**
	 * The name of the extension
	 */
	@SuppressWarnings("squid:S2387")
	public static final String EXTENSIONNAME = "sapcommonbol";

	// implement here constants used by this extension

	/**
	 * Name of address bean
	 */
	public static final String ALIAS_BO_ADDRESS = "sapCommonAddress";
	/**
	 * Name of converter bean
	 */
	public static final String ALIAS_BO_CONVERTER = "sapCommonConverter";
	/**
	 * Name of transfer item bean
	 */
	public static final String ALIAS_BEAN_TRANSFER_ITEM = "sapCommonTransferItem";
	/**
	 * Mapping key for diviision
	 */
	public static final String BEAN_ID_DIVISION_MAPPING_KEY = "sapDivisionMappingKey";
	/**
	 * Mapping key for distribution channel
	 */
	public static final String BEAN_ID_DIST_CHANNEL_MAPPING_KEY = "sapDistChannelMappingKey";
	/**
	 * Bean name for distribution channel mapping
	 */
	public static final String BEAN_ID_DIST_CHANNEL_MAPPING = "sapDistChannelMapping";
	/**
	 * Bean name fore division mapping
	 */
	public static final String BEAN_ID_DIVISION_MAPPING = "sapDivisionMapping";
	/**
	 * Bean name for currency cache
	 */
	public static final String BEAN_ID_CACHE_CURRENCIES = "sapCommonCurrencyCacheRegion";
	/**
	 * Bean name for unitss cache
	 */
	public static final String BEAN_ID_CACHE_UNITS = "sapCommonUnitCacheRegion";

	private SapcommonbolConstants()
	{
		//empty to avoid instantiating this constant class
	}

}
