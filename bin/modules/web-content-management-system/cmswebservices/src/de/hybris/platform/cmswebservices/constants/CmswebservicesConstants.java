/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmswebservices.constants;

/**
 * Global class for all Cmswebservices constants. You can add global constants for your extension into this class.
 */
public final class CmswebservicesConstants extends GeneratedCmswebservicesConstants
{
	public static final String EXTENSIONNAME = "cmswebservices";
	public static final String API_VERSION = "/v1";
	public static final String URI_CATALOG_ID = "catalogId";
	public static final String URI_CATALOG_VERSION = "catalogVersion";
	public static final String URI_VERSION_ID = "versionId";
	public static final String URI_UUID = "uuid";
	public static final String URI_ITEM_UUID = "itemUUID";
	public static final String URI_SITE_ID = "siteId";
	public static final String URI_SLOT_ID = "slotId";
	public static final String URI_PAGE_ID = "pageId";
	public static final String URI_PAGE_IDS = "pageIds";
	public static final String URI_PAGE_SIZE = "pageSize";
	public static final String URI_CURRENT_PAGE = "currentPage";
	public static final String QUERY_PARAM_MODE = "mode";
	public static final String MODE_CLONEABLE_TO = "cloneableTo";
	public static final String URI_TYPECODE = "typeCode";
	public static final String URI_SORT = "sort";

	public static final String HEADER_LOCATION = "Location";

	/**
	 * To solve a serialization problem, CMSItemController get Collections returns a map instead of a WsDto So these
	 * define the map keys, which would normally be Dto properties
	 */
	public static final String WSDTO_RESPONSE_PARAM_RESULTS = "response";
	public static final String WSDTO_RESPONSE_PARAM_PAGINATION = "pagination";

	private CmswebservicesConstants()
	{
		//empty to avoid instantiating this constant class
	}

}
