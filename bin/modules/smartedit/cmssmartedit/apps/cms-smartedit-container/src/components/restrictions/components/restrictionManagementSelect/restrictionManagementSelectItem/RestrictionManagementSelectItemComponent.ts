/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { ItemComponentData, ITEM_COMPONENT_DATA_TOKEN } from 'smarteditcommons';

@Component({
    selector: 'se-restriction-management-select-item',
    changeDetection: ChangeDetectionStrategy.OnPush,
    template: `<span class="se-restriction-management-item-name">{{
        data.item.name | seL10n | async
    }}</span>`
})
export class RestrictionManagementSelectItemComponent {
    constructor(@Inject(ITEM_COMPONENT_DATA_TOKEN) public data: ItemComponentData) {}
}
