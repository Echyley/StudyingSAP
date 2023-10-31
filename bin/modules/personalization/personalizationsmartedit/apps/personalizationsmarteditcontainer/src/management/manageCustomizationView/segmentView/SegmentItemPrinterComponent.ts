/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { ItemComponentData, ITEM_COMPONENT_DATA_TOKEN } from 'smarteditcommons';

/**
 * Component represents the default Item of Select Component.
 * Displayed by Item Printer Component.
 */
@Component({
    selector: 'segment-item-printer',
    changeDetection: ChangeDetectionStrategy.OnPush,
    template: `
        <div>
            <div class="perso__item-name">{{ data.item.code }}</div>
            <div
                class="perso-wrap-ellipsis perso__wrapper-description"
                [attr.title]="data.item.description"
            >
                {{ data.item.description }}
            </div>
        </div>
    `
})
export class SegmentItemPrinterComponent {
    constructor(@Inject(ITEM_COMPONENT_DATA_TOKEN) public data: ItemComponentData) {}
}
