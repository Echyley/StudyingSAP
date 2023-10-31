/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Cloneable } from '@smart/utils';
import { IStorage } from './IStorage';
import { IStorageOptions } from './IStorageOptions';

export abstract class IStorageGateway {
    handleStorageRequest(
        storageConfiguration: IStorageOptions,
        method: keyof IStorage<Cloneable, Cloneable>,
        args: Cloneable[]
    ): Promise<any> {
        'proxyFunction';
        return Promise.resolve();
    }
}
