/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { TypedMap } from '@smart/utils';
import { BehaviorSubject } from 'rxjs';

import { IContextualMenuConfiguration } from 'smarteditcommons/services/interfaces/IContextualMenuConfiguration';
import { ContextualMenu, IContextualMenuButton } from './IContextualMenuButton';

export abstract class IContextualMenuService {
    public onContextualMenuItemsAdded: BehaviorSubject<string>;

    public addItems(contextualMenuItemsMap: TypedMap<IContextualMenuButton[]>): void {
        'proxyFunction';
    }

    public removeItemByKey(itemKey: string): void {
        'proxyFunction';
    }

    public containsItem(itemKey: string): boolean {
        'proxyFunction';
        return null;
    }

    public getContextualMenuByType(componentType: string): IContextualMenuButton[] {
        'proxyFunction';
        return null;
    }

    public refreshMenuItems(): void {
        'proxyFunction';
    }

    public getContextualMenuItems(
        configuration: IContextualMenuConfiguration
    ): Promise<ContextualMenu> {
        return null;
    }
}
