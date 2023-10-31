/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { DROPDOWN_MENU_ITEM_DATA, IDropdownMenuItemData } from 'smarteditcommons';
import { CustomizationViewService } from '../../CustomizationViewService';
import { TargetGroupDropdownItemSelectedItem } from './types';

@Component({
    selector: 'perso-edit-variation-item',
    template: `
        <a
            (click)="editVariation()"
            class="se-dropdown-item fd-list__link"
            translate="personalization.modal.customizationvariationmanagement.targetgrouptab.variation.options.edit"
        ></a>
    `,
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class EditVariationItemComponent {
    private selectedItem: TargetGroupDropdownItemSelectedItem;

    constructor(
        @Inject(DROPDOWN_MENU_ITEM_DATA) dropdownMenuData: IDropdownMenuItemData,
        private customizationViewService: CustomizationViewService
    ) {
        ({ selectedItem: this.selectedItem } = dropdownMenuData);
    }

    public editVariation(): void {
        this.customizationViewService.editVariationAction(this.selectedItem.variation);
    }
}
