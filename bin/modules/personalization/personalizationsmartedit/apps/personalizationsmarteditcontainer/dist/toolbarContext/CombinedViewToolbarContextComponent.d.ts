import { OnInit, DoCheck } from '@angular/core';
import { PersonalizationsmarteditContextUtils } from 'personalizationcommons';
import { CombinedViewCommonsService } from 'personalizationsmarteditcontainer/combinedView/CombinedViewCommonsService';
import { PersonalizationsmarteditContextService } from 'personalizationsmarteditcontainer/service/PersonalizationsmarteditContextServiceOuter';
import { CrossFrameEventService } from 'smarteditcommons';
export declare class CombinedViewToolbarContextComponent implements OnInit, DoCheck {
    private combinedViewCommonsService;
    private personalizationsmarteditContextService;
    private personalizationsmarteditContextUtils;
    private crossFrameEventService;
    visible: boolean;
    title: string;
    subtitle: string;
    private selectedCustomization;
    private oldSelectedCustomization;
    private oldSelectedCustomizationEnabled;
    constructor(combinedViewCommonsService: CombinedViewCommonsService, personalizationsmarteditContextService: PersonalizationsmarteditContextService, personalizationsmarteditContextUtils: PersonalizationsmarteditContextUtils, crossFrameEventService: CrossFrameEventService);
    ngOnInit(): void;
    ngDoCheck(): void;
    clear(): void;
}
