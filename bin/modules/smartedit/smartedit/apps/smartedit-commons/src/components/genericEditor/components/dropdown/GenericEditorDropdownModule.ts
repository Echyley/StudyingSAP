/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { CommonModule } from '@angular/common';
import { NgModule, Optional } from '@angular/core';

import { SelectModule } from '../../../../components/select';
import { SystemEventService, LogService } from '../../../../services';
import { CLICK_DROPDOWN, LINKED_DROPDOWN, DROPDOWN_POPULATOR } from '../../../../utils';
import { GenericEditorDropdownComponent } from './GenericEditorDropdownComponent';
import { GenericEditorDropdownServiceFactory } from './GenericEditorDropdownServiceFactory';
import { GenericEditorUtil } from './GenericEditorUtil';
import {
    CustomDropdownPopulatorsToken,
    DropdownPopulatorModule,
    OptionsDropdownPopulator,
    UriDropdownPopulator
} from './populators';
import {
    CLICK_DROPDOWN_TOKEN,
    DROPDOWN_IMPLEMENTATION_SUFFIX_TOKEN,
    IGenericEditorDropdownServiceConstructor,
    LINKED_DROPDOWN_TOKEN
} from './types';

@NgModule({
    imports: [CommonModule, SelectModule, DropdownPopulatorModule],
    declarations: [GenericEditorDropdownComponent],
    entryComponents: [GenericEditorDropdownComponent],
    exports: [GenericEditorDropdownComponent],
    providers: [
        {
            provide: DROPDOWN_IMPLEMENTATION_SUFFIX_TOKEN,
            useValue: DROPDOWN_POPULATOR
        },
        {
            provide: LINKED_DROPDOWN_TOKEN,
            useValue: LINKED_DROPDOWN
        },
        {
            provide: CLICK_DROPDOWN_TOKEN,
            useValue: CLICK_DROPDOWN
        },
        GenericEditorUtil,
        // Injected by <se-generic-editor-dropdown>. It doesn't create a new instance (as it supposed to do because it's a factory function).
        // Instead, it returns a constructor function that is instantiated in `GenericEditorDropdownComponent#ngOnInit`.
        {
            provide: IGenericEditorDropdownServiceConstructor,
            useFactory: GenericEditorDropdownServiceFactory,
            deps: [
                GenericEditorUtil,
                LogService,
                LINKED_DROPDOWN_TOKEN,
                CLICK_DROPDOWN_TOKEN,
                DROPDOWN_IMPLEMENTATION_SUFFIX_TOKEN,
                SystemEventService,
                OptionsDropdownPopulator,
                UriDropdownPopulator,
                [new Optional(), CustomDropdownPopulatorsToken] // Only available when Custom Populator has been provided.
            ]
        }
    ]
})
export class GenericEditorDropdownModule {}
