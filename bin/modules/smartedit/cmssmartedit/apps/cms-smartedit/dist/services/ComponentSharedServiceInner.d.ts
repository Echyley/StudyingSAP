import { IComponentSharedService, ICMSComponent } from 'cmscommons';
import { ComponentInfoService } from './ComponentInfoService';
export declare class ComponentSharedService extends IComponentSharedService {
    private componentInfoService;
    constructor(componentInfoService: ComponentInfoService);
    isComponentShared(componentParam: string | ICMSComponent): Promise<boolean>;
    private determineComponent;
}
