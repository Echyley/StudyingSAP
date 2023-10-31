/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ButtonModule } from '@fundamental-ngx/core';
import {
    InfiniteScrollingModule,
    MoreTextModule,
    PopupOverlayModule,
    TooltipModule,
    TranslationModule
} from 'smarteditcommons';

import {
    PageVersionMenuComponent,
    VersionsSearchComponent,
    VersionItemContextComponent,
    VersionItemComponent,
    VersionItemMenuComponent,
    VersionsPanelComponent
} from './components';
import { ManagePageVersionService, RollbackPageVersionService } from './services';

@NgModule({
    imports: [
        TranslationModule.forChild(),
        CommonModule,
        FormsModule,
        TooltipModule,
        PopupOverlayModule,
        MoreTextModule,
        InfiniteScrollingModule,
        ButtonModule
    ],
    declarations: [
        VersionItemContextComponent,
        VersionItemMenuComponent,
        VersionItemComponent,
        VersionsSearchComponent,
        VersionsPanelComponent,
        PageVersionMenuComponent
    ],
    entryComponents: [PageVersionMenuComponent, VersionItemContextComponent],
    providers: [ManagePageVersionService, RollbackPageVersionService]
})
export class VersioningModule {}
