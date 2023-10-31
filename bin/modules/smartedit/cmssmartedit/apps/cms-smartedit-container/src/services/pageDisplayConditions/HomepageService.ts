/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { CONTEXT_CATALOG } from 'cmscommons';
import {
    EventMessageData,
    ICatalogService,
    ICatalogVersion,
    IHomepage,
    IUriContext,
    SystemEventService,
    TypedMap,
    ICMSPage
} from 'smarteditcommons';

/**
 * Expose through angular the event for sendEventShowReplaceParentHomePageInfo()
 */
export const CMS_EVENT_SHOW_REPLACE_PARENT_HOMEPAGE_INFO =
    'CMS_EVENT_SHOW_REPLACE_PARENT_HOMEPAGE_INFO';

/** @internal */
/**
 * Expose through angular the event for sendEventHideReplaceParentHomePageInfo()
 */
export const CMS_EVENT_HIDE_REPLACE_PARENT_HOMEPAGE_INFO =
    'CMS_EVENT_HIDE_REPLACE_PARENT_HOMEPAGE_INFO';

/** @internal */
/**
 * !NGDOC
 * Status of a ICatalogHomepageDetails
 */
export enum CatalogHomepageDetailsStatus {
    // Status is being calculated
    PENDING = 'PENDING',
    // No current homepage for the given catalog version
    NO_HOMEPAGE = 'NO_HOMEPAGE',
    // There is a homepage is in the given catalog version (not inherited)
    LOCAL = 'LOCAL',
    // The old homepage is in the given catalog version
    OLD = 'OLD',
    // There is a homepage, but it is inherited from a parent catalog
    PARENT = 'PARENT'
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
export enum HomepageType {
    /**
     * An enum value of type HomepageType describing if a cms page was previously a homepage.
     */
    OLD = 'old',

    /**
     * An enum value of type HomepageType describing if a cms page is a current homepage.
     */
    CURRENT = 'current',

    /**
     * An enum value of type HomepageType describing if a cms page is a fallback homepage.
     */
    FALLBACK = 'fallback'
}

/**
 * This service is used to determine if a cms page is a current, a previous homepage, or neither.
 */
@Injectable()
export class HomepageService {
    constructor(
        private catalogService: ICatalogService,
        private systemEventService: SystemEventService
    ) {}

    /**
     * Send an event to show info to the user about replacing a homepage from a parent catalog.
     */
    public sendEventHideReplaceParentHomePageInfo(data?: EventMessageData): void {
        this.systemEventService.publish(CMS_EVENT_HIDE_REPLACE_PARENT_HOMEPAGE_INFO, data);
    }

    /**
     * Send an event to hide the info to the user about replacing a homepage from a parent catalog.
     */
    public sendEventShowReplaceParentHomePageInfo(data?: EventMessageData): void {
        this.systemEventService.publish(CMS_EVENT_SHOW_REPLACE_PARENT_HOMEPAGE_INFO, data);
    }

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
    public async getHomepageDetailsForContext(
        uriContext: IUriContext
    ): Promise<ICatalogHomepageDetails> {
        const catalogVersion: ICatalogVersion = await this.catalogService.getContentCatalogVersion(
            uriContext
        );
        const homepageForCurrentCatalog: IHomepage = catalogVersion.homepage
            ? catalogVersion.homepage.current
            : null;
        return this.buildHomepageDetailsForContext(homepageForCurrentCatalog, uriContext);
    }

    /**
     * This method checks if the cms page is a current homepage.
     *
     * @param cmsPage The cms page.
     * @param uriContext The uriContext.
     *
     * @returns A promise resolved with a boolean indicating whether the cms page is the current homepage.
     */
    public isCurrentHomepage(cmsPage: ICMSPage, uriContext: IUriContext): Promise<boolean> {
        return this.isHomepageType(cmsPage, uriContext, HomepageType.CURRENT);
    }

    /**
     * This method checks if the cms page is a current homepage.
     *
     * @param cmsPage The cms page.
     * @param uriContext The uriContext.
     *
     * @returns A promise resolved with a boolean indicating whether the cms page is a previous homepage.
     */
    public isOldHomepage(cmsPage: ICMSPage, uriContext: IUriContext): Promise<boolean> {
        return this.isHomepageType(cmsPage, uriContext, HomepageType.OLD);
    }

    /**
     * This method returns the homepage type of a cms page.
     *
     * @param cmsPage The cms page.
     * @param uriContext The uriContext.
     *
     * @returns A promise resolved with a enum type indicating whether the cms page is a current, previous homepage or null if neither.
     */
    public async getHomepageType(
        cmsPage: ICMSPage,
        uriContext: IUriContext
    ): Promise<HomepageType> {
        const catalog: ICatalogVersion = await this.catalogService.getContentCatalogVersion(
            uriContext
        );
        if (!catalog || !catalog.homepage) {
            return null;
        }

        if (
            catalog.homepage.current &&
            catalog.homepage.current.uid === cmsPage.uid &&
            catalog.homepage.current.catalogVersionUuid === cmsPage.catalogVersion
        ) {
            return HomepageType.CURRENT;
        } else if (catalog.homepage.old && catalog.homepage.old.uid === cmsPage.uid) {
            return this.compareCatalogVersions<HomepageType>(
                catalog.homepage.old.catalogVersionUuid,
                cmsPage.catalogVersion,
                (oldHomepageCatalog: ICatalogVersion, pageCatalog: ICatalogVersion) =>
                    oldHomepageCatalog.catalogId === pageCatalog.catalogId ? HomepageType.OLD : null
            );
        } else if (catalog.homepage.fallback && catalog.homepage.fallback.uid === cmsPage.uid) {
            return this.compareCatalogVersions<HomepageType>(
                catalog.homepage.fallback.catalogVersionUuid,
                cmsPage.catalogVersion,
                (fallbackCatalog: ICatalogVersion, pageCatalog: ICatalogVersion) =>
                    fallbackCatalog.uuid === pageCatalog.uuid ? HomepageType.FALLBACK : null
            );
        }

        return null;
    }

    /**
     * @param uriContext The uriContext.
     *
     * @returns A promise resolved to true when the catalog has a fallback homepage.
     */
    public async hasFallbackHomePage(uriContext: IUriContext): Promise<boolean> {
        const catalog: ICatalogVersion = await this.catalogService.getContentCatalogVersion(
            uriContext
        );
        if (!catalog || !catalog.homepage) {
            throw Error(
                `HomepageService.hasFallbackHomePage - Catalog does not have homepage fallback property`
            );
        }
        return !!catalog.homepage.fallback;
    }

    /**
     * @param cmsPage The cms page.
     * @param uriContext The uriContext.
     *
     * @returns A promise resolved to true when the page can be synced.
     */
    public async canSyncHomepage(cmsPage: ICMSPage, uriContext: IUriContext): Promise<boolean> {
        const isOld = await this.isOldHomepage(cmsPage, uriContext);
        if (!isOld) {
            return true;
        }
        const catalog: ICatalogVersion = await this.catalogService.getContentCatalogVersion(
            uriContext
        );
        return this.compareCatalogVersions<boolean>(
            catalog.homepage.current.catalogVersionUuid,
            cmsPage.catalogVersion,
            (currentCatalog: ICatalogVersion, pageCatalog: ICatalogVersion) =>
                cmsPage.uid !== catalog.homepage.current.uid &&
                currentCatalog.catalogId !== pageCatalog.catalogId
        );
    }

    private async compareCatalogVersions<T>(
        catalogAUuid: string,
        catalogBUuid: string,
        compare: (a: ICatalogVersion, b: ICatalogVersion) => T
    ): Promise<T> {
        const [catalogVersionA, catalogVersionB] = await Promise.all([
            this.catalogService.getCatalogVersionByUuid(catalogAUuid),
            this.catalogService.getCatalogVersionByUuid(catalogBUuid)
        ]);
        return compare(catalogVersionA, catalogVersionB);
    }

    private async isHomepageType(
        cmsPage: ICMSPage,
        uriContext: IUriContext,
        type: HomepageType
    ): Promise<boolean> {
        const homepageType: HomepageType = await this.getHomepageType(cmsPage, uriContext);
        return homepageType === type;
    }

    private async buildHomepageDetailsForContext(
        homepageForCurrentCatalog: IHomepage,
        uriContext: IUriContext
    ): Promise<ICatalogHomepageDetails> {
        if (!homepageForCurrentCatalog) {
            return {
                status: CatalogHomepageDetailsStatus.NO_HOMEPAGE
            };
        }

        const homepageCatalogVersion: ICatalogVersion =
            await this.catalogService.getCatalogVersionByUuid(
                homepageForCurrentCatalog.catalogVersionUuid
            );
        const homepageComesFromParent =
            homepageCatalogVersion.catalogId !== uriContext[CONTEXT_CATALOG];
        const currentCatalogVersion: ICatalogVersion =
            await this.catalogService.getContentCatalogVersion(uriContext);

        if (homepageComesFromParent) {
            return {
                status: CatalogHomepageDetailsStatus.PARENT,
                parentCatalogName: homepageCatalogVersion.catalogName,
                parentCatalogVersion: homepageCatalogVersion.version,
                targetCatalogName: currentCatalogVersion.catalogName,
                targetCatalogVersion: currentCatalogVersion.version
            };
        }

        return {
            status: CatalogHomepageDetailsStatus.LOCAL,
            currentHomepageName: homepageForCurrentCatalog.name,
            currentHomepageUid: homepageForCurrentCatalog.uid,
            oldHomepageUid: homepageCatalogVersion.homepage
                ? homepageCatalogVersion.homepage.old
                    ? homepageCatalogVersion.homepage.old.uid
                    : null
                : null
        };
    }
}
