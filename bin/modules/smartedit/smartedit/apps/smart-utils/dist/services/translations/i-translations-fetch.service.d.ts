/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { TypedMap } from '../../dtos';
export declare type TranslationMap = TypedMap<string>;
/**
 * @module blabla
 */
export declare abstract class ITranslationsFetchService {
    abstract get(lang: string): Promise<TranslationMap>;
    abstract isReady(): Promise<boolean>;
    abstract waitToBeReady(): Promise<void>;
}
