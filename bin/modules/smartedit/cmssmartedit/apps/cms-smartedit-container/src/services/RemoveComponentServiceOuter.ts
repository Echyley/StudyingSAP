/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { IRemoveComponentService } from 'cmscommons';
import { GatewayProxied } from 'smarteditcommons';

@GatewayProxied('removeComponent')
export class RemoveComponentService extends IRemoveComponentService {}
