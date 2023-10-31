import { CMSRestriction } from 'cmscommons';
import { LogService, ModalService } from 'smarteditcommons';
export declare class RestrictionsViewerComponent {
    private modalService;
    private logService;
    restrictions: CMSRestriction[];
    constructor(modalService: ModalService, logService: LogService);
    showRestrictions(event: Event): Promise<any>;
}
