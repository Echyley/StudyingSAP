import { ChangeDetectorRef, OnInit } from '@angular/core';
import { ContextualMenuItemData } from 'smarteditcommons';
import { IContextAwareEditableItemService } from '../../services';
export declare class SharedComponentButtonComponent implements OnInit {
    private contextAwareEditableItemService;
    private cdr;
    isReady: boolean;
    message: string;
    private smarteditComponentId;
    constructor(item: ContextualMenuItemData, contextAwareEditableItemService: IContextAwareEditableItemService, cdr: ChangeDetectorRef);
    ngOnInit(): Promise<void>;
}
