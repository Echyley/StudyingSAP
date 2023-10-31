/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ChangeDetectionStrategy, Component, forwardRef, Inject } from '@angular/core';
import { DROPDOWN_MENU_ITEM_DATA, IDropdownMenuItemData } from 'smarteditcommons';
import { TargetGroupTabComponent } from '../TargetGroupTabComponent';
import { TargetGroupDropdownItemSelectedItem } from './types';

@Component({
    selector: 'perso-remove-variation-item',
    template: `
        <a
            (click)="remove()"
            class="se-dropdown-item fd-list__link fd-has-color-status-3"
            translate="personalization.modal.customizationvariationmanagement.targetgrouptab.variation.options.remove"
        ></a>
    `,
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class RemoveVariationItemComponent {
    private selectedItem: TargetGroupDropdownItemSelectedItem;

    constructor(
        @Inject(DROPDOWN_MENU_ITEM_DATA) dropdownMenuItemData: IDropdownMenuItemData,
        @Inject(forwardRef(() => TargetGroupTabComponent))
        private targetGroupTabComponent: TargetGroupTabComponent
    ) {
        ({ selectedItem: this.selectedItem } = dropdownMenuItemData);
    }

    public remove(): void {
        this.targetGroupTabComponent.removeVariation(this.selectedItem.variation);
    }
}
