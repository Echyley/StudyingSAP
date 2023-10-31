/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { TranslationModule } from 'smarteditcommons';
import { ScrollZoneBodyComponent } from './scrollZoneBody';
import { ScrollZoneBodyContainerComponent } from './scrollZoneBodyContainer';

import { ScrollZoneComponent } from './ScrollZoneComponent';

@NgModule({
    imports: [CommonModule, TranslationModule.forChild()],
    providers: [],
    declarations: [ScrollZoneComponent, ScrollZoneBodyContainerComponent, ScrollZoneBodyComponent],
    entryComponents: [ScrollZoneComponent],
    exports: [ScrollZoneBodyComponent, ScrollZoneComponent]
})
export class ScrollZoneModule {}
