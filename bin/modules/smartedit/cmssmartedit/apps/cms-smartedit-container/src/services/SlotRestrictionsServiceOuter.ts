/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { GatewayProxied, ISlotRestrictionsService } from 'smarteditcommons';

@GatewayProxied(
    'getAllComponentTypesSupportedOnPage',
    'getSlotRestrictions',
    'determineComponentStatusInSlot',
    'isSlotEditable'
)
export class SlotRestrictionsService extends ISlotRestrictionsService {}
