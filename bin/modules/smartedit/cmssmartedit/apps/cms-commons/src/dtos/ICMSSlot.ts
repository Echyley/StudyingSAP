/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { CMSItem } from 'smarteditcommons';

/**
 * @description
 * Interface for cms-slot information.
 * This interface first defined for page-tree
 */
export interface ICMSSlot extends CMSItem {
    active: boolean;
    cmsComponents: string[];
    synchronizationBlocked: boolean;
}
