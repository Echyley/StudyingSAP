/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { IComponentSharedService, ICMSComponent } from 'cmscommons';
import { GatewayProxied } from 'smarteditcommons';
import { ComponentInfoService } from './ComponentInfoService';

@GatewayProxied()
@Injectable()
export class ComponentSharedService extends IComponentSharedService {
    constructor(private componentInfoService: ComponentInfoService) {
        super();
    }

    public async isComponentShared(componentParam: string | ICMSComponent): Promise<boolean> {
        const component = await this.determineComponent(componentParam);
        if (!component.slots) {
            throw new Error(
                'ComponentSharedService::isComponentShared - Component must have slots property.'
            );
        }
        return component.slots.length > 1;
    }

    private determineComponent(componentParam: string | ICMSComponent): Promise<ICMSComponent> {
        if (typeof componentParam === 'string') {
            return this.componentInfoService.getById(componentParam, true);
        } else if (componentParam.uuid) {
            return this.componentInfoService.getById(componentParam.uuid, true);
        }
        return Promise.resolve(componentParam);
    }
}
