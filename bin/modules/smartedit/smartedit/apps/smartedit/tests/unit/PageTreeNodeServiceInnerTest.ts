/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import 'jasmine';
import { PageTreeNodeService } from 'smartedit/services';
import {
    ID_ATTRIBUTE,
    TYPE_ATTRIBUTE,
    CONTAINER_ID_ATTRIBUTE,
    CATALOG_VERSION_UUID_ATTRIBUTE,
    UUID_ATTRIBUTE,
    CONTAINER_TYPE_ATTRIBUTE,
    IComponentHandlerService,
    ELEMENT_UUID_ATTRIBUTE,
    LogService,
    CrossFrameEventService,
    WindowUtils,
    CONTENT_SLOT_TYPE
} from 'smarteditcommons';

describe('PageTreeNodeService - inner', () => {
    const window = {} as any;
    let windowUtils: jasmine.SpyObj<WindowUtils>;
    const yjQueryBody = {} as any;
    let pageTreeNodeService: any;
    let yjQuery: any;
    let componentHandlerService: jasmine.SpyObj<IComponentHandlerService>;
    let crossFrameEventService: jasmine.SpyObj<CrossFrameEventService>;
    let logService: jasmine.SpyObj<LogService>;
    let slot1: jasmine.SpyObj<HTMLElement>;
    let slot2: jasmine.SpyObj<HTMLElement>;
    let component1: jasmine.SpyObj<HTMLElement>;
    let component2: jasmine.SpyObj<HTMLElement>;
    const nodes = [];

    const createValidComponent = (baseName: string, index: string): jasmine.SpyObj<HTMLElement> => {
        const component = jasmine.createSpyObj(baseName, [
            'attr',
            'is',
            'scrollIntoView',
            'getBoundingClientRect',
            'get'
        ]);
        component.attr.withArgs(ID_ATTRIBUTE).and.returnValue(`component${index}-component-id`);
        component.attr.withArgs(UUID_ATTRIBUTE).and.returnValue(`component${index}-component-uuid`);
        component.attr.withArgs(TYPE_ATTRIBUTE).and.returnValue(`component${index}-component-type`);
        component.attr
            .withArgs(CATALOG_VERSION_UUID_ATTRIBUTE)
            .and.returnValue(`component${index}-catalog-version-uuid`);
        component.attr
            .withArgs(CONTAINER_ID_ATTRIBUTE)
            .and.returnValue(`component${index}-catalog-container-id`);
        component.attr
            .withArgs(CONTAINER_TYPE_ATTRIBUTE)
            .and.returnValue(`component${index}-catalog-container-type`);
        component.is.and.returnValue(true);
        component.attr
            .withArgs(ELEMENT_UUID_ATTRIBUTE)
            .and.returnValue(`component${index}-smartedit-element-uuid`);

        return component;
    };

    const createValidSlot = (baseName: string, index: string): jasmine.SpyObj<HTMLElement> => {
        const slot = jasmine.createSpyObj(baseName, [
            'attr',
            'is',
            'scrollIntoView',
            'getBoundingClientRect',
            'get'
        ]);
        slot.attr.withArgs(ID_ATTRIBUTE).and.returnValue(`slot${index}-component-id`);
        slot.attr.withArgs(UUID_ATTRIBUTE).and.returnValue(`slot${index}-component-uuid`);
        slot.attr.withArgs(TYPE_ATTRIBUTE).and.returnValue(`slot${index}-component-type`);
        slot.attr
            .withArgs(CATALOG_VERSION_UUID_ATTRIBUTE)
            .and.returnValue(`slot${index}-catalog-version-uuid`);
        slot.attr
            .withArgs(CONTAINER_ID_ATTRIBUTE)
            .and.returnValue(`slot${index}-catalog-container-id`);
        slot.attr
            .withArgs(CONTAINER_TYPE_ATTRIBUTE)
            .and.returnValue(`slot${index}-catalog-container-type`);
        slot.is.and.returnValue(true);
        slot.attr
            .withArgs(ELEMENT_UUID_ATTRIBUTE)
            .and.returnValue(`slot${index}-smartedit-element-uuid`);

        return slot;
    };

    beforeEach(() => {
        yjQuery = jasmine.createSpy('yjQuery');
        yjQuery.and.callFake((selector: jasmine.SpyObj<HTMLElement> | 'body') => {
            if (selector === 'body') {
                return yjQueryBody;
            } else {
                return selector;
            }
        });

        componentHandlerService = jasmine.createSpyObj('componentHandlerService', [
            'getFirstSmartEditComponentChildren'
        ]);

        crossFrameEventService = jasmine.createSpyObj('crossFrameEventService', ['publish']);

        // valid slot1
        slot1 = createValidSlot('slot1', '1');

        // valid component1
        component1 = createValidComponent('component1', '1');

        // valid component2
        component2 = createValidComponent('component2', '2');

        // invalid slot2
        slot2 = jasmine.createSpyObj('slot2', ['attr', 'is']);
        (slot2 as any).attr.withArgs(ID_ATTRIBUTE).and.returnValue('slot2-component-id');
        (slot2 as any).attr.withArgs(UUID_ATTRIBUTE).and.returnValue('slot2-component-uuid');
        (slot2 as any).attr.withArgs(TYPE_ATTRIBUTE).and.returnValue('slot2-component-type');
        (slot2 as any).attr.withArgs(CATALOG_VERSION_UUID_ATTRIBUTE).and.returnValue(null);
        (slot2 as any).is.and.returnValue(true);
        windowUtils = jasmine.createSpyObj('windowUtils', ['getWindow']);
        windowUtils.getWindow.and.returnValue(window);
        pageTreeNodeService = new PageTreeNodeService(
            componentHandlerService,
            yjQuery,
            crossFrameEventService,
            windowUtils,
            logService
        );

        componentHandlerService.getFirstSmartEditComponentChildren.and.callFake(((node: any) => {
            if (node === yjQueryBody) {
                return [slot1, slot2];
            } else if (node === slot1) {
                return [component1];
            } else {
                return [];
            }
        }) as any);

        nodes.push({ parent: slot1 });
    });

    describe('buildSlotNodes', () => {
        it('buildSlotNodes will build an array of slotNodes under the html body', async () => {
            pageTreeNodeService.buildSlotNodes();

            const slotNodes = await pageTreeNodeService.getSlotNodes();
            expect(slotNodes.length).toBe(1);
            expect(slotNodes[0].componentId).toBe('slot1-component-id');
            expect(slotNodes[0].componentUuid).toBe('slot1-component-uuid');
            expect(slotNodes[0].elementUuid).toBe('slot1-smartedit-element-uuid');

            expect(slotNodes[0].childrenNode[0].componentId).toBe('component1-component-id');
            expect(slotNodes[0].childrenNode[0].componentUuid).toBe('component1-component-uuid');
            expect(slotNodes[0].childrenNode[0].elementUuid).toBe(
                'component1-smartedit-element-uuid'
            );
        });
    });

    describe('updateSlotNodes', () => {
        it('WHEN component update THEN refresh the slot', async () => {
            pageTreeNodeService.buildSlotNodes();

            componentHandlerService.getFirstSmartEditComponentChildren
                .withArgs(slot1)
                .and.returnValue([component2] as any);
            pageTreeNodeService.updateSlotNodes(nodes);

            const slotNodes = await pageTreeNodeService.getSlotNodes();

            expect(slotNodes[0].childrenNode[0].componentId).toBe('component2-component-id');
            expect(slotNodes[0].childrenNode[0].componentUuid).toBe('component2-component-uuid');
            expect(slotNodes[0].childrenNode[0].elementUuid).toBe(
                'component2-smartedit-element-uuid'
            );
        });

        it('WHEN slot update THEN rebuild entire page', () => {
            // WHEN
            const refreshNodes = [];
            const node = jasmine.createSpyObj('node', ['attr']);
            node.attr.withArgs(TYPE_ATTRIBUTE).and.returnValue(CONTENT_SLOT_TYPE);
            refreshNodes.push({ parent: slot1, node });
            spyOn(pageTreeNodeService, 'buildSlotNodes');
            pageTreeNodeService.slotNodes = [];

            // THEN
            pageTreeNodeService.updateSlotNodes(refreshNodes);

            expect(pageTreeNodeService.buildSlotNodes).toHaveBeenCalled();
        });
    });

    describe('scrollToElement', () => {
        let slot3: jasmine.SpyObj<HTMLElement> = null;
        const UUID = 'uuid';

        beforeEach(function () {
            window.innerHeight = 1000;
            window.innerWidth = 1000;
            slot3 = createValidSlot('slot3', '3');
            yjQuery
                .withArgs('[data-smartedit-element-uuid="uuid"]' as any)
                .and.returnValue(slot3 as any);
            (slot3 as any).get.and.returnValue([slot3]);
        });

        it('scrollToElement when element is above viewport', async () => {
            (slot3 as any).getBoundingClientRect.and.returnValue({
                top: -150,
                left: 0,
                bottom: -10,
                right: 200
            });
            await pageTreeNodeService.scrollToElement(UUID);

            expect(slot3.scrollIntoView).toHaveBeenCalledWith({
                behavior: 'smooth',
                block: 'center'
            });
        });

        it('scrollToElement when element is under viewport', async () => {
            (slot3 as any).getBoundingClientRect.and.returnValue({
                top: 1150,
                left: 0,
                bottom: 1300,
                right: 200
            });
            await pageTreeNodeService.scrollToElement(UUID);

            expect(slot3.scrollIntoView).toHaveBeenCalledWith({
                behavior: 'smooth',
                block: 'center'
            });
        });

        it('scrollToElement when element is partially inside viewport', async () => {
            (slot3 as any).getBoundingClientRect.and.returnValue({
                top: 900,
                left: 0,
                bottom: 1300,
                right: 200
            });
            await pageTreeNodeService.scrollToElement(UUID);

            expect(slot3.scrollIntoView).toHaveBeenCalledTimes(0);
        });
    });
});
