/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, OnInit, Input, DoCheck, OnDestroy, Inject } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import * as LodashService from 'lodash';
import {
    PersonalizationsmarteditMessageHandler,
    PersonalizationsmarteditUtils,
    PersonalizationsmarteditDateUtils,
    PersonalizationsmarteditContextUtils,
    PersonalizationsmarteditCommerceCustomizationService,
    CUSTOMIZE_VIEW_TOOLBAR_ITEM_KEY,
    CustomizationVariation,
    Customization
} from 'personalizationcommons';
import { CustomizeViewServiceProxy } from 'personalizationsmarteditcontainer/customizeView/CustomizeViewServiceOuterProxy';
import {
    PersonalizationsmarteditContextService,
    PersonalizationsmarteditPreviewService,
    PersonalizationsmarteditRestService
} from 'personalizationsmarteditcontainer/service';
import {
    CrossFrameEventService,
    promiseUtils,
    SystemEventService,
    SHOW_TOOLBAR_ITEM_CONTEXT,
    ToolbarItemInternal,
    TOOLBAR_ITEM
} from 'smarteditcommons';
import { ManageCustomizationViewManager } from '../../management/manageCustomizationView';

interface CustomizationListEntity extends Customization {
    enabled?: boolean;
    collapsed?: boolean;
    subMenu?: boolean;
}
@Component({
    selector: 'customizations-list',
    templateUrl: './CustomizationsListComponent.html'
})
export class CustomizationsListComponent implements OnInit, DoCheck, OnDestroy {
    @Input() public customizationsList: CustomizationListEntity[];
    @Input() public requestProcessing: boolean;
    private sourceContainersComponentsInfo: any = {};
    private customizationsListPreCount = 0;
    private customizationsModifiedUnsubscribe: () => void;

    constructor(
        private translateService: TranslateService,
        protected personalizationsmarteditContextService: PersonalizationsmarteditContextService,
        protected personalizationsmarteditRestService: PersonalizationsmarteditRestService,
        protected personalizationsmarteditCommerceCustomizationService: PersonalizationsmarteditCommerceCustomizationService,
        protected personalizationsmarteditMessageHandler: PersonalizationsmarteditMessageHandler,
        protected personalizationsmarteditUtils: PersonalizationsmarteditUtils,
        protected personalizationsmarteditDateUtils: PersonalizationsmarteditDateUtils,
        protected personalizationsmarteditContextUtils: PersonalizationsmarteditContextUtils,
        protected personalizationsmarteditPreviewService: PersonalizationsmarteditPreviewService,
        protected personalizationsmarteditManager: ManageCustomizationViewManager,
        protected customizeViewServiceProxy: CustomizeViewServiceProxy,
        protected systemEventService: SystemEventService,
        protected crossFrameEventService: CrossFrameEventService,
        @Inject(TOOLBAR_ITEM) private toolbarItem: ToolbarItemInternal
    ) {}

    ngOnDestroy(): void {
        if (this.customizationsModifiedUnsubscribe) {
            this.customizationsModifiedUnsubscribe();
        }
    }

    ngOnInit(): void {
        this.sourceContainersComponentsInfo = {};
        this.customizationsModifiedUnsubscribe = this.systemEventService.subscribe(
            'CUSTOMIZATIONS_MODIFIED',
            () => {
                this.refreshCustomizeContext();
            }
        );
    }

    ngDoCheck(): void {
        if (this.customizationsListPreCount !== this.customizationsList.length) {
            this.updateCustomizationList();
            this.customizationsListPreCount = this.customizationsList.length;
        }
    }

    public updateCustomizationList(): void {
        this.customizationsList.forEach((customization) => {
            customization.collapsed = true;
            if (
                (
                    this.personalizationsmarteditContextService.getCustomize()
                        .selectedCustomization || {}
                ).code === customization.code
            ) {
                customization.collapsed = false;
                this.updateCustomizationData(customization);
            }
            this.personalizationsmarteditUtils.getAndSetCatalogVersionNameL10N(customization);
        });
    }

    public editCustomizationAction(customization: CustomizationListEntity): void {
        this.personalizationsmarteditContextUtils.clearCombinedViewContextAndReloadPreview(
            this.personalizationsmarteditPreviewService,
            this.personalizationsmarteditContextService
        );
        this.personalizationsmarteditManager.openEditCustomizationModal(customization.code, '');
        this.toolbarItem.isOpen = false;
    }

    public customizationRowClick(customization: CustomizationListEntity, select: boolean): void {
        this.clearAllSubMenu();
        customization.collapsed = !customization.collapsed;

        if (!customization.collapsed) {
            this.updateCustomizationData(customization);
        }
        if (select) {
            this.customizationClick(customization);
        }

        this.customizationsList
            .filter((cust) => customization.code !== cust.code)
            .forEach((cust) => {
                cust.collapsed = true;
            });
    }

    public customizationClick(customization: CustomizationListEntity): void {
        const combinedView = this.personalizationsmarteditContextService.getCombinedView();
        const currentVariations =
            this.personalizationsmarteditContextService.getCustomize().selectedVariations;
        const visibleVariations = this.getVisibleVariations(customization);
        const customize = this.personalizationsmarteditContextService.getCustomize();
        customize.selectedCustomization = customization;
        customize.selectedVariations = visibleVariations;
        this.personalizationsmarteditContextService.setCustomize(customize);
        if (visibleVariations.length > 0) {
            const allVariations = this.personalizationsmarteditUtils
                .getVariationCodes(visibleVariations)
                .join(',');
            this.getAndSetComponentsForVariation(
                customization.code,
                allVariations,
                customization.catalog,
                customization.catalogVersion
            );
        }
        if (
            (LodashService.isObjectLike(currentVariations) &&
                !LodashService.isArray(currentVariations)) ||
            combinedView.enabled
        ) {
            this.updatePreviewTicket();
        }

        this.personalizationsmarteditContextUtils.clearCombinedViewContext(
            this.personalizationsmarteditContextService
        );
        this.crossFrameEventService.publish(
            SHOW_TOOLBAR_ITEM_CONTEXT,
            CUSTOMIZE_VIEW_TOOLBAR_ITEM_KEY
        );
    }

    public getSelectedVariationClass(variation: CustomizationListEntity): string {
        if (
            LodashService.isEqual(
                variation.code,
                (
                    (this.personalizationsmarteditContextService.getCustomize()
                        .selectedVariations || {}) as CustomizationVariation
                ).code
            )
        ) {
            return 'selectedVariation';
        } else {
            return '';
        }
    }

    public getSelectedCustomizationClass(customization: CustomizationListEntity): string {
        if (
            LodashService.isEqual(
                customization.code,
                (
                    this.personalizationsmarteditContextService.getCustomize()
                        .selectedCustomization || {}
                ).code
            ) &&
            LodashService.isArray(
                this.personalizationsmarteditContextService.getCustomize().selectedVariations
            )
        ) {
            return 'selectedCustomization';
        } else {
            return '';
        }
    }

    public variationClick(
        customization: CustomizationListEntity,
        variation: CustomizationVariation
    ): void {
        const customize = this.personalizationsmarteditContextService.getCustomize();
        customize.selectedCustomization = customization;
        customize.selectedVariations = variation;
        this.personalizationsmarteditContextService.setCustomize(customize);
        this.personalizationsmarteditContextUtils.clearCombinedViewContext(
            this.personalizationsmarteditContextService
        );
        this.getAndSetComponentsForVariation(
            customization.code,
            variation.code,
            customization.catalog,
            customization.catalogVersion
        );
        this.updatePreviewTicket(customization.code, [variation]);
        this.crossFrameEventService.publish(
            SHOW_TOOLBAR_ITEM_CONTEXT,
            CUSTOMIZE_VIEW_TOOLBAR_ITEM_KEY
        );
    }

    public hasCommerceActions(variation: CustomizationVariation): boolean {
        return this.personalizationsmarteditUtils.hasCommerceActions(variation);
    }

    public getCommerceCustomizationTooltip(variation: CustomizationVariation): string {
        return this.personalizationsmarteditUtils.getCommerceCustomizationTooltip(
            variation,
            '',
            ''
        );
    }

    public getActivityStateForCustomization(customization: CustomizationListEntity): string {
        return this.personalizationsmarteditUtils.getActivityStateForCustomization(customization);
    }

    public customizationsListTrackBy(index: number, _customizationsList: any): number {
        return index;
    }

    public getActivityStateForVariation(
        customization: CustomizationListEntity,
        variation: CustomizationVariation
    ): string {
        return this.personalizationsmarteditUtils.getActivityStateForVariation(
            customization,
            variation
        );
    }

    public clearAllSubMenu(): void {
        for (const customization of this.customizationsList) {
            customization.subMenu = false;
        }
    }

    public getEnablementTextForCustomization(customization: CustomizationListEntity): string {
        return this.personalizationsmarteditUtils.getEnablementTextForCustomization(
            customization,
            'personalization.toolbar.pagecustomizations'
        );
    }

    public getEnablementTextForVariation(variation: CustomizationVariation): string {
        return this.personalizationsmarteditUtils.getEnablementTextForVariation(
            variation,
            'personalization.toolbar.pagecustomizations'
        );
    }

    public isEnabled(item: any): boolean {
        return this.personalizationsmarteditUtils.isPersonalizationItemEnabled(item);
    }

    public getDatesForCustomization(customization: CustomizationListEntity): string {
        let activityStr = '';
        let startDateStr = '';
        let endDateStr = '';

        if (customization.enabledStartDate || customization.enabledEndDate) {
            startDateStr = this.personalizationsmarteditDateUtils.formatDateWithMessage(
                customization.enabledStartDate
            );
            endDateStr = this.personalizationsmarteditDateUtils.formatDateWithMessage(
                customization.enabledEndDate
            );
            if (!customization.enabledStartDate) {
                startDateStr = ' ...';
            }
            if (!customization.enabledEndDate) {
                endDateStr = '... ';
            }
            activityStr += ' (' + startDateStr + ' - ' + endDateStr + ') ';
        }
        return activityStr;
    }

    public customizationSubMenuAction(customization: CustomizationListEntity): void {
        if (!customization.subMenu) {
            this.clearAllSubMenu();
        }
        customization.subMenu = !customization.subMenu;
    }

    public isCustomizationFromCurrentCatalog(customization: CustomizationListEntity): boolean {
        return this.personalizationsmarteditUtils.isItemFromCurrentCatalog(
            customization,
            this.personalizationsmarteditContextService.getSeData()
        );
    }

    public statusNotDeleted(variation: CustomizationVariation): boolean {
        return this.personalizationsmarteditUtils.isItemVisible(variation);
    }

    private matchActionForVariation(action: any, variation: CustomizationVariation): boolean {
        return (
            action.variationCode === variation.code &&
            action.actionCatalog === variation.catalog &&
            action.actionCatalogVersion === variation.catalogVersion
        );
    }

    private numberOfAffectedComponentsForActions(actionsForVariation: any[]): number {
        let result = 0;
        actionsForVariation.forEach((action) => {
            result += parseInt(this.sourceContainersComponentsInfo[action.containerId], 10) || 0;
        });
        return result;
    }

    private initSourceContainersComponentsInfo(): Promise<unknown> {
        const deferred = promiseUtils.defer();
        (this.customizeViewServiceProxy.getSourceContainersInfo() as any).then(
            (response: any) => {
                this.sourceContainersComponentsInfo = response;
                deferred.resolve();
            },
            () => {
                // error callback
                this.personalizationsmarteditMessageHandler.sendError(
                    this.translateService.instant(
                        'personalization.error.gettingnumberofaffectedcomponentsforvariation'
                    )
                );
                deferred.reject();
            }
        );
        return deferred.promise;
    }

    private paginatedGetAndSetNumberOfAffectedComponentsForVariations(
        customization: CustomizationListEntity,
        currentPage: number
    ): void {
        this.personalizationsmarteditRestService
            .getCxCmsActionsOnPageForCustomization(customization, currentPage)
            .then(
                (response: any) => {
                    customization.variations.forEach((variation: any) => {
                        const actionsForVariation = response.actions.filter((action: any) =>
                            this.matchActionForVariation(action, variation)
                        );
                        variation.numberOfAffectedComponents =
                            currentPage === 0 ? 0 : variation.numberOfAffectedComponents;
                        variation.numberOfAffectedComponents +=
                            this.numberOfAffectedComponentsForActions(actionsForVariation);
                    });

                    const nextPage = currentPage + 1;
                    if (nextPage < response.pagination.totalPages) {
                        this.paginatedGetAndSetNumberOfAffectedComponentsForVariations(
                            customization,
                            nextPage
                        );
                    }
                },
                () => {
                    // error callback
                    this.personalizationsmarteditMessageHandler.sendError(
                        this.translateService.instant(
                            'personalization.error.gettingnumberofaffectedcomponentsforvariation'
                        )
                    );
                }
            );
    }

    private getAndSetNumberOfAffectedComponentsForVariations(
        customization: CustomizationListEntity
    ): void {
        const customize = this.personalizationsmarteditContextService.getCustomize();
        const isUpToDate: boolean = (customize.selectedComponents || []).every(
            (componentId: keyof unknown) =>
                this.sourceContainersComponentsInfo[componentId] !== undefined
        );
        if (
            !isUpToDate ||
            customize.selectedComponents === null ||
            LodashService.isEqual(this.sourceContainersComponentsInfo, {})
        ) {
            this.initSourceContainersComponentsInfo().finally(() => {
                this.paginatedGetAndSetNumberOfAffectedComponentsForVariations(customization, 0);
            });
        } else if (isUpToDate) {
            this.paginatedGetAndSetNumberOfAffectedComponentsForVariations(customization, 0);
        }
    }

    private getNumberOfAffectedComponentsForCorrespondingVariation(
        variationsArray: any[],
        variationCode: any
    ): any {
        const foundVariation = variationsArray.filter((elem) => elem.code === variationCode);
        return (foundVariation[0] || {}).numberOfAffectedComponents;
    }

    private updateCustomizationData(customization: CustomizationListEntity): void {
        this.personalizationsmarteditRestService
            .getVariationsForCustomization(customization.code, customization)
            .then(
                (response: any) => {
                    const variations = response.variations
                        ? LodashService.cloneDeep(response.variations)
                        : [];
                    variations.forEach((variation, index) => {
                        if (!this.statusNotDeleted(variation)) {
                            variations.splice(index, 1);
                        } else {
                            variation.numberOfAffectedComponents =
                                this.getNumberOfAffectedComponentsForCorrespondingVariation(
                                    customization.variations,
                                    variation.code
                                );
                            variation.numberOfCommerceActions =
                                this.personalizationsmarteditCommerceCustomizationService.getCommerceActionsCount(
                                    variation
                                );
                            variation.commerceCustomizations =
                                this.personalizationsmarteditCommerceCustomizationService.getCommerceActionsCountMap(
                                    variation
                                );
                        }
                    });
                    customization.variations = variations || [];
                    this.getAndSetNumberOfAffectedComponentsForVariations(customization);
                },
                () => {
                    // error callback
                    this.personalizationsmarteditMessageHandler.sendError(
                        this.translateService.instant('personalization.error.gettingcustomization')
                    );
                }
            );
    }

    private getVisibleVariations(customization: CustomizationListEntity): any {
        return this.personalizationsmarteditUtils.getVisibleItems(customization.variations);
    }

    private getAndSetComponentsForVariation(
        customizationId: string,
        variationId: string,
        catalog: string,
        catalogVersion: string
    ): void {
        this.personalizationsmarteditRestService
            .getComponentsIdsForVariation(customizationId, variationId, catalog, catalogVersion)
            .then(
                (response: any) => {
                    const customize = this.personalizationsmarteditContextService.getCustomize();
                    customize.selectedComponents = response.components;
                    this.personalizationsmarteditContextService.setCustomize(customize);
                },
                () => {
                    // error callback
                    this.personalizationsmarteditMessageHandler.sendError(
                        this.translateService.instant(
                            'personalization.error.gettingcomponentsforvariation'
                        )
                    );
                }
            );
    }

    private updatePreviewTicket(customizationId?: string, variationArray?: any[]): void {
        const variationKeys = this.personalizationsmarteditUtils.getVariationKey(
            customizationId,
            variationArray
        );
        this.personalizationsmarteditPreviewService.updatePreviewTicketWithVariations(
            variationKeys
        );
    }

    private refreshCustomizeContext(): void {
        const customize = LodashService.cloneDeep(
            this.personalizationsmarteditContextService.getCustomize()
        );
        if (customize.selectedCustomization) {
            this.personalizationsmarteditRestService
                .getCustomization(customize.selectedCustomization)
                .then(
                    (response: any) => {
                        customize.selectedCustomization = response;
                        if (
                            customize.selectedVariations &&
                            !LodashService.isArray(customize.selectedVariations)
                        ) {
                            response.variations
                                .filter(
                                    (item: any) =>
                                        (customize.selectedVariations as CustomizationVariation)
                                            .code === item.code
                                )
                                .forEach((variation: any) => {
                                    customize.selectedVariations = variation;
                                    if (
                                        !this.personalizationsmarteditUtils.isItemVisible(variation)
                                    ) {
                                        customize.selectedCustomization = null;
                                        customize.selectedVariations = null;
                                        this.personalizationsmarteditPreviewService.removePersonalizationDataFromPreview();
                                    }
                                });
                        }
                        this.personalizationsmarteditContextService.setCustomize(customize);
                    },
                    () => {
                        this.personalizationsmarteditMessageHandler.sendError(
                            this.translateService.instant(
                                'personalization.error.gettingcustomization'
                            )
                        );
                    }
                );
        }
    }
}
