/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { EventEmitter } from '@angular/core';
import { IComponentMenuConditionAndCallbackService } from 'cmscommons';
import { PageTreeComponent } from 'cmssmarteditcontainer/components/pageTree';
import { ComponentNode, NodeInfoService } from 'cmssmarteditcontainer/services';
import { IPageTreeNodeService, IPermissionService, LogService } from 'smarteditcommons';
import { createComponentNode } from '../util';

describe('PageTreeComponent', () => {
    let pageTreeComponent: PageTreeComponent;
    let pageTreeNodeService: jasmine.SpyObj<IPageTreeNodeService>;
    let nodeInfoService: jasmine.SpyObj<NodeInfoService>;
    let logService: jasmine.SpyObj<LogService>;
    let $event: jasmine.SpyObj<Event>;
    let onComponentExpanded: jasmine.SpyObj<EventEmitter<ComponentNode>>;
    let componentMenuConditionAndCallbackService: jasmine.SpyObj<IComponentMenuConditionAndCallbackService>;
    let permissionService: jasmine.SpyObj<IPermissionService>;

    beforeEach(() => {
        $event = jasmine.createSpyObj('$event', ['stopPropagation']);
        nodeInfoService = jasmine.createSpyObj('nodeInfoService', [
            'publishComponentSelected',
            'getChildComponents'
        ]);
        onComponentExpanded = jasmine.createSpyObj('onComponentExpanded', ['emit']);
        logService = jasmine.createSpyObj('logService', ['info', 'error']);
        pageTreeNodeService = jasmine.createSpyObj('pageTreeNodeService', [
            'scrollToElement',
            'existedSmartEditElement'
        ]);
        pageTreeComponent = new PageTreeComponent(
            pageTreeNodeService,
            nodeInfoService,
            logService,
            componentMenuConditionAndCallbackService,
            permissionService
        );

        pageTreeComponent.component = createComponentNode('0');
        pageTreeComponent.slotId = 'slot-id-1';
        pageTreeComponent.slotUuid = 'slot-uuid-1';
        pageTreeComponent.slotElementUuid = 'slot-element-uuid-1';
        pageTreeComponent.onComponentExpanded = onComponentExpanded;
    });

    describe('onClickComponentNode', () => {
        it('WHEN onClickComponentNode is triggered AND component is not expanded THEN publish event ', async () => {
            pageTreeComponent.component.isExpanded = true;

            await pageTreeComponent.onClickComponentNode($event);

            expect(nodeInfoService.publishComponentSelected).toHaveBeenCalledWith(
                pageTreeComponent.component,
                false,
                pageTreeComponent.slotElementUuid
            );
        });

        it(
            'WHEN onClickComponentNode is triggered ' +
                'AND component is expanded THEN scroll to element and publish component selected ',
            async () => {
                pageTreeComponent.component.isExpanded = false;
                pageTreeNodeService.existedSmartEditElement.and.returnValue(Promise.resolve(true));
                await pageTreeComponent.onClickComponentNode($event);

                expect(onComponentExpanded.emit).toHaveBeenCalledWith(pageTreeComponent.component);
                expect(pageTreeNodeService.scrollToElement).toHaveBeenCalledWith(
                    'component-element-uuid-0'
                );
                expect(nodeInfoService.publishComponentSelected).toHaveBeenCalledWith(
                    pageTreeComponent.component,
                    true,
                    pageTreeComponent.slotElementUuid
                );
            }
        );

        it(
            'WHEN onClickComponentNode is triggered ' +
                'AND component is expanded BUT smartEdit element is not existed ' +
                'THEN will never publish component selected',
            async () => {
                pageTreeComponent.component.isExpanded = false;
                pageTreeNodeService.existedSmartEditElement.and.returnValue(Promise.resolve(false));
                await pageTreeComponent.onClickComponentNode($event);

                expect(onComponentExpanded.emit).toHaveBeenCalledWith(pageTreeComponent.component);
                expect(pageTreeNodeService.scrollToElement).toHaveBeenCalledWith(
                    'component-element-uuid-0'
                );
                expect(nodeInfoService.publishComponentSelected).toHaveBeenCalledTimes(0);
            }
        );
    });
});
