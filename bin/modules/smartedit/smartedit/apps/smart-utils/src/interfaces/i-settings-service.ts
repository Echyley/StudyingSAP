/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { TypedMap } from '../dtos';

export abstract class ISettingsService {
    load(): Promise<TypedMap<string | boolean>> {
        'proxyFunction';
        return Promise.resolve({ key: '' });
    }

    get(key: string): Promise<string> {
        'proxyFunction';
        return Promise.resolve('');
    }

    getBoolean(key: string): Promise<boolean> {
        'proxyFunction';
        return Promise.resolve(true);
    }

    getStringList(key: string): Promise<string[]> {
        'proxyFunction';
        return Promise.resolve([]);
    }
}
