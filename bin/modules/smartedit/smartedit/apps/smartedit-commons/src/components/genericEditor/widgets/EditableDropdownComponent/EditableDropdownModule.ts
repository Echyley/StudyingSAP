/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';

import { GenericEditorDropdownModule } from '../../components/dropdown/GenericEditorDropdownModule';
import { EditableDropdownComponent } from './EditableDropdownComponent';

@NgModule({
    imports: [CommonModule, GenericEditorDropdownModule],
    declarations: [EditableDropdownComponent],
    entryComponents: [EditableDropdownComponent],
    exports: [EditableDropdownComponent]
})
export class EditableDropdownModule {}
