/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, EventEmitter, Input, Output, Type } from '@angular/core';
import {
    GenericEditorDropdownComponent,
    GenericEditorField,
    SelectReset,
    TypedMap
} from 'smarteditcommons';

@Component({
    selector: 'se-generic-editor-dropdown',
    template: ''
})
export class FakeGenericEditorDropdownComponent implements Partial<GenericEditorDropdownComponent> {
    @Input() field: GenericEditorField;
    @Input() qualifier: string;
    @Input() model: TypedMap<any>;
    @Input() showRemoveButton?: boolean;
    @Input() itemComponent?: Type<any>;
    @Input() reset: SelectReset;
    @Output() resetChange = new EventEmitter<SelectReset>();
}
