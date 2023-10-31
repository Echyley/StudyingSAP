/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ButtonModule } from '@fundamental-ngx/core';
import {
    DropdownMenuModule,
    L10nPipeModule,
    NgTreeModule,
    TooltipModule,
    TranslationModule
} from 'smarteditcommons';

import { ToolbarModule } from 'smarteditcontainer';
import { NavigationEditorLinkComponent } from './navigationEditor/NavigationEditorLinkComponent';
import { NavigationEditorNodeService } from './navigationEditor/NavigationEditorNodeService';
import { NavigationEditorTreeComponent } from './navigationEditor/NavigationEditorTreeComponent';
import { NavigationNodeComponent } from './navigationEditor/NavigationNodeComponent';
import { NodeAncestryService } from './navigationEditor/NodeAncestryService';
import { NavigationManagementPageComponent } from './NavigationManagementPageComponent';
import {
    BreadcrumbComponent,
    NavigationNodePickerComponent,
    NavigationNodePickerRenderComponent,
    NavigationNodeSelectorComponent
} from './navigationNode';
import { NavigationNodeEditorModalService } from './navigationNodeEditor/NavigationNodeEditorModalService';

@NgModule({
    imports: [
        CommonModule,
        DropdownMenuModule,
        L10nPipeModule,
        TranslationModule.forChild(),
        NgTreeModule,
        TooltipModule,
        ToolbarModule,
        ButtonModule
    ],
    declarations: [
        NavigationEditorTreeComponent,
        NavigationNodeComponent,
        NavigationEditorLinkComponent,
        BreadcrumbComponent,
        NavigationNodePickerComponent,
        NavigationNodePickerRenderComponent,
        NavigationNodeSelectorComponent,
        NavigationManagementPageComponent
    ],
    entryComponents: [
        NavigationEditorTreeComponent,
        NavigationNodeComponent,
        NavigationEditorLinkComponent,
        BreadcrumbComponent,
        NavigationNodePickerComponent,
        NavigationNodePickerRenderComponent,
        NavigationNodeSelectorComponent
    ],
    exports: [NavigationEditorTreeComponent],
    providers: [NodeAncestryService, NavigationEditorNodeService, NavigationNodeEditorModalService]
})
export class NavigationModule {}
