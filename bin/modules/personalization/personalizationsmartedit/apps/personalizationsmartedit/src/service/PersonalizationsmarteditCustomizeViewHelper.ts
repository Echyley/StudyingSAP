/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { Dictionary, countBy } from 'lodash';
import { PersonalizationsmarteditComponentHandlerService } from 'personalizationsmartedit/service/PersonalizationsmarteditComponentHandlerService';

@Injectable()
export class PersonalizationsmarteditCustomizeViewHelper {
    constructor(
        private personalizationsmarteditComponentHandlerService: PersonalizationsmarteditComponentHandlerService
    ) {}

    getSourceContainersInfo(): Dictionary<number> {
        let slotsSelector: string =
            this.personalizationsmarteditComponentHandlerService.getAllSlotsSelector();
        slotsSelector += ' [data-smartedit-container-source-id]'; // space at beginning is important
        const slots: any =
            this.personalizationsmarteditComponentHandlerService.getFromSelector(slotsSelector);
        const slotIds = slots.map((key: number, val: JQuery) => {
            const component: JQuery =
                this.personalizationsmarteditComponentHandlerService.getFromJQuerySelector(val);
            return {
                containerId:
                    this.personalizationsmarteditComponentHandlerService.getParentContainerIdForComponent(
                        component
                    ),
                containerSourceId:
                    this.personalizationsmarteditComponentHandlerService.getParentContainerSourceIdForComponent(
                        component
                    )
            };
        });

        return countBy(slotIds, 'containerSourceId');
    }
}
