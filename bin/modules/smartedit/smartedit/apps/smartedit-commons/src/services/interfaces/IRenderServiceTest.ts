/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { LogService } from '@smart/utils';
import { WindowUtils } from 'smarteditcommons/utils';
import { jQueryHelper } from 'testhelpers';
import { CrossFrameEventService } from '../crossFrame/CrossFrameEventService';
import { ModalService } from '../modal';
import { IPerspectiveService } from '../perspectives/IPerspectiveService';
import { SystemEventService } from '../SystemEventService';
import { INotificationService } from './INotificationService';
import { IPageInfoService } from './IPageInfoService';
import { IRenderService } from './IRenderService';

class MockRenderService extends IRenderService {
    constructor(
        _yjQuery: JQueryStatic,
        _systemEventService: SystemEventService,
        _notificationService: INotificationService,
        _pageInfoService: IPageInfoService,
        _perspectiveService: IPerspectiveService,
        _crossFrameEventService: CrossFrameEventService,
        _windowUtils: WindowUtils,
        _modalService: ModalService,
        _logService: LogService
    ) {
        super(
            _yjQuery,
            _systemEventService,
            _notificationService,
            _pageInfoService,
            _perspectiveService,
            _crossFrameEventService,
            _windowUtils,
            _modalService,
            _logService
        );
    }
}

let renderService: MockRenderService;

let yjQuery: any;

let systemEventService: jasmine.SpyObj<SystemEventService>;
let notificationService: jasmine.SpyObj<INotificationService>;
let pageInfoService: jasmine.SpyObj<IPageInfoService>;
let perspectiveService: jasmine.SpyObj<IPerspectiveService>;
let crossFrameEventService: jasmine.SpyObj<CrossFrameEventService>;
let modalService: jasmine.SpyObj<ModalService>;
let logService: jasmine.SpyObj<LogService>;
let mockDocument: jasmine.SpyObj<Document>;

const windowUtils = new WindowUtils();

const ESC_KEY_CODE = 27;
const HOTKEY_NOTIFICATION_ID = 'HOTKEY_NOTIFICATION_ID';

describe('IRenderService abstract class', () => {
    beforeEach(() => {
        buildCommonSpyObjects();
    });

    it('IRenderService declares the expected set of empty functions', () => {
        expect(IRenderService.prototype.renderComponent).toBeEmptyFunction();
        expect(IRenderService.prototype.renderRemoval).toBeEmptyFunction();
        expect(IRenderService.prototype.toggleOverlay).toBeEmptyFunction();
        expect(IRenderService.prototype.refreshOverlayDimensions).toBeEmptyFunction();
        expect(IRenderService.prototype.blockRendering).toBeEmptyFunction();
        expect(IRenderService.prototype.isRenderingBlocked).toBeEmptyFunction();
    });

    it('IRenderService initializes successfully and binds events', () => {
        spyOn(IRenderService.prototype as any, '_bindEvents').and.callThrough();

        initService();

        expect(mockDocument.addEventListener.calls.count()).toBe(2);
        expect(mockDocument.addEventListener.calls.argsFor(0)).toEqual([
            'keyup',
            jasmine.any(Function)
        ]);
        expect(mockDocument.addEventListener.calls.argsFor(1)).toEqual([
            'click',
            jasmine.any(Function)
        ]);
    });

    it('WHEN ESC key is pressed in a non storefront view THEN _keyPressEvent is not triggered', () => {
        pageInfoService.getPageUUID.and.returnValue(
            Promise.reject({
                name: 'InvalidStorefrontPageError'
            })
        );

        spyOn(IRenderService.prototype as any, '_keyPressEvent');

        initService();

        (renderService as any)._keyUpEventHandler(getEventByKeyCode(ESC_KEY_CODE));

        expect((renderService as any)._keyPressEvent).not.toHaveBeenCalled();
    });

    it('WHEN ESC key is pressed in storefront view with no perspective set THEN _keyPressEvent is not triggered', () => {
        pageInfoService.getPageUUID.and.returnValue(Promise.resolve('somePageUuid'));
        perspectiveService.isHotkeyEnabledForActivePerspective.and.returnValue(
            Promise.resolve(false)
        );
        initService();

        spyOn(renderService as any, '_keyPressEvent');

        (renderService as any)._keyUpEventHandler(getEventByKeyCode(ESC_KEY_CODE));

        expect((renderService as any)._keyPressEvent).not.toHaveBeenCalled();
    });

    it('WHEN NON-ESC key is pressed THEN _keyPressEvent is not triggered', () => {
        pageInfoService.getPageUUID.and.returnValue(Promise.resolve('somePageUuid'));
        perspectiveService.isHotkeyEnabledForActivePerspective.and.returnValue(
            Promise.resolve(true)
        );

        initService();

        spyOn(renderService as any, '_keyPressEvent');

        (renderService as any)._keyUpEventHandler(getEventByKeyCode(17)); // press other key than ESC

        expect((renderService as any)._keyPressEvent).not.toHaveBeenCalled();
    });

    it('WHEN ESC key is pressed in storefront view with some perspective set THEN _keyPressEvent is triggered', (done) => {
        pageInfoService.getPageUUID.and.returnValue(Promise.resolve('somePageUuid'));
        perspectiveService.isHotkeyEnabledForActivePerspective.and.returnValue(
            Promise.resolve(true)
        );

        initService();

        spyOn(renderService as any, '_keyPressEvent');

        (renderService as any)
            ._keyUpEventHandler(getEventByKeyCode(ESC_KEY_CODE))
            .then(function () {
                expect((renderService as any)._keyPressEvent).toHaveBeenCalled();
                done();
            });
    });

    it('GIVEN when a modal window is open WHEN ESC key is pressed THEN nothing happens', () => {
        spyOn(IRenderService.prototype, 'isRenderingBlocked').and.returnValue(
            Promise.resolve(true)
        );
        spyOn(IRenderService.prototype, 'blockRendering');
        spyOn(IRenderService.prototype, 'renderPage');
        spyOn(IRenderService.prototype as any, '_shouldEnableKeyPressEvent').and.returnValue(
            Promise.resolve(true)
        );
        modalService.hasOpenModals.and.returnValue(true);

        initService();

        (renderService as any)._keyUpEventHandler();

        expect(renderService.blockRendering).not.toHaveBeenCalled();
        expect(renderService.renderPage).not.toHaveBeenCalled();
        expect(notificationService.pushNotification).not.toHaveBeenCalled();
        expect(notificationService.removeNotification).not.toHaveBeenCalled();
    });

    it('GIVEN when the rendering is not blocked WHEN Click event is triggered THEN nothing happens', () => {
        spyOn(IRenderService.prototype, 'isRenderingBlocked').and.returnValue(
            Promise.resolve(false)
        );
        spyOn(IRenderService.prototype, 'blockRendering');
        spyOn(IRenderService.prototype, 'renderPage');

        initService();

        (renderService as any)._clickEvent();

        expect(renderService.blockRendering).not.toHaveBeenCalled();
        expect(renderService.renderPage).not.toHaveBeenCalled();
        expect(notificationService.removeNotification).not.toHaveBeenCalled();
    });

    describe('GIVEN when all modal window are closed and the rendering is ', () => {
        it('already blocked WHEN ESC key is pressed THEN rendering is unblocked, renderPage is called to re-render the overlay AND the hotkey notification is hidden', async () => {
            spyOn(IRenderService.prototype, 'isRenderingBlocked').and.returnValue(
                Promise.resolve(true)
            );
            spyOn(IRenderService.prototype, 'blockRendering');
            spyOn(IRenderService.prototype, 'renderPage');
            modalService.hasOpenModals.and.returnValue(false);

            initService();

            await (renderService as any)._keyPressEvent();

            expect(renderService.blockRendering).toHaveBeenCalledWith(false);
            expect(renderService.renderPage).toHaveBeenCalledWith(true);
            expect(notificationService.removeNotification).toHaveBeenCalledWith(
                HOTKEY_NOTIFICATION_ID
            );
        });

        it('not blocked WHEN ESC key is pressed THEN rendering is blocked, renderPage is called but without re-rendering the overlay, an event is triggered and the hotkey notification is shown', async () => {
            spyOn(IRenderService.prototype, 'isRenderingBlocked').and.returnValue(
                Promise.resolve(false)
            );
            spyOn(IRenderService.prototype, 'blockRendering').and.callThrough();
            spyOn(IRenderService.prototype, 'renderPage');

            modalService.hasOpenModals.and.returnValue(false);

            initService();
            await (renderService as any)._keyPressEvent();

            expect(renderService.blockRendering).toHaveBeenCalledWith(true);
            expect(renderService.renderPage).toHaveBeenCalledWith(false);
            expect(systemEventService.publishAsync).toHaveBeenCalledWith('OVERLAY_DISABLED');
            expect(notificationService.pushNotification).toHaveBeenCalledWith(
                (renderService as any).HOTKEY_NOTIFICATION_CONFIGURATION
            );
        });
    });

    describe('GIVEN when the rendering is blocked ', () => {
        beforeEach(() => {
            spyOn(IRenderService.prototype, 'isRenderingBlocked').and.returnValue(
                Promise.resolve(true)
            );
            spyOn(IRenderService.prototype, 'blockRendering');
            spyOn(IRenderService.prototype, 'renderPage');
        });

        it('WHEN Click event is triggered outside of the frame THEN rendering is unblocked, renderPage is called to re-render the overlay and the hotkey notification is hidden', async () => {
            spyOn(windowUtils, 'isIframe').and.returnValue(false);

            initService();

            await (renderService as any)._clickEvent();

            expect(IRenderService.prototype.blockRendering).toHaveBeenCalledWith(false);
            expect(IRenderService.prototype.renderPage).toHaveBeenCalledWith(true);
            expect(notificationService.removeNotification).toHaveBeenCalledWith(
                HOTKEY_NOTIFICATION_ID
            );
        });

        it('WHEN Click event is triggered inside the frame THEN nothing happens', () => {
            spyOn(windowUtils, 'isIframe').and.returnValue(true);
            initService();

            (renderService as any)._clickEvent();

            expect(renderService.blockRendering).not.toHaveBeenCalled();
            expect(renderService.renderPage).not.toHaveBeenCalled();
            expect(notificationService.removeNotification).not.toHaveBeenCalled();
        });

        it('WHEN Click event is triggered outside of the frame THEN a cross frame event is published', () => {
            spyOn(windowUtils, 'isIframe').and.returnValue(false);

            initService();
            (renderService as any)._clickEvent();

            expect(crossFrameEventService.publish).toHaveBeenCalled();
        });
    });
});

function initService() {
    renderService = new MockRenderService(
        yjQuery,
        systemEventService,
        notificationService,
        pageInfoService,
        perspectiveService,
        crossFrameEventService,
        windowUtils,
        modalService,
        logService
    );
}

function getEventByKeyCode(keyCode: number): JQuery.Event {
    const event: JQuery.Event = window.smarteditJQuery.Event('keyup');
    event.which = keyCode;
    return event;
}

function buildCommonSpyObjects() {
    renderService = null;
    notificationService = jasmine.createSpyObj('notificationService', [
        'pushNotification',
        'removeNotification'
    ]);
    systemEventService = jasmine.createSpyObj('systemEventService', ['publishAsync']);
    pageInfoService = jasmine.createSpyObj('pageInfoService', ['getPageUUID']);
    perspectiveService = jasmine.createSpyObj('perspectiveService', [
        'isHotkeyEnabledForActivePerspective'
    ]);
    crossFrameEventService = jasmine.createSpyObj('crossFrameEventService', [
        'publish',
        'subscribe'
    ]);
    crossFrameEventService.publish.and.resolveTo([]);

    modalService = jasmine.createSpyObj('modalService', ['hasOpenModals']);
    modalService.hasOpenModals.and.returnValue(false);

    logService = jasmine.createSpyObj('logService', ['error']);

    yjQuery = jQueryHelper.jQuery();
    yjQuery.and.callFake((arg: any) => {
        if (typeof arg === 'string') {
            arg = jQueryHelper.wrap(arg);
        }
        arg.on = (): void => null;
        return arg;
    });
    mockDocument = jasmine.createSpyObj('document', ['addEventListener']);
    spyOn(IRenderService.prototype as any, '_getDocument').and.returnValue(mockDocument);
}
