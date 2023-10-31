/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { clone } from 'lodash';
import { BehaviorSubject, Observable } from 'rxjs';

import {
    stringUtils,
    GatewayProxied,
    IAnnouncementConfig,
    IAnnouncementService,
    LogService
} from 'smarteditcommons';

export interface IAnnouncement extends IAnnouncementConfig {
    timer?: number;
    id: string;
}

export const ANNOUNCEMENT_DEFAULTS = {
    timeout: 20000,
    closeable: true
};

@GatewayProxied('showAnnouncement', 'closeAnnouncement')
@Injectable()
export class AnnouncementService extends IAnnouncementService {
    private announcements$ = new BehaviorSubject<IAnnouncement[]>([]);

    constructor(private logService: LogService) {
        super();
    }

    public showAnnouncement(announcementConfig: IAnnouncementConfig): Promise<string> {
        this.validateAnnouncementConfig(announcementConfig);

        const announcement: IAnnouncement = clone<IAnnouncementConfig>(
            announcementConfig
        ) as IAnnouncement;
        announcement.id = stringUtils.encode(announcementConfig);

        announcement.timeout =
            !!announcement.timeout && announcement.timeout > 0
                ? announcement.timeout
                : ANNOUNCEMENT_DEFAULTS.timeout;
        if (announcement.timeout > 0) {
            announcement.timer = setTimeout(() => {
                this._closeAnnouncement(announcement);
            }, announcement.timeout) as unknown as number;
        }
        this.announcements$.next([...this.announcements$.getValue(), announcement]);

        return Promise.resolve(announcement.id);
    }

    public getAnnouncements(): Observable<IAnnouncement[]> {
        return this.announcements$.asObservable();
    }

    public closeAnnouncement(announcementId: string): Promise<void> {
        const announcement = this.announcements$
            .getValue()
            .find((announcementObj) => announcementObj.id === announcementId);
        if (announcement) {
            this._closeAnnouncement(announcement);
        }

        return Promise.resolve();
    }

    private _closeAnnouncement(announcement: IAnnouncement): void {
        if (announcement.timer) {
            clearTimeout(announcement.timer);
        }

        const announcements = this.announcements$.getValue();
        const newAnnouncements = announcements.filter(
            (announcementObj) => announcementObj.id !== announcement.id
        );
        this.announcements$.next(newAnnouncements);
    }

    private validateAnnouncementConfig(announcementConfig: IAnnouncementConfig): void {
        const { message, component } = announcementConfig;

        if (!message && !component) {
            this.logService.warn(
                'AnnouncementService.validateAnnouncementConfig - announcement must contain at least a message or component property',
                announcementConfig
            );
        }

        if (message && component) {
            throw new Error(
                'AnnouncementService.validateAnnouncementConfig - either message or component must be provided'
            );
        }
    }
}
