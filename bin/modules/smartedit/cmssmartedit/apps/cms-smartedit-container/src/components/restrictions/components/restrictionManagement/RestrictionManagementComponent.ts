/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, EventEmitter, Input, OnChanges, OnInit, Output } from '@angular/core';
import { IUriContext, CMSItem } from 'smarteditcommons';
import {
    EditingRestrictionConfig,
    RestrictionPickerConfigService,
    SelectingRestrictionConfig
} from '../../services';

@Component({
    selector: 'se-restriction-management',
    templateUrl: './RestrictionManagementComponent.html'
})
export class RestrictionManagementComponent implements OnInit, OnChanges {
    @Input() config: EditingRestrictionConfig | SelectingRestrictionConfig;
    @Input() uriContext: IUriContext;
    @Input() submitFn: () => Promise<CMSItem>;
    @Input() isDirtyFn: () => boolean;

    @Output() submitFnChange: EventEmitter<() => Promise<CMSItem>>;
    @Output() isDirtyFnChange: EventEmitter<() => boolean>;

    public submitInternal: () => Promise<CMSItem>;
    public isDirtyInternal: () => boolean;
    public isReady: boolean;
    public isEditMode: boolean;

    constructor(private restrictionPickerConfigService: RestrictionPickerConfigService) {
        this.submitFnChange = new EventEmitter();
        this.isDirtyFnChange = new EventEmitter();
        this.isReady = false;
        this.isEditMode = false;
    }

    ngOnInit(): void {
        // To prevent "ExpressionChangeAfterItHasBeenChecked" error, because we change values
        // for instance in PageRestrictionsStepComponent.ts
        setTimeout(() => {
            this.submitFnChange.emit(() => this.submitInternal().then((value) => value));
            this.isDirtyFnChange.emit(() => this.isDirtyInternal && this.isDirtyInternal());
        });
    }

    ngOnChanges(): void {
        if (!this.config) {
            return;
        }

        if (this.restrictionPickerConfigService.isValidConfig(this.config)) {
            this.isReady = true;
            this.isEditMode = this.restrictionPickerConfigService.isEditingMode(
                this.config as EditingRestrictionConfig
            );
        } else {
            throw 'RestrictionManagementComponent - invalid restrictionPickerConfig';
        }
    }
}
