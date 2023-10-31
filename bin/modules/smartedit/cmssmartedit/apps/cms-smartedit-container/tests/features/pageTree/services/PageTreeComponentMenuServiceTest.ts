/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import 'jasmine';
import { IComponentMenuConditionAndCallbackService, IEditorEnablerService } from 'cmscommons';
import { PageTreeComponentMenuService } from 'cmssmarteditcontainer/services';
import { IContextualMenuConfiguration, IPermissionService } from 'smarteditcommons';

describe('pageTreeComponentMenuService', () => {
    let editorEnablerService: jasmine.SpyObj<IEditorEnablerService>;
    let componentMenuConditionAndCallbackService: jasmine.SpyObj<IComponentMenuConditionAndCallbackService>;
    let permissionService: jasmine.SpyObj<IPermissionService>;

    let pageTreeComponentMenuService: PageTreeComponentMenuService;
    let configuration: IContextualMenuConfiguration;

    beforeEach(() => {
        componentMenuConditionAndCallbackService = jasmine.createSpyObj(
            'componentMenuConditionAndCallbackService',
            [
                'externalCondition',
                'sharedCondition',
                'editConditionForHiddenComponent',
                'removeCondition',
                'cloneCondition'
            ]
        );
        editorEnablerService = jasmine.createSpyObj('editorEnablerService', [
            'isSlotEditableForNonExternalComponent'
        ]);

        permissionService = jasmine.createSpyObj('permissionService', ['isPermitted']);

        pageTreeComponentMenuService = new PageTreeComponentMenuService(
            editorEnablerService,
            componentMenuConditionAndCallbackService,
            permissionService
        );

        configuration = {
            componentAttributes: {
                smarteditCatalogVersionUuid: 'smarteditCatalogVersionUuid',
                smarteditComponentId: 'smarteditComponentId',
                smarteditComponentType: 'smarteditComponentType',
                smarteditComponentUuid: 'smarteditComponentUuid',
                smarteditElementUuid: 'smarteditElementUuid'
            },
            componentType: 'componentType',
            componentId: 'componentId'
        };
    });

    it('getPageTreeComponentMenus will return component menus that matches conditions and permissions', async () => {
        configuration.isComponentHidden = false;

        permissionService.isPermitted
            .withArgs([
                {
                    names: ['se.context.menu.edit.component']
                }
            ])
            .and.returnValue(Promise.resolve(false));

        permissionService.isPermitted
            .withArgs([
                {
                    names: ['se.context.menu.remove.component']
                }
            ])
            .and.returnValue(Promise.resolve(true));

        permissionService.isPermitted
            .withArgs([
                {
                    names: ['se.clone.component']
                }
            ])
            .and.returnValue(Promise.resolve(true));

        componentMenuConditionAndCallbackService.externalCondition.and.returnValue(
            Promise.resolve(false)
        );
        componentMenuConditionAndCallbackService.sharedCondition.and.returnValue(
            Promise.resolve(false)
        );
        componentMenuConditionAndCallbackService.removeCondition.and.returnValue(
            Promise.resolve(true)
        );
        componentMenuConditionAndCallbackService.cloneCondition.and.returnValue(
            Promise.resolve(false)
        );

        let menus = await pageTreeComponentMenuService.getPageTreeComponentMenus(configuration);
        menus = menus.filter((item) => !!item);
        expect(menus.length).toBe(1);
        expect(menus[0].key).toBe('se.cms.remove');
        expect(editorEnablerService.isSlotEditableForNonExternalComponent).not.toHaveBeenCalled();
    });

    it('getPageTreeComponentMenus will return hidden component menus that matches conditions and permissions', async () => {
        configuration.isComponentHidden = true;

        permissionService.isPermitted
            .withArgs([
                {
                    names: ['se.context.menu.edit.component']
                }
            ])
            .and.returnValue(Promise.resolve(true));

        permissionService.isPermitted
            .withArgs([
                {
                    names: ['se.context.menu.remove.component']
                }
            ])
            .and.returnValue(Promise.resolve(false));

        permissionService.isPermitted
            .withArgs([
                {
                    names: ['se.clone.component']
                }
            ])
            .and.returnValue(Promise.resolve(true));

        componentMenuConditionAndCallbackService.editConditionForHiddenComponent.and.returnValue(
            Promise.resolve(true)
        );

        componentMenuConditionAndCallbackService.cloneCondition.and.returnValue(
            Promise.resolve(true)
        );

        let menus = await pageTreeComponentMenuService.getPageTreeComponentMenus(configuration);
        menus = menus.filter((item) => !!item);
        expect(menus.length).toBe(2);
        expect(menus[0].key).toBe('se.cms.edit');
        expect(menus[1].key).toBe('clonecomponentbutton');
        expect(componentMenuConditionAndCallbackService.externalCondition).toHaveBeenCalled();
        expect(componentMenuConditionAndCallbackService.sharedCondition).toHaveBeenCalled();
    });
});
