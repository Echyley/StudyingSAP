/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { IComponentEditingFacade } from 'cmscommons';
import { GatewayProxied } from 'smarteditcommons';

@GatewayProxied(
    'addNewComponentToSlot',
    'addExistingComponentToSlot',
    'cloneExistingComponentToSlot',
    'moveComponent'
)
@Injectable()
export class ComponentEditingFacade extends IComponentEditingFacade {}
