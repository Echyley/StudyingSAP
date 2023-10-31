import { ChangeDetectorRef, OnInit } from '@angular/core';
import { Customization } from 'personalizationcommons';
import { Observable } from 'rxjs';
import { CustomizationViewService } from '../CustomizationViewService';
export declare class BasicInfoTabComponent implements OnInit {
    private customizationViewService;
    private cdr;
    customization$: Observable<Customization>;
    datetimeConfigurationEnabled: boolean;
    constructor(customizationViewService: CustomizationViewService, cdr: ChangeDetectorRef);
    ngOnInit(): Promise<void>;
    changeName(name: string): void;
    changeDescription(description: string): void;
    toggleDatetimeConfiguration(reset?: boolean): void;
    toggleCustomizationStatus(statusBoolean: boolean): void;
    onEnabledStartDateChange(date: string): void;
    onEnabledEndDateChange(date: string): void;
    private resetDatetimeConfiguration;
}
