/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ICMSComponent } from 'cmscommons';
import { ComponentInfoService } from 'cmssmartedit/services/ComponentInfoService';
import {
    CrossFrameEventService,
    LogService,
    PromiseUtils,
    functionsUtils,
    CMSModesService,
    CmsitemsRestService
} from 'smarteditcommons';

describe('ComponentInfoService', () => {
    let componentInfoService: ComponentInfoService;
    let cmsitemsRestService: jasmine.SpyObj<CmsitemsRestService>;
    let crossFrameEventService: jasmine.SpyObj<CrossFrameEventService>;
    let promiseUtils: PromiseUtils;
    let yjQuery: jasmine.Spy;
    let logService: jasmine.SpyObj<LogService>;
    let cMSModesService: jasmine.SpyObj<CMSModesService>;

    const COMPONENT1 = ({
        id: 1,
        uuid: 'uuid-001',
        attr: () => 'uuid-001'
    } as any) as ICMSComponent;
    const COMPONENT2 = ({
        id: 2,
        uuid: 'uuid-002',
        attr: () => 'uuid-002'
    } as any) as ICMSComponent;
    const MOCK_COMPONENTS = [COMPONENT1, COMPONENT2];

    const COMPONENTS_DATA = [
        {
            uuid: COMPONENT1.uuid
        },
        {
            uuid: COMPONENT2.uuid
        }
    ] as ICMSComponent[];

    const getExpectedComponentData = (componentUuid: string) =>
        COMPONENTS_DATA.find((component) => component.uuid === componentUuid);

    beforeEach(() => {
        crossFrameEventService = jasmine.createSpyObj('crossFrameEventService', ['subscribe']);

        cmsitemsRestService = jasmine.createSpyObj('cmsitemsRestService', ['getById', 'getByIds']);
        cmsitemsRestService.getByIds.and.returnValue(
            Promise.resolve<any>({
                response: COMPONENTS_DATA
            })
        );

        logService = jasmine.createSpyObj('logService', ['error']);
        crossFrameEventService = jasmine.createSpyObj<CrossFrameEventService>(
            'crossFrameEventService',
            ['subscribe']
        );
        cMSModesService = jasmine.createSpyObj<CMSModesService>('cMSModesService', [
            'isVersioningPerspectiveActive'
        ]);
        cMSModesService.isVersioningPerspectiveActive.and.returnValue(Promise.resolve(false));

        promiseUtils = new PromiseUtils();

        yjQuery = jasmine.createSpy('yJQuery');
        yjQuery.and.callFake((item) => ({
            attr: () => item.attr()
        }));

        componentInfoService = new ComponentInfoService(
            (yjQuery as any) as JQueryStatic,
            logService,
            crossFrameEventService,
            cmsitemsRestService,
            promiseUtils,
            cMSModesService
        );
    });

    it('WHEN components are added it should call getComponentsDataByUUIDs', () => {
        spyOn(componentInfoService as any, 'getComponentsDataByUUIDs');

        (componentInfoService as any).onComponentsAddedToOverlay(MOCK_COMPONENTS);

        expect((componentInfoService as any).getComponentsDataByUUIDs).toHaveBeenCalledWith([
            COMPONENT1.uuid,
            COMPONENT2.uuid
        ]);
    });

    it('WHEN components are added THEN getById should return the component data', async () => {
        await (componentInfoService as any).onComponentsAddedToOverlay(MOCK_COMPONENTS);

        const result = await componentInfoService.getById(COMPONENT1.uuid);
        expect(result).toEqual(getExpectedComponentData(COMPONENT1.uuid));
    });

    it(
        'WHEN getById is called and the component data is not cached, it should resolve when the component data is' +
            ' ready',
        async () => {
            (componentInfoService as any).getComponentsDataByUUIDs([COMPONENT2.uuid]);
            spyOn(window.document, 'querySelectorAll').and.returnValue([COMPONENT2] as any);

            const result = await componentInfoService.getById(COMPONENT2.uuid);
            expect(result).toEqual(getExpectedComponentData(COMPONENT2.uuid));
        }
    );

    it('WHEN getById is called and the component data fetch failed, it should reject the getById promise', async () => {
        cmsitemsRestService.getById.and.callFake(() =>
            Promise.reject({
                message: 'error while retrieving cmsitems'
            })
        );

        (componentInfoService as any).getComponentsDataByUUIDs([COMPONENT2.uuid]);
        try {
            await componentInfoService.getById(COMPONENT2.uuid);

            functionsUtils.assertFail();
        } catch (e) {
            expect(e.message).toEqual('error while retrieving cmsitems');
        }
    });

    it('GIVEN value is cached WHEN getById is called THEN it should be retrieved from the cache', async () => {
        // GIVEN
        (componentInfoService as any).forceAddComponent(COMPONENT1);

        // WHEN
        const promise = await componentInfoService.getById(COMPONENT1.uuid);

        // THEN
        expect(promise).toBe(COMPONENT1);
        expect(cmsitemsRestService.getById.calls.count()).toBe(0);
    });

    it(
        'WHEN getById is called with the forceImmediateResult enabled THEN it should query the backend right away AND' +
            ' return the component data',
        async () => {
            // WHEN

            cmsitemsRestService.getById.and.callFake(((uuid) => {
                if (uuid === COMPONENT1.uuid) {
                    return Promise.resolve(COMPONENT1);
                }
            }) as any);

            // THEN
            const result = await componentInfoService.getById(COMPONENT1.uuid, true);
            expect(result).toEqual(COMPONENT1);
            expect(cmsitemsRestService.getById.calls.count()).toBe(1);
        }
    );

    it(
        'WHEN getById is called with the forceImmediateResult enabled AND the call fails THEN it should reject the' +
            ' promise',
        async () => {
            // GIVEN
            cmsitemsRestService.getById.and.callFake(() =>
                Promise.reject({
                    message: 'error while retrieving cmsitems'
                })
            );

            // WHEN
            try {
                await componentInfoService.getById(COMPONENT1.uuid, true);

                functionsUtils.assertFail();
            } catch (e) {
                // THEN
                expect(e.message).toEqual('error while retrieving cmsitems');
            }
        }
    );

    it(
        'GIVEN value is cached WHEN getById is called with the forceImmediateResult enabled THEN it should be' +
            ' retrieved from the cache',
        async () => {
            // GIVEN
            (componentInfoService as any).forceAddComponent(COMPONENT1);

            // WHEN
            const promise = await componentInfoService.getById(COMPONENT1.uuid, true);

            // THEN
            expect(promise).toBe(COMPONENT1);
            expect(cmsitemsRestService.getById.calls.count()).toBe(0);
        }
    );

    it('WHEN component is added to page THEN the cache must be updated', async () => {
        cmsitemsRestService.getById.and.callFake(((uuid) => {
            if (uuid === COMPONENT1.uuid) {
                return Promise.resolve(COMPONENT1);
            }
        }) as any);
        const promise = await componentInfoService.getById(COMPONENT1.uuid);

        // THEN
        expect(promise).toEqual(COMPONENT1);
    });

    it('WHEN component is removed by force THEN it must be removed from the cache', () => {
        // GIVEN
        const componentToRemove = {
            uuid: COMPONENT1.uuid,
            slots: ['some other uuid']
        };
        (componentInfoService as any).forceAddComponent(componentToRemove);

        // WHEN
        (componentInfoService as any).forceRemoveComponent(componentToRemove);

        // THEN
        expect((componentInfoService as any).isComponentCached(COMPONENT1.uuid)).toBeFalsy();
    });

    it('should subscribe on PAGE_CHANGE event', () => {
        expect(crossFrameEventService.subscribe).toHaveBeenCalledWith(
            'PAGE_CHANGE',
            jasmine.any(Function)
        );
    });

    it('should subscribe on OVERLAY_RERENDERED_EVENT event', () => {
        expect(crossFrameEventService.subscribe).toHaveBeenCalledWith(
            'overlayRerendered',
            jasmine.any(Function)
        );
    });

    it('should subscribe on USER_HAS_CHANGED event', () => {
        expect(crossFrameEventService.subscribe).toHaveBeenCalledWith(
            'USER_HAS_CHANGED',
            jasmine.any(Function)
        );
    });

    it('should subscribe to COMPONENT_CREATED_EVENT', () => {
        expect(crossFrameEventService.subscribe).toHaveBeenCalledWith(
            'COMPONENT_CREATED_EVENT',
            jasmine.any(Function)
        );
    });

    it('should subscribe to COMPONENT_UPDATED_EVENT', () => {
        expect(crossFrameEventService.subscribe).toHaveBeenCalledWith(
            'COMPONENT_UPDATED_EVENT',
            jasmine.any(Function)
        );
    });

    it('should subscribe to COMPONENT_REMOVED_EVENT', () => {
        expect(crossFrameEventService.subscribe).toHaveBeenCalledWith(
            'COMPONENT_REMOVED_EVENT',
            jasmine.any(Function)
        );
    });
});
