/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Pipe, PipeTransform } from '@angular/core';
import { TypedMap } from '@smart/utils';

/**
 * Pipe used to filter array of objects by object passed as an argument.
 *
 * The pipe will return array of objects that contains the exact keys and values of passed object.
 *
 * ### Example
 *
 *      <div *ngFor='let item of items | seProperty:{ isEnabled: true }'></div>
 */
@Pipe({ name: 'seProperty' })
export class PropertyPipe<T extends TypedMap<any>> implements PipeTransform {
    transform(array: T[], propObject: TypedMap<string>): T[] {
        if (!array) {
            return undefined;
        }

        if (!propObject) {
            return [...array];
        }

        return array.filter((item) =>
            Object.keys(propObject).every((key: string) => item[key] === propObject[key])
        );
    }
}
