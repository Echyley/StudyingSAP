import { ICMSComponent, RemoveComponentInfo } from 'cmscommons';
import { IAlertService, IRenderService, IRestServiceFactory, SystemEventService } from 'smarteditcommons';
import { ComponentInfoService } from './ComponentInfoService';
export declare class RemoveComponentService {
    private alertService;
    private componentInfoService;
    private renderService;
    private systemEventService;
    private resource;
    constructor(restServiceFactory: IRestServiceFactory, alertService: IAlertService, componentInfoService: ComponentInfoService, renderService: IRenderService, systemEventService: SystemEventService);
    removeComponent(configuration: RemoveComponentInfo): Promise<ICMSComponent>;
}
