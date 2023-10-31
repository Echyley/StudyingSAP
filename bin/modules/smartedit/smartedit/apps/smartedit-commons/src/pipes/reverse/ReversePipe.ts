/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Pipe, PipeTransform } from '@angular/core';

/**
 * Returns an array containing the items from the specified collection in reverse order.
 */
@Pipe({ name: 'seReverse' })
export class ReversePipe implements PipeTransform {
    transform<T>(value: T[]): T[] | undefined {
        if (!value) {
            return undefined;
        }
        return value.slice().reverse();
    }
}
