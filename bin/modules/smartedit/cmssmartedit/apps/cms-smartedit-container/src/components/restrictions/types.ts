/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { CMSRestriction } from 'cmscommons';
import { IDropdownMenuItem, CMSItem } from 'smarteditcommons';

export type RestrictionCMSItem = CMSItem &
    CMSRestriction & {
        /**
         * @internal
         * Actions for restriction dropdown
         */
        actions?: IDropdownMenuItem[];

        /**
         * @internal
         * Determine whether restriction can be edited
         */
        canBeEdited?: boolean;

        /**
         * @internal
         * Restriction index, used for propagating restrictions without ID
         */
        restrictionIndex?: number;
    };
