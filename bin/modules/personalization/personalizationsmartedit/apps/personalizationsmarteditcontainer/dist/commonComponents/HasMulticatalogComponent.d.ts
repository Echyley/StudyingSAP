import { OnInit } from '@angular/core';
import { PersonalizationsmarteditContextService } from 'personalizationsmarteditcontainer/service/PersonalizationsmarteditContextServiceOuter';
export declare class HasMulticatalogComponent implements OnInit {
    protected personalizationsmarteditContextService: PersonalizationsmarteditContextService;
    hasMulticatalog: boolean;
    constructor(personalizationsmarteditContextService: PersonalizationsmarteditContextService);
    ngOnInit(): void;
    getSeExperienceData(): any;
}
