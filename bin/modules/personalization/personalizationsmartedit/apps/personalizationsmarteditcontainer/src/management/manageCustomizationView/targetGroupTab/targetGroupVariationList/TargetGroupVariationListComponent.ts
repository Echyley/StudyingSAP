/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, Input, SimpleChanges } from '@angular/core';
import {
    Customization,
    CustomizationVariation,
    PersonalizationsmarteditUtils,
    Trigger
} from 'personalizationcommons';
import { IDropdownMenuItem } from 'smarteditcommons';
import { CustomizationViewService } from '../../CustomizationViewService';
import { TriggerService } from '../../TriggerService';
import {
    EditVariationItemComponent,
    MoveVariationDownItemComponent,
    MoveVariationUpItemComponent,
    RemoveVariationItemComponent,
    ToggleVariationActiveItemComponent
} from '../targetGroupDropdownItem';

export interface CustomizationVariationListItem extends CustomizationVariation {
    /** "(Enabled)" or "(Disabled)" */
    enablementText: string;
    /** Css Class to style based on Enabled or Disabled state. */
    activityState: string;
    /** True - "Applies to All Users"; False - Segments are displayed. */
    isDefault: boolean;
}

@Component({
    selector: 'perso-target-group-variation-list',
    templateUrl: './TargetGroupVariationListComponent.html'
})
export class TargetGroupVariationListComponent {
    @Input() variations: CustomizationVariation[];
    @Input() customization: Customization;

    /** List of actions for a given Variation. Such as: Edit, Disable, Move Up, Move Down, Remove. */
    public readonly dropdownItems: IDropdownMenuItem[];
    /** Extended variations to be rendered. */
    public variationsForView: CustomizationVariationListItem[];

    constructor(
        private customizationViewService: CustomizationViewService,
        private triggerService: TriggerService,
        private persoUtils: PersonalizationsmarteditUtils
    ) {
        this.dropdownItems = [
            { component: EditVariationItemComponent },
            { component: ToggleVariationActiveItemComponent },
            { component: MoveVariationUpItemComponent },
            { component: MoveVariationDownItemComponent },
            { component: RemoveVariationItemComponent }
        ];

        this.variationsForView = [];
    }

    ngOnChanges(changes: SimpleChanges): void {
        const variationsChange = changes.variations;

        if (variationsChange) {
            const variations: CustomizationVariation[] = variationsChange.currentValue;
            this.variationsForView = variations.map((variation) => {
                const enablementText = this.getEnablementTextForVariation(variation);
                const activityState = this.getActivityStateForVariation(
                    this.customization,
                    variation
                );
                const isDefault = this.isDefaultTrigger(variation.triggers);
                return {
                    ...variation,
                    enablementText,
                    activityState,
                    isDefault
                };
            });
        }
    }

    public editVariation(variation: CustomizationVariation): void {
        this.customizationViewService.editVariationAction(variation);
    }

    private isDefaultTrigger(triggers: Trigger[]): boolean {
        return this.triggerService.isDefault(triggers);
    }

    private getActivityStateForVariation(
        customization: Customization,
        variation: CustomizationVariation
    ): string {
        return this.persoUtils.getActivityStateForVariation(customization, variation);
    }

    private getEnablementTextForVariation(variation: CustomizationVariation): string {
        const enablementText = this.persoUtils.getEnablementTextForVariation(
            variation,
            'personalization.modal.customizationvariationmanagement.targetgrouptab'
        );
        return `(${enablementText})`;
    }
}
