import { Injector, OnChanges } from '@angular/core';
import { IAnnouncementService } from 'smarteditcommons';
import { IAnnouncement } from 'smarteditcontainer/services/announcement/AnnouncementServiceOuter';
import './AnnouncementComponent.scss';
export declare class AnnouncementComponent implements OnChanges {
    private announcementService;
    private injector;
    announcement: IAnnouncement;
    get fadeAnimation(): boolean;
    announcementInjector: Injector;
    constructor(announcementService: IAnnouncementService, injector: Injector);
    ngOnChanges(): void;
    hasComponent(): boolean;
    hasMessage(): boolean;
    hasMessageTitle(): boolean;
    isCloseable(): boolean;
    closeAnnouncement(): void;
    private createAnnouncementInjector;
}
