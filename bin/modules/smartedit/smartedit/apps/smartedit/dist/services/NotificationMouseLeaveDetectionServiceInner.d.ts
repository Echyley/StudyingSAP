import { IBound, INotificationMouseLeaveDetectionService } from 'smarteditcommons';
/**
 * This service makes it possible to track the mouse position to detect when it leaves the notification panel.
 * It is solely meant to be used with the notificationService.
 */
/** @internal */
export declare class NotificationMouseLeaveDetectionService extends INotificationMouseLeaveDetectionService {
    private document;
    private notificationPanelBounds;
    private mouseLeaveCallback;
    constructor(document: Document);
    protected _remoteStartDetection(innerBounds: IBound): Promise<void>;
    protected _remoteStopDetection(): Promise<void>;
    protected _getBounds(): Promise<IBound>;
    protected _getCallback(): Promise<() => void>;
}
