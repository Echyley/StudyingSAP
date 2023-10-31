/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { InjectionToken, Type } from '@angular/core';
import { TypedMap } from '@smart/utils';

export type AnnouncementData<T = TypedMap<any>> = T & {
    id: string;
};

export const ANNOUNCEMENT_DATA = new InjectionToken<AnnouncementData>('ANNOUNCEMENT_DATA');

/**
 * Describes the Announcement configuration for  {@link AnnouncementComponent}.
 */
export interface IAnnouncementConfig {
    /**
     * The announcement's message to display.
     */
    message?: string;

    /**
     * Optional title for the announcement's message.
     *
     * This is only used when message is available.
     */
    messageTitle?: string;

    /**
     * The announcement's component to display.
     */
    component?: Type<any>;

    /**
     * Data to pass along to the component through the [token]{@link ANNOUNCEMENT_DATA#token}.
     */
    data?: AnnouncementData;

    /**
     * The optional boolean that determines if a close button must be displayed or not.
     * The default is true.
     */
    closeable?: boolean;

    /**
     * The optional timeout in milliseconds that determines when to close the announcement.
     * The default is 5000.
     */
    timeout?: number;
}

/**
 * Interface for Announcement service
 */
export abstract class IAnnouncementService {
    /**
     * This method creates a new announcement and displays it.
     *
     * The configuration must contain either message or component.
     *
     * @returns Returns a promise with announcement id.
     */
    showAnnouncement(announcementConfig: IAnnouncementConfig): PromiseLike<string> {
        'proxyFunction';
        return null;
    }

    /**
     * This method is used to close the announcement by given announcement id.
     */
    closeAnnouncement(announcementId: string): PromiseLike<void> {
        'proxyFunction';
        return null;
    }
}
