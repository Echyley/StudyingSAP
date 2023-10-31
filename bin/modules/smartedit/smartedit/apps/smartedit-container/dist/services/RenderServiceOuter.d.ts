/// <reference types="jquery" />
import { CrossFrameEventService, INotificationService, IPageInfoService, IPerspectiveService, IRenderService, LogService, ModalService, SystemEventService, WindowUtils } from 'smarteditcommons';
/** @internal */
export declare class RenderService extends IRenderService {
    protected yjQuery: JQueryStatic;
    protected crossFrameEventService: CrossFrameEventService;
    protected systemEventService: SystemEventService;
    protected logService: LogService;
    private renderingBlocked;
    constructor(yjQuery: JQueryStatic, crossFrameEventService: CrossFrameEventService, systemEventService: SystemEventService, notificationService: INotificationService, pageInfoService: IPageInfoService, perspectiveService: IPerspectiveService, windowUtils: WindowUtils, modalService: ModalService, logService: LogService);
    blockRendering(block: boolean): void;
    isRenderingBlocked(): Promise<boolean>;
}
