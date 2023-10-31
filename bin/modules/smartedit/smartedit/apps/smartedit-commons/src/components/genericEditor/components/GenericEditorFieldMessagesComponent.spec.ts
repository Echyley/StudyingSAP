/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TranslateModule } from '@ngx-translate/core';
import {
    GenericEditorField,
    GenericEditorFieldMessage,
    VALIDATION_MESSAGE_TYPES
} from 'smarteditcommons';
import { GenericEditorFieldMessagesComponent } from './GenericEditorFieldMessagesComponent';

describe('GenericEditorFieldMessagesComponent', () => {
    let genericEditorFieldMessagesComponent: GenericEditorFieldMessagesComponent;
    let fixture: ComponentFixture<GenericEditorFieldMessagesComponent>;
    let nativeElement: HTMLElement;

    async function createGenericEditorFieldMessagesComponentContext() {
        await TestBed.configureTestingModule({
            imports: [TranslateModule.forRoot()],
            declarations: [GenericEditorFieldMessagesComponent],
            schemas: [NO_ERRORS_SCHEMA]
        })
            .overrideComponent(GenericEditorFieldMessagesComponent, {
                set: {
                    selector: 'se-generic-editor-field-messages',
                    templateUrl:
                        'base/src/components/genericEditor/components/GenericEditorFieldMessagesComponent.html'
                }
            })
            .compileComponents();

        fixture = TestBed.createComponent(GenericEditorFieldMessagesComponent);
        genericEditorFieldMessagesComponent = fixture.componentInstance;

        fixture.detectChanges();
        nativeElement = fixture.debugElement.nativeElement;
    }

    beforeEach(async () => {
        await createGenericEditorFieldMessagesComponentContext();
    });

    it('Should create GenericEditorFieldMessages Component correctly', () => {
        expect(genericEditorFieldMessagesComponent).toBeDefined();
    });

    it(
        'Given the Editor modifies a tab - ' +
            'WHEN there has validation error - ' +
            'THEN an error will be display',
        () => {
            const errorMessage = {
                message: 'this has error',
                type: VALIDATION_MESSAGE_TYPES.VALIDATION_ERROR,
                marker: 'qualifier'
            } as GenericEditorFieldMessage;
            genericEditorFieldMessagesComponent.field = {
                messages: [errorMessage]
            } as GenericEditorField;
            genericEditorFieldMessagesComponent.qualifier = 'qualifier';

            genericEditorFieldMessagesComponent.ngDoCheck();
            fixture.detectChanges();
            expect(nativeElement.querySelector('.se-generic-editor__error')).toBeTruthy();
            expect(nativeElement.querySelector('.se-generic-editor__warning')).toBeNull();
        }
    );

    it(
        'Given the Editor modifies a tab - ' +
            'WHEN there has warning - ' +
            'THEN an waring will be display',
        () => {
            const warningMessage = {
                message: 'this has warning',
                type: VALIDATION_MESSAGE_TYPES.WARNING,
                marker: 'qualifier'
            } as GenericEditorFieldMessage;
            genericEditorFieldMessagesComponent.field = {
                messages: [warningMessage]
            } as GenericEditorField;
            genericEditorFieldMessagesComponent.qualifier = 'qualifier';

            genericEditorFieldMessagesComponent.ngDoCheck();
            fixture.detectChanges();
            expect(nativeElement.querySelector('.se-generic-editor__error')).toBeNull();
            expect(nativeElement.querySelector('.se-generic-editor__warning')).toBeTruthy();
        }
    );
});
