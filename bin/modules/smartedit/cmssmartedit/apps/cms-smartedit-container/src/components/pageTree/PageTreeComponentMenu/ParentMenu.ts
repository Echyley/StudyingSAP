/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ComponentAttributes, IContextualMenuButton, TypedMap } from 'smarteditcommons';
import { ComponentNode } from '../../../services/pageTree/NodeInfoService';
export abstract class ParentMenu {
    abstract remainOpenMap: TypedMap<boolean>;
    abstract component: ComponentNode;
    abstract slotId: string;
    abstract slotUuid: string;
    abstract componentAttributes: ComponentAttributes;

    abstract canShowTemplate(menuItem: IContextualMenuButton): boolean;
    abstract onHideItemPopup(hideMoreMenu?: boolean): void;
    abstract triggerMenuItemAction(item: IContextualMenuButton, $event: Event): void;
    abstract getItems(): IContextualMenuButton[];

    /*
     setRemainOpen receives a key name and a boolean value
     the button name needs to be unique across all buttons so it won' t collide with other button actions.
     */
    abstract setRemainOpen(key: string, remainOpen: boolean): void;
}
