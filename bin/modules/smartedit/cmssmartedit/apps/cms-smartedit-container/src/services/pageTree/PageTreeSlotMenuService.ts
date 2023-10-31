/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import {
    SlotSharedButtonComponent,
    SlotSyncButtonComponent,
    SlotUnsharedButtonComponent
} from 'cmscommons';
import { IPermissionService } from 'smarteditcommons';
import { IPageTreeMenuBaseService } from './IPageTreeMenuBaseService';

@Injectable()
export class PageTreeSlotMenuService extends IPageTreeMenuBaseService {
    constructor(permissionService: IPermissionService) {
        super(permissionService);

        this.originalMenus = [
            {
                key: 'se.slotSharedButton',
                nameI18nKey: 'slotcontextmenu.title.shared.button',
                regexpKeys: ['^.*Slot$'],
                action: {
                    component: SlotSharedButtonComponent
                },
                permissions: ['se.slot.context.menu.shared.icon']
            },
            {
                key: 'slotUnsharedButton',
                nameI18nKey: 'slotcontextmenu.title.unshared.button',
                regexpKeys: ['^.*Slot$'],
                action: { component: SlotUnsharedButtonComponent },
                permissions: ['se.slot.context.menu.unshared.icon']
            },
            {
                key: 'se.slotSyncButton',
                nameI18nKey: 'slotcontextmenu.title.sync.button',
                regexpKeys: ['^.*Slot$'],
                action: { component: SlotSyncButtonComponent },
                permissions: ['se.sync.slot.context.menu']
            }
        ];
    }
}
