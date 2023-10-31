/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import 'jasmine';
import { ICMSComponent, ISlotVisibilityService } from 'cmscommons';
import { NodeInfoService } from 'cmssmarteditcontainer/services';
import {
    CmsitemsRestService,
    CrossFrameEventService,
    IPageTreeNodeService,
    LogService,
    PageTreeNode
} from 'smarteditcommons';
import { createPageTreeNode, createCMSComponent } from '../util';

describe('nodeInfoService', () => {
    let cmsitemsRestService: jasmine.SpyObj<CmsitemsRestService>;
    let slotVisibilityService: jasmine.SpyObj<ISlotVisibilityService>;
    let logService: jasmine.SpyObj<LogService>;
    let crossFrameEventService: jasmine.SpyObj<CrossFrameEventService>;
    let pageTreeNodeService: jasmine.SpyObj<IPageTreeNodeService>;

    let nodeInfoService: NodeInfoService;

    const slot1 = createPageTreeNode('slot', '1');
    const component1 = createPageTreeNode('component', '1');
    slot1.childrenNode.push(component1);
    const nodes: PageTreeNode[] = [slot1];

    const cmsComponent1 = createCMSComponent('1', ['slot-uuid-1']);
    const cmsComponent2 = createCMSComponent('2', ['slot-uuid-1']);
    const componentFromBackends: ICMSComponent[] = [cmsComponent1];
    const hiddenComponent: ICMSComponent[] = [cmsComponent2];

    beforeEach(() => {
        crossFrameEventService = jasmine.createSpyObj('crossFrameEventService', [
            'publish',
            'subscribe'
        ]);
        cmsitemsRestService = jasmine.createSpyObj<CmsitemsRestService>('cmsitemsRestService', [
            'getByIds'
        ]);
        logService = jasmine.createSpyObj<LogService>('logService', ['error']);
        slotVisibilityService = jasmine.createSpyObj<ISlotVisibilityService>(
            'slotVisibilityService',
            ['getHiddenComponents']
        );
        pageTreeNodeService = jasmine.createSpyObj('pageTreeNodeService', ['getSlotNodes']);
        pageTreeNodeService.getSlotNodes.and.returnValue(Promise.resolve(nodes));

        slotVisibilityService.getHiddenComponents
            .withArgs('slot-id-1')
            .and.returnValue(Promise.resolve(hiddenComponent));
        cmsitemsRestService.getByIds
            .withArgs(['component-uuid-1'], 'DEFAULT')
            .and.returnValue(Promise.resolve({ response: componentFromBackends } as any));

        nodeInfoService = new NodeInfoService(
            crossFrameEventService,
            cmsitemsRestService,
            logService,
            slotVisibilityService,
            pageTreeNodeService
        );
    });

    it('buildNodesInfo will get additional information about all components under slot from the background, including hidden components', async () => {
        const slotNodes = await nodeInfoService.buildNodesInfo();

        expect(slotNodes.length).toBe(1);
        expect(slotNodes[0].componentId).toBe('slot-id-1');
        expect(slotNodes[0].componentUuid).toBe('slot-uuid-1');

        expect(slotNodes[0].componentNodes.length).toBe(2);
        expect(slotNodes[0].componentNodes[0].isHidden).toBeFalse();
        expect(slotNodes[0].componentNodes[0].componentId).toBe('component-id-1');
        expect(slotNodes[0].componentNodes[0].name).toBe('component-name-1');
        expect(slotNodes[0].componentNodes[1].isHidden).toBeTrue();
        expect(slotNodes[0].componentNodes[1].uid).toBe('component-id-2');
        expect(slotNodes[0].componentNodes[1].name).toBe('component-name-2');
        expect(slotNodes[0].hiddenComponentCount).toBe(1);
    });

    it('updatePartTreeNodesInfo will update information about all components in slot', async () => {
        await nodeInfoService.buildNodesInfo();

        const component3 = createPageTreeNode('component', '3');
        const cmsComponent3 = createCMSComponent('3', ['slot-uuid-1']);
        const updatedNodes = {
            'slot-element-uuid-1': [component3]
        };

        slotVisibilityService.getHiddenComponents
            .withArgs('slot-id-1')
            .and.returnValue(Promise.resolve([cmsComponent1, cmsComponent2]));
        cmsitemsRestService.getByIds
            .withArgs(['component-uuid-3'], 'DEFAULT')
            .and.returnValue(Promise.resolve({ response: [cmsComponent3] } as any));
        const slotNodes = await nodeInfoService.updatePartTreeNodesInfo(updatedNodes);

        expect(slotNodes.length).toBe(1);
        expect(slotNodes[0].componentId).toBe('slot-id-1');
        expect(slotNodes[0].componentUuid).toBe('slot-uuid-1');

        expect(slotNodes[0].componentNodes.length).toBe(3);
        expect(slotNodes[0].componentNodes[0].isHidden).toBeFalse();
        expect(slotNodes[0].componentNodes[0].componentId).toBe('component-id-3');
        expect(slotNodes[0].componentNodes[0].name).toBe('component-name-3');
        expect(slotNodes[0].componentNodes[1].isHidden).toBeTrue();
        expect(slotNodes[0].componentNodes[1].uid).toBe('component-id-1');
        expect(slotNodes[0].componentNodes[1].name).toBe('component-name-1');
        expect(slotNodes[0].componentNodes[2].isHidden).toBeTrue();
        expect(slotNodes[0].componentNodes[2].uid).toBe('component-id-2');
        expect(slotNodes[0].componentNodes[2].name).toBe('component-name-2');
        expect(slotNodes[0].hiddenComponentCount).toBe(2);
    });

    it('updatePartTreeNodesInfoBySlotUuid will update information about all components in slot', async () => {
        await nodeInfoService.buildNodesInfo();

        slotVisibilityService.getHiddenComponents
            .withArgs('slot-id-1')
            .and.returnValue(Promise.resolve([cmsComponent2]));

        const slotNodes = await nodeInfoService.updatePartTreeNodesInfoBySlotUuid('slot-uuid-1');

        expect(slotNodes.length).toBe(1);
        expect(slotNodes[0].componentId).toBe('slot-id-1');
        expect(slotNodes[0].componentUuid).toBe('slot-uuid-1');

        expect(slotNodes[0].componentNodes.length).toBe(2);
        expect(slotNodes[0].componentNodes[0].isHidden).toBeFalse();
        expect(slotNodes[0].componentNodes[0].componentId).toBe('component-id-1');
        expect(slotNodes[0].componentNodes[0].name).toBe('component-name-1');
        expect(slotNodes[0].componentNodes[1].isHidden).toBeTrue();
        expect(slotNodes[0].componentNodes[1].uid).toBe('component-id-2');
        expect(slotNodes[0].componentNodes[1].name).toBe('component-name-2');
        expect(slotNodes[0].hiddenComponentCount).toBe(1);
    });
});
