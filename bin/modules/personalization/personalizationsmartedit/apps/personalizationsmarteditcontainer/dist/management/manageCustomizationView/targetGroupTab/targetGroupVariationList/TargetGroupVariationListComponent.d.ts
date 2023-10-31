import { SimpleChanges } from '@angular/core';
import { Customization, CustomizationVariation, PersonalizationsmarteditUtils } from 'personalizationcommons';
import { IDropdownMenuItem } from 'smarteditcommons';
import { CustomizationViewService } from '../../CustomizationViewService';
import { TriggerService } from '../../TriggerService';
export interface CustomizationVariationListItem extends CustomizationVariation {
    enablementText: string;
    activityState: string;
    isDefault: boolean;
}
export declare class TargetGroupVariationListComponent {
    private customizationViewService;
    private triggerService;
    private persoUtils;
    variations: CustomizationVariation[];
    customization: Customization;
    readonly dropdownItems: IDropdownMenuItem[];
    variationsForView: CustomizationVariationListItem[];
    constructor(customizationViewService: CustomizationViewService, triggerService: TriggerService, persoUtils: PersonalizationsmarteditUtils);
    ngOnChanges(changes: SimpleChanges): void;
    editVariation(variation: CustomizationVariation): void;
    private isDefaultTrigger;
    private getActivityStateForVariation;
    private getEnablementTextForVariation;
}
