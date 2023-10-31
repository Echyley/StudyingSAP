import { EventEmitter, OnChanges, OnInit } from '@angular/core';
import { IUriContext, CMSItem } from 'smarteditcommons';
import { EditingRestrictionConfig, RestrictionPickerConfigService, SelectingRestrictionConfig } from '../../services';
export declare class RestrictionManagementComponent implements OnInit, OnChanges {
    private restrictionPickerConfigService;
    config: EditingRestrictionConfig | SelectingRestrictionConfig;
    uriContext: IUriContext;
    submitFn: () => Promise<CMSItem>;
    isDirtyFn: () => boolean;
    submitFnChange: EventEmitter<() => Promise<CMSItem>>;
    isDirtyFnChange: EventEmitter<() => boolean>;
    submitInternal: () => Promise<CMSItem>;
    isDirtyInternal: () => boolean;
    isReady: boolean;
    isEditMode: boolean;
    constructor(restrictionPickerConfigService: RestrictionPickerConfigService);
    ngOnInit(): void;
    ngOnChanges(): void;
}
