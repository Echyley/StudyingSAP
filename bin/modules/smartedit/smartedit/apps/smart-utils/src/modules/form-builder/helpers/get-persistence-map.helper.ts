/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { AbstractForm, FormField, FormGrouping, FormList } from '../models';

/**
 * Get an address book of persisting fields to the actual form path.
 * Example:
 * {
 *   'property': ['tab', 'property']
 *   ...
 * }
 * Where tab is not a persisting property of the model.
 */
export const getPersistenceMap = (
    form: AbstractForm,
    map: { [s: string]: string[] } = {},
    from: string[] = [],
    to: string[] = []
): { [path: string]: string[] } => {
    if (form instanceof FormField) {
        if (form.persist) {
            map[from.join('.')] = to;
        }

        return map;
    }

    if (form instanceof FormList || form instanceof FormGrouping) {
        if (form.persist && from.length) {
            map[from.join('.')] = to;
        }

        Object.keys(form.controls).forEach((current: string) => {
            const child = (form.controls as any)[current];
            const toActual = [...to, current];

            if (child.persist) {
                return getPersistenceMap(child, map, [...from, current], toActual);
            }

            return getPersistenceMap(child, map, from, toActual);
        });
    }

    return map;
};
