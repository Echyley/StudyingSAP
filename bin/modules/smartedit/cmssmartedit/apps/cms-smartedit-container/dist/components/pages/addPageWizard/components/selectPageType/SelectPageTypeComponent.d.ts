import { ChangeDetectorRef, EventEmitter, OnInit } from '@angular/core';
import { TypePermissionsRestService } from 'cmscommons';
import { PageType, PageTypeService } from '../../../../../dao/PageTypeService';
export declare class SelectPageTypeComponent implements OnInit {
    private pageTypeService;
    private typePermissionsRestService;
    private cdr;
    pageTypeCode: string;
    onTypeSelected: EventEmitter<PageType>;
    pageTypes: PageType[];
    constructor(pageTypeService: PageTypeService, typePermissionsRestService: TypePermissionsRestService, cdr: ChangeDetectorRef);
    ngOnInit(): Promise<void>;
    selectType(pageType: PageType): void;
    isSelected(pageType: PageType): boolean;
    private loadPageTypes;
}
