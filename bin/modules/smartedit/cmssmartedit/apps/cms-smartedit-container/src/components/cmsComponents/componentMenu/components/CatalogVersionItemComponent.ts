/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, Inject } from '@angular/core';
import { ItemComponentData, ITEM_COMPONENT_DATA_TOKEN, CatalogVersion } from 'smarteditcommons';

@Component({
    selector: 'se-catalog-version-item',
    template: `
        <div class="se-component-menu__select-local">
            <span
                class="sap-icon--globe se-component-menu__select-globe"
                *ngIf="item.isCurrentCatalog"></span>
            <span class="se-component-menu__select-text"
                >{{ item.catalogName | seL10n | async }} - {{ item.catalogVersionId }}</span
            >
        </div>
    `
})
export class CatalogVersionItemComponent {
    public item: CatalogVersion;

    constructor(@Inject(ITEM_COMPONENT_DATA_TOKEN) data: ItemComponentData<CatalogVersion>) {
        this.item = data.item;
    }
}
