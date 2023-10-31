/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { toPairs } from 'lodash';
/**
 * @internal
 *
 * Generic registry for mapping keys to items.
 */
export abstract class Registry<T> {
    private _map: Map<string, T>;

    constructor(items: { [name: string]: T } = {}) {
        this._map = new Map(toPairs(items));
    }

    /**
     * Adds a item to the registry.
     *
     * @param {string} name
     * @param {T} item
     */
    add(name: string, item: T): void {
        if (this._map.has(name)) {
            throw new Error(
                `${this._service}: is overriding an element named '${name}' in its registry.`
            );
        }
        this._map.set(name, item);
    }

    /**
     * Gets an a item in the registry.
     *
     * @param {string} name
     * @returns {T}
     */
    get(name: string): T | undefined {
        if (!this._map.has(name)) {
            throw new Error(`${this._service}: does not have '${name}' in its registry.`);
        }
        return this._map.get(name);
    }

    /**
     * @internal
     */
    private get _service(): string {
        return this.constructor.name;
    }
}
