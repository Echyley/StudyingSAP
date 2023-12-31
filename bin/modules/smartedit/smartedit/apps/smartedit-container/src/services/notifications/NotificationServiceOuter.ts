/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { OnDestroy } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { distinctUntilChanged, map, skip } from 'rxjs/operators';

import {
    EVENT_NOTIFICATION_CHANGED,
    GatewayProxied,
    INotificationConfiguration,
    INotificationPush,
    INotificationRemove,
    INotificationService,
    LogService,
    SystemEventService
} from 'smarteditcommons';

/** @internal */
const enum NotificationChangeActionType {
    PUSH = 'PUSH',
    REMOVE = 'REMOVE',
    REMOVE_ALL = 'REMOVE_ALL'
}

/** @internal */
interface NotificationChange {
    type: NotificationChangeActionType;
    payload?: INotificationPush | INotificationRemove;
}
/**
 * The notification service is used to display visual cues to inform the user of the state of the application.
 */
/** @internal */
@GatewayProxied('pushNotification', 'removeNotification', 'removeAllNotifications')
export class NotificationService implements INotificationService, OnDestroy {
    private notificationsChangeAction: BehaviorSubject<NotificationChange | undefined>;
    private notifications: BehaviorSubject<INotificationConfiguration[]>;

    constructor(private systemEventService: SystemEventService, private logService: LogService) {
        this.notificationsChangeAction = new BehaviorSubject<NotificationChange | undefined>(
            undefined
        );
        this.notifications = new BehaviorSubject<INotificationConfiguration[]>([]);

        this.initNotificationsChangeAction();
    }

    ngOnDestroy(): void {
        this.notifications.unsubscribe();
        this.notificationsChangeAction.unsubscribe();
    }

    public pushNotification(configuration: INotificationPush): Promise<void> {
        const action: NotificationChange = {
            type: NotificationChangeActionType.PUSH,
            payload: configuration
        };
        this.notificationsChangeAction.next(action);

        return Promise.resolve();
    }

    public removeNotification(notificationId: string): Promise<void> {
        const action: NotificationChange = {
            type: NotificationChangeActionType.REMOVE,
            payload: {
                id: notificationId
            }
        };
        this.notificationsChangeAction.next(action);

        return Promise.resolve();
    }

    public removeAllNotifications(): Promise<void> {
        const action: NotificationChange = {
            type: NotificationChangeActionType.REMOVE_ALL
        };
        this.notificationsChangeAction.next(action);

        return Promise.resolve();
    }

    public isNotificationDisplayed(notificationId: string): boolean {
        return !!this.getNotification(notificationId);
    }

    public getNotification(notificationId: string): INotificationConfiguration {
        return this.notifications
            .getValue()
            .find((notification) => notification.id === notificationId);
    }

    public getNotifications(): Observable<INotificationConfiguration[]> {
        return this.notifications.asObservable();
    }

    private initNotificationsChangeAction(): void {
        this.notificationsChangeAction
            .pipe(
                distinctUntilChanged((_, action) => this.emitWhenActionIsAvailable(action)),
                // Skip first emission with "undefined" value.
                // First "undefined" is needed for invoking distinctUntilChanged (which requires at least 2 values emited) when first notification is added.
                skip(1),
                map((action) => this.resolveNotifications(action))
            )
            .subscribe((notifications) => {
                this.notifications.next(notifications);
                this.systemEventService.publishAsync(EVENT_NOTIFICATION_CHANGED);
            });
    }

    /**
     * Meant for case when a user has quickly pressed ESC key multiple times.
     * There might be some delay when adding / removing a notification because these methods are called in async context.
     * This may lead to the situation where notification has not yet been removed, but ESC key has called the pushNotification.
     *
     * @returns false (emit), true (do not emit)
     */
    private emitWhenActionIsAvailable(action: NotificationChange): boolean {
        const { payload: newNotification } = action;
        const notification =
            (action.type === NotificationChangeActionType.PUSH ||
                action.type === NotificationChangeActionType.REMOVE) &&
            this.getNotification(newNotification.id);

        switch (action.type) {
            case NotificationChangeActionType.PUSH:
                if (notification) {
                    this.logService.debug(
                        `Notification already exists for id:"${newNotification.id}"`
                    );
                    return true;
                }
                return false;
            case NotificationChangeActionType.REMOVE:
                if (!notification) {
                    this.logService.debug(
                        `Attempt to remove a non existing notification for id:"${newNotification.id}"`
                    );
                    return true;
                }
                return false;
            case NotificationChangeActionType.REMOVE_ALL:
            default:
                return false;
        }
    }

    private resolveNotifications(action: NotificationChange): INotificationConfiguration[] {
        const { payload: newNotification } = action;
        switch (action.type) {
            case NotificationChangeActionType.PUSH:
                return [...this.notifications.getValue(), newNotification as INotificationPush];
            case NotificationChangeActionType.REMOVE:
                return this.notifications
                    .getValue()
                    .filter((notification) => notification.id !== newNotification.id);
            case NotificationChangeActionType.REMOVE_ALL:
            default:
                return [];
        }
    }
}
