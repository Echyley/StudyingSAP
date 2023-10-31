/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */

import { ISelectItem } from '../../interfaces/i-select-item';
import { IToolingLanguage } from '../../services';
import { ISelectAdapter } from '../select/i-select-adapter';

export class LanguageDropdownAdapter implements ISelectAdapter {
    static transform(item: IToolingLanguage, id: number): ISelectItem<IToolingLanguage> {
        return {
            id,
            label: item.name,
            value: item
        };
    }
}
