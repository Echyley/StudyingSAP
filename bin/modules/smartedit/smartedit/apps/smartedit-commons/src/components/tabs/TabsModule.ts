/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ListModule } from '@fundamental-ngx/core/list';
import { TabsModule as FundamentalTabsModule } from '@fundamental-ngx/core/tabs';
import { SelectModule, TranslationModule } from '@smart/utils';
import { TooltipModule } from '../tooltip/TooltipModule';
import { TabComponent } from './TabComponent';
import { TabsComponent } from './TabsComponent';

@NgModule({
    imports: [
        CommonModule,
        SelectModule,
        TooltipModule,
        FundamentalTabsModule,
        ListModule,
        TranslationModule.forChild()
    ],
    declarations: [TabsComponent, TabComponent],
    entryComponents: [TabsComponent, TabComponent],
    exports: [TabsComponent, TabComponent]
})
export class TabsModule {}
