/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { TranslationModule } from '@smart/utils';

import { SliderPanelComponent } from './SliderPanelComponent';
import { SliderPanelServiceFactory } from './SliderPanelServiceFactory';

@NgModule({
    imports: [CommonModule, TranslationModule.forChild()],
    declarations: [SliderPanelComponent],
    entryComponents: [SliderPanelComponent],
    providers: [SliderPanelServiceFactory],
    exports: [SliderPanelComponent]
})
export class SliderPanelModule {}
