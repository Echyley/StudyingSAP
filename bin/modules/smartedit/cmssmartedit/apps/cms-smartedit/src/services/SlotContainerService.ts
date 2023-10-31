/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { includes } from 'lodash';

import {
    IExperienceService,
    IRestService,
    IRestServiceFactory,
    PAGE_CONTEXT_CATALOG,
    PAGE_CONTEXT_CATALOG_VERSION,
    PAGE_CONTEXT_SITE_ID
} from 'smarteditcommons';

export interface ContainerInfo {
    containerId: string;
    containerType: string;
    pageId: string;
    slotId: string;
    components: string[];
}

interface PageContentSlotContainerListResponse {
    pageContentSlotContainerList: ContainerInfo[];
}
/**
 * This service allows retrieving information about the containers found in a given page.
 */
@Injectable()
export class SlotContainerService {
    private containersInPage: ContainerInfo[];
    private containersRestService: IRestService<PageContentSlotContainerListResponse>;

    constructor(
        restServiceFactory: IRestServiceFactory,
        private experienceService: IExperienceService
    ) {
        const contentSlotContainerResourceURI = `/cmswebservices/v1/sites/${PAGE_CONTEXT_SITE_ID}/catalogs/${PAGE_CONTEXT_CATALOG}/versions/${PAGE_CONTEXT_CATALOG_VERSION}/pagescontentslotscontainers?pageId=:pageId`;
        this.containersRestService = restServiceFactory.get(contentSlotContainerResourceURI);
    }

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
    public async getComponentContainer(
        slotId: string,
        componentUuid: string
    ): Promise<ContainerInfo> {
        const containersInPage = await this.loadContainersInPageInfo();
        const containers = containersInPage.filter(
            (container: ContainerInfo) =>
                container.slotId === slotId && includes(container.components, componentUuid)
        );

        return containers.length > 0 ? containers[0] : null;
    }

    private async loadContainersInPageInfo(): Promise<ContainerInfo[]> {
        if (this.containersInPage) {
            return this.containersInPage;
        }
        const experience = await this.experienceService.getCurrentExperience();
        const result = await this.containersRestService.get({ pageId: experience.pageId });

        this.containersInPage = result.pageContentSlotContainerList;
        return this.containersInPage;
    }
}
