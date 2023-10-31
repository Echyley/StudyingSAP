/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { TranslationModule } from '@smart/utils';

import { DropdownMenuModule } from '../dropdown/dropdownMenu';
import { NgTreeModule } from '../treeModule/TreeModule';
import { EditableListComponent, EditableListDefaultItem } from './EditableListComponent';

@NgModule({
    imports: [CommonModule, NgTreeModule, DropdownMenuModule, TranslationModule.forChild()],
    declarations: [EditableListComponent, EditableListDefaultItem],
    entryComponents: [EditableListComponent, EditableListDefaultItem],
    exports: [EditableListComponent, EditableListDefaultItem]
})
export class EditableListModule {}
