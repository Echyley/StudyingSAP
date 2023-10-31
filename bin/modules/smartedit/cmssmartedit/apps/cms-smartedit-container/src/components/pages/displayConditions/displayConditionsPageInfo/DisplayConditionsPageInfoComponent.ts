/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, ChangeDetectionStrategy, Input } from '@angular/core';

@Component({
    selector: 'se-display-conditions-page-info',
    templateUrl: './DisplayConditionsPageInfoComponent.html',
    styleUrls: ['./DisplayConditionsPageInfoComponent.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class DisplayConditionsPageInfoComponent {
    @Input() isPrimary: boolean;
    @Input() pageName: string;
    @Input() pageType: string;

    public getPageDisplayConditionI18nKey(): string {
        return this.isPrimary
            ? 'se.cms.display.conditions.primary.id'
            : 'se.cms.display.conditions.variation.id';
    }

    public getPageDisplayConditionDescriptionI18nKey(): string {
        return this.isPrimary
            ? 'se.cms.display.conditions.primary.description'
            : 'se.cms.display.conditions.variation.description';
    }
}
