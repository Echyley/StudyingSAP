/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/* eslint-disable max-classes-per-file */
/**
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { toPairs } from 'lodash';
import { Observable, Subject } from 'rxjs';

/* tslint:disable:max-classes-per-file */

/**
 * Event payload when a property changes.
 */
export class InputPropertyChange {
    constructor(public key: string, public value: any) {}
}

/**
 * Used for storing component input values for the dynamic component. The values
 * are set onto the dynamic component's properties that are decorated by the @DynamicInput()
 * decorator. Values can be retrieved or set programmatically by the form element's 'input'
 * property.
 */
export class InputProperties {
    readonly changes: Observable<InputPropertyChange> = new Subject<InputPropertyChange>();
    /**
     * @internal
     */
    private readonly _map: Map<string, any>;

    constructor(object: { [key: string]: any } = {}) {
        this._map = new Map(toPairs(object));
    }

    /**
     * Get a property.
     *
     * @param {keyof T} key
     * @returns {T[keyof T] | undefined} value
     */
    get<T>(key: keyof T): T[keyof T] | undefined {
        return this._map.get(key as string);
    }

    /**
     * Setting a property.
     *
     * @param {keyof T} key
     * @param {T[keyof T]} value
     * @param {boolean} emit If emit is set to false. It will not emit changes to the
     * the component for those observing for property changes.
     */
    set<T>(key: keyof T, value: T[keyof T], emit = true): void {
        this._map.set(key as string, value);
        if (emit) {
            (this.changes as Subject<InputPropertyChange>).next(
                new InputPropertyChange(key as string, value)
            );
        }
    }
}
