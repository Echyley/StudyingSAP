import { Observable } from 'rxjs';
import { IAnnouncementConfig, IAnnouncementService, LogService } from 'smarteditcommons';
export interface IAnnouncement extends IAnnouncementConfig {
    timer?: number;
    id: string;
}
export declare const ANNOUNCEMENT_DEFAULTS: {
    timeout: number;
    closeable: boolean;
};
export declare class AnnouncementService extends IAnnouncementService {
    private logService;
    private announcements$;
    constructor(logService: LogService);
    showAnnouncement(announcementConfig: IAnnouncementConfig): Promise<string>;
    getAnnouncements(): Observable<IAnnouncement[]>;
    closeAnnouncement(announcementId: string): Promise<void>;
    private _closeAnnouncement;
    private validateAnnouncementConfig;
}
