/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { StringUtils } from '@smart/utils';
import { IPrioritized } from './interfaces/IPrioritized';

/**
 * The PriorityService handles arrays of {@link IPrioritized} elements.
 */
@Injectable()
export class PriorityService {
    constructor(private readonly stringUtils: StringUtils) {}
    /**
     * Will sort the candidate array by ascendign or descending priority.
     * Even if the priority is not defined for a number of elements, the sorting will still be consistent over invocations
     * @param candidate Elements to be sorted
     * @param ascending If true, candidate will be sorted by ascending priority.
     * @returns The sorted candidate array.
     */
    sort<T extends IPrioritized>(candidate: T[], ascending = true): T[] {
        return candidate.sort((item1: IPrioritized, item2: IPrioritized) => {
            let output: number = item1.priority - item2.priority;
            if (output === 0) {
                output = this.stringUtils
                    .encode(item1)
                    .localeCompare(this.stringUtils.encode(item2));
            }
            return output;
        });
    }
}
