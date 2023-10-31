/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ChangeDetectionStrategy, Component, Input, OnInit } from '@angular/core';
import { INotificationConfiguration } from 'smarteditcommons';

/**
 * This component renders an notification based on a one of the template type provided with configuration object.
 *
 * See [example]{@link INotificationConfiguration#componentName}.
 */
@Component({
    selector: 'se-notification',
    changeDetection: ChangeDetectionStrategy.OnPush,
    templateUrl: './NotificationComponentTemplate.html'
})
export class NotificationComponent implements OnInit {
    @Input() notification: INotificationConfiguration;

    public id: string;

    ngOnInit(): void {
        this.id =
            this.notification && this.notification.id
                ? 'se-notification-' + this.notification.id
                : '';
    }
}
