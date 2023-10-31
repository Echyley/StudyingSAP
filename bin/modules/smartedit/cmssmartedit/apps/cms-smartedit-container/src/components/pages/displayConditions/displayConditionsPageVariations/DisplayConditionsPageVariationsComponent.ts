/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, ChangeDetectionStrategy, Input } from '@angular/core';
import { IDisplayConditionsPageVariation } from 'cmssmarteditcontainer/facades';
import { ClientPagedListColumnKey } from 'smarteditcommons';
import { CreationDateRendererComponent } from './CreationDateRendererComponent';

@Component({
    selector: 'se-display-conditions-page-variations',
    templateUrl: './DisplayConditionsPageVariationsComponent.html',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class DisplayConditionsPageVariationsComponent {
    @Input() variations: IDisplayConditionsPageVariation[];

    public itemsPerPage: number;
    public keys: ClientPagedListColumnKey[];

    constructor() {
        this.itemsPerPage = 3;

        this.keys = [
            {
                property: 'pageName',
                i18n: 'se.cms.display.conditions.header.page.name'
            },
            {
                property: 'creationDate',
                i18n: 'se.cms.display.conditions.header.creation.date',
                component: CreationDateRendererComponent
            },
            {
                property: 'restrictions',
                i18n: 'se.cms.display.conditions.header.restrictions'
            }
        ];
    }
}
