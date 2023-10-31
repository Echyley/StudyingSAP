import { OnDestroy } from '@angular/core';
import { Observable } from 'rxjs';
import { INotificationConfiguration, INotificationPush, INotificationService, LogService, SystemEventService } from 'smarteditcommons';
/**
 * The notification service is used to display visual cues to inform the user of the state of the application.
 */
/** @internal */
export declare class NotificationService implements INotificationService, OnDestroy {
    private systemEventService;
    private logService;
    private notificationsChangeAction;
    private notifications;
    constructor(systemEventService: SystemEventService, logService: LogService);
    ngOnDestroy(): void;
    pushNotification(configuration: INotificationPush): Promise<void>;
    removeNotification(notificationId: string): Promise<void>;
    removeAllNotifications(): Promise<void>;
    isNotificationDisplayed(notificationId: string): boolean;
    getNotification(notificationId: string): INotificationConfiguration;
    getNotifications(): Observable<INotificationConfiguration[]>;
    private initNotificationsChangeAction;
    /**
     * Meant for case when a user has quickly pressed ESC key multiple times.
     * There might be some delay when adding / removing a notification because these methods are called in async context.
     * This may lead to the situation where notification has not yet been removed, but ESC key has called the pushNotification.
     *
     * @returns false (emit), true (do not emit)
     */
    private emitWhenActionIsAvailable;
    private resolveNotifications;
}
