import { CrossFrameEventService } from 'smarteditcommons';
export declare class OpenNodeInPageTreeService {
    private crossFrameEventService;
    private timeout;
    constructor(crossFrameEventService: CrossFrameEventService);
    publishOpenEvent(elementUuid: string): void;
}
