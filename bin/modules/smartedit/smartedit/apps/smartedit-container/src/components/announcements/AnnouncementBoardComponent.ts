/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ChangeDetectionStrategy, Component, Inject, OnInit } from '@angular/core';
import { Observable } from 'rxjs';

import { IAnnouncementService } from 'smarteditcommons';
import {
    AnnouncementService,
    IAnnouncement
} from 'smarteditcontainer/services/announcement/AnnouncementServiceOuter';

import './AnnouncementBoardComponent.scss';

/**
 * Renders list of announcements
 */
/** @internal */
@Component({
    selector: 'se-announcement-board',
    changeDetection: ChangeDetectionStrategy.OnPush,
    templateUrl: './AnnouncementBoardComponent.html'
})
export class AnnouncementBoardComponent implements OnInit {
    public announcements$: Observable<IAnnouncement[]>;
    constructor(@Inject(IAnnouncementService) private announcementService: AnnouncementService) {}

    ngOnInit(): void {
        this.announcements$ = this.announcementService.getAnnouncements();
    }

    public announcementTrackBy(index: number, item: IAnnouncement): string {
        return item.id;
    }
}
