/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { Customization, PERSONALIZATION_MODEL_STATUS_CODES } from 'personalizationcommons';
import { Observable } from 'rxjs';
import { take } from 'rxjs/operators';
import { CustomizationViewService } from '../CustomizationViewService';
@Component({
    selector: 'basic-info-tab',
    templateUrl: './BasicInfoTabComponent.html',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class BasicInfoTabComponent implements OnInit {
    public customization$: Observable<Customization>;

    public datetimeConfigurationEnabled: boolean;

    constructor(
        private customizationViewService: CustomizationViewService,
        private cdr: ChangeDetectorRef
    ) {
        this.datetimeConfigurationEnabled = false;
    }

    async ngOnInit(): Promise<void> {
        this.customization$ = this.customizationViewService.getCustomization$();

        const customization = await this.customizationViewService
            .getCustomization$()
            .pipe(take(1))
            .toPromise();

        this.datetimeConfigurationEnabled =
            !!customization.enabledStartDate || !!customization.enabledEndDate;
        this.cdr.detectChanges();
    }

    public changeName(name: string): void {
        const customization = this.customizationViewService.selectCustomization();
        this.customizationViewService.setCustomization({
            ...customization,
            name
        });
    }

    public changeDescription(description: string): void {
        const customization = this.customizationViewService.selectCustomization();
        this.customizationViewService.setCustomization({
            ...customization,
            description
        });
    }

    public toggleDatetimeConfiguration(reset = false): void {
        this.datetimeConfigurationEnabled = !this.datetimeConfigurationEnabled;

        if (reset) {
            this.resetDatetimeConfiguration();
        }
    }

    public toggleCustomizationStatus(statusBoolean: boolean): void {
        const customization = this.customizationViewService.selectCustomization();
        this.customizationViewService.setCustomization({
            ...customization,
            statusBoolean,
            status: statusBoolean
                ? PERSONALIZATION_MODEL_STATUS_CODES.ENABLED
                : PERSONALIZATION_MODEL_STATUS_CODES.DISABLED
        });
    }

    public onEnabledStartDateChange(date: string): void {
        const customization = this.customizationViewService.selectCustomization();
        this.customizationViewService.setCustomization({
            ...customization,
            enabledStartDate: date
        });
    }

    public onEnabledEndDateChange(date: string): void {
        const customization = this.customizationViewService.selectCustomization();
        this.customizationViewService.setCustomization({
            ...customization,
            enabledEndDate: date
        });
    }

    private resetDatetimeConfiguration(): void {
        const customization = this.customizationViewService.selectCustomization();
        this.customizationViewService.setCustomization({
            ...customization,
            enabledStartDate: undefined,
            enabledEndDate: undefined
        });
    }
}
