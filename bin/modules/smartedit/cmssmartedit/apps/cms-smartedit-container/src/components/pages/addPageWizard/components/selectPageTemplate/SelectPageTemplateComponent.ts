/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    ChangeDetectionStrategy,
    ChangeDetectorRef,
    Component,
    EventEmitter,
    Input,
    OnChanges,
    Output,
    SimpleChanges,
    ViewRef
} from '@angular/core';
import { IUriContext, TypedMap, CMSPageTypes, PageTemplateType } from 'smarteditcommons';
import { PageTemplateService } from '../../../../../services/PageTemplateService';

@Component({
    selector: 'se-select-page-template',
    templateUrl: './SelectPageTemplateComponent.html',
    styleUrls: ['../../addPageWizard.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class SelectPageTemplateComponent implements OnChanges {
    @Input() uriContext: IUriContext;
    @Input() pageTypeCode: string;
    @Output() onTemplateSelected: EventEmitter<PageTemplateType>;

    public searchString: string;
    public pageTemplates: PageTemplateType[];
    public filteredPageTemplates: PageTemplateType[];

    private selectedTemplate: PageTemplateType;
    private cache: TypedMap<PageTemplateType[]>;

    constructor(private pageTemplateService: PageTemplateService, private cdr: ChangeDetectorRef) {
        this.searchString = '';
        this.onTemplateSelected = new EventEmitter();
        this.cache = {};
        this.pageTemplates = [];
    }

    async ngOnChanges(changes: SimpleChanges): Promise<void> {
        if (changes.pageTypeCode?.currentValue) {
            await this.onInputUpdated();
            this.setDefaultFilteredPageTemplates();

            if (!(this.cdr as ViewRef).destroyed) {
                this.cdr.detectChanges();
            }
        }
    }

    public templateSelected(pageTemplate: PageTemplateType): void {
        this.selectedTemplate = pageTemplate;
        this.onTemplateSelected.emit(pageTemplate);
    }

    public isSelected(pageTemplate: PageTemplateType): boolean {
        return pageTemplate.uid === this.selectedTemplate?.uid;
    }

    public clearSearch(): void {
        this.searchString = '';
        this.setDefaultFilteredPageTemplates();
    }

    public onSearchChange(value: string): void {
        this.searchString = value;
        this.filterByQuery(this.searchString);
    }

    private async onInputUpdated(): Promise<void> {
        this.clearSearch();
        this.selectedTemplate = null;

        return this.setPageTemplates();
    }

    private async setPageTemplates(): Promise<void> {
        if (this.cache[this.pageTypeCode]) {
            this.pageTemplates = this.cache[this.pageTypeCode];
            return;
        }
        this.pageTemplates = [];

        const pageTemplates = await this.pageTemplateService.getPageTemplatesForType(
            this.uriContext,
            this.pageTypeCode as CMSPageTypes
        );
        this.cache[this.pageTypeCode] = pageTemplates.templates;
        this.pageTemplates = this.cache[this.pageTypeCode];
    }

    private filterByQuery(query?: string): void {
        if (!query) {
            this.setDefaultFilteredPageTemplates();
            return;
        }

        this.filteredPageTemplates = this.pageTemplates.filter((template) => {
            const terms = query.split(' ');
            return terms.every((term) => template.name.toLowerCase().includes(term.toLowerCase()));
        });
    }

    private setDefaultFilteredPageTemplates(): void {
        this.filteredPageTemplates = [...this.pageTemplates];
    }
}
