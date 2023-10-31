/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { CatalogFilterDropdownComponent } from 'personalizationsmarteditcontainer/commonComponents/CatalogFilterDropdownComponent';
import { PageFilterDropdownComponent } from 'personalizationsmarteditcontainer/commonComponents/PageFilterDropdownComponent';
import { StatusFilterDropdownComponent } from 'personalizationsmarteditcontainer/commonComponents/StatusFilterDropdownComponent';
import { SelectModule, TranslationModule } from 'smarteditcommons';
import { CatalogVersionFilterDropdownComponent } from './CatalogVersionFilterDropdownComponent';
import { CatalogVersionFilterItemPrinterComponent } from './CatalogVersionFilterItemPrinterComponent';
import { HasMulticatalogComponent } from './HasMulticatalogComponent';

/**
 * @ngdoc overview
 * @name PersonalizationsmarteditSharedComponentsModule
 */

@NgModule({
    imports: [CommonModule, TranslationModule.forChild(), SelectModule],
    declarations: [
        HasMulticatalogComponent,
        CatalogVersionFilterDropdownComponent,
        CatalogVersionFilterItemPrinterComponent,
        StatusFilterDropdownComponent,
        PageFilterDropdownComponent,
        CatalogFilterDropdownComponent
    ],
    entryComponents: [
        HasMulticatalogComponent,
        CatalogVersionFilterDropdownComponent,
        CatalogVersionFilterItemPrinterComponent,
        StatusFilterDropdownComponent,
        PageFilterDropdownComponent,
        CatalogFilterDropdownComponent
    ],
    exports: [
        HasMulticatalogComponent,
        CatalogVersionFilterDropdownComponent,
        CatalogVersionFilterItemPrinterComponent,
        StatusFilterDropdownComponent,
        PageFilterDropdownComponent,
        CatalogFilterDropdownComponent
    ],
    providers: []
})
export class PersonalizationsmarteditSharedComponentsModule {}
