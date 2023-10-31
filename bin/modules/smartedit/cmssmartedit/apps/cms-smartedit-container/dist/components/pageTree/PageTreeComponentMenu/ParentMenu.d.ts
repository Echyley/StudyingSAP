import { ComponentAttributes, IContextualMenuButton, TypedMap } from 'smarteditcommons';
import { ComponentNode } from '../../../services/pageTree/NodeInfoService';
export declare abstract class ParentMenu {
    abstract remainOpenMap: TypedMap<boolean>;
    abstract component: ComponentNode;
    abstract slotId: string;
    abstract slotUuid: string;
    abstract componentAttributes: ComponentAttributes;
    abstract canShowTemplate(menuItem: IContextualMenuButton): boolean;
    abstract onHideItemPopup(hideMoreMenu?: boolean): void;
    abstract triggerMenuItemAction(item: IContextualMenuButton, $event: Event): void;
    abstract getItems(): IContextualMenuButton[];
    abstract setRemainOpen(key: string, remainOpen: boolean): void;
}
