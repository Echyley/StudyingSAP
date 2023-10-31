/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ButtonModule } from '@fundamental-ngx/core';
import {
    L10nPipeModule,
    MessageModule,
    SpinnerModule,
    TooltipModule,
    TranslationModule
} from 'smarteditcommons';

import { SynchronizationPanelComponent, SynchronizationPanelItemComponent } from './components';

@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        TranslationModule.forChild(),
        TooltipModule,
        MessageModule,
        SpinnerModule,
        ButtonModule,
        L10nPipeModule
    ],
    declarations: [SynchronizationPanelComponent, SynchronizationPanelItemComponent],
    entryComponents: [SynchronizationPanelComponent],
    exports: [SynchronizationPanelComponent]
})
export class SynchronizationPanelModule {}
