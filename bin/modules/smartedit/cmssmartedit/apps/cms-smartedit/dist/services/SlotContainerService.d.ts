import { IExperienceService, IRestServiceFactory } from 'smarteditcommons';
export interface ContainerInfo {
    containerId: string;
    containerType: string;
    pageId: string;
    slotId: string;
    components: string[];
}
/**
 * This service allows retrieving information about the containers found in a given page.
 */
export declare class SlotContainerService {
    private experienceService;
    private containersInPage;
    private containersRestService;
    constructor(restServiceFactory: IRestServiceFactory, experienceService: IExperienceService);
    /**
     * This method is used to retrieve the information about the container holding the provided component.
     * If the component is not inside a container, the method returns null.
     *
     * @param slotId The SmartEdit id of the slot where the component in question is located.
     * @param componentUuid The UUID of the component as defined in the database.
     *
     * @returns A promise that resolves to the information of the container of the component provided.
     * Will be null if the component is not inside a container.
     */
    getComponentContainer(slotId: string, componentUuid: string): Promise<ContainerInfo>;
    private loadContainersInPageInfo;
}
