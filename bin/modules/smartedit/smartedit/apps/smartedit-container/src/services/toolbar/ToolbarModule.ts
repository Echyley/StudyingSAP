/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { PopoverModule } from '@fundamental-ngx/core';
import { TranslateModule } from '@ngx-translate/core';

import {
    ClickOutsideModule,
    IToolbarServiceFactory,
    PreventVerticalOverflowModule,
    PropertyPipeModule,
    ResizeObserverModule
} from 'smarteditcommons';
import { TopToolbarsModule } from '../../components/topToolbars';
import {
    ToolbarActionComponent,
    ToolbarActionOutletComponent,
    ToolbarComponent,
    ToolbarItemContextComponent,
    ToolbarSectionItemComponent
} from './components';
import { ToolbarServiceFactory } from './services';

@NgModule({
    imports: [
        TopToolbarsModule,
        CommonModule,
        TranslateModule.forChild(),
        PropertyPipeModule,
        ResizeObserverModule,
        PopoverModule,
        ClickOutsideModule,
        PreventVerticalOverflowModule
    ],
    providers: [
        {
            provide: IToolbarServiceFactory,
            useClass: ToolbarServiceFactory
        }
    ],
    declarations: [
        ToolbarActionComponent,
        ToolbarActionOutletComponent,
        ToolbarComponent,
        ToolbarItemContextComponent,
        ToolbarSectionItemComponent
    ],
    entryComponents: [
        ToolbarActionComponent,
        ToolbarActionOutletComponent,
        ToolbarComponent,
        ToolbarItemContextComponent,
        ToolbarSectionItemComponent
    ],
    exports: [
        ToolbarActionComponent,
        ToolbarActionOutletComponent,
        ToolbarComponent,
        ToolbarItemContextComponent,
        ToolbarSectionItemComponent
    ]
})
export class ToolbarModule {}
