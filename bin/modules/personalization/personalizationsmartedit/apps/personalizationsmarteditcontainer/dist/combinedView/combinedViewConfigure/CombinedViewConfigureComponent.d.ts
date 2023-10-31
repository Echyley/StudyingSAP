import { OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { CombinedView, CombinedViewSelectItem, CustomizationVariation, PersonalizationsmarteditMessageHandler, PersonalizationsmarteditUtils } from 'personalizationcommons';
import { ComponentMenuService, FetchStrategy, ModalManagerService, SelectReset } from 'smarteditcommons';
import { PersonalizationsmarteditContextService, PersonalizationsmarteditRestService } from '../../service';
import { CombinedViewConfigureService } from './CombinedViewConfigureService';
export declare class CombinedViewConfigureComponent implements OnInit {
    protected translateService: TranslateService;
    protected contextService: PersonalizationsmarteditContextService;
    protected messageHandler: PersonalizationsmarteditMessageHandler;
    protected personalizationsmarteditUtils: PersonalizationsmarteditUtils;
    protected componentMenuService: ComponentMenuService;
    private restService;
    private modalManager;
    private combinedViewConfigureService;
    moreCustomizationsRequestProcessing: boolean;
    combinedView: CombinedView;
    customizationPageFilter: string;
    combinedViewItemsFetchStrategy: FetchStrategy<CombinedViewSelectItem>;
    selectedCombinedViewItemId: string;
    selectedCombinedViewItems: CombinedViewSelectItem[];
    catalogFilter: string;
    combinedViewItemPrinter: any;
    resetSelectedItems: SelectReset;
    constructor(translateService: TranslateService, contextService: PersonalizationsmarteditContextService, messageHandler: PersonalizationsmarteditMessageHandler, personalizationsmarteditUtils: PersonalizationsmarteditUtils, componentMenuService: ComponentMenuService, restService: PersonalizationsmarteditRestService, modalManager: ModalManagerService, combinedViewConfigureService: CombinedViewConfigureService);
    ngOnInit(): void;
    selectElement: (item: CombinedViewSelectItem, model?: string) => Promise<void>;
    removeSelectedItem: (item: CombinedViewSelectItem) => void;
    getClassForElement: (index: number) => string;
    getLetterForElement: (index: number) => string;
    isItemInSelectDisabled: (item: CombinedViewSelectItem) => boolean;
    pageFilterChange: (itemId: string) => void;
    catalogFilterChange: (itemId: string) => void;
    isItemFromCurrentCatalog: (item: CustomizationVariation) => boolean;
    private reset;
    private customizedVarItemsFetchPage;
    private customizedVarItemsFetchEntity;
    private getDefaultStatus;
    private isCombinedViewContextPersRemoved;
    private getCustomizationsCategoryFilter;
    private constructCombinedViewSelectItem;
    private onCancel;
    private onSave;
}
