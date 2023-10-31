/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { IStorageProperties } from 'smarteditcommons';

/**
 *
 * defaultStorageProperties are the default [IStorageProperties]{@link IStorageProperties} of the
 * storage system. These values should not be reference directly at build/compile time, but rather through the
 * angular provider that exposes them. See [IStoragePropertiesService]{@link IStoragePropertiesService}
 * for more details.
 *
 * ```
 * {
 *     STORAGE_IDLE_EXPIRY: 1000 * 60 * 60 * 24 * 30, // 30 days
 *     STORAGE_TYPE_LOCAL_STORAGE: "se.storage.type.localstorage",
 *     STORAGE_TYPE_SESSION_STORAGE: "se.storage.type.sessionstorage",
 *     STORAGE_TYPE_IN_MEMORY: "se.storage.type.inmemory",
 *     LOCAL_STORAGE_KEY_STORAGE_MANAGER_METADATA: "se.storage.storagemanager.metadata",
 *     LOCAL_STORAGE_ROOT_KEY: "se.storage.root",
 *     SESSION_STORAGE_ROOT_KEY: "se.storage.root"
 * }
 * ```
 */
/** @internal */
export const defaultStorageProperties: IStorageProperties = {
    STORAGE_IDLE_EXPIRY: 1000 * 60 * 60 * 24 * 30, // 30 days

    // STORAGE TYPES
    STORAGE_TYPE_LOCAL_STORAGE: 'se.storage.type.localstorage',
    STORAGE_TYPE_SESSION_STORAGE: 'se.storage.type.sessionstorage',
    STORAGE_TYPE_IN_MEMORY: 'se.storage.type.inmemory',

    // LOCAL STORAGE KEYS
    LOCAL_STORAGE_KEY_STORAGE_MANAGER_METADATA: 'se.storage.storagemanager.metadata',
    LOCAL_STORAGE_ROOT_KEY: 'se.storage.root',
    SESSION_STORAGE_ROOT_KEY: 'se.storage.root'
};
