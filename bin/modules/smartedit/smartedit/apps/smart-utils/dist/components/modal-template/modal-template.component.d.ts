/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { ChangeDetectorRef, OnDestroy, OnInit, Type } from '@angular/core';
import { Observable } from 'rxjs';
import { ModalButtonOptions } from '../../interfaces';
import { ModalManagerService } from '../../services/modal/modal-manager.service';
export declare class ModalTemplateComponent implements OnInit, OnDestroy {
    private modalManager;
    private cdr;
    /** Component rendered in modal body.  */
    component$: Observable<Type<any> | undefined>;
    title$: Observable<string>;
    titleSuffix$: Observable<string>;
    isDismissButtonVisible$: Observable<boolean>;
    buttons: ModalButtonOptions[];
    private buttons$;
    private buttonsSubscription;
    constructor(modalManager: ModalManagerService, cdr: ChangeDetectorRef);
    ngOnInit(): void;
    ngOnDestroy(): void;
    onButtonClicked(button: ModalButtonOptions): void;
    dismiss(): void;
}
