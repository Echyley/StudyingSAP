/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ContainerInfo, SlotContainerService } from 'cmssmartedit/services/SlotContainerService';
import { IExperienceService, IRestServiceFactory } from 'smarteditcommons';

describe('slotContainerService', () => {
    // --------------------------------------------------------------------------------------
    // Constants
    // --------------------------------------------------------------------------------------
    const PAGE_ID = 'some page ID';
    const SLOT_ID = 'some slot';
    const CONTAINER_ID = 'some container ID';
    const COMPONENT_IN_CONTAINER_ID = 'withContainer';
    const COMPONENT_WITHOUT_CONTAINER_ID = 'withoutContainer';

    // --------------------------------------------------------------------------------------
    // Variables
    // --------------------------------------------------------------------------------------
    let slotContainerService: SlotContainerService;
    let experienceService: jasmine.SpyObj<IExperienceService>;
    let containersRestService: jasmine.SpyObj<IRestServiceFactory>;
    let restServiceFactory: jasmine.SpyObj<IRestServiceFactory>;
    let sampleContainerInfo: ContainerInfo;

    // --------------------------------------------------------------------------------------
    // Tests
    // --------------------------------------------------------------------------------------
    beforeEach(() => {
        sampleContainerInfo = {
            containerId: CONTAINER_ID,
            containerType: 'some container type',
            pageId: PAGE_ID,
            slotId: SLOT_ID,
            components: [COMPONENT_IN_CONTAINER_ID]
        };

        containersRestService = jasmine.createSpyObj('containersRestService', ['get']);
        containersRestService.get.and.returnValue(
            Promise.resolve({
                pageContentSlotContainerList: [sampleContainerInfo]
            }) as any
        );
        experienceService = jasmine.createSpyObj('experienceService', ['getCurrentExperience']);
        experienceService.getCurrentExperience.and.returnValue(
            Promise.resolve({ pageId: PAGE_ID }) as any
        );
        restServiceFactory = jasmine.createSpyObj<IRestServiceFactory>('restServiceFactory', [
            'get'
        ]);
        restServiceFactory.get.and.returnValue(containersRestService as any);

        slotContainerService = new SlotContainerService(restServiceFactory, experienceService);
    });

    it(`WHEN the service is called
        THEN it calls the right web service`, () => {
        // GIVEN
        const expectedServiceEndpoint =
            '/cmswebservices/v1/sites/CURRENT_PAGE_CONTEXT_SITE_ID/catalogs/CURRENT_PAGE_CONTEXT_CATALOG/versions/CURRENT_PAGE_CONTEXT_CATALOG_VERSION/pagescontentslotscontainers?pageId=:pageId';

        // WHEN/THEN
        expect(restServiceFactory.get).toHaveBeenCalledWith(expectedServiceEndpoint);
    });

    it(`GIVEN container information is retrieved before
        WHEN getComponentContainer is called again
        THEN it retrieves the information from cached values`, async () => {
        // GIVEN
        expect(containersRestService.get).toHaveBeenCalledTimes(0);

        // WHEN/THEN
        await slotContainerService.getComponentContainer(SLOT_ID, COMPONENT_IN_CONTAINER_ID);
        expect(containersRestService.get).toHaveBeenCalledTimes(1);

        await slotContainerService.getComponentContainer(SLOT_ID, COMPONENT_IN_CONTAINER_ID);
        expect(containersRestService.get).toHaveBeenCalledTimes(1);
    });

    it(`GIVEN a component is not inside a container
        WHEN getComponentContainer is called
        THEN it returns null`, async () => {
        // GIVEN

        // WHEN
        const promise = await slotContainerService.getComponentContainer(
            SLOT_ID,
            COMPONENT_WITHOUT_CONTAINER_ID
        );

        // THEN
        expect(promise).toBeNull();
    });

    it(`GIVEN a component is inside a container
        WHEN getComponentContainer is called
        THEN it returns the container information`, async () => {
        // GIVEN

        // WHEN
        const promise = await slotContainerService.getComponentContainer(
            SLOT_ID,
            COMPONENT_IN_CONTAINER_ID
        );

        // THEN
        expect(promise).toEqual(sampleContainerInfo);
    });
});
