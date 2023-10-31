/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import * as lodash from 'lodash';
import { Cloneable, GatewayProxied, ISharedDataService, TypedMap } from 'smarteditcommons';

/** @internal */
@GatewayProxied()
@Injectable()
export class SharedDataService extends ISharedDataService {
    private storage: TypedMap<Cloneable> = {};

    constructor() {
        super();
    }

    get(key: string): Promise<Cloneable> {
        return Promise.resolve(this.storage[key]);
    }

    set(key: string, value: Cloneable): Promise<void> {
        this.storage[key] = value;
        return Promise.resolve();
    }

    update(key: string, modifyingCallback: (oldValue: any) => any): Promise<void> {
        return this.get(key).then((oldValue: any) =>
            modifyingCallback(oldValue).then((newValue: any) => this.set(key, newValue))
        );
    }

    remove(key: string): Promise<Cloneable> {
        const value = this.storage[key];
        delete this.storage[key];
        return Promise.resolve(value);
    }

    containsKey(key: string): Promise<boolean> {
        return Promise.resolve(lodash.has(this.storage, key));
    }
}
