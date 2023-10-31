import { OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { AnnouncementService, IAnnouncement } from 'smarteditcontainer/services/announcement/AnnouncementServiceOuter';
import './AnnouncementBoardComponent.scss';
export declare class AnnouncementBoardComponent implements OnInit {
    private announcementService;
    announcements$: Observable<IAnnouncement[]>;
    constructor(announcementService: AnnouncementService);
    ngOnInit(): void;
    announcementTrackBy(index: number, item: IAnnouncement): string;
}
