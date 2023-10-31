/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, Inject, OnDestroy } from '@angular/core';
import { FormField, FormGrouping } from '@smart/utils';
import { Subscription } from 'rxjs';

import { TabData, TAB_DATA } from '../../tabs';

@Component({
    selector: 'se-generic-editor-field-wrapper',
    template: ` <ng-template [formRenderer]="form"></ng-template> `
})
export class GenericEditorFieldWrapperComponent implements OnDestroy {
    public form: FormField;
    private _subscription: Subscription;

    constructor(
        @Inject(TAB_DATA)
        { model: form, tabId, tab }: TabData<FormGrouping>
    ) {
        this.form = form.controls[tabId] as FormField;

        this._subscription = this.form.statusChanges.subscribe((status) => {
            tab.hasErrors = status === 'INVALID';
        });
    }

    ngOnDestroy(): void {
        this._subscription.unsubscribe();
    }
}
