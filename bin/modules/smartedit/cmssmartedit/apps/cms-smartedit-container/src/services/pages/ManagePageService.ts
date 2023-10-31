/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { PAGE_CONTEXT_CATALOG } from 'cmscommons';
import { cloneDeep } from 'lodash';
import {
    pageDeletionEvictionTag,
    pageRestoredEvictionTag,
    rarelyChangingContent,
    Cached,
    CrossFrameEventService,
    IAlertService,
    ICatalogService,
    IPageInfoService,
    IRestService,
    IRestServiceFactory,
    IUriContext,
    IWaitDialogService,
    SmarteditRoutingService,
    SystemEventService,
    ValidationError,
    IConfirmationModalService,
    EVENT_CONTENT_CATALOG_UPDATE,
    EVENTS,
    LogService,
    PAGE_CONTEXT_SITE_ID,
    ISharedDataService,
    IExperience,
    ICatalogVersion,
    IBaseCatalog,
    EXPERIENCE_STORAGE_KEY,
    CmsitemsRestService,
    ICMSPage,
    CMSPageStatus,
    WorkflowService
} from 'smarteditcommons';
import { CatalogVersionRestService, PagesVariationsRestService } from '../../dao';

import { PageRestoredAlertService } from '../actionableAlert';
import { HomepageService, HomepageType } from '../pageDisplayConditions';
import { PageRestoreModalService } from './pageRestore/PageRestoreModalService';

export enum CMSPageOperation {
    TRASH_PAGE = 'TRASH_PAGE'
}

export interface CMSPageOperationPayload {
    catalogId?: string;
    operation: CMSPageOperation;
    sourceCatalogVersion: string;
    targetCatalogVersion: string;
}

@Injectable()
export class ManagePageService {
    private readonly resourcePageOperationsURI: string;
    private pageOperationsRESTService: IRestService<CMSPageOperationPayload>;

    constructor(
        private readonly logService: LogService,
        private readonly smarteditRoutingService: SmarteditRoutingService,
        private readonly alertService: IAlertService,
        private readonly cmsitemsRestService: CmsitemsRestService,
        private readonly systemEventService: SystemEventService,
        private readonly crossFrameEventService: CrossFrameEventService,
        private readonly pageInfoService: IPageInfoService,
        private readonly confirmationModalService: IConfirmationModalService,
        private readonly pagesVariationsRestService: PagesVariationsRestService,
        private readonly waitDialogService: IWaitDialogService,
        private readonly pageRestoreModalService: PageRestoreModalService,
        private readonly pageRestoredAlertService: PageRestoredAlertService,
        private readonly homepageService: HomepageService,
        private readonly workflowService: WorkflowService,
        private readonly catalogService: ICatalogService,
        private readonly restServiceFactory: IRestServiceFactory,
        private readonly sharedDataService: ISharedDataService,
        private readonly catalogVersionRestService: CatalogVersionRestService
    ) {
        this.resourcePageOperationsURI = `/cmssmarteditwebservices/v1/sites/${PAGE_CONTEXT_SITE_ID}/catalogs/${PAGE_CONTEXT_CATALOG}/pages/:pageId/operations`;
    }

    /**
     * Get the number of soft deleted pages for the provided context.
     */
    @Cached({
        actions: [rarelyChangingContent],
        tags: [pageDeletionEvictionTag, pageRestoredEvictionTag]
    })
    public async getSoftDeletedPagesCount(uriContext: IUriContext): Promise<number> {
        const requestParams = {
            pageSize: 10,
            currentPage: 0,
            typeCode: 'AbstractPage',
            itemSearchParams: 'pageStatus:deleted',
            catalogId: uriContext.CURRENT_CONTEXT_CATALOG,
            catalogVersion: uriContext.CURRENT_CONTEXT_CATALOG_VERSION,
            fields: 'BASIC'
        };

        const result = await this.cmsitemsRestService.get<ICMSPage>(requestParams);

        return result.pagination.totalCount;
    }

    /**
     * This method triggers the soft deletion of a CMS page.
     *
     * @param pageInfo The page object containing the uuid and the name of the page to be deleted.
     */
    public async softDeletePage(pageInfo: ICMSPage, uriContext: IUriContext): Promise<void> {
        const _pageInfo: ICMSPage = cloneDeep(pageInfo);

        const builtURIContext: IUriContext = {
            catalogId: uriContext.CURRENT_CONTEXT_CATALOG,
            catalogVersion: uriContext.CURRENT_CONTEXT_CATALOG_VERSION,
            siteId: uriContext.CURRENT_CONTEXT_SITE_ID
        };

        const confirmationModalDescription = await this.getConfirmationModalDescription(
            _pageInfo,
            uriContext
        );

        const operationConfirmed = await this.confirmSoftDelete(
            confirmationModalDescription,
            pageInfo.name
        );

        if (!operationConfirmed) {
            return;
        }

        try {
            _pageInfo.identifier = pageInfo.uuid;
            _pageInfo.pageStatus = CMSPageStatus.DELETED;

            await this.cmsitemsRestService.update(_pageInfo);
            this.crossFrameEventService.publish(EVENTS.PAGE_DELETED);
            this.alertService.showSuccess({
                message: 'se.cms.actionitem.page.trash.alert.success.description',
                messagePlaceholders: {
                    pageName: pageInfo.name
                },
                minWidth: '',
                mousePersist: true,
                duration: 1000,
                dismissible: true,
                width: '300px'
            });

            this.smarteditRoutingService.go(
                '/pages/:siteId/:catalogId/:catalogVersion'
                    .replace(':siteId', builtURIContext.siteId)
                    .replace(':catalogId', builtURIContext.catalogId)
                    .replace(':catalogVersion', builtURIContext.catalogVersion)
            );
        } catch (exception) {
            this.logService.warn('Something went wrong when soft deleting page', exception);
        }
    }

    /**
     * This method triggers the permanent deletion of a CMS page.
     */
    public async hardDeletePage(pageInfo: ICMSPage): Promise<void> {
        const operationConfirmed = await this.confirmHardDelete(pageInfo.name);

        if (!operationConfirmed) {
            return;
        }

        await this.cmsitemsRestService.delete(pageInfo.uuid);
        this.alertService.showSuccess('se.cms.page.permanently.delete.alert.success');
        this.systemEventService.publishAsync(EVENT_CONTENT_CATALOG_UPDATE);
        this.crossFrameEventService.publish(EVENTS.PAGE_DELETED);
    }

    /**
     *  This method triggers the restoration a CMS page.
     */
    public async restorePage(pageInfo: ICMSPage): Promise<void> {
        const _pageInfo: ICMSPage = cloneDeep(pageInfo);

        _pageInfo.pageStatus = CMSPageStatus.ACTIVE;
        _pageInfo.identifier = pageInfo.uuid;

        this.waitDialogService.showWaitModal(null);

        try {
            const response = await this.cmsitemsRestService.update(_pageInfo, {
                headers: { 'Ignore-Interceptor': 'NonValidationErrorInterceptor' }
            });

            this.waitDialogService.hideWaitModal();
            this.systemEventService.publishAsync(EVENT_CONTENT_CATALOG_UPDATE, response);
            this.pageRestoredAlertService.displayPageRestoredSuccessAlert(_pageInfo);
            this.crossFrameEventService.publish(EVENTS.PAGE_RESTORED);
        } catch (exception) {
            const errors: ValidationError[] = exception.error.errors;
            this.waitDialogService.hideWaitModal();
            this.pageRestoreModalService.handleRestoreValidationErrors(_pageInfo, errors);
        }
    }

    /**
     * This method indicates whether the given page can be soft deleted.
     * Only the following pages are eligible for soft deletion:
     * 1. the variation pages
     * 2. the primary pages associated with no variation pages
     * 3. the page is not in a workflow
     */
    public async isPageTrashable(cmsPage: ICMSPage, uriContext: IUriContext): Promise<boolean> {
        try {
            const [hasFallbackHomepageOrIsPrimaryWithoutVariations, isInWorkflow] =
                await Promise.all([
                    this.hasFallbackHomepageOrIsPrimaryWithoutVariations(cmsPage, uriContext),
                    this.workflowService.isPageInWorkflow(cmsPage)
                ]);
            return hasFallbackHomepageOrIsPrimaryWithoutVariations && !isInWorkflow;
        } catch {
            return false;
        }
    }

    /**
     * Determines whether page can be cloned or not
     *
     * Checks if there is permission for given page in given catalog version to be cloned
     * This method uses only "outer" parts for that check so there is no need for iframe to be available
     *
     * !NOTE: Logic here is very similar to logic used in RulesAndPermissionsRegistrationService where "se.cloneable.page" rule is registered.
     * So if any changes are done here it should be considered to adjust those changes in mentioned service as well.
     *
     */
    public async isPageCloneable(pageUuid: string, catalogVersion: string): Promise<boolean> {
        const catalogDescriptor = (
            (await this.sharedDataService.get(EXPERIENCE_STORAGE_KEY)) as IExperience
        ).catalogDescriptor;
        const pageUriContext = {
            CURRENT_CONTEXT_SITE_ID: catalogDescriptor.siteId,
            CURRENT_CONTEXT_CATALOG: catalogDescriptor.catalogId,
            CURRENT_CONTEXT_CATALOG_VERSION: catalogDescriptor.catalogVersion
        };

        const [catalogs, pageInfo, targets] = await Promise.all([
            this.catalogService.getContentCatalogsForSite(catalogDescriptor.siteId),
            this.cmsitemsRestService.getById<ICMSPage>(pageUuid),
            this.catalogVersionRestService.getCloneableTargets(pageUriContext)
        ]);

        // Inspired by ExperienceServiceOuter#updateExperiencePageContext
        const pageContext: ICatalogVersion = catalogs
            // merge catalog and catalog.parents into one array to simplify searching
            .reduce(
                (acc: IBaseCatalog[], catalog: IBaseCatalog) => {
                    if (catalog.parents && catalog.parents.length) {
                        acc = acc.concat(catalog.parents);
                    }
                    return acc;
                },
                [...catalogs]
            )
            // flattening versions
            .reduce(
                (acc: ICatalogVersion[], catalog: IBaseCatalog) => acc.concat(catalog.versions),
                []
            )
            // finding demanded version
            .find((version: ICatalogVersion) => version.uuid === catalogVersion);

        if (pageContext?.active) {
            return targets.versions.length > 0 && !pageInfo.copyToCatalogsDisabled;
        }

        return targets.versions.length > 0;
    }

    /**
     * Get the disabled trash tooltip message.
     */
    public async getDisabledTrashTooltipMessage(
        pageInfo: ICMSPage,
        uriContext: IUriContext
    ): Promise<string> {
        let translate = 'se.cms.tooltip.movetotrash';
        const isPageInWorkflow = await this.workflowService.isPageInWorkflow(pageInfo);
        const homepageType = await this.homepageService.getHomepageType(pageInfo, uriContext);

        if (homepageType === HomepageType.CURRENT) {
            translate = 'se.cms.tooltip.current.homepage.movetotrash';
        } else if (homepageType === HomepageType.OLD) {
            translate = 'se.cms.tooltip.old.homepage.movetotrash';
        } else if (isPageInWorkflow) {
            translate = 'se.cms.tooltip.page.in.workflow.movetotrash';
        }

        return translate;
    }

    /**
     * Will trash the given page in the corresponding active catalog version.
     */
    public async trashPageInActiveCatalogVersion(pageUid: string): Promise<void> {
        const uriContext = await this.catalogService.retrieveUriContext();
        const activeVersion = await this.catalogService.getContentCatalogActiveVersion(uriContext);
        const operationConfirmed = await this.confirmTrashingPageInActiveCatalogVersion(
            activeVersion
        );

        if (!operationConfirmed) {
            return;
        }

        try {
            this.pageOperationsRESTService = this.restServiceFactory.get(
                this.resourcePageOperationsURI.replace(':pageId', pageUid)
            );
            await this.pageOperationsRESTService.save({
                operation: 'TRASH_PAGE',
                sourceCatalogVersion: uriContext.CURRENT_CONTEXT_CATALOG_VERSION,
                targetCatalogVersion: activeVersion
            });

            this.alertService.showSuccess({
                message: 'se.cms.sync.page.status.success.alert',
                messagePlaceholders: {
                    pageId: pageUid,
                    catalogVersion: activeVersion
                },
                minWidth: '',
                mousePersist: true,
                duration: 1000,
                dismissible: true,
                width: '300px'
            });
        } catch (exception) {
            this.logService.warn(
                'trashPageInActiveCatalogVersion - page could not be trashed',
                exception
            );
        }
    }

    private async getConfirmationModalDescription(
        pageInfo: ICMSPage,
        uriContext: IUriContext
    ): Promise<string> {
        try {
            const pageUUID = await this.pageInfoService.getPageUUID();
            if (!pageUUID) {
                this.logService.error('deletePageService::deletePage - pageUUID is undefined');
                return Promise.reject();
            }

            const homepageType = await this.homepageService.getHomepageType(pageInfo, uriContext);
            if (homepageType !== null || pageInfo.homepage) {
                return 'se.cms.actionitem.page.trash.confirmation.description.storefront.homepage';
            }

            return 'se.cms.actionitem.page.trash.confirmation.description.storefront';
        } catch {
            return 'se.cms.actionitem.page.trash.confirmation.description.pagelist';
        }
    }

    private async confirmSoftDelete(description: string, pageName: string): Promise<boolean> {
        try {
            await this.confirmationModalService.confirm({
                description,
                descriptionPlaceholders: {
                    pageName
                },
                title: 'se.cms.actionitem.page.trash.confirmation.title'
            });

            return true;
        } catch {
            return false;
        }
    }

    private async confirmHardDelete(pageName: string): Promise<boolean> {
        try {
            await this.confirmationModalService.confirm({
                title: 'se.cms.actionitem.page.permanently.delete.confirmation.title',
                description: 'se.cms.actionitem.page.permanently.delete.confirmation.description',
                descriptionPlaceholders: {
                    pageName
                }
            });

            return true;
        } catch {
            return false;
        }
    }

    private async confirmTrashingPageInActiveCatalogVersion(
        activeVersion: string
    ): Promise<boolean> {
        try {
            await this.confirmationModalService.confirm({
                title: 'se.cms.sync.page.status.confirm.title',
                description: 'se.cms.sync.page.status.confirm.description',
                descriptionPlaceholders: {
                    catalogVersion: activeVersion
                }
            });

            return true;
        } catch {
            return false;
        }
    }

    private async hasFallbackHomepageOrIsPrimaryWithoutVariations(
        cmsPage: ICMSPage,
        uriContext: IUriContext
    ): Promise<boolean> {
        const homepageType = await this.homepageService.getHomepageType(cmsPage, uriContext);

        if (homepageType !== null || cmsPage.homepage) {
            return this.homepageService.hasFallbackHomePage(uriContext);
        }

        const variationPagesUids =
            await this.pagesVariationsRestService.getVariationsForPrimaryPageId(cmsPage.uid);
        return variationPagesUids.length === 0;
    }
}
