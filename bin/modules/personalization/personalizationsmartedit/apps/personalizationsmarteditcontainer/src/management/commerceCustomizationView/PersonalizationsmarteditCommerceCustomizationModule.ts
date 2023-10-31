/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { PersonalizationsmarteditCommonsComponentsModule } from 'personalizationcommons';
import { PersonalizationsmarteditCommerceCustomizationView } from 'personalizationsmarteditcontainer/management/commerceCustomizationView/PersonalizationsmarteditCommerceCustomizationViewService';
import { TranslationModule, SelectModule } from 'smarteditcommons';
import { ActionsDataFactory } from './ActionsDataFactory';
import { CommerceCustomizationViewComponent } from './CommerceCustomizationViewComponent';

@NgModule({
    imports: [
        TranslationModule.forChild(),
        CommonModule,
        FormsModule,
        SelectModule,
        PersonalizationsmarteditCommonsComponentsModule
    ],
    declarations: [CommerceCustomizationViewComponent],
    entryComponents: [CommerceCustomizationViewComponent],
    providers: [PersonalizationsmarteditCommerceCustomizationView, ActionsDataFactory]
})
export class PersonalizationsmarteditCommerceCustomizationModule {}
