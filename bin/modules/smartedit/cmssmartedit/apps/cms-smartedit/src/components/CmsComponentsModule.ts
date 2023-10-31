/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

import './contextualSlotDropdown.scss';
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ButtonModule } from '@fundamental-ngx/core';
import { PopupOverlayModule, TooltipModule, TranslationModule } from 'smarteditcommons';
import { ExternalComponentDecoratorComponent } from './externalComponent';
import {
    SlotDisabledComponent,
    ExternalSlotDisabledDecoratorComponent,
    SharedSlotDisabledDecoratorComponent
} from './slotSharedDisabled';
import {
    HiddenComponentMenuComponent,
    SlotVisibilityButtonComponent,
    SlotVisibilityComponent
} from './slotVisibility';
import { SyncIndicatorDecorator } from './synchronize';

@NgModule({
    imports: [
        CommonModule,
        TooltipModule,
        TranslationModule.forChild(),
        PopupOverlayModule,
        ButtonModule
    ],
    declarations: [
        ExternalComponentDecoratorComponent,
        SlotDisabledComponent,
        ExternalSlotDisabledDecoratorComponent,
        SharedSlotDisabledDecoratorComponent,
        HiddenComponentMenuComponent,
        SlotVisibilityComponent,
        SlotVisibilityButtonComponent,
        SyncIndicatorDecorator
    ],
    entryComponents: [
        ExternalComponentDecoratorComponent,
        SlotDisabledComponent,
        ExternalSlotDisabledDecoratorComponent,
        SharedSlotDisabledDecoratorComponent,
        HiddenComponentMenuComponent,
        SlotVisibilityComponent,
        SlotVisibilityButtonComponent,
        SyncIndicatorDecorator
    ]
})
export class CmsComponentsModule {}
