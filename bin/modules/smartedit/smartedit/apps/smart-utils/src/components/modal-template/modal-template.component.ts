/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    ChangeDetectorRef,
    Component,
    OnDestroy,
    OnInit,
    Type,
    ViewEncapsulation,
    ViewRef
} from '@angular/core';
import { Observable, Subscription } from 'rxjs';

import { ModalButtonOptions } from '../../interfaces';
import { ModalManagerService } from '../../services/modal/modal-manager.service';

@Component({
    selector: 'fundamental-modal-template',
    encapsulation: ViewEncapsulation.None,
    providers: [ModalManagerService], // Can be injected only by this component and children components (such as Modal Component).
    styles: [
        `
            .fd-dialog__title {
                min-height: 20px;
            }
        `
    ],
    template: `
        <fd-dialog>
            <fd-dialog-header>
                <h1 fd-title id="fd-title-{{ (title$ | async) || '' }}">
                    {{ (title$ | async) || '' | translate }}
                    {{ (titleSuffix$ | async) || '' | translate }}
                </h1>
                <button
                    fd-dialog-close-button
                    *ngIf="isDismissButtonVisible$ | async"
                    (click)="dismiss()"
                    class="fd-button fd-button--transparent fd-dialog__close"
                    title="{{ 'se.btn.close.title' | translate }}"></button>
            </fd-dialog-header>
            <fd-dialog-body>
                <ng-container *ngIf="component$ | async as component">
                    <ng-container *ngComponentOutlet="component"></ng-container>
                </ng-container>
            </fd-dialog-body>
            <fd-dialog-footer *ngIf="buttons && buttons.length > 0">
                <button
                    *ngFor="let button of buttons"
                    [disabled]="button.disabledFn ? button.disabledFn!() : button.disabled"
                    [fdType]="button.style"
                    [attr.id]="button.id"
                    [label]="button.label | translate"
                    (click)="onButtonClicked(button)"
                    fd-button></button>
            </fd-dialog-footer>
        </fd-dialog>
    `
})
export class ModalTemplateComponent implements OnInit, OnDestroy {
    /** Component rendered in modal body.  */
    public component$: Observable<Type<any> | undefined>;
    public title$: Observable<string>;
    public titleSuffix$: Observable<string>;
    public isDismissButtonVisible$: Observable<boolean>;

    public buttons!: ModalButtonOptions[];

    private buttons$: Observable<ModalButtonOptions[]>;
    private buttonsSubscription: Subscription | undefined;

    constructor(private modalManager: ModalManagerService, private cdr: ChangeDetectorRef) {
        this.component$ = this.modalManager.getComponent();
        this.title$ = this.modalManager.getTitle();
        this.titleSuffix$ = this.modalManager.getTitleSuffix();
        this.isDismissButtonVisible$ = this.modalManager.getIsDismissButtonVisible();
        this.buttons$ = this.modalManager.getButtons();
    }

    ngOnInit(): void {
        this.modalManager.init();

        this.buttonsSubscription = this.buttons$.subscribe((value) => {
            this.buttons = value;
            // For some consumere, adding buttons can result in not properly rendered view
            if (!(this.cdr as ViewRef).destroyed) {
                this.cdr.detectChanges();
            }
        });
    }

    ngOnDestroy(): void {
        if (this.buttonsSubscription) {
            this.buttonsSubscription.unsubscribe();
        }
    }

    public onButtonClicked(button: ModalButtonOptions): void {
        this.modalManager.onButtonClicked(button);
    }

    public dismiss(): void {
        this.modalManager.dismiss();
    }
}
