/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By, DomSanitizer } from '@angular/platform-browser';
import { TranslateModule } from '@ngx-translate/core';
import {
    MediaAdvancedPropertiesComponent,
    MediaFormatUploadedComponent,
    MediaPreviewComponent
} from 'cmssmarteditcontainer/components/genericEditor/media/components';

describe('MediaFormatUploadedComponent', () => {
    let component: MediaFormatUploadedComponent;
    let fixture: ComponentFixture<MediaFormatUploadedComponent>;
    let nativeElement: HTMLElement;

    // valid url to avoid error in console.
    const testURL =
        '/base/src/components/genericEditor/media/components/mediaFormat/mediaFormatUploaded/MediaFormatUploadedComponent.html';

    async function createMediaFormatUploadedComponentContext() {
        await TestBed.configureTestingModule({
            imports: [TranslateModule.forRoot()],
            declarations: [
                MediaFormatUploadedComponent,
                MediaAdvancedPropertiesComponent,
                MediaPreviewComponent
            ],
            providers: [
                {
                    provide: DomSanitizer,
                    useValue: {
                        sanitize: (ctx: any, val: string) => val,
                        bypassSecurityTrustResourceUrl: (val: string) => val
                    }
                }
            ],
            schemas: [NO_ERRORS_SCHEMA]
        })
            .overrideComponent(MediaFormatUploadedComponent, {
                set: {
                    templateUrl:
                        '/base/src/components/genericEditor/media/components/mediaFormat/mediaFormatUploaded/MediaFormatUploadedComponent.html',
                    styleUrls: [
                        '/base/src/components/genericEditor/media/components/mediaFormat/mediaFormatUploaded/MediaFormatUploadedComponent.scss'
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
                    ]
                }
            })
            .compileComponents();

        fixture = TestBed.createComponent(MediaFormatUploadedComponent);
        component = fixture.componentInstance;

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
        nativeElement = fixture.debugElement.nativeElement;
    }

    beforeEach(async () => {
        await createMediaFormatUploadedComponentContext();
    });

    afterEach(() => {
        TestBed.resetTestingModule();
    });

    it('Should create MediaFormatUploadedComponent Component correctly', () => {
        expect(component).toBeDefined();
    });

    it('WHEN I select a valid image AND upload successfully THEN I expect to see the image selector dropdown with the newly uploaded image', () => {
        const image: HTMLImageElement = nativeElement.querySelector('img');
        expect(image).toBeTruthy();
        expect(image.src).toContain(testURL);
    });

    it(
        'GIVEN a media is selected ' +
            'WHEN I click the advanced information ' +
            'THEN I expect to see a popover with the alt text, code and description in it.',
        () => {
            const description: HTMLElement = nativeElement.querySelector(
                '#se-media-advanced-info-description-id'
            );
            const code: HTMLElement = nativeElement.querySelector(
                '#se-media-advanced-info-code-id'
            );
            const altText: HTMLElement = nativeElement.querySelector(
                '#se-media-advanced-info-altText-id'
            );

            expect(description).toBeTruthy();
            expect(description.innerHTML).toContain(component.media.description);

            expect(code).toBeTruthy();
            expect(code.innerHTML).toContain(component.media.code);

            expect(altText).toBeTruthy();
            expect(altText.innerHTML).toContain(component.media.altText);
        }
    );

    it('GIVEN a media is selected WHEN I click the preview button THEN I expect to see a popover with the image in it.', () => {
        const mediaPreviewFixture: MediaPreviewComponent = fixture.debugElement.query(
            By.directive(MediaPreviewComponent)
        ).componentInstance;

        const mediaPreview: HTMLImageElement = nativeElement
            .querySelector('se-media-preview')
            .querySelector('img');

        expect(mediaPreviewFixture).toBeTruthy();
        expect(mediaPreviewFixture.imageUrl).toContain(testURL);

        expect(mediaPreview).toBeTruthy();
        expect(mediaPreview.src).toContain(testURL);
    });

    it('should show Remove Button WHEN field is disable', () => {
        component.isFieldDisabled = true;
        fixture.detectChanges();

        expect(nativeElement.querySelector('se-media-remove-button')).toBeTruthy();
    });
});
