/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { EventEmitter } from '@angular/core';
import { PageTreeSlot } from 'cmssmarteditcontainer/components/pageTree';
import { SlotNode, NodeInfoService } from 'cmssmarteditcontainer/services';
import {
    IPageTreeNodeService,
    LogService,
    SystemEventService,
    IUserTrackingService
} from 'smarteditcommons';
import { createSlotNode } from '../util';

describe('PageTreeSlot', () => {
    let pageTreeSlot: PageTreeSlot;
    let pageTreeNodeService: jasmine.SpyObj<IPageTreeNodeService>;
    let nodeInfoService: jasmine.SpyObj<NodeInfoService>;
    let logService: jasmine.SpyObj<LogService>;
    let $event: jasmine.SpyObj<Event>;
    let onSlotExpanded: jasmine.SpyObj<EventEmitter<SlotNode>>;
    let systemEventService: jasmine.SpyObj<SystemEventService>;
    let userTrackingService: jasmine.SpyObj<IUserTrackingService>;

    beforeEach(() => {
        $event = jasmine.createSpyObj('$event', ['stopPropagation']);
        nodeInfoService = jasmine.createSpyObj('nodeInfoService', [
            'publishSlotSelected',
            'publishComponentSelected'
        ]);
        logService = jasmine.createSpyObj('logService', ['info', 'error']);
        pageTreeNodeService = jasmine.createSpyObj('pageTreeNodeService', [
            'scrollToElement',
            'existedSmartEditElement'
        ]);

        systemEventService = jasmine.createSpyObj('systemEventService', ['subscribe']);
        onSlotExpanded = jasmine.createSpyObj('onSlotExpanded', ['emit']);
        userTrackingService = jasmine.createSpyObj<IUserTrackingService>('userTrackingService', [
            'trackingUserAction'
        ]);

        pageTreeSlot = new PageTreeSlot(
            pageTreeNodeService,
            nodeInfoService,
            systemEventService,
            logService,
            userTrackingService
        );
        pageTreeSlot.node = createSlotNode('0');
        pageTreeSlot.onSlotExpanded = onSlotExpanded;
    });

    describe('onClickSlotNode', () => {
        it(
            'WHEN onClickSlotNode is triggered AND slot is not expanded ' +
                'THEN publish slot selected event ',
            async () => {
                pageTreeSlot.node.isExpanded = true;
                pageTreeSlot.node.componentNodes = [{ isExpanded: true }] as any;
                await pageTreeSlot.onClickSlotNode($event);

                expect(nodeInfoService.publishSlotSelected).toHaveBeenCalledWith(pageTreeSlot.node);
                expect(pageTreeSlot.node.componentNodes[0].isExpanded).toBeFalse();
            }
        );

        it(
            'WHEN onClickSlotNode is triggered AND slot is expanded ' +
                'THEN scroll to element and publish slot selected event ',
            async () => {
                pageTreeSlot.node.isExpanded = false;
                pageTreeNodeService.existedSmartEditElement.and.returnValue(Promise.resolve(true));

                await pageTreeSlot.onClickSlotNode($event);

                expect(onSlotExpanded.emit).toHaveBeenCalledWith(pageTreeSlot.node);
                expect(pageTreeNodeService.scrollToElement).toHaveBeenCalledWith(
                    'component-element-uuid-0'
                );
                await expect(nodeInfoService.publishSlotSelected).toHaveBeenCalledWith(
                    pageTreeSlot.node
                );
            }
        );

        it('WHEN onClickSlotNode is triggered , trigger user action tracking', async () => {
            await pageTreeSlot.onClickSlotNode($event);
            expect(userTrackingService.trackingUserAction).toHaveBeenCalled();
        });

        it(
            'WHEN onClickSlotNode is triggered AND slot is expanded ' +
                'BUT smartEdit element is not existed ' +
                'THEN will never publish slot selected event ',
            async () => {
                pageTreeSlot.node.isExpanded = false;
                pageTreeNodeService.existedSmartEditElement.and.returnValue(Promise.resolve(false));

                await pageTreeSlot.onClickSlotNode($event);

                expect(onSlotExpanded.emit).toHaveBeenCalledWith(pageTreeSlot.node);
                expect(pageTreeNodeService.scrollToElement).toHaveBeenCalledWith(
                    'component-element-uuid-0'
                );
                await expect(nodeInfoService.publishSlotSelected).toHaveBeenCalledTimes(0);
            }
        );
    });

    describe('onComponentExpanded', () => {
        it(
            'WHEN a component is expanded ' +
                'THEN the other components should be unexpanded and publish the event ',
            () => {
                pageTreeSlot.node.componentNodes = [
                    { elementUuid: 'uuid-1', isExpanded: true },
                    { elementUuid: 'uuid-2', isExpanded: false }
                ] as any;

                pageTreeSlot.onComponentExpanded({
                    elementUuid: 'uuid-2',
                    isExpanded: false
                } as any);
                expect(nodeInfoService.publishComponentSelected).toHaveBeenCalledWith(
                    { elementUuid: 'uuid-1', isExpanded: false } as any,
                    false,
                    pageTreeSlot.node.elementUuid
                );
            }
        );
    });
});
