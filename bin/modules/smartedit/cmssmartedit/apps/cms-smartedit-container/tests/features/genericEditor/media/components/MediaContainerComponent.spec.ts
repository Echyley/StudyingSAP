/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    ChangeDetectionStrategy,
    ChangeDetectorRef,
    DebugElement,
    Inject,
    Injector,
    NO_ERRORS_SCHEMA,
    ViewEncapsulation
} from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By, DomSanitizer } from '@angular/platform-browser';
import { ButtonModule } from '@fundamental-ngx/core';
import { TranslateModule } from '@ngx-translate/core';
import { TypePermissionsRestService } from 'cmscommons';
import {
    MediaAdvancedPropertiesComponent,
    MediaContainer,
    MediaContainerComponent,
    MediaContainerSelectorComponent,
    MediaErrorsComponent,
    MediaFileSelectorComponent,
    MediaFormatComponent,
    MediaFormatType,
    MediaFormatUploadedComponent,
    MediaPreviewComponent,
    MediaRemoveButtonComponent,
    MediaUploadFieldComponent,
    MediaUploadFormComponent
} from 'cmssmarteditcontainer/components/genericEditor/media/components';
import {
    MediaBackendValidationHandler,
    MediaFolderService,
    MediaUtilService
} from 'cmssmarteditcontainer/components/genericEditor/media/services';
import {
    ErrorContext,
    FileValidatorFactory,
    GENERIC_EDITOR_WIDGET_DATA,
    GenericEditorMediaType,
    GenericEditorWidgetData,
    IFileValidation,
    IGenericEditor,
    ISharedDataService,
    LogService,
    MEDIA_SELECTOR_I18N_KEY_TOKEN,
    SystemEventService,
    TypedMap,
    ICMSMedia,
    IMediaToUpload,
    Media,
    IMediaService
} from 'smarteditcommons';
import { LoadConfigManagerService } from 'smarteditcontainer';

describe('MediaContainerComponent', () => {
    let component: MediaContainerComponent;
    let fixture: ComponentFixture<MediaContainerComponent>;
    let nativeElement: HTMLElement;

    let systemEventService: jasmine.SpyObj<SystemEventService>;
    let logService: jasmine.SpyObj<LogService>;
    let typePermissionsRestService: jasmine.SpyObj<TypePermissionsRestService>;
    let loadConfigManagerService: jasmine.SpyObj<LoadConfigManagerService>;
    let fileValidationService: jasmine.SpyObj<IFileValidation>;
    let sharedDataService: jasmine.SpyObj<ISharedDataService>;
    let mediaContainerWidgetData: GenericEditorWidgetData<TypedMap<MediaContainer>>;
    let mediaService: jasmine.SpyObj<IMediaService>;
    let mediaUtilService: jasmine.SpyObj<MediaUtilService>;
    const fileValidatorFactory = new FileValidatorFactory();
    const cdr = jasmine.createSpyObj<ChangeDetectorRef>('changeDetectorRef', ['detectChanges']);
    let mediaBackendValidationHandler: jasmine.SpyObj<MediaBackendValidationHandler>;
    let mediaUploaderService: jasmine.SpyObj<IMediaToUpload>;
    let mediaFolderService: jasmine.SpyObj<MediaFolderService>;
    let inject: jasmine.SpyObj<Injector>;
    const i18n_key = {
        UPLOAD: 'se.media.format.upload',
        REPLACE: 'se.media.format.replace',
        UNDER_EDIT: 'se.media.format.under.edit',
        REMOVE: 'se.media.format.remove'
    };
    const fileName = 'test';
    const testUrl =
        '/base/src/components/genericEditor/media/components/mediaContainer/MediaContainerComponent.html';
    const image = {
        altText: fileName,
        code: fileName,
        description: fileName,
        downloadUrl: testUrl,
        mime: 'img',
        url: testUrl,
        id: 'uuid'
    } as Media;

    const imageUpload = {
        altText: fileName,
        code: fileName,
        description: fileName,
        downloadUrl: testUrl,
        mime: 'jpg',
        url: testUrl,
        uuid: 'uuid',
        catalogId: 'catalogID',
        catalogVersion: 'catalogVersion'
    } as ICMSMedia;

    async function createMediaContainerComponentContext() {
        systemEventService = jasmine.createSpyObj<SystemEventService>('systemEventService', [
            'subscribe',
            'publishAsync'
        ]);
        systemEventService.subscribe.and.returnValue(function () {});
        logService = jasmine.createSpyObj<LogService>('logService', ['warn']);
        typePermissionsRestService = jasmine.createSpyObj<TypePermissionsRestService>(
            'typePermissionsRestService',
            ['hasAllPermissionsForTypes']
        );
        typePermissionsRestService.hasAllPermissionsForTypes.and.returnValue(
            Promise.resolve({
                MediaContainer: {
                    read: true,
                    create: true,
                    change: true,
                    remove: true
                },
                MediaFormat: {
                    read: true,
                    create: true,
                    change: true,
                    remove: true
                }
            })
        );
        loadConfigManagerService = jasmine.createSpyObj<LoadConfigManagerService>(
            'loadConfigManagerService',
            ['loadAsObject']
        );
        fileValidationService = jasmine.createSpyObj<IFileValidation>('IFileValidation', [
            'validate'
        ]);
        sharedDataService = jasmine.createSpyObj<ISharedDataService>('sharedDataService', ['get']);
        mediaContainerWidgetData = {
            field: {
                qualifier: 'media',
                cmsStructureType: '',
                containedTypes: ['MediaContainer', 'MediaFormat'],
                allowMediaType: GenericEditorMediaType.IMAGE,
                editable: true,
                options: [
                    // {
                    //     id: 'desktop',
                    //     label: 'Desktop'
                    // },
                    // {
                    //     id: 'mobile',
                    //     label: 'Mobile'
                    // },
                    {
                        id: 'widescreen',
                        label: 'WideScreen'
                    }
                ]
            },
            model: {
                en: {
                    qualifier: 'initialMediaContainerName',
                    catalogVersion: undefined,
                    medias: {},
                    mediaContainerUuid: undefined
                }
            },
            editor: {
                initialContent: false,
                form: {
                    pristine: {
                        thumbnail: {}
                    }
                }
            } as unknown as IGenericEditor,
            qualifier: 'en',
            id: undefined,
            isFieldDisabled: () => false
        };
        mediaService = jasmine.createSpyObj<IMediaService>('mediaService', ['getMedia']);
        mediaUtilService = jasmine.createSpyObj<MediaUtilService>('mediaUtilService', [
            'getAcceptedFileTypes'
        ]);
        mediaUtilService.getAcceptedFileTypes.and.returnValue(['jpg']);
        mediaService.getMedia.and.returnValue(Promise.resolve(image));
        mediaUploaderService = jasmine.createSpyObj<IMediaToUpload>('mediaUploaderService', [
            'uploadMedia'
        ]);
        mediaUploaderService.uploadMedia.and.returnValue(Promise.resolve(imageUpload));

        await TestBed.configureTestingModule({
            imports: [TranslateModule.forRoot(), ButtonModule],
            declarations: [
                MediaContainerComponent,
                MediaContainerSelectorComponent,
                MediaFormatComponent,
                MediaFileSelectorComponent,
                MediaUploadFormComponent,
                MediaFormatUploadedComponent,
                MediaErrorsComponent,
                MediaRemoveButtonComponent,
                MediaUploadFieldComponent,
                MediaAdvancedPropertiesComponent,
                MediaPreviewComponent
            ],
            schemas: [NO_ERRORS_SCHEMA]
        })
            .overrideComponent(MediaContainerSelectorComponent, {
                set: {
                    templateUrl:
                        '/base/src/components/genericEditor/media/components/mediaContainer/mediaContainerSelector/MediaContainerSelectorComponent.html',
                    styleUrls: [
                        '/base/src/components/genericEditor/media/components/mediaContainer/mediaContainerSelector/MediaContainerSelectorComponent.scss'
                    ],
                    providers: [
                        { provide: ChangeDetectorRef, useValue: cdr },
                        { provide: SystemEventService, useValue: systemEventService }
                    ]
                }
            })
            .overrideComponent(MediaFileSelectorComponent, {
                set: {
                    templateUrl:
                        '/base/src/components/genericEditor/media/components/mediaFileSelector/MediaFileSelectorComponent.html',
                    styleUrls: [
                        '/base/src/components/genericEditor/media/components/mediaFileSelector/MediaFileSelectorComponent.scss'
                    ],
                    providers: [{ provide: Inject, useValue: inject }]
                }
            })
            .overrideComponent(MediaUploadFormComponent, {
                set: {
                    templateUrl:
                        '/base/src/components/genericEditor/media/components/mediaUploadForm/MediaUploadFormComponent.html',
                    styleUrls: [
                        '/base/src/components/genericEditor/media/components/mediaUploadForm/MediaUploadFormComponent.scss'
                    ],
                    providers: [
                        { provide: ChangeDetectorRef, useValue: cdr },
                        { provide: FileValidatorFactory, useValue: fileValidatorFactory },
                        {
                            provide: MediaBackendValidationHandler,
                            useValue: mediaBackendValidationHandler
                        },
                        { provide: IMediaToUpload, useValue: mediaUploaderService },
                        { provide: MediaFolderService, useValue: mediaFolderService },
                        { provide: MediaUtilService, useValue: mediaUtilService }
                    ]
                }
            })
            .overrideComponent(MediaFormatUploadedComponent, {
                set: {
                    templateUrl:
                        '/base/src/components/genericEditor/media/components/mediaFormat/mediaFormatUploaded/MediaFormatUploadedComponent.html',
                    styleUrls: [
                        '/base/src/components/genericEditor/media/components/mediaFormat/mediaFormatUploaded/MediaFormatUploadedComponent.scss'
                    ],
                    providers: [
                        {
                            provide: DomSanitizer,
                            useValue: {
                                sanitize: (ctx: any, val: string) => val,
                                bypassSecurityTrustResourceUrl: (val: string) => val
                            }
                        }
                    ]
                }
            })
            .overrideComponent(MediaAdvancedPropertiesComponent, {
                set: {
                    templateUrl:
                        '/base/src/components/genericEditor/media/components/mediaAdvancedProperties/MediaAdvancedPropertiesComponent.html',
                    styleUrls: [
                        '/base/src/components/genericEditor/media/components/mediaAdvancedProperties/MediaAdvancedPropertiesComponent.scss'
                    ]
                }
            })
            .overrideComponent(MediaPreviewComponent, {
                set: {
                    templateUrl:
                        '/base/src/components/genericEditor/media/components/mediaPreview/MediaPreviewComponent.html',
                    styleUrls: [
                        '/base/src/components/genericEditor/media/components/mediaPreview/MediaPreviewComponent.scss'
                    ],
                    encapsulation: ViewEncapsulation.None,
                    host: {
                        '[class.se-media-preview]': 'true'
                    },
                    changeDetection: ChangeDetectionStrategy.Default
                }
            })
            .overrideComponent(MediaErrorsComponent, {
                set: {
                    templateUrl:
                        '/base/src/components/genericEditor/media/components/mediaErrors/MediaErrorsComponent.html',
                    styleUrls: [
                        '/base/src/components/genericEditor/media/components/mediaErrors/MediaErrorsComponent.scss'
                    ]
                }
            })
            .overrideComponent(MediaRemoveButtonComponent, {
                set: {
                    templateUrl:
                        '/base/src/components/genericEditor/media/components/mediaRemoveButton/MediaRemoveButtonComponent.html',
                    styleUrls: [
                        '/base/src/components/genericEditor/media/components/mediaRemoveButton/MediaRemoveButtonComponent.scss'
                    ]
                }
            })
            .overrideComponent(MediaUploadFieldComponent, {
                set: {
                    templateUrl:
                        '/base/src/components/genericEditor/media/components/mediaUploadForm/mediaUploadField/MediaUploadFieldComponent.html'
                }
            })
            .overrideComponent(MediaFormatComponent, {
                set: {
                    templateUrl:
                        '/base/src/components/genericEditor/media/components/mediaFormat/MediaFormatComponent.html',
                    styleUrls: [
                        '/base/src/components/genericEditor/media/components/mediaFormat/MediaFormatComponent.scss'
                    ],
                    encapsulation: ViewEncapsulation.None,
                    providers: [
                        { provide: IMediaService, useValue: mediaService },
                        { provide: MediaUtilService, useValue: mediaUtilService },
                        { provide: MEDIA_SELECTOR_I18N_KEY_TOKEN, useValue: i18n_key }
                    ]
                }
            })
            .overrideComponent(MediaContainerComponent, {
                set: {
                    templateUrl:
                        '/base/src/components/genericEditor/media/components/mediaContainer/MediaContainerComponent.html',
                    styleUrls: [
                        '/base/src/components/genericEditor/media/components/mediaContainer/MediaContainerComponent.scss'
                    ],
                    providers: [
                        { provide: SystemEventService, useValue: systemEventService },
                        { provide: LogService, useValue: logService },
                        {
                            provide: TypePermissionsRestService,
                            useValue: typePermissionsRestService
                        },
                        { provide: LoadConfigManagerService, useValue: loadConfigManagerService },
                        { provide: IFileValidation, useValue: fileValidationService },
                        { provide: ISharedDataService, useValue: sharedDataService },
                        { provide: GENERIC_EDITOR_WIDGET_DATA, useValue: mediaContainerWidgetData }
                    ]
                }
            })
            .compileComponents();

        fixture = TestBed.createComponent(MediaContainerComponent);
        component = fixture.componentInstance;

        fixture.detectChanges();
        nativeElement = fixture.debugElement.nativeElement;
    }

    beforeEach(async () => {
        await createMediaContainerComponentContext();
    });

    afterEach(() => {
        TestBed.resetTestingModule();
    });

    it('Should create MediaContainerComponent correctly', () => {
        expect(component).toBeDefined();
    });

    it('Should hasReadPermissionOnMediaRelatedTypes is true', () => {
        fixture.detectChanges();
        expect(component.hasReadPermissionOnMediaRelatedTypes).toEqual(true);
    });

    it(
        'WHEN I select an inflection point and select a file to upload ' +
            'THEN I expect to see the media upload form populated',
        () => {
            const mockFile = {
                name: fileName
            } as File;
            component.image = {
                file: mockFile,
                format: MediaFormatType.desktop
            };

            fixture.detectChanges();
            expect(nativeElement.querySelector('.se-media-upload-form')).toBeTruthy();
            expect(nativeElement.querySelectorAll('se-media-upload-field')).toBeTruthy();
            expect(nativeElement.querySelectorAll('se-media-upload-field').length).toEqual(3);

            const description: DebugElement = fixture.debugElement.query(
                By.css('se-media-upload-field[ng-reflect-field-name="description"]')
            );
            expect(description).toBeTruthy();
            expect(description.nativeElement.getAttribute('ng-reflect-field-value')).toEqual(
                fileName
            );

            const code: DebugElement = fixture.debugElement.query(
                By.css('se-media-upload-field[ng-reflect-field-name="code"]')
            );
            expect(code).toBeTruthy();
            expect(code.nativeElement.getAttribute('ng-reflect-field-value')).toEqual(fileName);

            const altText: DebugElement = fixture.debugElement.query(
                By.css('se-media-upload-field[ng-reflect-field-name="alt-text"]')
            );
            expect(altText).toBeTruthy();
            expect(altText.nativeElement.getAttribute('ng-reflect-field-value')).toEqual(fileName);
        }
    );

    it(
        'WHEN I select an inflection point and attempt to upload an invalid file   ' +
            'THEN I expect to see the errors populated',
        () => {
            const mockFile = new File([''], fileName, { type: 'html/text' });
            component.image = {
                file: mockFile,
                format: MediaFormatType.desktop
            };

            fixture.detectChanges();
            const submit: HTMLButtonElement = nativeElement.querySelector(
                '#se-media-upload-btn-id__submit'
            );
            submit.click();
            fixture.detectChanges();

            expect(nativeElement.querySelector('.upload-field-error--code')).toBeTruthy();
            expect(nativeElement.querySelector('.upload-field-error--code').innerHTML).toEqual(
                'se.upload.file.type.invalid'
            );
        }
    );

    it(
        'WHEN I select an inflection point with no image selected and upload  ' +
            'THEN I expect to see that inflection point updated with the newly uploaded image',
        async () => {
            component.model[component.lang].medias = {
                widescreen: image
            };

            await fixture.whenStable();
            fixture.detectChanges();

            const fixMediaFormat = fixture.debugElement.query(By.css('se-media-format'));
            const formatComponent: MediaFormatComponent = fixMediaFormat.componentInstance;

            await formatComponent.ngOnInit();
            await fixture.whenStable();
            fixture.detectChanges();

            const previewImage: HTMLImageElement = nativeElement.querySelector(
                '.se-media-preview__image'
            );
            expect(previewImage).toBeTruthy();
            expect(previewImage.src).toContain(testUrl);

            const previewImageThumbnail: HTMLImageElement = nativeElement.querySelector(
                '.se-media-preview__image-thumbnail'
            );

            expect(previewImageThumbnail).toBeTruthy();
            expect(previewImageThumbnail.src).toContain(testUrl);

            expect(nativeElement.querySelector('se-media-remove-button')).toBeTruthy();
        }
    );

    describe('Advanced Media Container', () => {
        beforeEach(async () => {
            loadConfigManagerService.loadAsObject.and.returnValue(
                Promise.resolve({ advancedMediaContainerManagement: true })
            );
            await fixture.whenStable();
            fixture.detectChanges();
        });

        it('WHEN switched to Advanced Media Container THEN should display a list of media containers', () => {
            // THEN
            expect(nativeElement.querySelector('se-media-container-selector')).toBeTruthy();
        });
    });

    describe('initialization', () => {
        beforeEach(() => {
            loadConfigManagerService.loadAsObject.and.returnValue(
                Promise.resolve({
                    advancedMediaContainerManagement: true
                })
            );
        });

        it('GIVEN component is cloned in advanced media management mode WHEN has mediaContainerUuid THEN media container name is prefixed', async () => {
            (component as any).editor.initialContent = {
                cloneComponent: true
            };

            component.model.en.qualifier = 'existing_qualifier';
            component.model.en.mediaContainerUuid = 'uuid';
            const onMediaContainerNameChangeSpy = spyOn(component, 'onMediaContainerNameChange');
            const sessionStorageSet = spyOn(window.sessionStorage, 'setItem');
            const sessionStorageGet = spyOn(window.sessionStorage, 'getItem');

            await component.ngOnInit();

            expect(onMediaContainerNameChangeSpy).toHaveBeenCalled();
            expect(sessionStorageSet).toHaveBeenCalled();
            expect(sessionStorageGet).toHaveBeenCalled();
            expect(component.initialMediaContainerName).toEqual(component.model.en.qualifier);
        });

        it('GIVEN component is cloned in advanced media management mode WHEN has no mediaContainerUuid THEN should not clone mediaContainer', async () => {
            (component as any).editor.initialContent = {
                cloneComponent: true
            };

            component.model.en.qualifier = 'existing_qualifier';
            const onMediaContainerNameChangeSpy = spyOn(component, 'onMediaContainerNameChange');

            await component.ngOnInit();

            expect(onMediaContainerNameChangeSpy).not.toHaveBeenCalled();
        });

        it('GIVEN user no read permission on any of the media types WHEN initialized THEN hasReadPermissionOnMediaRelatedTypes will be set to false', async () => {
            typePermissionsRestService.hasAllPermissionsForTypes.and.returnValue(
                Promise.resolve({
                    MediaContainer: {
                        read: false,
                        create: true,
                        change: true,
                        remove: true
                    },
                    MediaFormat: {
                        read: true,
                        create: false,
                        change: true,
                        remove: true
                    }
                })
            );

            await component.ngOnInit();

            expect(component.hasReadPermissionOnMediaRelatedTypes).toBe(false);
        });
    });

    describe('onFileSelect', () => {
        const mockFile = {
            name: 'someName'
        } as File;
        it('GIVEN file is selected AND file is valid file object THEN it sets the image', async () => {
            const files = [mockFile] as unknown as FileList;

            fileValidationService.validate.and.returnValue(Promise.resolve());

            await component.onFileSelect(files);

            expect(component.fileValidationErrors).toEqual([]);
            expect(component.image).toEqual({
                file: mockFile,
                format: undefined
            });
        });

        it('GIVEN file is selected AND file is valid file string THEN it sets the image string', async () => {
            const fileStr = 'fileAddress';
            await component.onFileSelect(fileStr);

            expect(component.fileValidationErrors).toEqual([]);
            expect(component.image).toEqual({
                file: fileStr,
                format: undefined
            });
        });

        it('GIVEN file is selected AND file is invalid THEN it clears image AND sets validation errors', async () => {
            const files = [mockFile] as unknown as FileList;
            const mockError: ErrorContext = {
                message: '',
                subject: 'code'
            };

            fileValidationService.validate.and.callFake((_file, _any, errorsContext) => {
                errorsContext.push(mockError);
                return Promise.reject(errorsContext);
            });

            await component.onFileSelect(files);

            expect(component.image).toBeNull();
            expect(component.fileValidationErrors).toEqual([mockError]);
        });
    });

    it('onImageUploadSuccess WHEN called THEN it sets uploaded file uuid for given media format AND resets the image AND file validation errors', () => {
        component.onFileUploadSuccess('123', MediaFormatType.widescreen);

        expect(component.model[component.lang].medias[MediaFormatType.widescreen]).toEqual('123');

        expect(component.fileValidationErrors).toEqual([]);
        expect(component.image).toBeNull();
    });

    it('onMediaContainerCreate WHEN called THEN model of given language is cleared AND container name is set AND medias are reset', () => {
        const name = 'new media container';
        component.onMediaContainerCreate(name);

        expect(component.model[component.lang].qualifier).toBe(name);
        expect(component.model[component.lang].medias).toEqual({});
    });

    it('onMediaContainerRemove WHEN called THEN model is cleared', () => {
        const mediaContainer = component.model[component.lang];

        component.onMediaContainerRemove();

        // properties are removed, object should be the same
        expect(component.model[component.lang]).toBe(mediaContainer);

        expect(component.model[component.lang].catalogVersion).toBeUndefined();
        expect(component.model[component.lang].medias).toEqual({});
        expect(component.model[component.lang].qualifier).toBeUndefined();
        expect(component.model[component.lang].mediaContainerUuid).toBeUndefined();
    });
});
