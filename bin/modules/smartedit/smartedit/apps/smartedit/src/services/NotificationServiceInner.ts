/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { GatewayProxied, INotificationService } from 'smarteditcommons';

/**
 * The notification service is used to display visual cues to inform the user of the state of the application.
 */
/** @internal */
@GatewayProxied('pushNotification', 'removeNotification', 'removeAllNotifications')
export class NotificationService extends INotificationService {}
