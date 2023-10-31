/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';

import { FormsModule } from '@angular/forms';
import {
    TooltipModule,
    TranslationModule,
    SelectModule,
    InfiniteScrollingModule,
    L10nPipeModule,
    TabsModule,
    ComponentMenuService
} from 'smarteditcommons';
import { ComponentMenuComponent } from './ComponentMenuComponent';
import {
    ComponentItemComponent,
    ComponentTypeComponent,
    ComponentSearchComponent,
    CatalogVersionItemComponent
} from './components';
import { ComponentsTabComponent } from './tabs/ComponentsTabComponent';
import { ComponentTypesTabComponent } from './tabs/ComponentTypesTabComponent';

@NgModule({
    imports: [
        CommonModule,
        TooltipModule,
        TranslationModule.forChild(),
        FormsModule,
        SelectModule,
        InfiniteScrollingModule,
        L10nPipeModule,
        TabsModule
    ],
    providers: [ComponentMenuService],
    declarations: [
        ComponentItemComponent,
        ComponentTypeComponent,
        ComponentSearchComponent,
        CatalogVersionItemComponent,
        ComponentsTabComponent,
        ComponentTypesTabComponent,
        ComponentMenuComponent
    ],
    entryComponents: [
        ComponentItemComponent,
        ComponentTypeComponent,
        ComponentSearchComponent,
        CatalogVersionItemComponent,
        ComponentsTabComponent,
        ComponentTypesTabComponent,
        ComponentMenuComponent
    ]
})
export class ComponentMenuModule {}
