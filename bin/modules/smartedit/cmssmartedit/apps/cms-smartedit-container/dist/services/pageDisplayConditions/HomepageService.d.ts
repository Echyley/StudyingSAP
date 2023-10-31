import { EventMessageData, ICatalogService, IUriContext, SystemEventService, TypedMap, ICMSPage } from 'smarteditcommons';
/**
 * Expose through angular the event for sendEventShowReplaceParentHomePageInfo()
 */
export declare const CMS_EVENT_SHOW_REPLACE_PARENT_HOMEPAGE_INFO = "CMS_EVENT_SHOW_REPLACE_PARENT_HOMEPAGE_INFO";
/** @internal */
/**
 * Expose through angular the event for sendEventHideReplaceParentHomePageInfo()
 */
export declare const CMS_EVENT_HIDE_REPLACE_PARENT_HOMEPAGE_INFO = "CMS_EVENT_HIDE_REPLACE_PARENT_HOMEPAGE_INFO";
/** @internal */
/**
 * !NGDOC
 * Status of a ICatalogHomepageDetails
 */
export declare enum CatalogHomepageDetailsStatus {
    PENDING = "PENDING",
    NO_HOMEPAGE = "NO_HOMEPAGE",
    LOCAL = "LOCAL",
    OLD = "OLD",
    PARENT = "PARENT"
}
/**
 * ICatalogHomepageDetails is a mashup of a bunch of different values needed in the UI
 * Since the logic is a big insane, to try and keep the components clean we dump all the crap into
 * this 1 object.
 * Depending on the status, some of the other fields will be filled, but not others
 */
export interface ICatalogHomepageDetails {
    status: CatalogHomepageDetailsStatus;
    parentCatalogName?: TypedMap<string>;
    parentCatalogVersion?: string;
    targetCatalogName?: TypedMap<string>;
    targetCatalogVersion?: string;
    currentHomepageName?: string;
    currentHomepageUid?: string;
    oldHomepageUid?: string;
}
/**
 * An enum type representing the homepage type of a cms page.
 */
export declare enum HomepageType {
    /**
     * An enum value of type HomepageType describing if a cms page was previously a homepage.
     */
    OLD = "old",
    /**
     * An enum value of type HomepageType describing if a cms page is a current homepage.
     */
    CURRENT = "current",
    /**
     * An enum value of type HomepageType describing if a cms page is a fallback homepage.
     */
    FALLBACK = "fallback"
}
/**
 * This service is used to determine if a cms page is a current, a previous homepage, or neither.
 */
export declare class HomepageService {
    private catalogService;
    private systemEventService;
    constructor(catalogService: ICatalogService, systemEventService: SystemEventService);
    /**
     * Send an event to show info to the user about replacing a homepage from a parent catalog.
     */
    sendEventHideReplaceParentHomePageInfo(data?: EventMessageData): void;
    /**
     * Send an event to hide the info to the user about replacing a homepage from a parent catalog.
     */
    sendEventShowReplaceParentHomePageInfo(data?: EventMessageData): void;
    /**
     * getHomepageDetailsForContext is a mashup of logic needed for the frontend
     * Both the pageDisplayConditions and newPageDisplayConditions components use it for various
     * ui related things, like enable/disable of the homepage checkbox, or show messages on the UI
     *
     * Given a uriContext, basically there are 3 mains return types, mashed into 1 typescript type
     *
     * 1) CatalogHomepageDetailsStatus.NO_HOMEPAGE
     * This means that the given uriContext has no homepage whatsoever. This probably indicates an issue with the data.
     *
     * 2) CatalogHomepageDetailsStatus.PARENT
     * This means that the current homepage for the given uriContext is inherited from a parent catalog.
     * In this case, the returned ICatalogHomepageDetails contains the parentCatalogName, parentCatalogVersion,
     * targetCatalogName, and targetCatalogVersion,
     *
     * 3) CatalogHomepageDetailsStatus.LOCAL
     * This means that the current homepage for the given uriContext belongs to the catalog of that uriContext.
     * In this case, the returned ICatalogHomepageDetails contains the currentHomepageName, currentHomepageUid,
     * and oldHomepageUid
     *
     * @param uriContext A IUriContext object
     *
     * @returns ICatalogHomepageDetails with one of the 3 options as indicated above
     */
    getHomepageDetailsForContext(uriContext: IUriContext): Promise<ICatalogHomepageDetails>;
    /**
     * This method checks if the cms page is a current homepage.
     *
     * @param cmsPage The cms page.
     * @param uriContext The uriContext.
     *
     * @returns A promise resolved with a boolean indicating whether the cms page is the current homepage.
     */
    isCurrentHomepage(cmsPage: ICMSPage, uriContext: IUriContext): Promise<boolean>;
    /**
     * This method checks if the cms page is a current homepage.
     *
     * @param cmsPage The cms page.
     * @param uriContext The uriContext.
     *
     * @returns A promise resolved with a boolean indicating whether the cms page is a previous homepage.
     */
    isOldHomepage(cmsPage: ICMSPage, uriContext: IUriContext): Promise<boolean>;
    /**
     * This method returns the homepage type of a cms page.
     *
     * @param cmsPage The cms page.
     * @param uriContext The uriContext.
     *
     * @returns A promise resolved with a enum type indicating whether the cms page is a current, previous homepage or null if neither.
     */
    getHomepageType(cmsPage: ICMSPage, uriContext: IUriContext): Promise<HomepageType>;
    /**
     * @param uriContext The uriContext.
     *
     * @returns A promise resolved to true when the catalog has a fallback homepage.
     */
    hasFallbackHomePage(uriContext: IUriContext): Promise<boolean>;
    /**
     * @param cmsPage The cms page.
     * @param uriContext The uriContext.
     *
     * @returns A promise resolved to true when the page can be synced.
     */
    canSyncHomepage(cmsPage: ICMSPage, uriContext: IUriContext): Promise<boolean>;
    private compareCatalogVersions;
    private isHomepageType;
    private buildHomepageDetailsForContext;
}
