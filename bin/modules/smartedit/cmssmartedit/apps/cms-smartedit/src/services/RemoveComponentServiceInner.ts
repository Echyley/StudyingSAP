/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import {
    COMPONENT_REMOVED_EVENT,
    ICMSComponent,
    PAGES_CONTENT_SLOT_COMPONENT_RESOURCE_URI,
    RemoveComponentInfo
} from 'cmscommons';
import {
    GatewayProxied,
    IAlertService,
    IRenderService,
    IRestService,
    IRestServiceFactory,
    SystemEventService
} from 'smarteditcommons';
import { ComponentInfoService } from './ComponentInfoService';

@GatewayProxied('removeComponent')
@Injectable()
export class RemoveComponentService {
    private resource: IRestService<void>;
    constructor(
        restServiceFactory: IRestServiceFactory,
        private alertService: IAlertService,
        private componentInfoService: ComponentInfoService,
        private renderService: IRenderService,
        private systemEventService: SystemEventService
    ) {
        this.resource = restServiceFactory.get(
            `${PAGES_CONTENT_SLOT_COMPONENT_RESOURCE_URI}/contentslots/:slotId/components/:componentId`,
            'componentId'
        );
    }

    public async removeComponent(configuration: RemoveComponentInfo): Promise<ICMSComponent> {
        try {
            await this.resource.remove({
                slotId: configuration.slotId,
                componentId: configuration.slotOperationRelatedId
            });
        } catch {
            this.alertService.showDanger({
                message: 'se.cms.remove.component.failed',
                messagePlaceholders: {
                    slotID: configuration.slotId,
                    componentID: configuration.slotOperationRelatedId
                },
                minWidth: '',
                mousePersist: true,
                duration: 1000,
                dismissible: true,
                width: '300px'
            });
            return Promise.reject();
        }

        // This call will come from the cache.
        const component = await this.componentInfoService.getById(configuration.componentUuid);
        this.systemEventService.publish(COMPONENT_REMOVED_EVENT, component);

        this.renderService.renderSlots(configuration.slotId);

        return this.componentInfoService.getById(configuration.componentUuid, true);
    }
}
