import { EventEmitter, OnInit } from '@angular/core';
import { IUriContext, CMSItem } from 'smarteditcommons';
import { RestrictionsService } from '../../../../services/RestrictionsService';
export declare class RestrictionManagementEditComponent implements OnInit {
    private restrictionsService;
    restriction: CMSItem;
    getSupportedRestrictionTypes: () => Promise<string[]>;
    uriContext: IUriContext;
    isDirtyFn: () => boolean;
    submitFn: () => Promise<CMSItem>;
    isDirtyFnChange: EventEmitter<() => boolean>;
    submitFnChange: EventEmitter<() => Promise<CMSItem>>;
    submitInternal: () => Promise<CMSItem>;
    isDirtyInternal: () => boolean;
    ready: boolean;
    itemManagementMode: string;
    contentApi: string;
    structureApi: string;
    isTypeSupported: boolean;
    constructor(restrictionsService: RestrictionsService);
    ngOnInit(): Promise<void>;
    private emitActions;
    private isDirtyLocal;
}
