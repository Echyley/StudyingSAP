import { AfterViewChecked, ChangeDetectorRef, OnDestroy } from '@angular/core';
import { AbstractForm, AbstractForms, FormGrouping } from '@smart/utils';
import { TabData } from '../../tabs';
import { GenericEditorComponent } from '../GenericEditorComponent';
import { GenericEditorField } from '../types';
export declare class GenericEditorTabComponent implements OnDestroy, AfterViewChecked {
    private readonly cdr;
    ge: GenericEditorComponent;
    private data;
    forms: AbstractForms;
    fields: GenericEditorField[];
    private _subscription;
    constructor(cdr: ChangeDetectorRef, ge: GenericEditorComponent, data: TabData<FormGrouping>);
    ngOnInit(): void;
    ngAfterViewChecked(): void;
    ngOnDestroy(): void;
    getForm(id: string): AbstractForm;
}
