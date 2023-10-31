/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ChangeDetectorRef } from '@angular/core';
import { PageTreePanel } from 'cmssmarteditcontainer/components/pageTree';
import { SlotNode, NodeInfoService } from 'cmssmarteditcontainer/services';
import { CrossFrameEventService, LogService } from 'smarteditcommons';
import { createSlotNode, createComponentNode } from '../util';

describe('PageTreePanel', () => {
    let pageTreePanel: PageTreePanel;
    let crossFrameEventService: jasmine.SpyObj<CrossFrameEventService>;
    let nodeInfoService: jasmine.SpyObj<NodeInfoService>;
    let logService: jasmine.SpyObj<LogService>;
    let yjQuery: any;
    let cdr: jasmine.SpyObj<ChangeDetectorRef>;
    let htmlElement: jasmine.SpyObj<HTMLElement>;
    const slotNodes: SlotNode[] = [];
    const slot1 = createSlotNode('1');
    const slot2 = createSlotNode('2');
    const component1 = createComponentNode('1');
    const component2 = createComponentNode('2');

    beforeEach(() => {
        crossFrameEventService = jasmine.createSpyObj('crossFrameEventService', ['subscribe']);
        nodeInfoService = jasmine.createSpyObj('nodeInfoService', [
            'publishSlotSelected',
            'publishComponentSelected',
            'buildNodesInfo'
        ]);
        logService = jasmine.createSpyObj('logService', ['error']);
        yjQuery = jasmine.createSpy('yjQuery');
        cdr = jasmine.createSpyObj('cdr', ['detectChanges']);
        slot1.componentNodes.push(component1);
        slot2.componentNodes.push(component2);
        slotNodes.push(slot1);
        slotNodes.push(slot2);

        nodeInfoService.buildNodesInfo.and.returnValue(Promise.resolve(slotNodes));
        pageTreePanel = new PageTreePanel(
            crossFrameEventService,
            nodeInfoService,
            yjQuery,
            cdr,
            logService
        );
    });

    describe('onSlotExpanded', () => {
        it('WHEN onSlotExpanded is triggered THEN close other slots', async () => {
            await pageTreePanel.ngOnInit();

            slot2.isExpanded = true;
            slot2.componentNodes[0].isExpanded = true;

            pageTreePanel.onSlotExpanded(slot1);

            expect(slot2.isExpanded).toBeFalse();
            expect(slot2.componentNodes[0].isExpanded).toBeFalse();
        });
    });

    describe('handleEventOpenInPageTree', () => {
        it('Close the other slots and components and expand the triggered component and make it in viewport', async () => {
            await pageTreePanel.ngOnInit();
            slot1.isExpanded = true;
            slot1.componentNodes[0].isExpanded = true;
            htmlElement = jasmine.createSpyObj('htmlElement', ['scrollIntoView', 'get']);
            yjQuery
                .withArgs('div[node-smartedit-element-uuid="component-element-uuid-2"]' as any)
                .and.returnValue(htmlElement as any);
            (htmlElement as any).get.and.returnValue([htmlElement] as any);
            pageTreePanel.handleEventOpenInPageTree('eventId', 'component-element-uuid-2');
            expect(htmlElement.scrollIntoView).toHaveBeenCalledWith({
                behavior: 'smooth',
                block: 'center'
            });
            expect(slot1.isExpanded).toBeFalse();
            expect(slot1.componentNodes[0].isExpanded).toBeFalse();
            expect(slot2.isExpanded).toBeTrue();
            expect(slot2.componentNodes[0].isExpanded).toBeTrue();
        });
    });
});
