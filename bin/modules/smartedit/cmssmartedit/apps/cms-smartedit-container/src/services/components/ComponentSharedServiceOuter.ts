/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { IComponentSharedService } from 'cmscommons';
import { GatewayProxied } from 'smarteditcommons';

@GatewayProxied()
export class ComponentSharedService extends IComponentSharedService {}
