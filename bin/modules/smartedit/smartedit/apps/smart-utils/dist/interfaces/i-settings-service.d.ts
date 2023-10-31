/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { TypedMap } from '../dtos';
export declare abstract class ISettingsService {
    load(): Promise<TypedMap<string | boolean>>;
    get(key: string): Promise<string>;
    getBoolean(key: string): Promise<boolean>;
    getStringList(key: string): Promise<string[]>;
}
