/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { animate, style, transition, trigger } from '@angular/animations';
import {
    ChangeDetectionStrategy,
    Component,
    HostBinding,
    Injector,
    Input,
    OnChanges
} from '@angular/core';

import { IAnnouncementService, ANNOUNCEMENT_DATA } from 'smarteditcommons';
import {
    ANNOUNCEMENT_DEFAULTS,
    IAnnouncement
} from 'smarteditcontainer/services/announcement/AnnouncementServiceOuter';

import './AnnouncementComponent.scss';

/**
 * Renders an announcement in the upper right corner of the page based on the announcement configuration.
 * If `message` is provided, it will display the given message within the default announcement.
 * If `component` is provided, it will display that component which can access ANNOUNCEMENT_DATA via Injection Token.
 *
 */
/** @internal */
@Component({
    selector: 'se-announcement',
    changeDetection: ChangeDetectionStrategy.OnPush,
    animations: [
        trigger('fadeAnimation', [
            transition(':enter', [
                style({
                    opacity: 0,
                    transform: 'rotateY(90deg)'
                }),
                animate('0.5s'),
                style({
                    opacity: 1,
                    transform: 'translateX(0px)'
                })
            ]),
            transition(':leave', [
                animate('0.25s'),
                style({
                    opacity: '0',
                    transform: 'translateX(100%)'
                })
            ])
        ])
    ],
    templateUrl: './AnnouncementComponent.html'
})
export class AnnouncementComponent implements OnChanges {
    @Input() announcement: IAnnouncement;
    @HostBinding('@fadeAnimation') get fadeAnimation(): boolean {
        return true;
    }

    public announcementInjector: Injector;

    constructor(private announcementService: IAnnouncementService, private injector: Injector) {}

    ngOnChanges(): void {
        this.createAnnouncementInjector();
    }

    public hasComponent(): boolean {
        return this.announcement.hasOwnProperty('component');
    }

    public hasMessage(): boolean {
        return this.announcement.hasOwnProperty('message');
    }

    public hasMessageTitle(): boolean {
        return this.announcement.hasOwnProperty('messageTitle');
    }

    public isCloseable(): boolean {
        return this.announcement.hasOwnProperty('closeable')
            ? this.announcement.closeable
            : ANNOUNCEMENT_DEFAULTS.closeable;
    }

    public closeAnnouncement(): void {
        this.announcementService.closeAnnouncement(this.announcement.id);
    }

    private createAnnouncementInjector(): void {
        this.announcementInjector = Injector.create({
            parent: this.injector,
            providers: [
                {
                    provide: ANNOUNCEMENT_DATA,
                    useValue: {
                        id: this.announcement.id,
                        ...this.announcement.data
                    }
                }
            ]
        });
    }
}
