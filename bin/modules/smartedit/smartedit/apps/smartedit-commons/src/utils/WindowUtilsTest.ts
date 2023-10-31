/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { WindowUtils } from 'smarteditcommons';

describe('Windows Utils Test - getTargetFrame', function () {
    let windowUtils: WindowUtils;
    let $window: any;
    let isIframeMock: jasmine.Spy;
    let getSmarteditIframe: jasmine.Spy;
    const iframeWhateverId = 'whatever id';

    beforeEach(() => {
        $window = {
            addEventListener: jasmine.createSpy('addEventListener'),
            document: jasmine.createSpyObj('document', ['getElementById']),
            parent: {
                document: jasmine.createSpyObj('document', ['querySelector'])
            },
            top: {
                document: jasmine.createSpyObj('document', ['querySelector'])
            }
        };

        windowUtils = new WindowUtils();
        spyOn(windowUtils, 'getWindow').and.returnValue($window);
        isIframeMock = spyOn(windowUtils, 'isIframe');
        getSmarteditIframe = spyOn(windowUtils, 'getSmarteditIframe');
    });

    it('Cypress: SHOULD return the parent frame if called within the iframe', function () {
        $window.top.document.querySelector.and.returnValue({
            id: WindowUtils.CYPRESS_IFRAME_ID_9
        });
        $window.parent.document.querySelector
            .withArgs('iframe#' + WindowUtils.SMARTEDIT_IFRAME_ID)
            .and.returnValue(document.createElement('div'));
        const targetFrame = windowUtils.getGatewayTargetFrame();
        expect(targetFrame).toBe($window.parent);
    });

    it('Cypress: SHOULD return the iframe if called from the parent window', function () {
        const contentWindowContent = 'TestContentWindow' as any;

        $window.top.document.querySelector.and.returnValue({
            id: WindowUtils.CYPRESS_IFRAME_ID_9
        });
        $window.parent.document.querySelector
            .withArgs('iframe#' + WindowUtils.SMARTEDIT_IFRAME_ID)
            .and.returnValue(undefined);

        $window.document.getElementById.and.returnValue({
            contentWindow: contentWindowContent
        });

        const targetFrame = windowUtils.getGatewayTargetFrame();
        expect($window.document.getElementById).toHaveBeenCalledWith('ySmartEditFrame');
        expect(targetFrame).toBe(contentWindowContent);
    });

    it('SHOULD return the parent frame if called within the iframe', function () {
        $window.top.document.querySelector.and.returnValue({
            id: iframeWhateverId
        });
        isIframeMock.and.returnValue(true);
        const targetFrame = windowUtils.getGatewayTargetFrame();
        expect(targetFrame).toBe($window.parent);
    });

    it('SHOULD return the iframe if called from the parent window', function () {
        const contentWindowContent = 'TestContentWindow' as any;

        $window.top.document.querySelector.and.returnValue({
            id: iframeWhateverId
        });
        $window.document = jasmine.createSpyObj('document', ['getElementById']);
        $window.document.getElementById.and.returnValue({
            contentWindow: contentWindowContent
        });

        getSmarteditIframe.and.returnValue(true);
        const targetFrame = windowUtils.getGatewayTargetFrame();
        expect($window.document.getElementById).toHaveBeenCalledWith('ySmartEditFrame');
        expect(targetFrame).toBe(contentWindowContent);
    });

    it('SHOULD return null when called from the parent and no iframe exists', function () {
        $window.top.document.querySelector.and.returnValue({
            id: iframeWhateverId
        });
        $window.document = jasmine.createSpyObj('document', ['getElementById']);
        $window.document.getElementById.and.returnValue(null);

        getSmarteditIframe.and.returnValue(true);
        expect(windowUtils.getGatewayTargetFrame()).toBeNull();
    });
});
