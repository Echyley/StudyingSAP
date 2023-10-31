/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ChangeDetectorRef, NO_ERRORS_SCHEMA, Type } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ButtonModule, DialogRef } from '@fundamental-ngx/core';
import { TranslateModule } from '@ngx-translate/core';
import { BehaviorSubject } from 'rxjs';
import { TypedMap } from '../../dtos';
import { ModalButtonAction, ModalButtonOptions } from '../../interfaces';
import { ModalManagerService } from '../../services';
import { MockModule } from './mockComponent/mockModule';
import { TestComponent } from './mockComponent/TestComponent';
import { ModalTemplateComponent } from './modal-template.component';

describe('ModalTemplateComponent', () => {
    let modalTemplateComponent: ModalTemplateComponent;
    let fixture: ComponentFixture<ModalTemplateComponent>;
    let nativeElement: HTMLElement;

    const cdr = jasmine.createSpyObj<ChangeDetectorRef>('changeDetectorRef', ['detectChanges']);
    let buttons: ModalButtonOptions[] = [{ id: 'id', label: 'label' }];
    const title = 'My Title';
    const isDismissButtonVisible = true;
    const modalData: TypedMap<any> = {
        data: {
            editorStackId: 'testEditorStackId'
        }
    };
    let component: Type<any>;

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
    let modalRef: jasmine.SpyObj<DialogRef> = createModalRefMock();

    async function createModalTemplateComponentContext() {
        await TestBed.configureTestingModule({
            imports: [TranslateModule.forRoot(), ButtonModule, MockModule],
            declarations: [ModalTemplateComponent],
            providers: [
                { provide: DialogRef, useValue: modalRef },
                ModalManagerService,
                { provide: ChangeDetectorRef, useValue: cdr }
            ],
            schemas: [NO_ERRORS_SCHEMA]
        }).compileComponents();

        fixture = TestBed.createComponent(ModalTemplateComponent);
        modalTemplateComponent = fixture.componentInstance;

        fixture.detectChanges();
        nativeElement = fixture.debugElement.nativeElement;
    }

    it('Should create ModalTemplate Component correctly', async () => {
        await createModalTemplateComponentContext();
        expect(modalTemplateComponent).toBeDefined();
    });

    describe(
        'Given the editor callback is attached to a button assigned to a component - ' +
            'When the button is clicked - ',
        () => {
            it('Then the Editor Modal is displayed with Save button visible.', async () => {
                buttons = [{ id: 'id', label: 'label', disabled: false }];
                modalRef = createModalRefMock();

                await createModalTemplateComponentContext();
                fixture.detectChanges();

                const expectButtons = nativeElement.querySelector('fd-dialog-footer');
                expect(expectButtons.querySelector('button').disabled).toEqual(false);
            });

            it('Then the Editor Modal is displayed with the Save button disabled.', async () => {
                buttons = [{ id: 'id', label: 'label', disabled: true }];
                modalRef = createModalRefMock();

                await createModalTemplateComponentContext();
                fixture.detectChanges();

                const expectButtons = nativeElement.querySelector('fd-dialog-footer');
                expect(expectButtons.querySelector('button').disabled).toEqual(true);
            });

            it('Then the Generic Editor is loaded in the modal content for the given component', async () => {
                component = TestComponent;
                modalRef = createModalRefMock();

                await createModalTemplateComponentContext();
                fixture.detectChanges();

                expect(nativeElement.querySelector('test-component')).toBeTruthy();
            });
        }
    );

    describe('Given the Editor Cancel confirmation modal is visible - ', () => {
        afterAll(() => {
            modalRef.close.calls.reset();
        });
        it(
            'When I click OK to confirm the cancellation - ' + 'Then the modal window is closed',
            async () => {
                const submitCallback$ = new BehaviorSubject<string>('Submit'); //.next('Submit');

                buttons = [
                    {
                        id: 'submit',
                        label: 'Submit',
                        disabled: false,
                        callback: () => submitCallback$.asObservable(),
                        action: ModalButtonAction.Close
                    }
                ];
                component = TestComponent;
                modalRef = createModalRefMock();

                await createModalTemplateComponentContext();
                fixture.detectChanges();

                const submitButton: HTMLButtonElement = nativeElement.querySelector('#submit');
                submitButton.click();

                const closeSpy = modalRef.close.and.returnValue(null);
                expect(closeSpy).toHaveBeenCalled();
            }
        );

        it(
            'When I click the Cancel button to dismiss the cancellation - ' +
                'Then the modal window remains open',
            async () => {
                const cancelCallback$ = new BehaviorSubject<string>('Cancel');

                buttons = [
                    {
                        id: 'cancel',
                        label: 'Cancel',
                        disabled: false,
                        callback: () => cancelCallback$.asObservable(),
                        action: ModalButtonAction.Dismiss
                    }
                ];
                component = TestComponent;
                modalRef = createModalRefMock();

                await createModalTemplateComponentContext();
                fixture.detectChanges();

                const cancelButton: HTMLButtonElement = nativeElement.querySelector('#cancel');
                cancelButton.click();

                const closeSpy = modalRef.close.and.returnValue(null);
                expect(closeSpy).not.toHaveBeenCalled();
            }
        );
    });
});
