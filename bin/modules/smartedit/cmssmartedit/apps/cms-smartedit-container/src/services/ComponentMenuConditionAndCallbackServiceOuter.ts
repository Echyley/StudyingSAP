/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { IComponentMenuConditionAndCallbackService } from 'cmscommons';
import { GatewayProxied } from 'smarteditcommons';

@GatewayProxied(
    'externalCondition',
    'removeCondition',
    'removeCallback',
    'cloneCondition',
    'cloneCallback',
    'sharedCondition',
    'editConditionForHiddenComponent',
    'dragCondition'
)
@Injectable()
export class ComponentMenuConditionAndCallbackService extends IComponentMenuConditionAndCallbackService {}
