/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ElementRef } from '@angular/core';
import { IIframeClickDetectionService } from '../../services/interfaces';
import { ClickOutsideDirective } from './ClickOutsideDirective';

describe('Click Outside Directive', () => {
    let directive: ClickOutsideDirective;
    let mockDocument: Document;
    let nativeElement: HTMLElement;
    let nativeElementChild: HTMLElement;
    let notExistingElement: HTMLElement;
    let host: ElementRef;
    let iframeClickDetectionService: jasmine.SpyObj<IIframeClickDetectionService>;

    beforeEach(() => {
        mockDocument = document.implementation.createHTMLDocument();

        nativeElement = mockDocument.createElement('div');
        nativeElementChild = mockDocument.createElement('span');
        notExistingElement = mockDocument.createElement('div');

        nativeElement.appendChild(nativeElementChild);
        host = { nativeElement };

        mockDocument.documentElement.appendChild(nativeElement);

        iframeClickDetectionService = jasmine.createSpyObj('iframeClickDetectionService', [
            'registerCallback',
            'removeCallback'
        ]);

        directive = new ClickOutsideDirective(mockDocument, host, iframeClickDetectionService);
    });

    it('Document click will return undefined when target is a host element', () => {
        const emit = spyOn(directive.clickOutside, 'emit');

        expect(directive.onDocumentClick(nativeElement)).toBeUndefined();
        expect(emit).not.toHaveBeenCalled();
    });

    it('Document click will return undefined when target is a child of the host', () => {
        const emit = spyOn(directive.clickOutside, 'emit');

        expect(directive.onDocumentClick(nativeElementChild)).toBeUndefined();
        expect(emit).not.toHaveBeenCalled();
    });

    it('Document click will return undefined when target does not exist in DOM', () => {
        const emit = spyOn(directive.clickOutside, 'emit');

        expect(directive.onDocumentClick(notExistingElement)).toBeUndefined();
        expect(emit).not.toHaveBeenCalled();
    });

    it('Document click will trigger event to be emitted if target exists in DOM but it is not a host or a child of the host', () => {
        const emit = spyOn(directive.clickOutside, 'emit');
        const element = mockDocument.createElement('p');
        mockDocument.documentElement.appendChild(element);

        directive.onDocumentClick(element);
        expect(emit).toHaveBeenCalled();
    });

    it('registers event emitting callback on init', () => {
        directive.ngOnInit();

        expect(iframeClickDetectionService.registerCallback).toHaveBeenCalled();
    });

    it('removes event emitting callback on destroy', () => {
        directive.ngOnDestroy();

        expect(iframeClickDetectionService.removeCallback).toHaveBeenCalled();
    });
});
