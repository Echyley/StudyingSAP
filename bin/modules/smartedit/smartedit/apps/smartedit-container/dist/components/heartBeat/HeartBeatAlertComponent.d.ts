import { AlertRef } from '@fundamental-ngx/core';
import { CrossFrameEventService, IPerspectiveService } from 'smarteditcommons';
export declare class HeartBeatAlertComponent {
    private alertRef;
    private perspectiveService;
    private crossFrameEventService;
    constructor(alertRef: AlertRef, perspectiveService: IPerspectiveService, crossFrameEventService: CrossFrameEventService);
    switchToPreviewMode(): void;
}
