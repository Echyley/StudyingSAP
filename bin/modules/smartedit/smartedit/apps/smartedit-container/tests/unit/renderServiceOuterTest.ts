/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    annotationService,
    CrossFrameEventService,
    GatewayProxied,
    INotificationService,
    IPageInfoService,
    IPerspectiveService,
    LogService,
    ModalService,
    SystemEventService,
    WindowUtils
} from 'smarteditcommons';
import { RenderService } from 'smarteditcontainer/services';
import { jQueryHelper } from 'testhelpers';

describe('renderServiceOuter', () => {
    let renderService: RenderService;

    const yjQuery: JQueryStatic = jQueryHelper.jQuery();
    let systemEventService: jasmine.SpyObj<SystemEventService>;
    let notificationService: jasmine.SpyObj<INotificationService>;
    let pageInfoService: jasmine.SpyObj<IPageInfoService>;
    let perspectiveService: jasmine.SpyObj<IPerspectiveService>;
    let crossFrameEventService: jasmine.SpyObj<CrossFrameEventService>;
    let modalService: jasmine.SpyObj<ModalService>;
    let logService: jasmine.SpyObj<LogService>;

    const windowUtils = new WindowUtils();

    beforeEach(() => {
        systemEventService = jasmine.createSpyObj('systemEventService', ['publishAsync']);
        notificationService = jasmine.createSpyObj('notificationService', [
            'pushNotification',
            'removeNotification'
        ]);
        pageInfoService = jasmine.createSpyObj('pageInfoService', ['getPageUUID']);
        perspectiveService = jasmine.createSpyObj('perspectiveService', [
            'isHotkeyEnabledForActivePerspective'
        ]);
        crossFrameEventService = jasmine.createSpyObj('crossFrameEventService', [
            'publish',
            'subscribe'
        ]);

        modalService = jasmine.createSpyObj('modalService', ['hasOpenModals']);
        modalService.hasOpenModals.and.returnValue(false);

        logService = jasmine.createSpyObj('logService', ['error']);

        renderService = new RenderService(
            yjQuery,
            crossFrameEventService,
            systemEventService,
            notificationService,
            pageInfoService,
            perspectiveService,
            windowUtils,
            modalService,
            logService
        );
    });

    it('should be decorated with GatewayProxied', () => {
        const gatewayProxiedAnnotation = annotationService.getClassAnnotation(
            RenderService,
            GatewayProxied
        );
        expect(gatewayProxiedAnnotation).toEqual([
            'blockRendering',
            'isRenderingBlocked',
            'renderSlots',
            'renderComponent',
            'renderRemoval',
            'toggleOverlay',
            'refreshOverlayDimensions',
            'renderPage'
        ]);
    });

    it('leaves the expected set of functions empty', () => {
        expect(renderService.renderSlots).toBeEmptyFunction();
        expect(renderService.renderComponent).toBeEmptyFunction();
        expect(renderService.renderRemoval).toBeEmptyFunction();
        expect(renderService.toggleOverlay).toBeEmptyFunction();
        expect(renderService.refreshOverlayDimensions).toBeEmptyFunction();
    });

    it('will return false if nothing is set', async () => {
        const promise = await renderService.isRenderingBlocked();
        expect(promise).toEqual(false);
    });

    it('will return true if rendering is blocked', async () => {
        await renderService.blockRendering(true);
        const isRenderingBlocked = await renderService.isRenderingBlocked();
        expect(isRenderingBlocked).toEqual(true);
    });

    it('will return false if rendering is not blocked', async () => {
        await renderService.blockRendering(false);
        const isRenderingBlocked = await renderService.isRenderingBlocked();
        expect(isRenderingBlocked).toEqual(false);
    });
});
