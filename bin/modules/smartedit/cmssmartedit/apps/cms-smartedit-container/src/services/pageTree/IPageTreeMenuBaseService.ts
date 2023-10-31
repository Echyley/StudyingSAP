/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    IContextualMenuButton,
    IContextualMenuConfiguration,
    IPermissionService
} from 'smarteditcommons';

export abstract class IPageTreeMenuBaseService {
    protected originalMenus: IContextualMenuButton[];

    constructor(private readonly permissionService: IPermissionService) {}

    public async getPageTreeComponentMenus(
        configuration: IContextualMenuConfiguration
    ): Promise<IContextualMenuButton[]> {
        const menus = await this.buildMenusByPermission();
        const promises = menus.map(async (item: IContextualMenuButton) => {
            if (!item.condition) {
                return item;
            }
            const isItemEnabled = await item.condition(configuration);
            return isItemEnabled ? item : null;
        });

        return Promise.all(promises);
    }

    protected async buildMenusByPermission(): Promise<IContextualMenuButton[]> {
        const menus = await Promise.all(
            this.originalMenus.map(async (item) => {
                if (!item.permissions || item.permissions.length === 0) {
                    return item;
                }

                const allowed = await this.permissionService.isPermitted([
                    {
                        names: item.permissions
                    }
                ]);
                if (allowed) {
                    return item;
                }
                return null;
            })
        );
        return menus.filter((menu) => !!menu);
    }
}
