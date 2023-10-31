/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, OnInit, Inject } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import {
    COMBINED_VIEW_TOOLBAR_ITEM_KEY,
    CombinedView,
    CombinedViewSelectItem,
    CustomizationVariation,
    CustomizationVariationComponents,
    PersonalizationsmarteditContextUtils,
    PersonalizationsmarteditMessageHandler,
    PersonalizationsmarteditUtils
} from 'personalizationcommons';
import {
    CrossFrameEventService,
    IPermissionService,
    SHOW_TOOLBAR_ITEM_CONTEXT,
    ToolbarItemInternal,
    TOOLBAR_ITEM
} from 'smarteditcommons';
import {
    PersonalizationsmarteditContextService,
    PersonalizationsmarteditPreviewService,
    PersonalizationsmarteditRestService
} from '../service/';
import { CombinedViewCommonsService } from './CombinedViewCommonsService';
@Component({
    selector: 'combined-view-menu',
    templateUrl: './CombinedViewMenuComponent.html'
})
export class CombinedViewMenuComponent implements OnInit {
    public combinedView: CombinedView;
    public selectedItems: CombinedViewSelectItem[];
    public isCombinedViewConfigured: boolean;

    constructor(
        private translateService: TranslateService,
        private contextService: PersonalizationsmarteditContextService,
        private messageHandler: PersonalizationsmarteditMessageHandler,
        private restService: PersonalizationsmarteditRestService,
        private contextUtils: PersonalizationsmarteditContextUtils,
        private personalizationsmarteditUtils: PersonalizationsmarteditUtils,
        private previewService: PersonalizationsmarteditPreviewService,
        private combinedViewCommonsService: CombinedViewCommonsService,
        private crossFrameEventService: CrossFrameEventService,
        private permissionService: IPermissionService,
        @Inject(TOOLBAR_ITEM) private toolbarItem: ToolbarItemInternal
    ) {}

    ngOnInit(): void {
        this.combinedView = this.contextService.getCombinedView();
        this.selectedItems = this.combinedView.selectedItems || [];
        this.isCombinedViewConfigured = this.selectedItems.length !== 0;
    }

    public combinedViewClick(): void {
        this.contextUtils.clearCustomizeContextAndReloadPreview(
            this.previewService,
            this.contextService
        );
        this.combinedViewCommonsService.openManagerAction();
        this.toolbarItem.isOpen = false;
    }

    public async getAndSetComponentsForElement(
        customizationId: string,
        variationId: string,
        catalog: string,
        catalogVersion: string
    ): Promise<void> {
        try {
            const response: CustomizationVariationComponents =
                await this.restService.getComponentsIdsForVariation(
                    customizationId,
                    variationId,
                    catalog,
                    catalogVersion
                );
            const combinedView = this.contextService.getCombinedView();
            combinedView.customize.selectedComponents = response.components;
            this.contextService.setCombinedView(combinedView);
        } catch (e) {
            this.messageHandler.sendError(
                this.translateService.instant('personalization.error.gettingcomponentsforvariation')
            );
        }
    }

    public async itemClick(item: CombinedViewSelectItem): Promise<void> {
        const combinedView = this.contextService.getCombinedView();
        if (!combinedView.enabled) {
            return;
        }

        this.selectedItems.forEach((elem: CombinedViewSelectItem) => {
            elem.highlighted = false;
        });
        item.highlighted = true;

        combinedView.customize.selectedCustomization = item.customization;
        combinedView.customize.selectedVariations = item.variation;
        this.contextService.setCombinedView(combinedView);
        const roleGranted = await this.permissionService.isPermitted([
            {
                names: ['se.edit.page']
            }
        ]);
        if (roleGranted) {
            this.getAndSetComponentsForElement(
                item.customization.code,
                item.variation.code,
                item.customization.catalog,
                item.customization.catalogVersion
            );
        }
        this.combinedViewCommonsService.updatePreview(
            this.personalizationsmarteditUtils.getVariationKey(item.customization.code, [
                item.variation
            ])
        );
        this.crossFrameEventService.publish(
            SHOW_TOOLBAR_ITEM_CONTEXT,
            COMBINED_VIEW_TOOLBAR_ITEM_KEY
        );
    }

    public getClassForElement(index: number): string {
        return this.personalizationsmarteditUtils.getClassForElement(index);
    }

    public getLetterForElement(index: number): string {
        return this.personalizationsmarteditUtils.getLetterForElement(index);
    }

    public isItemFromCurrentCatalog(itemVariation: CustomizationVariation): boolean {
        return this.combinedViewCommonsService.isItemFromCurrentCatalog(itemVariation);
    }

    public clearAllCombinedViewClick(): void {
        this.selectedItems = [];
        this.combinedView.selectedItems = [];
        this.combinedView.enabled = false;
        this.contextService.setCombinedView(this.combinedView);
        this.combinedViewCommonsService.combinedViewEnabledEvent(this.combinedView.enabled);
    }
}
