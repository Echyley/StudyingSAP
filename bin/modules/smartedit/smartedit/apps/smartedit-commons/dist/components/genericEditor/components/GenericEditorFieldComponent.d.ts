import { ElementRef, Injector, OnDestroy } from '@angular/core';
import { ControlValueAccessor } from '@angular/forms';
import { Payload } from '@smart/utils';
import { GenericEditorComponent } from '../GenericEditorComponent';
import { GenericEditorField, GenericEditorWidgetData, IGenericEditor } from '../types';
export interface GenericEditorFieldComponentScope<T = any, D = any> extends GenericEditorWidgetData<D> {
    editor?: IGenericEditor;
    id: string;
    editorStackId: string;
    $ctrl?: GenericEditorFieldComponent<T>;
}
export declare class GenericEditorFieldComponent<T> implements ControlValueAccessor, OnDestroy {
    private elementRef;
    private injector;
    ge: GenericEditorComponent;
    field: GenericEditorField;
    model: Payload;
    qualifier: string;
    id: string;
    geWidget: ElementRef;
    widgetInjector: Injector;
    private _onChange;
    private _onTouched;
    private _unWatch;
    constructor(elementRef: ElementRef, injector: Injector, ge: GenericEditorComponent);
    writeValue(value: Payload): void;
    registerOnChange(fn: (value: Payload) => void): void;
    registerOnTouched(fn: () => void): void;
    ngOnInit(): void;
    ngOnDestroy(): void;
    ngAfterViewInit(): void;
    isFieldDisabled(): boolean;
    private createInjector;
}
