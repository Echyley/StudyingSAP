/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { DynamicPagedListModule, L10nPipeModule, SeTranslationModule } from 'smarteditcommons';
import { ToolbarModule } from 'smarteditcontainer';
import { TrashedPageListComponent } from './TrashedPageListComponent';

@NgModule({
    imports: [
        CommonModule,
        SeTranslationModule.forChild(),
        FormsModule,
        ToolbarModule,
        L10nPipeModule,
        DynamicPagedListModule
    ],
    declarations: [TrashedPageListComponent]
})
export class TrashedPageListModule {}
