/// <reference types="jquery" />
import { IComponentHandlerService } from 'smarteditcommons';
export declare class ComponentHandlerService extends IComponentHandlerService {
    constructor(yjQuery: JQueryStatic);
    isExternalComponent(smarteditComponentId: string, smarteditComponentType: string): Promise<boolean>;
    reloadInner(): Promise<void>;
}
