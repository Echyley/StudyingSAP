/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { IComponentVisibilityAlertService } from 'cmscommons';
import { GatewayProxied } from 'smarteditcommons';

@GatewayProxied('checkAndAlertOnComponentVisibility')
export class ComponentVisibilityAlertService extends IComponentVisibilityAlertService {}
