/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import {
    IEditorEnablerService,
    IComponentMenuConditionAndCallbackService,
    SharedComponentButtonComponent
} from 'cmscommons';
import {
    IContextualMenuConfiguration,
    IPermissionService,
    IContextualMenuButton
} from 'smarteditcommons';
import { IPageTreeMenuBaseService } from './IPageTreeMenuBaseService';

@Injectable()
export class PageTreeChildComponentMenuService extends IPageTreeMenuBaseService {
    constructor(
        private readonly editorEnablerService: IEditorEnablerService,
        private readonly componentMenuConditionAndCallbackService: IComponentMenuConditionAndCallbackService,
        permissionService: IPermissionService
    ) {
        super(permissionService);

        // Menus are displayed from left to right in the order in array
        this.originalMenus = [
            {
                key: 'se.cms.sharedcomponentbutton',
                i18nKey: 'se.cms.contextmenu.title.shared.component',
                displayIconClass: 'sap-icon--chain-link',
                permissions: [],
                condition: this.componentMenuConditionAndCallbackService.sharedCondition,
                action: {
                    component: SharedComponentButtonComponent
                }
            },
            {
                key: 'se.cms.edit',
                i18nKey: 'se.cms.contextmenu.title.edit',
                displayIconClass: 'sap-icon--edit',
                permissions: ['se.context.menu.edit.component'],
                action: {
                    callback: this.editorEnablerService.onClickEditButton
                },
                condition: async (
                    configuration: IContextualMenuConfiguration
                ): Promise<boolean> => {
                    if (configuration.isComponentHidden) {
                        return this.componentMenuConditionAndCallbackService.editConditionForHiddenComponent(
                            configuration
                        );
                    } else {
                        return this.editorEnablerService.isSlotEditableForNonExternalComponent(
                            configuration
                        );
                    }
                }
            }
        ];
    }

    public async getPageTreeChildComponentMenus(
        parentConfiguration: IContextualMenuConfiguration,
        configuration: IContextualMenuConfiguration
    ): Promise<IContextualMenuButton[]> {
        const menus = await this.buildMenusByPermission();
        const promises = menus.map(async (item: IContextualMenuButton) => {
            if (!item.condition) {
                return item;
            }
            let isItemEnabled: boolean;
            if (item.key === 'se.cms.edit') {
                isItemEnabled = await item.condition(parentConfiguration);
            } else {
                isItemEnabled = await item.condition(configuration);
            }
            return isItemEnabled ? item : null;
        });

        return Promise.all(promises);
    }
}
