import { OnDestroy } from '@angular/core';
import { FormField, FormGrouping } from '@smart/utils';
import { TabData } from '../../tabs';
export declare class GenericEditorFieldWrapperComponent implements OnDestroy {
    form: FormField;
    private _subscription;
    constructor({ model: form, tabId, tab }: TabData<FormGrouping>);
    ngOnDestroy(): void;
}
