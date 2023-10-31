/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { PersonalizationsmarteditCommonsComponentsModule } from 'personalizationcommons';
import { L10nPipeModule, SeTranslationModule, TooltipModule } from 'smarteditcommons';
import { ShowComponentInfoListComponent } from './ShowComponentInfoListComponent';

@NgModule({
    imports: [
        CommonModule,
        SeTranslationModule.forChild(),
        PersonalizationsmarteditCommonsComponentsModule, // TODO: wrap into separate modules in commons
        TooltipModule,
        L10nPipeModule
    ],
    declarations: [ShowComponentInfoListComponent],
    entryComponents: [ShowComponentInfoListComponent],
    exports: [ShowComponentInfoListComponent]
})
export class ShowComponentInfoListModule {}
