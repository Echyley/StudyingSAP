/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { isObject, isFunction } from 'lodash';
import { IRestrictionType } from '../../../dao/RestrictionTypesRestService';
import { RestrictionCMSItem } from '../types';

interface BaseRestrictionConfig {
    getRestrictionTypesFn?: () => Promise<IRestrictionType[]>;
    getSupportedRestrictionTypesFn?: () => Promise<string[]>;
    mode: string;
}

export interface EditingRestrictionConfig extends BaseRestrictionConfig {
    restriction: RestrictionCMSItem;
}

export interface SelectingRestrictionConfig extends BaseRestrictionConfig {
    existingRestrictions: RestrictionCMSItem[];
}

@Injectable()
export class RestrictionPickerConfigService {
    MODE_EDITING = 'editing';
    MODE_SELECT = 'select';

    public getConfigForEditing(
        existingRestriction: RestrictionCMSItem,
        getSupportedRestrictionTypesFn: () => Promise<string[]>
    ): EditingRestrictionConfig {
        return {
            mode: this.MODE_EDITING,
            restriction: existingRestriction,
            getSupportedRestrictionTypesFn
        };
    }

    public getConfigForSelecting(
        existingRestrictions: RestrictionCMSItem[],
        getRestrictionTypesFn: () => Promise<IRestrictionType[]>,
        getSupportedRestrictionTypesFn: () => Promise<string[]>
    ): SelectingRestrictionConfig {
        return {
            mode: this.MODE_SELECT,
            getRestrictionTypesFn,
            getSupportedRestrictionTypesFn,
            existingRestrictions
        };
    }

    public isEditingMode(config: EditingRestrictionConfig | SelectingRestrictionConfig): boolean {
        return config.mode === this.MODE_EDITING;
    }

    public isSelectMode(config: EditingRestrictionConfig | SelectingRestrictionConfig): boolean {
        return config.mode === this.MODE_SELECT;
    }

    public isValidConfig(config: SelectingRestrictionConfig | EditingRestrictionConfig): boolean {
        switch (config.mode) {
            case this.MODE_EDITING:
                return isObject((config as EditingRestrictionConfig).restriction);

            case this.MODE_SELECT:
                if (config.getSupportedRestrictionTypesFn) {
                    return (
                        isFunction(config.getRestrictionTypesFn) &&
                        isFunction(config.getSupportedRestrictionTypesFn)
                    );
                }
                return isFunction(config.getRestrictionTypesFn);

            default:
                return false;
        }
    }
}
