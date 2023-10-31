import { EventEmitter, OnInit, SimpleChanges } from '@angular/core';
import { IUriContext, CMSItem } from 'smarteditcommons';
export declare class ItemManagementComponent implements OnInit {
    item: CMSItem;
    uriContext: IUriContext;
    mode: string;
    contentApi: string;
    structureApi: string;
    componentType?: string;
    isDirty: () => boolean;
    submitFunction: () => Promise<CMSItem>;
    isDirtyChange: EventEmitter<() => boolean>;
    submitFunctionChange: EventEmitter<() => Promise<CMSItem>>;
    editorId: string;
    itemId: string;
    submit: () => Promise<CMSItem>;
    isDirtyInternal: () => boolean;
    reset: boolean;
    private readonly supportedModes;
    constructor();
    ngOnInit(): void;
    ngOnChanges(changes: SimpleChanges): void;
    private validateMode;
    private submitInternal;
    private isDirtyLocal;
}
