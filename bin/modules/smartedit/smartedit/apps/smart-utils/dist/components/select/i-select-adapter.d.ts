/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { ISelectItem } from '../../interfaces/i-select-item';
export declare abstract class ISelectAdapter {
    static transform<T>(item: T, id: number): ISelectItem<T>;
}
