/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, Inject } from '@angular/core';
import { ITEM_COMPONENT_DATA_TOKEN, ItemComponentData, SelectItem } from 'smarteditcommons';

interface SingleActiveCatalogAwareItem extends SelectItem {
    code: string;
    catalogId: string;
}

@Component({
    selector: 'se-single-active-catalog-aware-item-selector-item-renderer',
    template: `
        <span class="se-single-catalog-item">
            <span
                class="se-single-catalog-item__label"
                title="{{ data.item.label | seL10n | async }}"
                >{{ data.item.label | seL10n | async }}</span
            >
            <span
                class="se-single-catalog-item__code"
                title="{{ data.item.code | seL10n | async }}">
                {{ data.item.code }}</span
            >
            <span
                class="se-single-catalog-item__version"
                title="{{ data.item.catalogId | seL10n | async }}"
                >{{ data.item.catalogId }}</span
            >
        </span>
    `
})
export class SingeActiveCatalogAwareItemSelectorItemRendererComponent {
    constructor(
        @Inject(ITEM_COMPONENT_DATA_TOKEN)
        public data: ItemComponentData<SingleActiveCatalogAwareItem>
    ) {}
}
