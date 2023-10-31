/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { cloneDeep, isArray, isObject } from 'lodash';
import {
    CombinedView,
    Customize,
    IPersonalizationsmarteditContextObject,
    Personalization,
    SeData
} from '../dtos';
import {
    IPersonalizationsmarteditContextService,
    IPersonalizationsmarteditPreviewService
} from '../services';

@Injectable()
export class PersonalizationsmarteditContextUtils {
    public getContextObject(): IPersonalizationsmarteditContextObject {
        return {
            personalization: new Personalization(),
            customize: new Customize(),
            combinedView: new CombinedView(),
            seData: new SeData()
        };
    }

    public clearCustomizeContext(contextService: IPersonalizationsmarteditContextService): void {
        const customize = contextService.getCustomize();
        customize.enabled = false;
        customize.selectedCustomization = null;
        customize.selectedVariations = null;
        customize.selectedComponents = null;
        contextService.setCustomize(customize);
    }

    public clearCustomizeContextAndReloadPreview(
        previewService: IPersonalizationsmarteditPreviewService,
        contextService: IPersonalizationsmarteditContextService
    ): void {
        const selectedVariations = cloneDeep(contextService.getCustomize().selectedVariations);
        this.clearCustomizeContext(contextService);
        if (isObject(selectedVariations) && !isArray(selectedVariations)) {
            previewService.removePersonalizationDataFromPreview();
        }
    }

    public clearCombinedViewCustomizeContext(
        contextService: IPersonalizationsmarteditContextService
    ): void {
        const combinedView = contextService.getCombinedView();
        combinedView.customize.enabled = false;
        combinedView.customize.selectedCustomization = null;
        combinedView.customize.selectedVariations = null;
        combinedView.customize.selectedComponents = null;
        if (isArray(combinedView.selectedItems)) {
            combinedView.selectedItems.forEach((item: any) => {
                delete item.highlighted;
            });
        }
        contextService.setCombinedView(combinedView);
    }

    public clearCombinedViewContext(contextService: IPersonalizationsmarteditContextService): void {
        const combinedView = contextService.getCombinedView();
        combinedView.enabled = false;
        combinedView.selectedItems = null;
        contextService.setCombinedView(combinedView);
    }

    public clearCombinedViewContextAndReloadPreview(
        previewService: IPersonalizationsmarteditPreviewService,
        contextService: IPersonalizationsmarteditContextService
    ): void {
        const cvEnabled = cloneDeep(contextService.getCombinedView().enabled);
        const cvSelectedItems = cloneDeep(contextService.getCombinedView().selectedItems);
        this.clearCombinedViewContext(contextService);
        if (cvEnabled && cvSelectedItems) {
            previewService.removePersonalizationDataFromPreview();
        }
    }
}
