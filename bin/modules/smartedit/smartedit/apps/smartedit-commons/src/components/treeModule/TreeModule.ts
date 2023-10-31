/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { DragDropModule } from '@angular/cdk/drag-drop';
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { TranslationModule } from '@smart/utils';

import { L10nPipeModule } from '../../pipes/l10n';
import { TreeComponent } from './TreeComponent';
import { TreeNodeComponent } from './TreeNodeComponent';
import { TreeNodeRendererComponent } from './TreeNodeRendererComponent';

@NgModule({
    imports: [DragDropModule, CommonModule, L10nPipeModule, TranslationModule.forChild()],
    declarations: [TreeComponent, TreeNodeRendererComponent, TreeNodeComponent],
    exports: [TreeComponent, TreeNodeComponent],
    entryComponents: [TreeComponent, TreeNodeRendererComponent]
})
export class NgTreeModule {}
