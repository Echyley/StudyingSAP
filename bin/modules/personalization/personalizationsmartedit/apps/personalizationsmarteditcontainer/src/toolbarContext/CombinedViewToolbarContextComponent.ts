/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, OnInit, DoCheck } from '@angular/core';
import * as lodash from 'lodash';
import {
    PersonalizationsmarteditContextUtils,
    COMBINED_VIEW_TOOLBAR_ITEM_KEY,
    CustomizationVariation
} from 'personalizationcommons';
import { CombinedViewCommonsService } from 'personalizationsmarteditcontainer/combinedView/CombinedViewCommonsService';
import { PersonalizationsmarteditContextService } from 'personalizationsmarteditcontainer/service/PersonalizationsmarteditContextServiceOuter';
import {
    CrossFrameEventService,
    SHOW_TOOLBAR_ITEM_CONTEXT,
    HIDE_TOOLBAR_ITEM_CONTEXT
} from 'smarteditcommons';
@Component({
    selector: 'combined-view-toolbar-context',
    templateUrl: './SharedToolbarContextComponent.html'
})
export class CombinedViewToolbarContextComponent implements OnInit, DoCheck {
    public visible: boolean;
    public title: string;
    public subtitle: string;

    private selectedCustomization: any;
    private oldSelectedCustomization: any;
    private oldSelectedCustomizationEnabled: boolean;

    constructor(
        private combinedViewCommonsService: CombinedViewCommonsService,
        private personalizationsmarteditContextService: PersonalizationsmarteditContextService,
        private personalizationsmarteditContextUtils: PersonalizationsmarteditContextUtils,
        private crossFrameEventService: CrossFrameEventService
    ) {}

    ngOnInit(): void {
        const combinedView = this.personalizationsmarteditContextService.getCombinedView();
        this.visible = false;
        this.selectedCustomization = lodash.cloneDeep(combinedView.customize.selectedCustomization);

        if (this.selectedCustomization) {
            this.title = combinedView.customize.selectedCustomization.name;
            this.subtitle = (
                combinedView.customize.selectedVariations as CustomizationVariation
            ).name;
            this.visible = true;
        }

        this.oldSelectedCustomization = combinedView.customize.selectedCustomization;
        this.oldSelectedCustomizationEnabled = combinedView.enabled;
    }

    ngDoCheck(): void {
        const combinedView = this.personalizationsmarteditContextService.getCombinedView();
        const selectedCustomization = combinedView.customize.selectedCustomization;
        if (selectedCustomization && selectedCustomization !== this.oldSelectedCustomization) {
            this.oldSelectedCustomization = selectedCustomization;
            this.title = selectedCustomization.name;
            this.subtitle = (
                combinedView.customize.selectedVariations as CustomizationVariation
            ).name;
            this.visible = true;
            this.crossFrameEventService.publish(
                SHOW_TOOLBAR_ITEM_CONTEXT,
                COMBINED_VIEW_TOOLBAR_ITEM_KEY
            );
        } else if (!selectedCustomization) {
            this.visible = false;
            this.crossFrameEventService.publish(
                HIDE_TOOLBAR_ITEM_CONTEXT,
                COMBINED_VIEW_TOOLBAR_ITEM_KEY
            );
        }

        const selectedCustomizationEnabled = combinedView.enabled;
        if (
            selectedCustomizationEnabled &&
            selectedCustomizationEnabled !== this.oldSelectedCustomizationEnabled
        ) {
            this.oldSelectedCustomizationEnabled = selectedCustomizationEnabled;
            this.personalizationsmarteditContextUtils.clearCombinedViewCustomizeContext(
                this.personalizationsmarteditContextService
            );
        }
    }

    public clear(): void {
        this.personalizationsmarteditContextUtils.clearCombinedViewCustomizeContext(
            this.personalizationsmarteditContextService
        );
        const combinedView = this.personalizationsmarteditContextService.getCombinedView();
        const variations: any[] = [];
        (combinedView.selectedItems || []).forEach((item: any) => {
            variations.push({
                customizationCode: item.customization.code,
                variationCode: item.variation.code,
                catalog: item.variation.catalog,
                catalogVersion: item.variation.catalogVersion
            });
        });
        this.combinedViewCommonsService.updatePreview(variations);
        this.crossFrameEventService.publish(
            HIDE_TOOLBAR_ITEM_CONTEXT,
            COMBINED_VIEW_TOOLBAR_ITEM_KEY
        );
    }
}
