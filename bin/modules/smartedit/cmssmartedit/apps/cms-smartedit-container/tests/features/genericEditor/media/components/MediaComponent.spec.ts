/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ChangeDetectionStrategy, ChangeDetectorRef, NO_ERRORS_SCHEMA } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ButtonModule } from '@fundamental-ngx/core';
import { TranslateModule } from '@ngx-translate/core';
import { MediaComponent } from 'cmssmarteditcontainer/components/genericEditor/media/components';
import { MediaUtilService } from 'cmssmarteditcontainer/components/genericEditor/media/services';
import {
    FileValidationService,
    GENERIC_EDITOR_WIDGET_DATA,
    GenericEditorWidgetData,
    IFileValidation,
    ISharedDataService,
    LogService,
    TypedMap,
    IMediaService
} from 'smarteditcommons';
import { FakeMediaUploadFormComponent } from '../mockComponents/FakeMediaUploadFormComponent';

describe('MediaComponent', () => {
    let component: MediaComponent;
    let fixture: ComponentFixture<MediaComponent>;
    let nativeElement: HTMLElement;

    let fileValidationService: jasmine.SpyObj<FileValidationService>;
    let logService: jasmine.SpyObj<LogService>;
    let widgetData: GenericEditorWidgetData<TypedMap<string>>;
    let mediaUtilService: jasmine.SpyObj<MediaUtilService>;
    let mediaService: jasmine.SpyObj<IMediaService>;
    let sharedDataService: jasmine.SpyObj<ISharedDataService>;
    const cdr = jasmine.createSpyObj<ChangeDetectorRef>('changeDetectorRef', ['detectChanges']);

    async function createMediaComponentContext() {
        await TestBed.configureTestingModule({
            imports: [TranslateModule.forRoot(), ButtonModule],
            declarations: [MediaComponent, FakeMediaUploadFormComponent],
            providers: [
                { provide: ChangeDetectorRef, useValue: cdr },
                { provide: IFileValidation, useValue: fileValidationService },
                { provide: LogService, useValue: logService },
                { provide: MediaUtilService, useValue: mediaUtilService },
                { provide: IMediaService, useValue: mediaService },
                { provide: ISharedDataService, useValue: sharedDataService },
                { provide: GENERIC_EDITOR_WIDGET_DATA, useValue: widgetData }
            ],
            schemas: [NO_ERRORS_SCHEMA]
        })
            .overrideComponent(MediaComponent, {
                set: {
                    templateUrl:
                        '/base/src/components/genericEditor/media/components/media/MediaComponent.html',
                    styleUrls: [
                        '/base/src/components/genericEditor/media/components/media/MediaComponent.scss'
                    ],
                    changeDetection: ChangeDetectionStrategy.Default
                }
            })
            .compileComponents();

        fixture = TestBed.createComponent(MediaComponent);
        component = fixture.componentInstance;

        component.image = new File([''], 'validFile', { type: 'jpg' });

        fixture.detectChanges();
        nativeElement = fixture.debugElement.nativeElement;
    }

    beforeEach(async () => {
        fileValidationService = jasmine.createSpyObj<FileValidationService>(
            'fileValidationService',
            ['validate']
        );

        logService = jasmine.createSpyObj<LogService>('logService', ['warn']);
        mediaUtilService = jasmine.createSpyObj<MediaUtilService>('mediaUtilService', [
            'getAcceptedFileTypes'
        ]);
        mediaService = jasmine.createSpyObj<IMediaService>('mediaService', ['getMedia']);
        sharedDataService = jasmine.createSpyObj<ISharedDataService>('sharedDataService', ['get']);

        widgetData = {
            model: {
                en: undefined
            },
            qualifier: 'en',
            field: {
                initiated: ['1', '2']
            },
            isFieldDisabled: () => false
        } as any;
        await createMediaComponentContext();
    });

    afterEach(() => {
        TestBed.resetTestingModule();
    });

    it('Should create Media Component correctly', () => {
        expect(component).toBeDefined();
    });

    describe('Media Upload Form', () => {
        it('WHEN Cancel Button is clicked THEN Media Upload Form is not displayed', () => {
            component.resetImage();
            fixture.detectChanges();

            expect(nativeElement.querySelector('se-media-upload-form')).toBeNull();
            expect(component.image).toBeNull();
        });

        it('WHEN Upload Button is clicked THEN media is uploaded successfully AND form is not displayed', async () => {
            await component.onMediaUploaded('testId');
            fixture.detectChanges();

            expect(nativeElement.querySelector('se-media-upload-form')).toBeNull();
            expect(component.image).toBeNull();
        });

        describe('canShowFileSelector', () => {
            it('GIVEN model exists AND there is no id for given language AND there is no image THEN it returns true', () => {
                component.image = null;
                fixture.detectChanges();
                expect(component.canShowFileSelector()).toBe(true);
            });
        });
    });

    describe('new Component', () => {
        let newComponent: MediaComponent = null;
        beforeEach(() => {
            newComponent = new MediaComponent(
                cdr,
                fileValidationService,
                logService,
                mediaUtilService,
                mediaService,
                sharedDataService,
                widgetData
            );
        });
        describe('onFileSelect', () => {
            it('GIVEN selected file is valid file object THEN it sets the image properly', async () => {
                const files = [
                    {
                        name: 'someName'
                    }
                ] as unknown as FileList;
                fileValidationService.validate.and.returnValue(Promise.resolve());

                await component.onFileSelect(files);

                expect(component.fileErrors).toEqual([]);
                expect(component.image).toBe(files[0]);
            });

            it('GIVEN selected file is file reference string THEN it sets the image string properly', async () => {
                const fileString = 'imgString';

                await component.onFileSelect(fileString);

                expect(component.fileErrors).toEqual([]);
                expect(component.image).toBe(fileString);
            });

            it('GIVEN selected file is not valid THEN it resets the image AND logs the message about failure', async () => {
                const files = [
                    {
                        name: 'someName'
                    }
                ] as unknown as FileList;
                fileValidationService.validate.and.returnValue(Promise.reject());

                await component.onFileSelect(files);

                expect(logService.warn).toHaveBeenCalled();
                expect(component.image).toBeNull();
            });
        });

        describe('onMediaUploaded', () => {
            it('sets given id as media id by the language', () => {
                const id = '123';
                newComponent.onMediaUploaded(id);

                fixture.detectChanges();

                expect(widgetData.model.en).toBe(id);
            });

            it('GIVEN Generic Editor Field initiated array exists THEN it clears the array', async () => {
                const id = '123';
                await newComponent.onMediaUploaded(id);
                fixture.detectChanges();
                expect(widgetData.field.initiated.length).toBe(0);
            });
        });
    });
});
