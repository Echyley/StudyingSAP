/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Inject, Injector, NO_ERRORS_SCHEMA, SimpleChange } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { ButtonModule } from '@fundamental-ngx/core';
import { TranslateModule } from '@ngx-translate/core';
import {
    MediaFileSelectorComponent,
    MediaFormatComponent
} from 'cmssmarteditcontainer/components/genericEditor/media/components';
import { MediaUtilService } from 'cmssmarteditcontainer/components/genericEditor/media/services';
import { MEDIA_SELECTOR_I18N_KEY_TOKEN, IMediaService } from 'smarteditcommons';
import { FakeMediaFormatUploadedComponent } from '../mockComponents';

describe('MediaFormatComponent', () => {
    let component: MediaFormatComponent;
    let fixture: ComponentFixture<MediaFormatComponent>;
    let nativeElement: HTMLElement;

    let mediaService: jasmine.SpyObj<IMediaService>;
    let mediaUtilService: jasmine.SpyObj<MediaUtilService>;
    let inject: jasmine.SpyObj<Injector>;
    const i18n_key = {
        UPLOAD: 'se.media.format.upload',
        REPLACE: 'se.media.format.replace',
        UNDER_EDIT: 'se.media.format.under.edit',
        REMOVE: 'se.media.format.remove'
    };

    // valid url to avoid error in console.
    const testURL =
        '/base/src/components/genericEditor/media/components/mediaFormat/MediaFormatComponent.html';

    async function createMediaFormatComponentContext() {
        await TestBed.configureTestingModule({
            imports: [TranslateModule.forRoot(), ButtonModule],
            declarations: [
                MediaFormatComponent,
                FakeMediaFormatUploadedComponent,
                MediaFileSelectorComponent
            ],
            providers: [
                { provide: IMediaService, useValue: mediaService },
                { provide: MediaUtilService, useValue: mediaUtilService },
                { provide: MEDIA_SELECTOR_I18N_KEY_TOKEN, useValue: i18n_key }
            ],
            schemas: [NO_ERRORS_SCHEMA]
        })
            .overrideComponent(MediaFormatComponent, {
                set: {
                    templateUrl:
                        '/base/src/components/genericEditor/media/components/mediaFormat/MediaFormatComponent.html',
                    styleUrls: [
                        '/base/src/components/genericEditor/media/components/mediaFormat/MediaFormatComponent.scss'
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
                    providers: [
                        { provide: Inject, useValue: inject }
                        // { provide: MEDIA_FILE_SELECTOR_CUSTOM_TOKEN, useValue: mediaFileSelectorCustom }
                    ]
                }
            })
            .compileComponents();

        fixture = TestBed.createComponent(MediaFormatComponent);
        component = fixture.componentInstance;

        component.mediaFormat = 'Desktop';
        component.mediaLabel = 'testMediaLabel';
        fixture.detectChanges();
        nativeElement = fixture.debugElement.nativeElement;
    }

    beforeEach(async () => {
        mediaService = jasmine.createSpyObj<IMediaService>('mediaService', ['getMedia']);
        mediaUtilService = jasmine.createSpyObj<MediaUtilService>('mediaUtilService', [
            'getAcceptedFileTypes'
        ]);
        await createMediaFormatComponentContext();
    });

    afterEach(() => {
        TestBed.resetTestingModule();
    });

    it('Should create MediaFormatComponent Component correctly', () => {
        expect(component).toBeDefined();
    });

    it('should display the format', () => {
        expect(nativeElement.querySelector('.se-media-format__screen-type').innerHTML).toContain(
            component.mediaFormat
        );
    });

    it('GIVEN mediaUuid WHEN initialized THEN it fetches and sets the media', async () => {
        const uuid = '123';
        component.mediaUuid = uuid;
        const media = {
            id: 'testId',
            code: 'testCode',
            description: 'testDescription',
            altText: 'testAltText',
            url: testURL,
            downloadUrl: 'testDownloadUrl',
            mime: 'svg'
        };

        mediaService.getMedia.and.returnValue(Promise.resolve(media));
        await component.ngOnInit();

        expect(component.media).toBe(media);
        expect(mediaService.getMedia).toHaveBeenCalledWith(uuid);
    });

    it('GIVEN mediaUuid has changed AND is first change THEN should not fetch and set the media', () => {
        component.ngOnChanges({
            mediaUuid: new SimpleChange(undefined, '123', true)
        });

        expect(component.media).toBeNull();
        expect(mediaService.getMedia).not.toHaveBeenCalled();
    });

    it('GIVEN mediaUuid has changed AND it is not a first change THEN it fetches and sets the media properly', async () => {
        const uuid = '123';
        const media = {
            id: 'testId',
            code: 'testCode',
            description: 'testDescription',
            altText: 'testAltText',
            url: testURL,
            downloadUrl: 'testDownloadUrl',
            mime: 'svg'
        };

        mediaService.getMedia.and.returnValue(Promise.resolve(media));
        component.mediaUuid = uuid;
        await component.ngOnChanges({
            mediaUuid: new SimpleChange(undefined, uuid, false)
        });
        fixture.detectChanges();
        expect(component.media).toBe(media);
        expect(mediaService.getMedia).toHaveBeenCalledWith(uuid);
    });

    describe('GIVEN media uuid is present', () => {
        beforeEach(() => {
            component.mediaUuid = 'testUuid';
            component.isUnderEdit = false;
            component.media = {
                id: 'testId',
                code: 'testCode',
                description: 'testDescription',
                altText: 'testAltText',
                url: testURL,
                downloadUrl: 'testDownloadUrl',
                mime: 'svg'
            };

            fixture.detectChanges();
        });

        it('should show the media present view', () => {
            expect(nativeElement.querySelector('se-media-format-uploaded')).toBeTruthy();
            expect(nativeElement.querySelector('.se-media--present')).toBeTruthy();
            expect(nativeElement.querySelector('.se-media--absent')).toBeNull();
        });
    });

    describe('GIVEN media uuid is absent', () => {
        beforeEach(() => {
            component.mediaUuid = null;
            component.isEditable = true;
            component.mediaSelectorI18nKeys.UPLOAD = 'Upload';
            component.acceptedFileTypes = ['jpg'];
            fixture.detectChanges();
        });

        it('THEN the media absent view is shown', () => {
            expect(nativeElement.querySelector('se-media-format-uploaded')).toBeNull();
            expect(nativeElement.querySelector('.se-media--present')).toBeNull();
            expect(nativeElement.querySelector('.se-media--absent')).toBeTruthy();
            expect(nativeElement.querySelector('se-media-file-selector')).toBeTruthy();
        });

        it('THEN Upload Button is shown', () => {
            expect(nativeElement.querySelector('.se-file-selector__label')).toBeTruthy();
            expect(nativeElement.querySelector('.se-file-selector__label').innerHTML).toEqual(
                component.mediaSelectorI18nKeys.UPLOAD
            );

            expect(nativeElement.querySelector('input')).toBeTruthy();
            expect(nativeElement.querySelector('input').type).toEqual('file');
            expect(nativeElement.querySelector('input').accept).toContain(
                component.acceptedFileTypes[0]
            );
        });

        describe('AND field.editable is false', () => {
            beforeEach(() => {
                component.isEditable = false;
                fixture.detectChanges();
            });

            it('THEN File Selector should be disabled by css class', () => {
                expect(nativeElement.querySelector('.file-selector-disabled')).toBeTruthy();
            });
        });
    });

    describe('GIVEN the file is under edit', () => {
        beforeEach(() => {
            component.mediaUuid = 'testMediaUuid';
            component.isUnderEdit = true;
            fixture.detectChanges();
        });

        it('THEN the media uploading view is shown', () => {
            expect(nativeElement.querySelector('se-media-format-uploaded')).toBeNull();
            expect(nativeElement.querySelector('.se-media--present')).toBeNull();
            expect(nativeElement.querySelector('.se-media--absent')).toBeNull();
            expect(nativeElement.querySelector('.se-media--edit')).toBeTruthy();
        });

        it('THEN Editing Text is shown', () => {
            expect(nativeElement.querySelector('.se-media-preview--edit')).toBeTruthy();
            expect(nativeElement.querySelector('.se-media-preview--edit').innerHTML).toEqual(
                i18n_key.UNDER_EDIT
            );
        });
    });

    describe('GIVEN media uuid is present AND field is disabled', () => {
        beforeEach(() => {
            component.mediaUuid = 'testMediaUuid';
            component.isFieldDisabled = true;
            component.isEditable = true;
            component.isUnderEdit = false;
            component.media = {
                id: 'testId',
                code: 'testCode',
                description: 'testDescription',
                altText: 'testAltText',
                url: testURL,
                downloadUrl: 'testDownloadUrl',
                mime: 'svg'
            };
            fixture.detectChanges();
        });

        it('THEN Media File Selector is disabled by class', () => {
            expect(nativeElement.querySelector('se-media-file-selector')).toBeNull();
        });

        it('THEN Remove Button is disabled', () => {
            const mediaFormatUploadedComponent: FakeMediaFormatUploadedComponent =
                fixture.debugElement.query(
                    By.directive(FakeMediaFormatUploadedComponent)
                ).componentInstance;
            expect(mediaFormatUploadedComponent).toBeTruthy();
            expect(mediaFormatUploadedComponent).toBeTruthy();
            expect(mediaFormatUploadedComponent.isFieldDisabled).toEqual(true);
        });
    });
});
