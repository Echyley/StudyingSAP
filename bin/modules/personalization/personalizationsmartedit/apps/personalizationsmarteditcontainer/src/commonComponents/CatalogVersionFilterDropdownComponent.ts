/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, EventEmitter, Inject, Input, OnInit, Output, Type } from '@angular/core';
import { PersonalizationsmarteditContextService } from 'personalizationsmarteditcontainer/service/PersonalizationsmarteditContextServiceOuter';
import { take } from 'rxjs/operators';
import { ComponentMenuService, L10nPipe } from 'smarteditcommons';
import { CatalogVersionFilterItemPrinterComponent } from './CatalogVersionFilterItemPrinterComponent';
@Component({
    selector: 'catalog-version-filter-dropdown',
    template: `
        <se-select
            class="perso-filter"
            (click)="$event.stopPropagation()"
            [(model)]="selectedId"
            [onChange]="onChange"
            [fetchStrategy]="fetchStrategy"
            [itemComponent]="itemComponent"
            [searchEnabled]="false"
        ></se-select>
    `,
    providers: [L10nPipe]
})
export class CatalogVersionFilterDropdownComponent implements OnInit {
    @Input() public initialValue: string;
    @Output() public onSelectCallback: EventEmitter<string> = new EventEmitter<string>();
    public selectedId: string;
    public items: any[];
    public itemComponent: Type<any> = CatalogVersionFilterItemPrinterComponent;
    public fetchStrategy: {
        fetchAll: any;
    };

    constructor(
        protected componentMenuService: ComponentMenuService,
        @Inject(PersonalizationsmarteditContextService)
        protected personalizationsmarteditContextService: PersonalizationsmarteditContextService,
        private l10nPipe: L10nPipe
    ) {
        this.onChange = this.onChange.bind(this);
        this.fetchStrategy = {
            fetchAll: (): Promise<any[]> => Promise.resolve(this.items)
        };
    }

    async ngOnInit(): Promise<void> {
        this.items = await this.componentMenuService.getValidContentCatalogVersions();
        const experience = this.personalizationsmarteditContextService.getSeData().seExperienceData;
        this.items.forEach(async (item) => {
            item.isCurrentCatalog = item.id === experience.catalogDescriptor.catalogVersionUuid;
            item.catalogName = await this.l10nPipe
                .transform(item.catalogName)
                .pipe(take(1))
                .toPromise();
        });
        const selectedCatalogVersion = await this.componentMenuService.getInitialCatalogVersion(
            this.items
        );
        this.selectedId = this.initialValue || selectedCatalogVersion.id;
    }

    onChange(_changes: any): void {
        this.onSelectCallback.emit(this.selectedId);
    }
}
