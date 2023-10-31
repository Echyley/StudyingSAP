import { TypedMap } from '@smart/utils';
import { BehaviorSubject } from 'rxjs';
import { IContextualMenuConfiguration } from 'smarteditcommons/services/interfaces/IContextualMenuConfiguration';
import { ContextualMenu, IContextualMenuButton } from './IContextualMenuButton';
export declare abstract class IContextualMenuService {
    onContextualMenuItemsAdded: BehaviorSubject<string>;
    addItems(contextualMenuItemsMap: TypedMap<IContextualMenuButton[]>): void;
    removeItemByKey(itemKey: string): void;
    containsItem(itemKey: string): boolean;
    getContextualMenuByType(componentType: string): IContextualMenuButton[];
    refreshMenuItems(): void;
    getContextualMenuItems(configuration: IContextualMenuConfiguration): Promise<ContextualMenu>;
}
