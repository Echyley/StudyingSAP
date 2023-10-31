/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, ViewEncapsulation, ChangeDetectionStrategy, Inject } from '@angular/core';
import { SystemEventService, ItemComponentData, ITEM_COMPONENT_DATA_TOKEN } from 'smarteditcommons';
import { CMSLinkItem } from '../../../genericEditor/singleActiveCatalogAwareSelector';
import { ON_EDIT_NESTED_COMPONENT_EVENT } from '../CmsComponentConstants';

export interface CMSLinkDropdownItem extends CMSLinkItem {
    id: string;
}

@Component({
    selector: 'se-cms-dropdown-item',
    templateUrl: './CmsDropdownItemComponent.html',
    styleUrls: ['./CmsDropdownItemComponent.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    encapsulation: ViewEncapsulation.None
})
export class CmsDropdownItemComponent {
    public item: CMSLinkDropdownItem;
    public isSelected: boolean;
    public qualifier: string;

    constructor(
        @Inject(ITEM_COMPONENT_DATA_TOKEN) data: ItemComponentData<CMSLinkDropdownItem>,
        private systemEventService: SystemEventService
    ) {
        ({
            item: this.item,
            select: { id: this.qualifier },
            selected: this.isSelected
        } = data);
    }

    public onClick(event: Event): void {
        event.stopPropagation();

        if (this.isSelected) {
            this.systemEventService.publishAsync(ON_EDIT_NESTED_COMPONENT_EVENT, {
                qualifier: this.qualifier,
                item: this.item
            });
        }
    }
}
