/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { DROPDOWN_MENU_ITEM_DATA, IDropdownMenuItemData } from 'smarteditcommons';
import { CatalogNavigateToSite } from '../services/CatalogNavigateToSite';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    selector: 'se-catalog-hierarchy-node-menu-item',
    template: `
        <a fd-list-link class="se-dropdown-item" (click)="onSiteSelect()">
            {{ name | seL10n | async }}
        </a>
    `
})
export class CatalogHierarchyNodeMenuItemComponent {
    public uid: string;
    public name: string;
    constructor(
        private activateSite: CatalogNavigateToSite,
        @Inject(DROPDOWN_MENU_ITEM_DATA) data: IDropdownMenuItemData
    ) {
        const { name, uid } = data.dropdownItem as any;
        this.name = name;
        this.uid = uid;
    }

    onSiteSelect(): void {
        this.activateSite.navigate(this.uid);
    }
}
