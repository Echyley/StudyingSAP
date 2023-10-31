/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 * This is for combined view ranking
 */
import { Component, OnInit, Inject } from '@angular/core';
import { PersonalizationsmarteditUtils } from 'personalizationcommons';
import { ContextualMenuItemData, CONTEXTUAL_MENU_ITEM_DATA, YJQUERY_TOKEN } from 'smarteditcommons';
import {
    PersonalizationsmarteditComponentHandlerService,
    PersonalizationsmarteditContextualMenuService,
    PersonalizationsmarteditContextService
} from '../../service';
@Component({
    selector: 'perso-show-action-list',
    templateUrl: './ShowActionListComponentTemplate.html'
})
export class ShowActionListComponent implements OnInit {
    public isContextualMenuShowActionListEnabled: boolean;
    public selectedItems: any;
    public containerSourceId: string;
    public containerId: any;
    private containerIdExists: boolean;
    private $element: JQuery<HTMLElement>;
    private COMPONENT_SELECTOR = 'showactionlistbutton';

    constructor(
        @Inject(CONTEXTUAL_MENU_ITEM_DATA) private contextualMenuItem: ContextualMenuItemData,
        private persoContextService: PersonalizationsmarteditContextService,
        private persoUtils: PersonalizationsmarteditUtils,
        private persoComponentHandlerService: PersonalizationsmarteditComponentHandlerService,
        private persoContextualMenuService: PersonalizationsmarteditContextualMenuService,
        @Inject(YJQUERY_TOKEN) yjQuery: JQueryStatic
    ) {
        this.isContextualMenuShowActionListEnabled = false;
        this.$element = yjQuery(`[class~="${this.COMPONENT_SELECTOR}"]`);
    }

    ngOnInit(): void {
        this.selectedItems = this.persoContextService.getCombinedView().selectedItems;
        this.containerId = this.persoComponentHandlerService.getParentContainerIdForComponent(
            this.$element
        );
        this.containerIdExists = !!this.containerId;
        this.containerSourceId = this.containerIdExists
            ? this.persoComponentHandlerService.getContainerSourceIdForContainerId(this.containerId)
            : '';
        this.isContextualMenuShowActionListEnabled =
            this.persoContextualMenuService.isContextualMenuShowActionListEnabled(
                this.contextualMenuItem
            );
        (this.selectedItems || []).forEach((element: any, index: any) => {
            this.initItem(element);
        });
    }

    public getLetterForElement(index: number): string {
        return this.persoUtils.getLetterForElement(index);
    }

    public getClassForElement(index: number): string {
        return this.persoUtils.getClassForElement(index);
    }

    public initItem(item: any): void {
        item.visible = false;
        (item.variation.actions || []).forEach((elem: any) => {
            if (elem.containerId && elem.containerId === this.containerSourceId) {
                item.visible = true;
            }
        });
        this.persoUtils.getAndSetCatalogVersionNameL10N(item.variation);
    }

    public isCustomizationFromCurrentCatalog(customization: string): boolean {
        return this.persoUtils.isItemFromCurrentCatalog(
            customization,
            this.persoContextService.getSeData()
        );
    }
}
