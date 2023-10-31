/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import * as lodash from 'lodash';
import {
    CombinedView,
    CombinedViewSelectItem,
    Customization,
    CustomizationsFilter,
    CustomizationsList,
    CustomizationVariation,
    PERSONALIZATION_CUSTOMIZATION_PAGE_FILTER,
    PERSONALIZATION_VIEW_STATUS_MAPPING_CODES,
    PersonalizationsmarteditMessageHandler,
    PersonalizationsmarteditUtils,
    CustomizationStatus
} from 'personalizationcommons';
import { from as fromPromise, Observable } from 'rxjs';
import {
    CatalogVersion,
    ComponentMenuService,
    FetchStrategy,
    ModalButtonAction,
    ModalButtonStyle,
    ModalManagerService,
    Page,
    SelectReset
} from 'smarteditcommons';
import {
    PersonalizationsmarteditContextService,
    PersonalizationsmarteditRestService
} from '../../service';
import { CombinedViewConfigureService } from './CombinedViewConfigureService';
import { CombinedViewItemPrinterComponent } from './customizedVarItemPrinter';
@Component({
    selector: 'combined-view-configure',
    templateUrl: './CombinedViewConfigureComponent.html'
})
export class CombinedViewConfigureComponent implements OnInit {
    public moreCustomizationsRequestProcessing: boolean;
    public combinedView: CombinedView;
    public customizationPageFilter: string;
    public combinedViewItemsFetchStrategy: FetchStrategy<CombinedViewSelectItem>;
    public selectedCombinedViewItemId: string;
    public selectedCombinedViewItems: CombinedViewSelectItem[];
    public catalogFilter: string;
    public combinedViewItemPrinter;
    public resetSelectedItems: SelectReset;

    constructor(
        protected translateService: TranslateService,
        protected contextService: PersonalizationsmarteditContextService,
        protected messageHandler: PersonalizationsmarteditMessageHandler,
        protected personalizationsmarteditUtils: PersonalizationsmarteditUtils,
        protected componentMenuService: ComponentMenuService,
        private restService: PersonalizationsmarteditRestService,
        private modalManager: ModalManagerService,
        private combinedViewConfigureService: CombinedViewConfigureService
    ) {}

    ngOnInit(): void {
        this.combinedView = this.contextService.getCombinedView();
        this.selectedCombinedViewItems = [];
        this.selectedCombinedViewItems = lodash.cloneDeep(this.combinedView.selectedItems || []);
        this.combinedViewConfigureService.setSelectedItems(this.selectedCombinedViewItems);
        this.selectedCombinedViewItemId = null;

        this.moreCustomizationsRequestProcessing = false;

        this.combinedViewItemPrinter = CombinedViewItemPrinterComponent;
        this.combinedViewItemsFetchStrategy = {
            fetchPage: async (
                search: string,
                pageSize: number,
                currentPage: number
            ): Promise<Page<CombinedViewSelectItem>> =>
                this.customizedVarItemsFetchPage(search, pageSize, currentPage),
            fetchEntity: async (id): Promise<CombinedViewSelectItem> =>
                this.customizedVarItemsFetchEntity(id)
        };
        this.modalManager.addButtons([
            {
                id: 'confirmOk',
                label: 'personalization.modal.combinedview.button.ok',
                style: ModalButtonStyle.Primary,
                action: ModalButtonAction.Close,
                callback: (): Observable<void> => fromPromise(this.onSave()),
                disabledFn: (): boolean => {
                    const combinedView = this.contextService.getCombinedView();
                    let arrayEquals =
                        (combinedView.selectedItems || []).length === 0 &&
                        this.selectedCombinedViewItems.length === 0;
                    arrayEquals =
                        arrayEquals ||
                        lodash.isEqual(combinedView.selectedItems, this.selectedCombinedViewItems);
                    return arrayEquals;
                }
            },
            {
                id: 'confirmCancel',
                label: 'personalization.modal.combinedview.button.cancel',
                style: ModalButtonStyle.Default,
                action: ModalButtonAction.Dismiss,
                callback: (): Observable<void> => fromPromise(this.onCancel())
            }
        ]);
    }

    public selectElement = async (item: CombinedViewSelectItem, model?: string): Promise<void> => {
        if (!item) {
            return;
        }
        this.selectedCombinedViewItems.push(item);
        const catalogVersions: CatalogVersion[] =
            await this.componentMenuService.getValidContentCatalogVersions();
        const catalogsUuids = catalogVersions.map((elem: CatalogVersion) => elem.id);
        this.selectedCombinedViewItems.sort(
            (a: CombinedViewSelectItem, b: CombinedViewSelectItem) => {
                const aCatalogUuid = a.customization.catalog + '/' + a.customization.catalogVersion;
                const bCatalogUuid = b.customization.catalog + '/' + b.customization.catalogVersion;
                if (aCatalogUuid === bCatalogUuid) {
                    return a.customization.rank - b.customization.rank;
                }
                return catalogsUuids.indexOf(bCatalogUuid) - catalogsUuids.indexOf(aCatalogUuid);
            }
        );
        this.combinedViewConfigureService.setSelectedItems(this.selectedCombinedViewItems);

        this.reset();
    };

    public removeSelectedItem = (item: CombinedViewSelectItem): void => {
        this.selectedCombinedViewItems.splice(this.selectedCombinedViewItems.indexOf(item), 1);
        this.combinedViewConfigureService.setSelectedItems(this.selectedCombinedViewItems);
        this.reset();
    };

    public getClassForElement = (index: number): string =>
        this.personalizationsmarteditUtils.getClassForElement(index);

    public getLetterForElement = (index: number): string =>
        this.personalizationsmarteditUtils.getLetterForElement(index);

    public isItemInSelectDisabled = (item: CombinedViewSelectItem): boolean =>
        !!this.selectedCombinedViewItems.find(
            (currentItem: CombinedViewSelectItem) =>
                currentItem.customization.code === item.customization.code
        );

    public pageFilterChange = (itemId: string): void => {
        this.customizationPageFilter = itemId;
    };

    public catalogFilterChange = (itemId: string): void => {
        this.catalogFilter = itemId;
        this.combinedViewConfigureService.setCatalogFilter(this.catalogFilter);
    };

    public isItemFromCurrentCatalog = (item: CustomizationVariation): boolean =>
        this.personalizationsmarteditUtils.isItemFromCurrentCatalog(
            item,
            this.contextService.getSeData()
        );

    private reset(): void {
        if (this.resetSelectedItems) {
            this.resetSelectedItems(true);
        }
    }

    private async customizedVarItemsFetchPage(
        search: string,
        pageSize: number,
        currentPage: number
    ): Promise<Page<CombinedViewSelectItem>> {
        const filter = this.getCustomizationsCategoryFilter(search, pageSize, currentPage);
        try {
            const response: CustomizationsList = await this.restService.getCustomizations(filter);
            const customizedVarItems: CombinedViewSelectItem[] = [];
            response.customizations.forEach((customization: Customization) => {
                customization.variations
                    .filter((variation: CustomizationVariation) =>
                        this.personalizationsmarteditUtils.isItemVisible(variation)
                    )
                    .forEach((variation: CustomizationVariation) =>
                        customizedVarItems.push(
                            this.constructCombinedViewSelectItem(customization, variation)
                        )
                    );
            });
            return {
                pagination: response.pagination,
                results: customizedVarItems
            };
        } catch (e) {
            this.messageHandler.sendError(
                this.translateService.instant('personalization.error.gettingcustomizations')
            );
        }
    }

    private async customizedVarItemsFetchEntity(
        customizedVarItemId: string
    ): Promise<CombinedViewSelectItem> {
        if (!customizedVarItemId) {
            return;
        }
        const [customizationCode, variationCode] = customizedVarItemId.split('-');
        const customization: Customization = await this.restService.getCustomization({
            code: customizationCode
        });
        const variation: CustomizationVariation = customization.variations.filter(
            (element: CustomizationVariation) => element.code === variationCode
        )[0];
        return this.constructCombinedViewSelectItem(customization, variation);
    }

    private getDefaultStatus(): CustomizationStatus {
        const statusesMapping: CustomizationStatus[] =
            this.personalizationsmarteditUtils.getStatusesMapping();
        return statusesMapping.find(
            (elem: CustomizationStatus) =>
                elem.code === PERSONALIZATION_VIEW_STATUS_MAPPING_CODES.ALL
        );
    }

    private isCombinedViewContextPersRemoved(combinedView: CombinedView): boolean {
        const items: CombinedViewSelectItem[] = combinedView.selectedItems;
        return !items.find(
            (item: CombinedViewSelectItem) =>
                item.customization.code === combinedView.customize.selectedCustomization.code &&
                item.variation.code ===
                    (combinedView.customize.selectedVariations as CustomizationVariation).code
        );
    }

    private getCustomizationsCategoryFilter(
        name = '',
        currentSize: number,
        currentPage: number
    ): CustomizationsFilter {
        const categoryFilter: CustomizationsFilter = {
            currentSize,
            currentPage,
            name,
            statuses: this.getDefaultStatus().modelStatuses,
            catalogs: this.catalogFilter,
            pageId: undefined,
            pageCatalogId: undefined
        };
        if (
            this.customizationPageFilter ===
            PERSONALIZATION_CUSTOMIZATION_PAGE_FILTER.ONLY_THIS_PAGE
        ) {
            categoryFilter.pageId = this.contextService.getSeData().pageId;
            categoryFilter.pageCatalogId =
                this.contextService.getSeData().seExperienceData.pageContext.catalogId;
        }
        return categoryFilter;
    }

    private constructCombinedViewSelectItem = (
        customization,
        variation
    ): CombinedViewSelectItem => ({
        id: customization.code + '-' + variation.code,
        label: customization.name + ' > ' + variation.name,
        customization: {
            code: customization.code,
            name: customization.name,
            rank: customization.rank,
            catalog: customization.catalog,
            catalogVersion: customization.catalogVersion
        },
        variation: {
            code: variation.code,
            name: variation.name,
            catalog: variation.catalog,
            catalogVersion: variation.catalogVersion
        }
    });

    private onCancel(): Promise<void> {
        this.modalManager.close(null);
        return Promise.resolve();
    }

    private onSave(): Promise<void> {
        const combinedView = this.contextService.getCombinedView();
        combinedView.selectedItems = this.selectedCombinedViewItems;

        if (
            combinedView.enabled &&
            combinedView.customize.selectedVariations !== null &&
            this.isCombinedViewContextPersRemoved(combinedView)
        ) {
            combinedView.customize.selectedCustomization = null;
            combinedView.customize.selectedVariations = null;
            combinedView.customize.selectedComponents = null;
        }

        this.contextService.setCombinedView(combinedView);
        this.modalManager.close(null);
        return Promise.resolve();
    }
}
