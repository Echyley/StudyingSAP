/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import {
    IEditorEnablerService,
    IComponentMenuConditionAndCallbackService,
    SharedComponentButtonComponent,
    ExternalComponentButtonComponent
} from 'cmscommons';
import { IContextualMenuConfiguration, IPermissionService } from 'smarteditcommons';
import { IPageTreeMenuBaseService } from './IPageTreeMenuBaseService';

@Injectable()
export class PageTreeComponentMenuService extends IPageTreeMenuBaseService {
    constructor(
        private readonly editorEnablerService: IEditorEnablerService,
        private readonly componentMenuConditionAndCallbackService: IComponentMenuConditionAndCallbackService,
        permissionService: IPermissionService
    ) {
        super(permissionService);

        this.originalMenus = [
            {
                key: 'externalcomponentbutton',
                i18nKey: 'se.cms.contextmenu.title.externalcomponentbutton',
                displayIconClass: 'sap-icon--globe',
                permissions: [],
                condition: this.componentMenuConditionAndCallbackService.externalCondition,
                action: {
                    component: ExternalComponentButtonComponent
                }
            },
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
            },
            {
                key: 'se.cms.remove',
                i18nKey: 'se.cms.contextmenu.title.remove',
                displayIconClass: 'sap-icon--decline',
                permissions: ['se.context.menu.remove.component'],
                action: {
                    callback: this.componentMenuConditionAndCallbackService.removeCallback
                },
                condition: this.componentMenuConditionAndCallbackService.removeCondition
            },
            {
                key: 'clonecomponentbutton',
                i18nKey: 'se.cms.contextmenu.title.clone.component',
                displayIconClass: 'sap-icon--duplicate',
                permissions: ['se.clone.component'],
                action: {
                    callback: this.componentMenuConditionAndCallbackService.cloneCallback
                },
                condition: this.componentMenuConditionAndCallbackService.cloneCondition
            }
        ];
    }
}
