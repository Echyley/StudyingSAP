/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ButtonModule } from '@fundamental-ngx/core';
import { TranslateModule } from '@ngx-translate/core';
import {
    DropdownMenuModule,
    EditableListModule,
    L10nPipeModule,
    SelectModule,
    SliderPanelModule
} from 'smarteditcommons';

import {
    ItemSelectorPanelComponent,
    CatalogAwareSelectorComponent,
    CategoryNodeComponent,
    CategorySelectorItemComponent,
    MultiCategorySelectorComponent,
    ProductSelectorItemComponent,
    ProductNodeComponent,
    MultiProductSelectorComponent
} from './components';
import { CatalogInformationService } from './services';

@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        DropdownMenuModule,
        SliderPanelModule,
        SelectModule,
        TranslateModule.forChild(),
        L10nPipeModule,
        EditableListModule,
        ButtonModule
    ],
    providers: [CatalogInformationService],
    declarations: [
        ItemSelectorPanelComponent,
        ProductNodeComponent,
        CategoryNodeComponent,
        ProductSelectorItemComponent,
        CategorySelectorItemComponent,
        CatalogAwareSelectorComponent,
        MultiProductSelectorComponent,
        MultiCategorySelectorComponent
    ],
    entryComponents: [
        ProductNodeComponent,
        CategoryNodeComponent,
        ProductSelectorItemComponent,
        CategorySelectorItemComponent,
        MultiProductSelectorComponent,
        MultiCategorySelectorComponent
    ]
})
export class CatalogModule {}
