/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Inject } from '@angular/core';

import {
    CrossFrameEventService,
    GatewayProxied,
    INotificationService,
    IPageInfoService,
    IPerspectiveService,
    IRenderService,
    LogService,
    ModalService,
    SystemEventService,
    WindowUtils,
    YJQUERY_TOKEN
} from 'smarteditcommons';

/** @internal */
@GatewayProxied(
    'blockRendering',
    'isRenderingBlocked',
    'renderSlots',
    'renderComponent',
    'renderRemoval',
    'toggleOverlay',
    'refreshOverlayDimensions',
    'renderPage'
)
export class RenderService extends IRenderService {
    private renderingBlocked: boolean;

    constructor(
        @Inject(YJQUERY_TOKEN) protected yjQuery: JQueryStatic,
        protected crossFrameEventService: CrossFrameEventService,
        protected systemEventService: SystemEventService,
        notificationService: INotificationService,
        pageInfoService: IPageInfoService,
        perspectiveService: IPerspectiveService,
        windowUtils: WindowUtils,
        modalService: ModalService,
        protected logService: LogService
    ) {
        super(
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

    blockRendering(block: boolean): void {
        this.renderingBlocked = block;
    }

    isRenderingBlocked(): Promise<boolean> {
        return Promise.resolve(this.renderingBlocked || false);
    }
}
