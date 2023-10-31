/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    AfterViewChecked,
    ChangeDetectorRef,
    Component,
    Inject,
    OnDestroy,
    ViewRef
} from '@angular/core';
import { AbstractForm, AbstractForms, FormGrouping } from '@smart/utils';
import { values } from 'lodash';
import { Subscription } from 'rxjs';

import { TabData, TAB_DATA } from '../../tabs';
import { GenericEditorComponent } from '../GenericEditorComponent';
import { GenericEditorField } from '../types';

@Component({
    selector: 'se-ge-tab',
    templateUrl: './GenericEditorTabComponent.html'
})
export class GenericEditorTabComponent implements OnDestroy, AfterViewChecked {
    public forms: AbstractForms;

    public fields: GenericEditorField[];
    private _subscription: Subscription;

    constructor(
        private readonly cdr: ChangeDetectorRef,
        public ge: GenericEditorComponent,
        @Inject(TAB_DATA) private data: TabData<FormGrouping>
    ) {}

    ngOnInit(): void {
        const { model: master, tabId } = this.data;
        this.fields = master.getInput('fieldsMap')[tabId];

        const group = master.controls[tabId] as FormGrouping;
        this.forms = group.controls;

        this._subscription = group.statusChanges.subscribe(() => {
            const hasErrorMessages = values(this.forms).some((form) => {
                const field = form.getInput('field') as GenericEditorField;
                return field.messages && field.messages.length > 0;
            });

            this.data.tab.hasErrors = hasErrorMessages;
        });

        Object.keys(this.forms).forEach((key) => {
            this.forms[key].setInput('id', this.ge.editor.id);
        });
    }

    ngAfterViewChecked(): void {
        if (!(this.cdr as ViewRef).destroyed) {
            this.cdr.detectChanges();
        }
    }

    ngOnDestroy(): void {
        this._subscription.unsubscribe();
    }

    getForm(id: string): AbstractForm {
        return this.forms[id];
    }
}
