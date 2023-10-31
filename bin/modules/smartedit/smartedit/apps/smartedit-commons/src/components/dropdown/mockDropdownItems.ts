/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component } from '@angular/core';
import { noop } from 'lodash';
import { TypedMap } from 'smarteditcommons';
import { IDropdownMenuItem } from './dropdownMenu';

@Component({
    selector: '',
    template: ''
})
class MockDropdownMenuItemComponent {}

export const mockDropdownItems: TypedMap<IDropdownMenuItem[]> = {
    callback: [
        {
            callback: noop
        }
    ],
    component: [
        {
            component: MockDropdownMenuItemComponent
        }
    ]
};
