/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

import { Component, OnInit, DoCheck } from '@angular/core';
import * as lodash from 'lodash';
import {
    PersonalizationsmarteditContextUtils,
    CUSTOMIZE_VIEW_TOOLBAR_ITEM_KEY
} from 'personalizationcommons';
import {
    CrossFrameEventService,
    SHOW_TOOLBAR_ITEM_CONTEXT,
    HIDE_TOOLBAR_ITEM_CONTEXT
} from 'smarteditcommons';
import { PersonalizationsmarteditContextService } from '../service/PersonalizationsmarteditContextServiceOuter';
import { PersonalizationsmarteditPreviewService } from '../service/PersonalizationsmarteditPreviewService';
@Component({
    selector: 'personalizationsmartedit-customize-toolbar-context',
    templateUrl: './SharedToolbarContextComponent.html'
})
export class CustomizeToolbarContextComponent implements OnInit, DoCheck {
    public visible: boolean;
    public title: string;
    public subtitle: string;

    private selectedCustomization: any;
    private selectedVariations: any;
    private oldSelectedCustomization: any;
    private oldSelectedVariations: any;

    constructor(
        protected personalizationsmarteditContextService: PersonalizationsmarteditContextService,
        protected personalizationsmarteditContextUtils: PersonalizationsmarteditContextUtils,
        protected personalizationsmarteditPreviewService: PersonalizationsmarteditPreviewService,
        protected crossFrameEventService: CrossFrameEventService
    ) {}

    ngOnInit(): void {
        this.selectedCustomization = lodash.cloneDeep(
            this.personalizationsmarteditContextService.getCustomize().selectedCustomization
        );
        this.selectedVariations = lodash.cloneDeep(
            this.personalizationsmarteditContextService.getCustomize().selectedVariations
        );

        this.visible = false;

        if (this.selectedCustomization) {
            this.title =
                this.personalizationsmarteditContextService.getCustomize().selectedCustomization.name;
            this.visible = true;
            if (!lodash.isArray(this.selectedVariations)) {
                this.subtitle = this.selectedVariations.name;
            }
        }
        this.oldSelectedCustomization =
            this.personalizationsmarteditContextService.getCustomize().selectedCustomization;
        this.oldSelectedVariations =
            this.personalizationsmarteditContextService.getCustomize().selectedVariations;
    }

    ngDoCheck(): void {
        const selectedCustomization =
            this.personalizationsmarteditContextService.getCustomize().selectedCustomization;
        if (selectedCustomization && selectedCustomization !== this.oldSelectedCustomization) {
            this.oldSelectedCustomization = selectedCustomization;
            this.title = selectedCustomization.name;
            this.visible = true;
            this.crossFrameEventService.publish(
                SHOW_TOOLBAR_ITEM_CONTEXT,
                CUSTOMIZE_VIEW_TOOLBAR_ITEM_KEY
            );
        } else if (!selectedCustomization) {
            this.visible = false;
            this.crossFrameEventService.publish(
                HIDE_TOOLBAR_ITEM_CONTEXT,
                CUSTOMIZE_VIEW_TOOLBAR_ITEM_KEY
            );
        }

        const selectedVariations =
            this.personalizationsmarteditContextService.getCustomize().selectedVariations;
        if (selectedVariations && selectedVariations !== this.oldSelectedVariations) {
            this.oldSelectedVariations = selectedVariations;
            if (!lodash.isArray(selectedVariations)) {
                this.subtitle = selectedVariations.name;
            }
        }
    }

    public clear(): void {
        this.personalizationsmarteditContextUtils.clearCustomizeContextAndReloadPreview(
            this.personalizationsmarteditPreviewService,
            this.personalizationsmarteditContextService
        );
        this.crossFrameEventService.publish(
            HIDE_TOOLBAR_ITEM_CONTEXT,
            CUSTOMIZE_VIEW_TOOLBAR_ITEM_KEY
        );
    }
}
