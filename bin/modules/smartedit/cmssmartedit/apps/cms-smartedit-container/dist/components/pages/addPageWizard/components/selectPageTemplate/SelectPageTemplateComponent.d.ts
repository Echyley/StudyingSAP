import { ChangeDetectorRef, EventEmitter, OnChanges, SimpleChanges } from '@angular/core';
import { IUriContext, PageTemplateType } from 'smarteditcommons';
import { PageTemplateService } from '../../../../../services/PageTemplateService';
export declare class SelectPageTemplateComponent implements OnChanges {
    private pageTemplateService;
    private cdr;
    uriContext: IUriContext;
    pageTypeCode: string;
    onTemplateSelected: EventEmitter<PageTemplateType>;
    searchString: string;
    pageTemplates: PageTemplateType[];
    filteredPageTemplates: PageTemplateType[];
    private selectedTemplate;
    private cache;
    constructor(pageTemplateService: PageTemplateService, cdr: ChangeDetectorRef);
    ngOnChanges(changes: SimpleChanges): Promise<void>;
    templateSelected(pageTemplate: PageTemplateType): void;
    isSelected(pageTemplate: PageTemplateType): boolean;
    clearSearch(): void;
    onSearchChange(value: string): void;
    private onInputUpdated;
    private setPageTemplates;
    private filterByQuery;
    private setDefaultFilteredPageTemplates;
}
