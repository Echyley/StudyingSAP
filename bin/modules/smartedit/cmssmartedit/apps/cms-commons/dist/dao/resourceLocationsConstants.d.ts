/**
 * @ngdoc object
 * @name resourceLocationsModule.object:CONTEXT_CATALOG
 *
 * @description
 * Constant containing the name of the catalog uid placeholder in URLs
 */
export declare const CONTEXT_CATALOG = "CURRENT_CONTEXT_CATALOG";
/**
 * @ngdoc object
 * @name resourceLocationsModule.object:CONTEXT_CATALOG_VERSION
 *
 * @description
 * Constant containing the name of the catalog version placeholder in URLs
 */
export declare const CONTEXT_CATALOG_VERSION = "CURRENT_CONTEXT_CATALOG_VERSION";
/**
 * @ngdoc object
 * @name resourceLocationsModule.object:CONTEXT_SITE_ID
 *
 * @description
 * Constant containing the name of the site uid placeholder in URLs
 */
export declare const CONTEXT_SITE_ID = "CURRENT_CONTEXT_SITE_ID";
/**
 * @ngdoc object
 * @name resourceLocationsModule.object:PAGE_CONTEXT_CATALOG
 *
 * @description
 * Constant containing the name of the current page catalog uid placeholder in URLs
 */
export declare const PAGE_CONTEXT_CATALOG = "CURRENT_PAGE_CONTEXT_CATALOG";
/**
 * @ngdoc object
 * @name resourceLocationsModule.object:PAGE_CONTEXT_CATALOG_VERSION
 *
 * @description
 * Constant containing the name of the current page catalog version placeholder in URLs
 */
export declare const PAGE_CONTEXT_CATALOG_VERSION = "CURRENT_PAGE_CONTEXT_CATALOG_VERSION";
/**
 * @ngdoc object
 * @name resourceLocationsModule.object:TYPES_RESOURCE_URI
 *
 * @description
 * Resource URI of the component types REST service.
 */
export declare const TYPES_RESOURCE_URI = "/cmswebservices/v1/types";
/**
 * @ngdoc object
 * @name resourceLocationsModule.object:ITEMS_RESOURCE_URI
 *
 * @description
 * Resource URI of the custom components REST service.
 */
export declare const ITEMS_RESOURCE_URI: string;
/**
 * @ngdoc object
 * @name resourceLocationsModule.object:ITEMS_RESOURCE_URI
 *
 * @description
 * Resource URI of the custom components REST service.
 */
export declare const PAGES_CONTENT_SLOT_COMPONENT_RESOURCE_URI: string;
/**
 * @ngdoc object
 * @name resourceLocationsModule.object:CONTENT_SLOT_TYPE_RESTRICTION_RESOURCE_URI
 *
 * @description
 * Resource URI of the content slot type restrictions REST service.
 */
export declare const CONTENT_SLOT_TYPE_RESTRICTION_RESOURCE_URI: string;
/**
 * @ngdoc object
 * @name resourceLocationsModule.object:CONTENT_SLOT_TYPE_RESTRICTION_RESOURCE_URI
 *
 * @description
 * Resource URI of the content slot type restrictions REST service given the page uid.
 */
export declare const CONTENT_SLOTS_TYPE_RESTRICTION_RESOURCE_URI: string;
/**
 * @ngdoc object
 * @name resourceLocationsMod`ule.object:PAGES_LIST_RESOURCE_URI
 *
 * @description
 * Resource URI of the pages REST service.
 */
export declare const PAGES_LIST_RESOURCE_URI: string;
/**
 * @ngdoc object
 * @name resourceLocationsModule.object:PAGE_LIST_PATH
 *
 * @description
 * Path of the page list
 */
export declare const PAGE_LIST_PATH = "pages/:siteId/:catalogId/:catalogVersion";
/**
 * @ngdoc object
 * @name resourceLocationsModule.object:TRASHED_PAGE_LIST_PATH
 *
 * @description
 * Path of the page list
 */
export declare const TRASHED_PAGE_LIST_PATH = "trashedpages/:siteId/:catalogId/:catalogVersion";
/**
 * @ngdoc object
 * @name resourceLocationsModule.object:PAGES_CONTENT_SLOT_RESOURCE_URI
 *
 * @description
 * Resource URI of the page content slots REST service
 */
export declare const PAGES_CONTENT_SLOT_RESOURCE_URI: string;
/**
 * @ngdoc object
 * @name resourceLocationsModule.object:PAGE_TEMPLATES_URI
 *
 * @description
 * Resource URI of the page templates REST service
 */
export declare const PAGE_TEMPLATES_URI: string;
/**
 * @ngdoc object
 * @name resourceLocationsModule.object:NAVIGATION_MANAGEMENT_PAGE_PATH
 *
 * @description
 * Path to the Navigation Management
 */
export declare const NAVIGATION_MANAGEMENT_PAGE_PATH = "navigations/:siteId/:catalogId/:catalogVersion";
/**
 * @ngdoc object
 * @name resourceLocationsModule.object:NAVIGATION_MANAGEMENT_RESOURCE_URI
 *
 * @description
 * Resource URI of the navigations REST service.
 */
export declare const NAVIGATION_MANAGEMENT_RESOURCE_URI: string;
/**
 * @ngdoc object
 * @name resourceLocationsModule.object:NAVIGATION_MANAGEMENT_ENTRIES_RESOURCE_URI
 *
 * @description
 * Resource URI of the navigations REST service.
 */
export declare const NAVIGATION_MANAGEMENT_ENTRIES_RESOURCE_URI: string;
/**
 * @ngdoc object
 * @name resourceLocationsModule.object:NAVIGATION_MANAGEMENT_ENTRY_TYPES_RESOURCE_URI
 *
 * @description
 * Resource URI of the navigation entry types REST service.
 */
export declare const NAVIGATION_MANAGEMENT_ENTRY_TYPES_RESOURCE_URI = "/cmswebservices/v1/navigationentrytypes";
/**
 * @ngdoc object
 * @name resourceLocationsModule.CONTEXTUAL_PAGES_RESTRICTIONS_RESOURCE_URI
 *
 * @description
 * Resource URI of the pages restrictions REST service, with placeholders to be replaced by the currently selected catalog version.
 */
export declare const CONTEXTUAL_PAGES_RESTRICTIONS_RESOURCE_URI: string;
/**
 * @ngdoc object
 * @name resourceLocationsModule.PAGES_RESTRICTIONS_RESOURCE_URI
 *
 * @description
 * Resource URI of the pages restrictions REST service, with placeholders to be replaced by the currently selected catalog version.
 */
export declare const PAGES_RESTRICTIONS_RESOURCE_URI = "/cmswebservices/v1/sites/:siteUID/catalogs/:catalogId/versions/:catalogVersion/pagesrestrictions";
/**
 * @ngdoc object
 * @name resourceLocationsModule.RESTRICTION_TYPES_URI
 *
 * @description
 * Resource URI of the restriction types REST service.
 */
export declare const RESTRICTION_TYPES_URI = "/cmswebservices/v1/restrictiontypes";
/**
 * @ngdoc object
 * @name resourceLocationsModule.RESTRICTION_TYPES_URI
 *
 * @description
 * Resource URI of the pageTypes-restrictionTypes relationship REST service.
 */
export declare const PAGE_TYPES_RESTRICTION_TYPES_URI = "/cmswebservices/v1/pagetypesrestrictiontypes";
/**
 * @ngdoc object
 * @name resourceLocationsModule.PAGE_TYPES_URI
 *
 * @description
 * Resource URI of the page types REST service.
 */
export declare const PAGE_TYPES_URI = "/cmswebservices/v1/pagetypes";
/**
 * @ngdoc object
 * @name resourceLocationsModule.object:GET_PAGE_SYNCHRONIZATION_RESOURCE_URI
 *
 * @description
 * Resource URI to retrieve the full synchronization status of page related items
 */
export declare const GET_PAGE_SYNCHRONIZATION_RESOURCE_URI: string;
/**
 * @ngdoc object
 * @name resourceLocationsModule.object:POST_PAGE_SYNCHRONIZATION_RESOURCE_URI
 *
 * @description
 * Resource URI to perform synchronization of page related items
 */
export declare const POST_PAGE_SYNCHRONIZATION_RESOURCE_URI: string;
