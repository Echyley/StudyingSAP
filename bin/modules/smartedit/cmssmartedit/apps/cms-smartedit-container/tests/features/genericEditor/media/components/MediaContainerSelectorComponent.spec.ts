/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ChangeDetectionStrategy, ChangeDetectorRef, NO_ERRORS_SCHEMA } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { TranslateModule } from '@ngx-translate/core';
import { MediaContainerSelectorComponent } from 'cmssmarteditcontainer/components/genericEditor/media/components';
import { SystemEventService } from 'smarteditcommons';
import { FakeGenericEditorDropdownComponent } from '../../mockComponents';

describe('MediaContainerSelectorComponent', () => {
    let component: MediaContainerSelectorComponent;
    let fixture: ComponentFixture<MediaContainerSelectorComponent>;
    let nativeElement: HTMLElement;

    let systemEventService: jasmine.SpyObj<SystemEventService>;
    const cdr = jasmine.createSpyObj<ChangeDetectorRef>('changeDetectorRef', ['detectChanges']);

    async function createMediaContainerSelectorComponentContext() {
        systemEventService = jasmine.createSpyObj<SystemEventService>('systemEventService', [
            'subscribe',
            'publishAsync'
        ]);
        systemEventService.subscribe.and.returnValue(function () {});

        await TestBed.configureTestingModule({
            imports: [TranslateModule.forRoot(), FormsModule],
            declarations: [MediaContainerSelectorComponent, FakeGenericEditorDropdownComponent],
            schemas: [NO_ERRORS_SCHEMA]
        })
            .overrideComponent(MediaContainerSelectorComponent, {
                set: {
                    templateUrl:
                        '/base/src/components/genericEditor/media/components/mediaContainer/mediaContainerSelector/MediaContainerSelectorComponent.html',
                    styleUrls: [
                        '/base/src/components/genericEditor/media/components/mediaContainer/mediaContainerSelector/MediaContainerSelectorComponent.scss'
                    ],
                    changeDetection: ChangeDetectionStrategy.Default,
                    providers: [
                        { provide: ChangeDetectorRef, useValue: cdr },
                        { provide: SystemEventService, useValue: systemEventService }
                    ]
                }
            })
            .compileComponents();

        fixture = TestBed.createComponent(MediaContainerSelectorComponent);
        component = fixture.componentInstance;

        component.initialName = 'someValue';
        component.isEditable = true;
        component.eventNameAffix = 'testId';

        fixture.detectChanges();
        nativeElement = fixture.debugElement.nativeElement;
    }

    beforeEach(async () => {
        await createMediaContainerSelectorComponentContext();
    });

    afterEach(() => {
        TestBed.resetTestingModule();
    });

    it('Should create MediaContainerSelectorComponent Component correctly', () => {
        expect(component).toBeDefined();
    });

    describe('Advanced Media Container', () => {
        it('WHEN existing media container is selected THEN media container qualifier field is readonly', () => {
            // WHEN
            component.isAdvancedCloning = false;
            component.creationInProgress = false;
            fixture.detectChanges();

            // THEN
            expect(component.isNameReadOnly()).toEqual(true);
        });

        it('WHEN new media container is being created THEN media container qualifier field is editable', () => {
            // WHEN
            const input: HTMLInputElement = nativeElement.querySelector(
                '#media-container-qualifier-mediaContainer_testId'
            );
            expect(input.readOnly).toEqual(true);

            component.creationInProgress = true;
            fixture.detectChanges();

            expect(input.readOnly).toEqual(false);
        });
    });
});
