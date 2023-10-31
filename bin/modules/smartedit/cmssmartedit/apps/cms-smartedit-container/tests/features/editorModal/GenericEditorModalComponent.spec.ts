/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { NO_ERRORS_SCHEMA, Type } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ButtonModule, DialogRef } from '@fundamental-ngx/core';
import { TranslateModule } from '@ngx-translate/core';
import {
    GenericEditorModalComponent,
    GenericEditorModalComponentControls
} from 'cmssmarteditcontainer';
import {
    CrossFrameEventService,
    IConfirmationModalService,
    ModalButtonOptions,
    ModalManagerService,
    SystemEventService,
    TypedMap
} from 'smarteditcommons';

describe('GenericEditorModelComponent', () => {
    let genericEditorModalComponent: GenericEditorModalComponent;
    let fixture: ComponentFixture<GenericEditorModalComponent>;
    let nativeElement: HTMLElement;

    const confirmationModalService = jasmine.createSpyObj<IConfirmationModalService>(
        'confirmationModalService',
        ['confirm']
    );
    const crossFrameEventService = jasmine.createSpyObj<CrossFrameEventService>(
        'crossFrameEventService',
        ['publish']
    );
    const systemEventService = jasmine.createSpyObj<SystemEventService>('systemEventService', [
        'subscribe'
    ]);

    systemEventService.subscribe.and.returnValue(function () {});
    const buttons: ModalButtonOptions[] = [{ id: 'id', label: 'label' }];
    const title = 'My Title';
    const isDismissButtonVisible = true;
    const modalData: TypedMap<any> = {
        data: {
            editorStackId: 'testEditorStackId'
        }
    };
    const component: Type<any> = {} as Type<any>;

    function createModalRefMock(): jasmine.SpyObj<DialogRef> {
        return {
            ...jasmine.createSpyObj('modalRef', ['close', 'dismiss']),
            data: {
                component,
                modalData,
                templateConfig: {
                    isDismissButtonVisible,
                    title,
                    buttons
                }
            }
        };
    }
    const modalRef: jasmine.SpyObj<DialogRef> = createModalRefMock();

    async function createGenericEditorModelComponentContext() {
        await TestBed.configureTestingModule({
            imports: [TranslateModule.forRoot(), ButtonModule],
            declarations: [GenericEditorModalComponent],
            providers: [
                { provide: DialogRef, useValue: modalRef },
                ModalManagerService,
                { provide: IConfirmationModalService, useValue: confirmationModalService },
                { provide: CrossFrameEventService, useValue: crossFrameEventService },
                { provide: SystemEventService, useValue: systemEventService }
            ],
            schemas: [NO_ERRORS_SCHEMA]
        }).compileComponents();

        fixture = TestBed.createComponent(GenericEditorModalComponent);
        genericEditorModalComponent = fixture.componentInstance;

        (genericEditorModalComponent as any).modalManager.init();

        fixture.detectChanges();
        nativeElement = fixture.debugElement.nativeElement;
    }

    beforeEach(async () => {
        await createGenericEditorModelComponentContext();
    });

    afterEach(() => {
        TestBed.resetTestingModule();
    });

    it('Should create GenericEditorModal Component correctly', () => {
        expect(genericEditorModalComponent).toBeDefined();
    });

    describe('Given the Editor Modal is open - ', () => {
        afterEach(() => {
            confirmationModalService.confirm.calls.reset();
        });

        it(
            'And there are no changes to commit - ' +
                'When I click the Cancel button - ' +
                'Then the modal window is closed',
            () => {
                genericEditorModalComponent.controls = {
                    isDirty: () => false,
                    reset: () => Promise.resolve()
                } as GenericEditorModalComponentControls;
                confirmationModalService.confirm.and.returnValue(Promise.resolve(true));

                genericEditorModalComponent.onCancel();
                expect(confirmationModalService.confirm).not.toHaveBeenCalled();
            }
        );

        it(
            'And there are changes to commit - ' +
                'When I click the Cancel button - ' +
                'Then a cancel confirmation modal appears',
            () => {
                genericEditorModalComponent.controls = {
                    isDirty: () => true,
                    reset: () => Promise.resolve()
                } as GenericEditorModalComponentControls;
                confirmationModalService.confirm.and.returnValue(Promise.resolve(true));

                genericEditorModalComponent.onCancel();
                expect(confirmationModalService.confirm).toHaveBeenCalled();
            }
        );
        it(
            'And there are no changes to commit - ' +
                'When I click the X button - ' +
                'Then the modal window is closed',
            () => {
                genericEditorModalComponent.controls = {
                    isDirty: () => false,
                    reset: () => Promise.resolve()
                } as GenericEditorModalComponentControls;
                confirmationModalService.confirm.and.returnValue(Promise.resolve(true));
                (genericEditorModalComponent as any).modalManager.dismiss();

                expect(confirmationModalService.confirm).not.toHaveBeenCalled();
            }
        );

        it(
            'And there are changes to commit - ' +
                'When I click the X button - ' +
                'Then a cancel confirmation modal appears',
            () => {
                genericEditorModalComponent.controls = {
                    isDirty: () => true,
                    reset: () => Promise.resolve()
                } as GenericEditorModalComponentControls;
                confirmationModalService.confirm.and.returnValue(Promise.resolve(true));
                (genericEditorModalComponent as any).modalManager.dismiss();

                expect(confirmationModalService.confirm).toHaveBeenCalled();
            }
        );
    });
});
