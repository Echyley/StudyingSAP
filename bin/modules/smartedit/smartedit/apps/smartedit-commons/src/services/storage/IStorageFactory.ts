/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { IStorage } from './IStorage';
import { IStorageOptions } from './IStorageOptions';

/**
 * Represents a typical factory of {@link IStorage}(s).
 *
 * See {@link StorageManagerFactory}.
 */
export interface IStorageFactory {
    getStorage(configuration: IStorageOptions): Promise<IStorage<any, any>>;
}
