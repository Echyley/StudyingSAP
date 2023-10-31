/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injector } from '@angular/core';
import { ComponentFixture, fakeAsync, getTestBed, TestBed, tick } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import {
    LinkToggleComponent,
    LinkToggleDTO
} from 'cmssmarteditcontainer/components/genericEditor/linkToggle';
import { GENERIC_EDITOR_WIDGET_DATA, GenericEditorWidgetData } from 'smarteditcommons';

describe('LinkToggleComponent', () => {
    let component: LinkToggleComponent;
    let fixture: ComponentFixture<LinkToggleComponent>;
    let injectedData: GenericEditorWidgetData<LinkToggleDTO>;

    let injector: Injector;
    let translate: TranslateService;
    let nativeElement: HTMLElement;

    let externalLink: HTMLInputElement;
    let internalLink: HTMLInputElement;
    let urlLink: HTMLInputElement;

    function createLinkToggleComponentContext() {
        fixture = TestBed.createComponent(LinkToggleComponent);
        component = fixture.componentInstance;

        fixture.detectChanges();
        nativeElement = fixture.debugElement.nativeElement;

        externalLink = nativeElement.querySelector('#external-link');
        internalLink = nativeElement.querySelector('#internal-link');
        urlLink = nativeElement.querySelector('#urlLink');
    }

    beforeEach(async () => {
        injectedData = {
            field: { cmsStructureType: 'LinkToggle', qualifier: 'linkToggle' },
            model: { linkToggle: { external: true, urlLink: '/url-link' } }
        } as GenericEditorWidgetData<LinkToggleDTO>;

        await TestBed.configureTestingModule({
            imports: [FormsModule, TranslateModule.forRoot()],
            declarations: [LinkToggleComponent],
            providers: [{ provide: GENERIC_EDITOR_WIDGET_DATA, useValue: injectedData }]
        })
            .overrideComponent(LinkToggleComponent, {
                set: {
                    templateUrl:
                        '/base/src/components/genericEditor/linkToggle/LinkToggleComponent.html',
                    styleUrls: [
                        '/base/src/components/genericEditor/linkToggle/LinkToggleComponent.scss'
                    ]
                }
            })
            .compileComponents();
        createLinkToggleComponentContext();
    });

    it('Should create LinkToggle component correctly', () => {
        expect(component).toBeDefined();
    });

    it('should have the external element at browser', () => {
        expect(nativeElement.querySelector('label[for="external-link"]').textContent).toEqual(
            ' se.editor.linkto.external.label '
        );
    });

    it('should translate a string using the key value', async () => {
        injector = getTestBed();
        translate = injector.get(TranslateService);
        translate.setTranslation('en', { 'se.editor.linkto.external.label': 'external' });
        translate.use('en');

        fixture.detectChanges();
        await fixture.whenStable();

        expect(nativeElement.querySelector('label[for="external-link"]').textContent).toContain(
            'external'
        );
    });

    it('Should init LinkToggle component with urlLink value to "/url-link" and select external', () => {
        expect(component.model.linkToggle.urlLink).toEqual('/url-link');
        expect(component.model.linkToggle.external).toEqual(true);
        expect(externalLink.checked).toEqual(true);
        expect(internalLink.checked).toEqual(false);
        expect(urlLink.value).toEqual('/url-link');
    });

    it('Should remain select external When init model value of external field is undefined', fakeAsync(() => {
        component.model.linkToggle = {};
        createLinkToggleComponentContext();

        tick(1);
        expect(component.model.linkToggle.external).toEqual(true);
        expect(externalLink.checked).toEqual(true);
        expect(internalLink.checked).toEqual(false);
        expect(urlLink.value).toEqual('');
    }));

    it('Should select internal and has urlLink When init model with value of internal field and urlLink', fakeAsync(() => {
        component.model.linkToggle = { external: false, urlLink: '/internalUrl-link' };
        createLinkToggleComponentContext();

        tick(1);
        expect(component.model.linkToggle.external).toEqual(false);
        expect(externalLink.checked).toEqual(false);
        expect(internalLink.checked).toEqual(true);
        expect(urlLink.value).toEqual('/internalUrl-link');
    }));

    it('Should select internal and clear urlLink After click internal check radio', fakeAsync(() => {
        internalLink.click();
        fixture.detectChanges();

        expect(externalLink.checked).toEqual(false);
        expect(internalLink.checked).toEqual(true);

        tick(1);
        expect(component.model.linkToggle.urlLink).toEqual(null);
        expect(component.model.linkToggle.external).toEqual(false);
        expect(urlLink.value).toEqual('');
    }));
});
