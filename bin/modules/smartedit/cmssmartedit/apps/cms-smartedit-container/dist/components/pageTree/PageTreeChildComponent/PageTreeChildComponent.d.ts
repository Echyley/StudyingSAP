import { IContextualMenuConfiguration } from 'smarteditcommons';
import { NodeInfoService, ComponentNode } from '../../../services/pageTree/NodeInfoService';
export declare class PageTreeChildComponent {
    private readonly nodeInfoService;
    component: ComponentNode;
    parentMenuConfiguration: IContextualMenuConfiguration;
    slotId: string;
    slotUuid: string;
    constructor(nodeInfoService: NodeInfoService);
    onClickComponentNode($event: Event, component: ComponentNode): Promise<void>;
}
