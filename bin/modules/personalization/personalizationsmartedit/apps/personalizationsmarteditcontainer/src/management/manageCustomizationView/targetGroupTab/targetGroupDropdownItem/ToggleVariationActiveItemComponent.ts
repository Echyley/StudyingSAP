/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ChangeDetectionStrategy, Component, forwardRef, Inject, OnInit } from '@angular/core';
import { CustomizationVariation, PERSONALIZATION_MODEL_STATUS_CODES } from 'personalizationcommons';
import { DROPDOWN_MENU_ITEM_DATA, IDropdownMenuItemData } from 'smarteditcommons';
import { TargetGroupTabComponent } from '../TargetGroupTabComponent';
import { TargetGroupDropdownItemSelectedItem } from './types';

@Component({
    selector: 'perso-toggle-variation-active-item',
    template: `
        <a (click)="toggle()" class="se-dropdown-item fd-list__link" [translate]="label"></a>
    `,
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ToggleVariationActiveItemComponent implements OnInit {
    public label: string;

    private selectedItem: TargetGroupDropdownItemSelectedItem;

    constructor(
        @Inject(DROPDOWN_MENU_ITEM_DATA) dropdownMenuItemData: IDropdownMenuItemData,
        @Inject(forwardRef(() => TargetGroupTabComponent))
        private targetGroupTabComponent: TargetGroupTabComponent
    ) {
        ({ selectedItem: this.selectedItem } = dropdownMenuItemData);
    }

    ngOnInit(): void {
        this.label = this.selectedItem.variation.enabled
            ? 'personalization.modal.customizationvariationmanagement.targetgrouptab.variation.options.disable'
            : 'personalization.modal.customizationvariationmanagement.targetgrouptab.variation.options.enable';
    }

    public toggle(): void {
        this.toggleVariationActive(this.selectedItem.variation);
    }

    private toggleVariationActive(variation: CustomizationVariation): void {
        const enabled = !variation.enabled;
        const status = enabled
            ? PERSONALIZATION_MODEL_STATUS_CODES.ENABLED
            : PERSONALIZATION_MODEL_STATUS_CODES.DISABLED;
        const newVariation = {
            ...variation,
            enabled,
            status
        };

        this.targetGroupTabComponent.updateVariation(newVariation);
    }
}
