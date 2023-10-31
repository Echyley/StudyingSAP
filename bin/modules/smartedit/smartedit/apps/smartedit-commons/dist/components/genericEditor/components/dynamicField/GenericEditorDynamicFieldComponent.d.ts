import { AfterViewChecked, ChangeDetectorRef } from '@angular/core';
import { FormField, Payload } from '@smart/utils';
import { GenericEditorField } from '../../types';
/**
 * TODO: Some parts of the generic editor field can be moved up to this component.
 * and then we could dynamic inject which form we should put the control in.
 */
export declare class GenericEditorDynamicFieldComponent implements AfterViewChecked {
    private readonly cdr;
    form: FormField;
    component: Payload;
    field: GenericEditorField;
    qualifier: string;
    id: string;
    constructor(cdr: ChangeDetectorRef);
    ngAfterViewChecked(): void;
}
