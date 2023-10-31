import { IPageTreeService } from 'smarteditcommons';
export declare class PageTreeService extends IPageTreeService {
    constructor();
    registerTreeComponent(item: any): Promise<void>;
    getTreeComponent(): Promise<any>;
}
