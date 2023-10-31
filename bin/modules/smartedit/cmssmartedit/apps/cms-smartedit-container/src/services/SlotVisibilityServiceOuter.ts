/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ISlotVisibilityService } from 'cmscommons';
import { GatewayProxied } from 'smarteditcommons';

@GatewayProxied('getHiddenComponents')
export class SlotVisibilityService extends ISlotVisibilityService {}
