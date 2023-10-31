/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { ComponentFixture, TestBed, fakeAsync, waitForAsync } from '@angular/core/testing';

import { TranslateService } from '@ngx-translate/core';
import { MoreTextComponent, TextTruncateService, TruncatedText } from 'smarteditcommons';
import { asyncData, click, advance } from 'testhelpers';

const TEXT_LIMIT_LESS_THEN_TEXT = 10;
const TEXT_LIMIT_MORE_THEN_TEXT = 100;
const TEXT = 'hello, how are you? What time is it now?';
const TRUNCATED_TEXT = 'hello, how';
const ELLIPSIS = '.....';
const CUSTOM_MORE_LABEL_I18N_KEY = 'moreLabel';
const CUSTOM_LESS_LABEL_I18N_KEY = 'lessLabel';

describe('MoreTextComponent', () => {
    let component: MoreTextComponent;
    let fixture: ComponentFixture<MoreTextComponent>;
    let nativeElement: HTMLElement;
    let translate: jasmine.SpyObj<TranslateService>;
    let textTruncateService: jasmine.SpyObj<TextTruncateService>;
    let truncatedText: Partial<TruncatedText>;
    let textPayloadEl: HTMLElement;
    let textToggleEl: HTMLElement;

    beforeEach(waitForAsync(() => {
        translate = jasmine.createSpyObj('translate', ['get']);
        translate.get.and.callFake((i18nKey) => {
            if (i18nKey === CUSTOM_MORE_LABEL_I18N_KEY) {
                return asyncData(CUSTOM_MORE_LABEL_I18N_KEY);
            } else if (i18nKey === CUSTOM_LESS_LABEL_I18N_KEY) {
                return asyncData(CUSTOM_LESS_LABEL_I18N_KEY);
            }
            return asyncData(i18nKey);
        });
        truncatedText = {
            getUntruncatedText() {
                return TEXT;
            },
            getTruncatedText() {
                return TRUNCATED_TEXT + ELLIPSIS;
            },
            isTruncated() {
                return true;
            }
        };
        textTruncateService = jasmine.createSpyObj('textTruncateService', [
            'truncateToNearestWord'
        ]);
        textTruncateService.truncateToNearestWord.and.returnValue(truncatedText as TruncatedText);
        TestBed.configureTestingModule({
            declarations: [MoreTextComponent],
            providers: [
                { provide: TranslateService, useValue: translate },
                { provide: TextTruncateService, useValue: textTruncateService }
            ],
            schemas: [NO_ERRORS_SCHEMA]
        })
            .overrideComponent(MoreTextComponent, {
                set: {
                    styleUrls: ['/base/src/components/moreText/MoreTextComponent.scss']
                }
            })
            .compileComponents()
            .then(() => {
                fixture = TestBed.createComponent(MoreTextComponent);
                component = fixture.componentInstance;
                component.text = TEXT;
                component.limit = TEXT_LIMIT_LESS_THEN_TEXT;
                component.ellipsis = ELLIPSIS;
                nativeElement = fixture.debugElement.nativeElement;
            });
    }));

    it('Should create MoreText Component correctly', () => {
        fixture.detectChanges();
        expect(component).toBeDefined();
    });

    it(
        'GIVEN component with a text containing more than 10 characters AND limit is 10 AND custom ellipsis' +
            'WHEN the MoreLink is clicked ' +
            'THEN the component shows the full text AND the button with "LessLink" title AND custom ellipsis is not shown',
        fakeAsync(() => {
            fixture.detectChanges();
            advance(fixture);
            getNativeElements();
            // GIVEN
            expect(textPayloadEl.textContent).toContain(TRUNCATED_TEXT);
            expect(textToggleEl.textContent).toBe(component.moreLabelI18nKey);
            expect(textPayloadEl.textContent).toContain(ELLIPSIS);

            // WHEN
            click(textToggleEl);
            advance(fixture);

            // THEN
            expect(textToggleEl.textContent).toBe(component.lessLabelI18nKey);
            expect(textPayloadEl.textContent).toContain(TEXT);
        })
    );

    it(
        'GIVEN component shows the full text AND the button with "LessLink" title is shown AND limit is 10' +
            'WHEN the LessLink is clicked ' +
            'THEN the component shows truncated text AND the button with "MoreLink" title',
        fakeAsync(() => {
            fixture.detectChanges();
            advance(fixture);
            getNativeElements();

            // GIVEN
            click(textToggleEl);
            advance(fixture);
            expect(textPayloadEl.textContent).toContain(TEXT);
            expect(textToggleEl.textContent).toBe(component.lessLabelI18nKey);

            // WHEN
            click(textToggleEl);
            advance(fixture);

            // THEN
            expect(textToggleEl.textContent).toBe(component.moreLabelI18nKey);
            expect(textPayloadEl.textContent).toContain(TRUNCATED_TEXT);
            expect(textPayloadEl.textContent).toContain(ELLIPSIS);
        })
    );

    it(
        'GIVEN component shows the full text AND limit is more then the text length ' +
            'THEN the does not show any button',
        fakeAsync(() => {
            component.limit = TEXT_LIMIT_MORE_THEN_TEXT;
            fixture.detectChanges();
            advance(fixture);
            getNativeElements();

            expect(textPayloadEl.textContent).toBe(TEXT);
            expect(textToggleEl).not.toBeTruthy();
        })
    );

    it(
        'GIVEN component with a text containing more than 10 characters AND limit is 10 AND the button with custom MoreLink title is shown ' +
            'WHEN the MoreLink is clicked ' +
            'THEN the shows the full text AND the button with custom LessLink title',
        fakeAsync(() => {
            component.moreLabelI18nKey = CUSTOM_MORE_LABEL_I18N_KEY;
            component.lessLabelI18nKey = CUSTOM_LESS_LABEL_I18N_KEY;
            fixture.detectChanges();
            advance(fixture);
            getNativeElements();

            // GIVEN
            expect(textToggleEl.textContent).toBe(CUSTOM_MORE_LABEL_I18N_KEY);

            // WHEN
            click(textToggleEl);
            advance(fixture);

            // THEN
            expect(textToggleEl.textContent).toBe(CUSTOM_LESS_LABEL_I18N_KEY);
        })
    );

    function getNativeElements() {
        textPayloadEl = nativeElement.querySelector('#y-more-text-payload');
        textToggleEl = nativeElement.querySelector('#y-more-text-toggle');
    }
});
