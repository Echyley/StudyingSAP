import { OnInit, DoCheck } from '@angular/core';
import { PersonalizationsmarteditContextUtils } from 'personalizationcommons';
import { CrossFrameEventService } from 'smarteditcommons';
import { PersonalizationsmarteditContextService } from '../service/PersonalizationsmarteditContextServiceOuter';
import { PersonalizationsmarteditPreviewService } from '../service/PersonalizationsmarteditPreviewService';
export declare class CustomizeToolbarContextComponent implements OnInit, DoCheck {
    protected personalizationsmarteditContextService: PersonalizationsmarteditContextService;
    protected personalizationsmarteditContextUtils: PersonalizationsmarteditContextUtils;
    protected personalizationsmarteditPreviewService: PersonalizationsmarteditPreviewService;
    protected crossFrameEventService: CrossFrameEventService;
    visible: boolean;
    title: string;
    subtitle: string;
    private selectedCustomization;
    private selectedVariations;
    private oldSelectedCustomization;
    private oldSelectedVariations;
    constructor(personalizationsmarteditContextService: PersonalizationsmarteditContextService, personalizationsmarteditContextUtils: PersonalizationsmarteditContextUtils, personalizationsmarteditPreviewService: PersonalizationsmarteditPreviewService, crossFrameEventService: CrossFrameEventService);
    ngOnInit(): void;
    ngDoCheck(): void;
    clear(): void;
}
