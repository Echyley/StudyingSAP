import { OnDestroy, OnInit } from '@angular/core';
import { CombinedViewSelectItem, CustomizationVariation, PersonalizationsmarteditUtils } from 'personalizationcommons';
import { ItemComponentData, SelectComponent } from 'smarteditcommons';
import { PersonalizationsmarteditContextService } from '../../../service';
import { CombinedViewConfigureService } from '../CombinedViewConfigureService';
export declare class CombinedViewItemPrinterComponent implements OnInit, OnDestroy {
    data: ItemComponentData;
    private contextService;
    private personalizationsmarteditUtils;
    private combinedViewConfigureService;
    catalogFilter: string;
    combinedViewSelectItem: CombinedViewSelectItem;
    isSelected: boolean;
    select: SelectComponent<CombinedViewSelectItem>;
    private catalogFilterSubscription;
    constructor(data: ItemComponentData, contextService: PersonalizationsmarteditContextService, personalizationsmarteditUtils: PersonalizationsmarteditUtils, combinedViewConfigureService: CombinedViewConfigureService);
    ngOnInit(): Promise<void>;
    ngOnDestroy(): void;
    isItemFromCurrentCatalog: (itemVariation: CustomizationVariation) => boolean;
    isItemSelected: (item: CombinedViewSelectItem) => boolean;
}
